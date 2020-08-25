package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Plugin(type = AssistantGUIPlugin.class)
public class Crop extends AbstractAssistantGUIPlugin {

    int width_in_pixels = 256;
    int height_in_pixels = 256;
    int depth_in_pixels = 256;

    int x_in_pixels = 0;
    int y_in_pixels = 0;
    int z_in_pixels = 0;

    public Crop() {
        super(new net.haesleinhuepf.clij2.plugins.Crop3D());
    }

    protected boolean configure() {
        ImagePlus imp = IJ.getImage();
        width_in_pixels = Math.min(width_in_pixels, imp.getWidth());
        height_in_pixels = Math.min(height_in_pixels, imp.getHeight());
        depth_in_pixels = Math.min(depth_in_pixels, imp.getNSlices());

        GenericDialog gdp = new GenericDialog("Crop");
        gdp.addNumericField("Width (in pixels)", width_in_pixels, 0);
        gdp.addNumericField("Height (in pixels)", height_in_pixels, 0);
        gdp.addNumericField("Depth (in pixels)", depth_in_pixels, 0);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return false;
        }

        setSource(IJ.getImage());
        width_in_pixels = (int) gdp.getNextNumber();
        height_in_pixels = (int) gdp.getNextNumber();
        depth_in_pixels = (int) gdp.getNextNumber();
        return true;
    }

    GenericDialog gd;

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        gd = new GenericDialog(AssistantUtilities.niceName(this.getClass().getSimpleName()));
        gd.addNumericField("X position (in pixels)", x_in_pixels, 0);
        addPlusMinusPanel(gd, "x long range");
        gd.addNumericField("Y position (in pixels)", y_in_pixels, 0);
        addPlusMinusPanel(gd, "y long range");
        gd.addNumericField("Z position (in pixels)", z_in_pixels, 0);
        addPlusMinusPanel(gd, "z long range");
        return gd;
    }

    ClearCLBuffer[] result = null;
    public synchronized void refresh()
    {
        if (gd != null) {
            try {
                x_in_pixels = (int) Double.parseDouble(((TextField) gd.getNumericFields().get(0)).getText());
                y_in_pixels = (int) Double.parseDouble(((TextField) gd.getNumericFields().get(1)).getText());
                z_in_pixels = (int) Double.parseDouble(((TextField) gd.getNumericFields().get(2)).getText());
            } catch (Exception e) {
                return;
            }
        }

        net.haesleinhuepf.clij2.plugins.Crop3D plugin = (net.haesleinhuepf.clij2.plugins.Crop3D) getCLIJMacroPlugin();
        ClearCLBuffer[] pushed;

        if (my_source.getStack() instanceof CLIJxVirtualStack) {

            my_source.killRoi();
            pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
            my_source.setRoi(x_in_pixels, y_in_pixels, width_in_pixels, height_in_pixels);

            args = new Object[] {
                    pushed[0],
                    null,
                    x_in_pixels,
                    y_in_pixels,
                    z_in_pixels,
                    width_in_pixels,
                    height_in_pixels,
                    depth_in_pixels
            };
        } else {
            my_source.setRoi(x_in_pixels, y_in_pixels, width_in_pixels, height_in_pixels);
            try {
                pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
            } catch (Exception e) {
                IJ.log(e.getMessage());
                return;
            }

            args = new Object[]{
                    pushed[0],
                    null,
                    0,
                    0,
                    z_in_pixels,
                    width_in_pixels,
                    height_in_pixels,
                    depth_in_pixels
            };
        }


        plugin.setArgs(args);

        if (result == null) {
            result = createOutputBufferFromSource(pushed);
        }
        args[1] = result[0];

        executeCL(pushed, result);

        cleanup(my_source, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));

        my_target.setTitle("Crop of " + my_source.getTitle());
        //ImagePlus.addImageListener(new MyImageListener());
        my_source.getWindow().getCanvas().addMouseListener(new MyListener());

        enhanceContrast();
    }

    class MyListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (my_source != null && my_source.getWindow() != null && my_source.getWindow().getCanvas() != null && gd != null) {
                Roi roi = my_source.getRoi();
                if (roi != null) {
                    if(roi.getBounds().x != x_in_pixels || roi.getBounds().y != y_in_pixels) {
                        ((TextField) gd.getNumericFields().get(0)).setText("" + roi.getBounds().x);
                        ((TextField) gd.getNumericFields().get(1)).setText("" + roi.getBounds().y);
                        setTargetInvalid();
                    }
                }
            }
        }
    }

    public void refreshDialogFromArguments() {
        // do nothing
    }


    public static void main(String[] args) {
        new ImageJ();
        ImagePlus imp = IJ.openImage("C:/structure/data/t1-head.tif");
        imp.show();

        new Crop().run("");
    }

}
