package net.haesleinhuepf.spimcat.segmentation.pixelclassification;

import ij.*;
import ij.gui.*;
import ij.io.SaveDialog;
import ij.plugin.HyperStackConverter;
import ij.plugin.RGBStackConverter;
import ij.plugin.Selection;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.Recorder;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.weka.*;
import net.haesleinhuepf.clijx.weka.gui.InteractivePanelPlugin;
import net.haesleinhuepf.clijx.weka.gui.kernels.MakeRGB;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

public class CLIJxWekaBinaryPixelClassification extends InteractivePanelPlugin implements PlugInFilter {


    static Color foregroundColor = Color.green;
    static Color backgroundColor = Color.magenta;
    static Color eraseColor = Color.yellow;
    float overlayAlpha = 0.5f;
    CLIJ2 clij2 = null;

    ImagePlus inputImp = null;

    protected Overlay overlay;

    ClearCLBuffer clInput = null;
    ClearCLBuffer clResult = null;
    CLIJxWeka2 clijxweka = null;
    private String pixelClassificationFeatureDefinition;
    private String pixelClassificationModelFile;

    public CLIJxWekaBinaryPixelClassification() {
        overlay = new Overlay();
    }


    @Override
    public int setup(String arg, ImagePlus imp) {
        return PlugInFilter.DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        inputImp = IJ.getImage();

        clij2 = CLIJ2.getInstance();

        clInput = clij2.push(inputImp);


        buildGUI();
        updateVisualisation();

        addForegroundClicked();
    }


    JToggleButton foregroundButton;
    JToggleButton backgroundButton;
    JToggleButton eraseButton;
    Button saveButton;
    Button applyButton;

    Button foregroundColorButton;
    Button backgroundColorButton;
    private void buildGUI() {
        final ImageWindow window = inputImp.getWindow();
        super.attach(window);

        {
            foregroundButton = new JToggleButton("Foreground");
            foregroundButton.setSize(40, 15);
            foregroundButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addForegroundClicked();
                }
            });
            guiPanel.add(foregroundButton);
        }
        {
            foregroundColorButton = new Button(" ");
            foregroundColorButton.setSize(15, 15);
            foregroundColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Color result = JColorChooser.showDialog(guiPanel, "Foreground", foregroundColor);//.getColor();
                    if (result == null) {
                        return;
                    }
                    foregroundColor = result;
                    updateVisualisation();
                    refresh();
                }
            });
            guiPanel.add(foregroundColorButton);
        }
        guiPanel.add(new Label(" "));
        {
            backgroundButton = new JToggleButton("Background");
            backgroundButton.setSize(40, 15);
            backgroundButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addBackgroundClicked();
                }
            });
            guiPanel.add(backgroundButton);
        }{
            backgroundColorButton = new Button(" ");
            backgroundColorButton.setSize(15, 15);
            backgroundColorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Color result = JColorChooser.showDialog(guiPanel, "Background", backgroundColor);//.getColor();
                    if (result == null) {
                        return;
                    }
                    backgroundColor = result;
                    updateVisualisation();
                    refresh();
                }
            });
            guiPanel.add(backgroundColorButton);
        }
        guiPanel.add(new Label(" "));
        {

            eraseButton = new JToggleButton("Erase");
            eraseButton.setSize(40, 15);
            eraseButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    eraseClicked();
                }
            });
            guiPanel.add(eraseButton);
        }
        guiPanel.add(new Label(" "));

        {
            Button configButton = new Button("Config");
            configButton.setSize(40, 15);
            configButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    configClicked();
                }
            });
            guiPanel.add(configButton);
        }

        {
            Button trainButton = new Button("Train");
            trainButton.setSize(40, 15);
            trainButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    trainClicked();
                }
            });
            guiPanel.add(trainButton);
        }

        {
            JSlider slider = new JSlider(0, 100);
            slider.setSize(25, 15);
            slider.setValue((int) (overlayAlpha * 100));
            slider.setToolTipText("Result overlay alpha");
            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    overlayAlpha = (float) slider.getValue() / 100;
                    updateVisualisation();
                }
            });
            guiPanel.add(slider);
        }

        {
            saveButton = new Button("Save");
            saveButton.setSize(40, 15);
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveModelClicked();
                }
            });
            saveButton.setEnabled(false);

            guiPanel.add(saveButton);
        }

        {
            applyButton = new Button("Apply");
            applyButton.setSize(40, 15);
            applyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    applyClicked();
                }
            });
            applyButton.setEnabled(false);

            guiPanel.add(applyButton);
        }

        {
            Button cancelButton = new Button("Cancel");
            cancelButton.setSize(40, 15);
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cancelClicked();
                }
            });
            guiPanel.add(cancelButton);
        }

        refresh();
    }

    private void configClicked() {
        GenericDialog gd = new GenericDialog("Configure classifier training");
        gd.addStringField("Features", pixelClassificationFeatureDefinition);
        gd.addMessage(new GenerateFeatureStack().getDescription());
        gd.showDialog();
        if (gd.wasCanceled()) {
            return;
        }
        pixelClassificationFeatureDefinition = gd.getNextString();
    }

    private void cancelClicked() {
        inputImp.setProcessor(clij2.pull(clInput).getProcessor());
        cleanUp();
    }

    private void cleanUp()
    {
        dismantle();

        clij2.release(clInput);
        clInput = null;
        clij2.release(clResult);
        clResult = null;
        inputImp.setOverlay(null);
        inputImp.killRoi();
        guiPanel.setVisible(false);
        guiPanel.dispose();
        guiPanel = null;
    }

    private void applyClicked() {
        inputImp.setProcessor(clij2.pullBinary(clResult).getProcessor());



        Recorder.setCommand(null);
        boolean recordBefore = Recorder.record;
        Recorder.record = false;

        String inputImage = inputImp.getTitle();
        String outputImage = "CLIJxWeka_Pixel_Classified";

        recordIfNotRecorded("run", "\"CLIJ Macro Extensions\", \"cl_device=[" + clij2.getGPUName() + "]\"");
        recordIfNotRecorded("Ext.CLIJ_push", "\"" + inputImage + "\"");

        Recorder.recordString("Ext.CLIJx_applyOCLWekaModel(\"" +inputImage+ "\", \"" + outputImage + "\", \"" + pixelClassificationModelFile + "\");");

        recordIfNotRecorded("Ext.CLIJ_pull", "\"" + outputImage + "\"");

        Recorder.record = recordBefore;

        cleanUp();
    }

    private void recordIfNotRecorded(String recordMethod, String recordParameters) {
        if (Recorder.getInstance() == null) {
            return;
        }
        String text = Recorder.getInstance().getText();
        if (text.contains(recordMethod) && text.contains(recordParameters)) {
            return;
        }
        record(recordMethod, recordParameters);
    }

    private void record(String recordMethod, String recordParameters) {
        if (Recorder.getInstance() == null) {
            return;
        }
        Recorder.recordString(recordMethod + "(" + recordParameters + ");\n");
        Recorder.record = true;
    }


    protected void refresh() {

        foregroundColorButton.setBackground(foregroundColor);
        backgroundColorButton.setBackground(backgroundColor);

        foregroundButton.setSelected(false);
        backgroundButton.setSelected(false);
        eraseButton.setSelected(false);

        foregroundButton.setBackground(null);
        backgroundButton.setBackground(null);
        eraseButton.setBackground(null);

        saveButton.setEnabled(clResult != null);
        applyButton.setEnabled(clResult != null);

        if (mouseMode == MouseMode.FOREGROUND) {
            Roi.setColor(foregroundColor);
            foregroundButton.setBackground(foregroundColor);
            //foregroundButton.setSelected(true);
        }
        if (mouseMode == MouseMode.BACKGROUND) {
            Roi.setColor(backgroundColor);
            backgroundButton.setBackground(backgroundColor);
            //backgroundButton.setSelected(true);
        }
        if (mouseMode == MouseMode.ERASE) {
            Roi.setColor(eraseColor);
            eraseButton.setBackground(eraseColor);
            //eraseButton.setSelected(true);
        }
        for (int i = 0; i < overlay.size(); i++) {
            Roi roi = overlay.get(i);
            if (roi.getName() != null) {
                if (roi.getName().compareTo(MouseMode.FOREGROUND.toString()) == 0) {
                    roi.setStrokeColor(foregroundColor);
                } else if (roi.getName().compareTo(MouseMode.BACKGROUND.toString()) == 0) {
                    roi.setStrokeColor(backgroundColor);
                }
            }
        }
        super.refresh();
    }

    private void updateVisualisation() {
        refresh();

        if (clResult != null) {
            ClearCLBuffer foreground = clij2.create(clInput.getDimensions(), NativeTypeEnum.UnsignedByte);
            ClearCLBuffer background = clij2.create(clInput.getDimensions(), NativeTypeEnum.UnsignedByte);
            clij2.multiplyImageAndScalar(clResult, foreground, 255);
            clij2.subtractImageFromScalar(foreground, background, 255);

            ClearCLBuffer rgb = clij2.create(new long[]{clInput.getWidth(), clInput.getHeight(), 3}, NativeTypeEnum.UnsignedByte);
            MakeRGB.makeRGB(clij2, clInput, foreground, background, rgb, (float) inputImp.getDisplayRangeMin(), (float) inputImp.getDisplayRangeMax(), foregroundColor, backgroundColor, overlayAlpha);

            ImageStack stack = clij2.pull(rgb).getStack();

            ImagePlus tempImp = new ImagePlus("temp", stack);
            tempImp = HyperStackConverter.toHyperStack(tempImp, 3, 1, 1);
            RGBStackConverter.convertToRGB(tempImp);

            inputImp.setProcessor(tempImp.getProcessor());

            clij2.release(foreground);
            clij2.release(background);
            clij2.release(rgb);

        } else {
            ImagePlus vis = clij2.pull(clInput);
            inputImp.setProcessor(vis.getProcessor());
        }
    }

    private void saveModelClicked() {
        if (clijxweka != null) {
            SaveDialog sd = new SaveDialog("Save model", pixelClassificationModelFile, ".model");
            String filename = sd.getFileName();
            if (filename != null) {
                clijxweka.saveClassifier(filename);
            }
        }
    }

    private void trainClicked() {
        ImagePlus groundTruth = NewImage.createFloatImage("ground_truth", inputImp.getWidth(), inputImp.getHeight(), 1, NewImage.FILL_BLACK);
        for (int i = 0; i < overlay.size(); i++) {
            Roi roi = overlay.get(i);
            if (roi instanceof PolygonRoi) {
                String name = roi.getName();
                roi = Selection.lineToArea(roi);
                groundTruth.setRoi(roi);
                IJ.run(groundTruth, "Multiply...", "value=0");
                IJ.run(groundTruth, "Add...", "value=" + (name.compareTo(MouseMode.FOREGROUND.toString()) == 0 ? 2 : 1));
            }
        }

        ClearCLBuffer clGroundTruth = clij2.push(groundTruth);

        if (clResult != null) {
            clij2.release(clResult);
        }
        clResult = clij2.create(clGroundTruth);

        ClearCLBuffer clFeatureStack = GenerateFeatureStack.generateFeatureStack(clij2, clInput, pixelClassificationFeatureDefinition);

        //clij2.show(clFeatureStack, "clFeatureStack");
        //clij2.show(clGroundTruth, "clGroundTruth");

        clijxweka = TrainWekaModelWithOptions.trainWekaModelWithOptions(clij2, clFeatureStack, clGroundTruth, pixelClassificationModelFile, 100, 2, 8);

        //String ocl = clijxweka.getOCL();

        ClearCLBuffer temp = clij2.create(clResult);

        //ApplyOCLWekaModel.applyOCL(clij2, clFeatureStack, clResult, ocl);
        ApplyWekaModel.applyWekaModel(clij2, clFeatureStack, temp, clijxweka);

        clij2.addImageAndScalar(temp, clResult, -1);
        temp.close();

        //clij2.show(clResult, "clResult");

        updateVisualisation();

        clij2.release(clGroundTruth);
        clij2.release(clFeatureStack);
        //clij2.release(clResult);
    }



    enum MouseMode {
        FOREGROUND,
        BACKGROUND,
        ERASE
    };
    MouseMode mouseMode = MouseMode.FOREGROUND;

    private void eraseClicked() {
        IJ.setTool("freeline");
        mouseMode = MouseMode.ERASE;
        Roi.setColor(eraseColor);
        refresh();
    }
    private void addBackgroundClicked() {
        IJ.setTool("freeline");
        mouseMode = MouseMode.BACKGROUND;
        Roi.setColor(backgroundColor);
        refresh();
    }
    private void addForegroundClicked() {
        IJ.setTool("freeline");
        mouseMode = MouseMode.FOREGROUND;
        Roi.setColor(foregroundColor);
        refresh();
    }

    protected void mouseUp(MouseEvent e) {
        System.out.println("mouse up" + e.isControlDown());
        Roi roi = inputImp.getRoi();
        if (roi != null) {
            if (mouseMode == MouseMode.FOREGROUND || mouseMode == MouseMode.BACKGROUND) {
                //roi = Selection.lineToArea(roi);
                roi.setStrokeWidth(2);
                if ((mouseMode == MouseMode.FOREGROUND && !e.isControlDown()) || (mouseMode != MouseMode.FOREGROUND && e.isControlDown())){
                    roi.setName(MouseMode.FOREGROUND.toString());
                    roi.setStrokeColor(foregroundColor);
                } else {
                    roi.setName(MouseMode.BACKGROUND.toString());
                    roi.setStrokeColor(backgroundColor);
                }
                overlay.add(roi);
            } else {
                System.out.println("Overlay size "+  overlay.size());
                for (int i = overlay.size() - 1; i >= 0; i--) {
                    Roi otherRoi = overlay.get(i);
                    if (otherRoi instanceof PolygonRoi) {
                        PolygonRoi polyline1 = (PolygonRoi) roi;
                        PolygonRoi polyline2 = (PolygonRoi) otherRoi;
                        if (linesIntersect(polyline1, polyline2)) {
                            overlay.remove(polyline2);
                        }
                    }
                }
            }
        }
        inputImp.killRoi();
        inputImp.setOverlay(overlay);
    }

    private boolean linesIntersect(PolygonRoi polyline1, PolygonRoi polyline2) {
        FloatPolygon polygon1 = polyline1.getFloatPolygon();
        FloatPolygon polygon2 = polyline2.getFloatPolygon();

        for (int i = 1; i < polygon1.npoints; i++) {
            for (int j = 1; j < polygon2.npoints; j++) {
                double ax1 = polygon1.xpoints[i];
                double ay1 = polygon1.ypoints[i];
                double ax2 = polygon1.xpoints[i - 1];
                double ay2 = polygon1.ypoints[i - 1];

                double bx1 = polygon2.xpoints[j];
                double by1 = polygon2.ypoints[j];
                double bx2 = polygon2.xpoints[j - 1];
                double by2 = polygon2.ypoints[j - 1];

                if (Line2D.linesIntersect(ax1, ax1, ax2, ay2, bx1, by1, bx2, by2)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void main(String... args) {
        String filename = //"C:/structure/data/Kota/NPC_T01.tif";
                "C:/structure/data/ByungHoLee/190124_ctrl_31_p6-1.tif";

        new ImageJ();

        ImagePlus imp = IJ.openImage(filename);
        //IJ.run(imp, "8-bit", "");
        //imp = new Duplicator().run(imp, 2,2,1,1,1,1);
        //imp.show();

        CLIJ2 clijx = CLIJ2.getInstance();
        imp.setC(2);
        ClearCLBuffer input = clijx.pushCurrentSlice(imp);
        /*ClearCLBuffer slice1 = clij2.create(input);
        ClearCLBuffer slice2 = clij2.create(input);
        ClearCLBuffer slice3 = clij2.create(input);

        clij2.flip(input, slice1, true, false);
        clij2.flip(input, slice2, true, true);
        clij2.flip(input, slice3, false, true);

        ClearCLBuffer */

        clijx.show(input,"input");

        new CLIJxWekaBinaryPixelClassification().run(null);
    }
}
