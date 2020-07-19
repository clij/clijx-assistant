package net.haesleinhuepf.spimcat.cropping;

import fiji.util.gui.GenericDialogPlus;
import ij.*;
import ij.gui.Roi;
import ij.plugin.Duplicator;
import ij.plugin.PlugIn;
import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.spimcat.io.VirtualTifStackOpener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

@Deprecated
public class Crop4D implements PlugIn {

    static CLIJ clij = null;

    boolean mouseDown = false;

    ImagePlus imp = null;
    ImagePlus impX = null;
    ImagePlus impY = null;
    ImagePlus impZ = null;

    int cropWidth;
    int cropHeight;
    int cropDepth;
    int cropX;
    int cropY;
    int cropZ;

    Object mutex = new Object();
    GenericDialogPlus gdp;
    double XZratio;

    ImageListener imageListener = new ImageListener() {
        @Override
        public void imageOpened(ImagePlus imp) {

        }

        @Override
        public void imageClosed(ImagePlus imp) {
            if (gdp != null) {

                gdp.setVisible(true);
            }
        }

        @Override
        public void imageUpdated(ImagePlus imp) {

        }
    };

    @Override
    public void run(String arg) {
        gdp = new GenericDialogPlus("Crop 4D Stack");
        ImagePlus.addImageListener(imageListener);

        ArrayList<String> deviceList = CLIJ.getAvailableDeviceNames();
        if (clij == null) {
            clij = CLIJ.getInstance();
        }
        String[] deviceArray = new String[deviceList.size()];
        deviceList.toArray(deviceArray);
        gdp.addChoice("CL_Device", deviceArray, clij.getClearCLContext().getDevice().getName());

        gdp.addImageChoice("Image", IJ.getImage().getTitle());
        //gdp.addNumericField("Zoom (influences speed and transform parameters)", 1.5, 1);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return;
        }

        String cl_devicename = gdp.getNextChoice();
        imp = gdp.getNextImage();
        float zoom = (float)1.0; // gdp.getNextNumber();

        System.out.println("Init gpu");

        CLIJx clijx = CLIJx.getInstance(cl_devicename);
        clijx.clear();
        clij = clijx.getCLIJ();
        System.out.println("Init gpu done");

        XZratio = imp.getCalibration().pixelWidth / imp.getCalibration().pixelDepth;


        double formerFrameStart = 15;
        double formerFrameEnd = 25;
        double formerBackgroundSubtractionSigma = 0;
        boolean formerLinearInterpolation = false;

        int formerFrame = imp.getFrame();
        cropWidth = imp.getWidth() / 2;
        cropHeight = imp.getHeight() / 2;
        cropDepth =  imp.getNSlices() / 2;
        cropX = imp.getWidth() / 4;
        cropY = imp.getHeight() / 4;
        cropZ = imp.getZ() / 4;

        int formerCropWidth = -1;
        int formerCropHeight = -1;
        int formerCropDepth = -1;
        int formerCropX = -1;
        int formerCropY = -1;
        int formerCropZ = -1;

        Point formerDialogPosition = null;


        while(true) {

            //# build up user interface
            gdp = new GenericDialogPlus("Crop Image 4D");
            gdp.addSlider("Export timelapse starts at current frame -", 0, 100, formerFrameStart);
            gdp.addSlider("Export timelapse ends at current frame +", 0, 100, formerFrameEnd);
            gdp.addSlider("Background subtraction sigma (visualisation only)", 0, 100, formerBackgroundSubtractionSigma);
            gdp.addCheckbox("Linear interpolation (visualisation only)", formerLinearInterpolation);
            gdp.addButton("Test stack output", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doExport(imp, cropX, cropY, cropZ, cropWidth, cropHeight, cropDepth, imp.getFrame(), imp.getFrame()).show();
                }
            });

            gdp.setModal(false);
            gdp.showDialog();
            if (formerDialogPosition != null) {
                gdp.setLocation(formerDialogPosition);
            }

            System.out.println("Dialog shown");

            //Checkbox doNoiseAndBackgroundRemovalCheckbox = (Checkbox) gdp.getCheckboxes().get(0);
            Scrollbar frameStartSlider = (Scrollbar) gdp.getSliders().get(0);
            Scrollbar frameEndSlider = (Scrollbar) gdp.getSliders().get(1);
            Scrollbar backgroundSigmaSlider = (Scrollbar) gdp.getSliders().get(2);
            Checkbox linearInterpolationCheckbox = (Checkbox) gdp.getCheckboxes().get(0);

            while(true) {

                impX = WindowManager.getImage("max x");
                impY = WindowManager.getImage("max y");
                impZ = WindowManager.getImage("max z");

                initMouse(imp);
                initMouse(impX);
                initMouse(impY);
                initMouse(impZ);

                if (gdp.wasOKed()) {
                    break;
                }
                if (gdp.wasCanceled()) {
                    break;
                }

                //synchronized (mutex)
                {


                    boolean changed = false;
                    changed = changed || (cropWidth != formerCropWidth);
                    changed = changed || (cropHeight != formerCropHeight);
                    changed = changed || (cropX != formerCropX);
                    changed = changed || (cropY != formerCropY);
                    changed = changed || (cropDepth != formerCropDepth);
                    changed = changed || (cropZ != formerCropZ);
                    changed = changed || (formerFrame != imp.getFrame());
                    changed = changed || (formerBackgroundSubtractionSigma != backgroundSigmaSlider.getValue());
                    changed = changed || (formerFrameEnd != frameEndSlider.getValue());
                    changed = changed || (formerFrameStart != frameStartSlider.getValue());
                    changed = changed || (formerLinearInterpolation != linearInterpolationCheckbox.getState());


                    //System.out.println("Mousedown: " + mouseDown);

                    if (changed && !mouseDown) {
                        formerFrame = imp.getFrame();
                        formerCropWidth = cropWidth;
                        formerCropHeight = cropHeight;
                        formerCropX = cropX;
                        formerCropY = cropY;
                        formerCropDepth = cropDepth;
                        formerCropZ = cropZ;
                        formerBackgroundSubtractionSigma = backgroundSigmaSlider.getValue();
                        formerFrameEnd = frameEndSlider.getValue();
                        formerFrameStart = frameStartSlider.getValue();
                        formerLinearInterpolation = linearInterpolationCheckbox.getState();
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    imp.killRoi();

                    showPreview(clijx, imp, formerFrame, formerBackgroundSubtractionSigma, cropX, cropY, cropZ, cropWidth, cropHeight, cropDepth, formerLinearInterpolation);


                    imp.setRoi(new Roi(cropX, cropY, cropWidth, cropHeight));
                    System.out.println("set roi " + cropX + "/" + cropY + "/" + cropZ);
                    if (impX != null) {
                        impX.setRoi((int) (cropZ / XZratio), cropY, (int) (cropDepth / XZratio), cropHeight);
                    }
                    if (impY != null) {
                        impY.setRoi(cropX, (int) (cropZ / XZratio), cropWidth, (int) (cropDepth / XZratio));
                    }
                    if (impZ != null) {
                        impZ.setRoi(cropX, cropY, cropWidth, cropHeight);
                    }
                }
            }
            if (gdp.wasOKed()) {
                formerDialogPosition = gdp.getLocation();
                System.out.println("Cropping");
                System.out.println("Pos " + cropX + "/" + cropY + "/" + cropY);
                System.out.println("Dim " + cropWidth + "/" + cropHeight + "/" + cropDepth);

                System.out.println("Frame start/end " + formerFrameStart + "/" + formerFrameEnd);

                doExport(imp, cropX, cropY, cropZ, cropWidth, cropHeight, cropDepth, (int) (imp.getFrame() - formerFrameStart), (int)(imp.getFrame() + formerFrameEnd)).show();
                System.out.println("Done");
            }

            if (gdp.wasCanceled()) {
                break;
            }
        }
        ImagePlus.removeImageListener(imageListener);
    }

    ArrayList<ImagePlus> initializedImps = new ArrayList<>();
    private void initMouse(final ImagePlus imagePlus) {
        if (imagePlus == null) {
            return;
        }

        if (initializedImps.contains(imagePlus)) {
            return;
        }
        initializedImps.add(imagePlus);

        imagePlus.getWindow().getCanvas().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //synchronized (mutex)
                {

                    Roi roi = imagePlus.getRoi();
                    if ((imp == imagePlus || impZ == imagePlus) && roi != null) {
                        cropWidth = roi.getBounds().width;
                        cropHeight = roi.getBounds().height;
                        cropX = roi.getBounds().x;
                        cropY = roi.getBounds().y;
                        System.out.println("mouse Z");
                    }
                    if (impX == imagePlus && roi != null) {
                        cropDepth = (int) (roi.getBounds().width * XZratio);
                        cropHeight = roi.getBounds().height;
                        cropZ = (int) (roi.getBounds().x * XZratio);
                        cropY = roi.getBounds().y;
                        System.out.println("mouse X");
                    }
                    if (impY == imagePlus && roi != null) {
                        cropWidth = roi.getBounds().width;
                        cropDepth = (int) (roi.getBounds().height * XZratio);
                        cropX = roi.getBounds().x;
                        cropZ = (int) (roi.getBounds().y * XZratio);
                        System.out.println("orig z:" + roi.getBounds().y);
                        System.out.println("new Z: " + cropZ);

                        System.out.println("mouse Y");
                    }
                    mouseDown = false;
                }
            }
        });

    }

    ClearCLBuffer buffer = null;
    int cachedFrame = -1;
    int cachedChannel = -1;
    private void showPreview(CLIJx clijx, ImagePlus imp, int formerFrame, double formerBackgroundSubtractionSigma, int cropX, int cropY, int cropZ, int cropWidth, int cropHeight, int cropDepth, boolean interpolation) {
        System.out.println("Ratio " + XZratio);

        clijx.stopWatch("");

        if (buffer == null || cachedFrame != imp.getFrame() || cachedChannel != imp.getC()) {

            ImagePlus imp2 = new Duplicator().run(imp, imp.getC(), imp.getC(), 1, imp.getNSlices(), imp.getFrame(), imp.getFrame());

            buffer = clijx.push(imp2);
            cachedFrame = imp.getFrame();
            cachedChannel = imp.getC();
            clijx.stopWatch("push");
        }

        if (formerBackgroundSubtractionSigma > 0) {
            ClearCLBuffer background = clijx.create(buffer);
            ClearCLBuffer backgroundSubtracted = clijx.create(buffer);
            clijx.gaussianBlur3D(buffer, background, formerBackgroundSubtractionSigma, formerBackgroundSubtractionSigma, 0);
            clijx.subtractImages(buffer, background, backgroundSubtracted);
            clijx.copy(backgroundSubtracted, buffer);
            clijx.release(background);
            clijx.release(backgroundSubtracted);
        }

        ClearCLBuffer maxXProjection = IncubatorUtilities.maximum_x_projection(clijx, buffer);
        ClearCLBuffer maxYProjection = IncubatorUtilities.maximum_y_projection(clijx ,buffer);
        ClearCLBuffer maxZProjection = IncubatorUtilities.maximum_z_projection(clijx, buffer);

        ClearCLBuffer maxXProjectionScaled = clijx.create(new long[]{(long) (maxXProjection.getWidth() / XZratio), maxXProjection.getHeight()}, maxXProjection.getNativeType());
        ClearCLBuffer maxYProjectionScaled = clijx.create(new long[]{maxYProjection.getWidth(), (long) (maxYProjection.getHeight() / XZratio)}, maxYProjection.getNativeType());

        clijx.activateSizeIndependentKernelCompilation();
        clijx.resample(maxXProjection, maxXProjectionScaled, 1.0 / XZratio, 1, 1, interpolation);
        clijx.activateSizeIndependentKernelCompilation();
        clijx.resample(maxYProjection, maxYProjectionScaled, 1, 1.0 / XZratio, 1, interpolation);

        clijx.showGrey(maxXProjectionScaled, "max x");
        clijx.showGrey(maxYProjectionScaled, "max y");
        clijx.showGrey(maxZProjection, "max z");

        clijx.release(maxXProjection);
        clijx.release(maxYProjection);
        clijx.release(maxZProjection);
        clijx.release(maxXProjectionScaled);
        clijx.release(maxYProjectionScaled);

        ClearCLBuffer crop = clijx.create(cropWidth, cropHeight, cropDepth);


        clijx.activateSizeIndependentKernelCompilation();
        clijx.crop3D(buffer, crop, cropX, cropY, cropZ);

        //clijx.showGrey(crop, "crop");

        maxXProjection = IncubatorUtilities.maximum_x_projection(clijx, buffer);
        maxYProjection = IncubatorUtilities.maximum_y_projection(clijx ,buffer);
        maxZProjection = IncubatorUtilities.maximum_z_projection(clijx, buffer);

        clijx.release(crop);

        maxXProjectionScaled = clijx.create(new long[]{(long) (maxXProjection.getWidth() / XZratio), maxXProjection.getHeight()}, maxXProjection.getNativeType());
        maxYProjectionScaled = clijx.create(new long[]{maxYProjection.getWidth(), (long) (maxYProjection.getHeight() / XZratio)}, maxYProjection.getNativeType());

        clijx.activateSizeIndependentKernelCompilation();
        clijx.resample(maxXProjection, maxXProjectionScaled, 1.0 / XZratio, 1, 1, interpolation);
        clijx.activateSizeIndependentKernelCompilation();
        clijx.resample(maxYProjection, maxYProjectionScaled, 1, 1.0 / XZratio, 1, interpolation);

        clijx.showGrey(maxXProjectionScaled, "crop max x");
        clijx.showGrey(maxYProjectionScaled, "crop max y");
        clijx.showGrey(maxZProjection, "crop max z");

        clijx.release(maxXProjection);
        clijx.release(maxYProjection);
        clijx.release(maxZProjection);
        clijx.release(maxXProjectionScaled);
        clijx.release(maxYProjectionScaled);

        clijx.stopWatch("full");

        /*
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        Window winX = WindowManager.getWindow("max x");
        Window winY = WindowManager.getWindow("max y");
        Window winZ = WindowManager.getWindow("max z");

        if (winX != null && winY != null && winZ != null ) {
            winX.setLocation(winZ.getX() + winZ.getWidth(), winZ.getY());
            winY.setLocation(winZ.getX(), winZ.getY() + winZ.getHeight());
        }

        winX = WindowManager.getWindow("crop max x");
        winY = WindowManager.getWindow("crop max y");
        winZ = WindowManager.getWindow("crop max z");

        if (winX != null && winY != null && winZ != null ) {
            winX.setLocation(winZ.getX() + winZ.getWidth(), winZ.getY());
            winY.setLocation(winZ.getX(), winZ.getY() + winZ.getHeight());
        }
    }

    public static ImagePlus doExport(ImagePlus imp, int cropX, int cropY, int cropZ, int cropWidth, int cropHeight, int cropDepth, int startFrame, int endFrame) {
        int firstZ = cropZ;
        int lastZ = firstZ + cropDepth;

        imp.setRoi(cropX, cropY, cropWidth, cropHeight);

        ImagePlus result =  new Duplicator().run(imp, 1, imp.getNChannels(), firstZ, lastZ, startFrame, endFrame);
        return  result;
    }

    public static void main(String... args) {
        new ImageJ();

        /*VirtualTifStackOpener.open(
                "C:/structure/data/William_LLSM_data/deconvolved data/",
                1, 1, 5,
                "micron"
        ).show();*/

        new VirtualTifStackOpener().run("");
        new Crop4D().run("");
    }


}
