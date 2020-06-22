package net.haesleinhuepf.spimcat.io;

import fiji.util.gui.GenericDialogPlus;
import ij.ImageJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.measure.Calibration;
import ij.plugin.HyperStackConverter;
import ij.plugin.PlugIn;

import java.io.FileNotFoundException;

/**
 * VirtualRawStackOpener
 * <p>
 * <p>
 * <p>
 * Author: @haesleinhuepf
 * 12 2019
 */
public class VirtualTifStackOpener implements PlugIn {

    private static String path = Prefs.getDefaultDirectory();

    private static double pixelSizeX = 0.1;
    private static double pixelSizeY = 0.1;
    private static double pixelSizeZ = 0.5;
    private static String pixelUnit = "micron";

    private static int numberOfChannels = 2;

    @Override
    public void run(String arg) {
        GenericDialogPlus gd = new GenericDialogPlus("Open Tif sequence");
        gd.addDirectoryField("Folder", path);
        gd.addNumericField("Pixel width", pixelSizeX, 4);
        gd.addNumericField("Pixel height", pixelSizeY, 4);
        gd.addNumericField("Pixel depth", pixelSizeZ, 4);
        gd.addStringField("Pixel unit", pixelUnit);
        gd.addNumericField("Number of channels", numberOfChannels, 0);
        gd.showDialog();
        if (gd.wasOKed()) {
            path = gd.getNextString();


            pixelSizeX = gd.getNextNumber();
            pixelSizeY = gd.getNextNumber();
            pixelSizeZ = gd.getNextNumber();
            pixelUnit = gd.getNextString();
            numberOfChannels = (int) gd.getNextNumber();

            ImagePlus imp = open(path, pixelSizeX, pixelSizeY, pixelSizeZ, pixelUnit, numberOfChannels);
            imp.show();
        }

    }

    public static ImagePlus open(String foldername, double pixelSizeX, double pixelSizeY, double pixelSizeZ, String pixelUnit) {
        return open(foldername, pixelSizeX, pixelSizeY, pixelSizeZ, pixelUnit, 1);
    }

    public static ImagePlus open(String foldername, double pixelSizeX, double pixelSizeY, double pixelSizeZ, String pixelUnit, int numberOfChannels) {

        VirtualTifStack stack = null;
        try {
            stack = VirtualTifStack.open(
                    foldername
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (numberOfChannels > 1) {
            stack.switchChannelsAndFrames(numberOfChannels);
        }
        ImagePlus imp = new ImagePlus(foldername, stack);
        //System.out.println("stack size " + imp.getStackSize());
        //System.out.println("stack2 size " + stack.getSize());

        ImagePlus imagePlus = HyperStackConverter.toHyperStack(imp, numberOfChannels, stack.getDepth(), stack.getSize() / stack.getDepth() / numberOfChannels);
        imagePlus.setTitle(foldername);
        Calibration calibration = imagePlus.getCalibration();
        calibration.pixelWidth = pixelSizeX;
        calibration.pixelHeight = pixelSizeY;
        calibration.pixelDepth = pixelSizeZ;
        calibration.setUnit(pixelUnit);
        return imagePlus;
    }

    public static void main(String... args) {
        new ImageJ();

        open(
                "C:/structure/data/William_LLSM_data/deconvolved data/",
                1, 1, 1,
                "micron", 2
        ).show();
    }
}
