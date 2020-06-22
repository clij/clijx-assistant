package net.haesleinhuepf.spimcat.io;

import fiji.util.gui.GenericDialogPlus;
import ij.ImageJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.measure.Calibration;
import ij.plugin.HyperStackConverter;
import ij.plugin.PlugIn;

/**
 * VirtualRawStackOpener
 * <p>
 * <p>
 * <p>
 * Author: @haesleinhuepf
 * 12 2019
 */
public class VirtualRawStackOpener implements PlugIn {

    private static String path = Prefs.getDefaultDirectory();
    private static int width = 512;
    private static int height = 1024;
    private static int depth = 100;
    private static int numberOfImageStacks = Integer.MAX_VALUE;
    private static int bitDepth = 16;
    private static boolean intelByteOrder = true;

    private static double pixelSizeX = 1.0;
    private static double pixelSizeY = 1.0;
    private static double pixelSizeZ = 1.0;
    private static String pixelUnit = "micron";


    @Override
    public void run(String arg) {
        GenericDialogPlus gd = new GenericDialogPlus("Open Raw sequence");
        gd.addDirectoryField("Folder", path);
        gd.addNumericField("Width", width, 0);
        gd.addNumericField("Height", height, 0);
        gd.addNumericField("Depth", depth, 0);
        gd.addNumericField("Number of images", numberOfImageStacks, 0);
        gd.addChoice("Bit depth", new String[]{"8", "16", "32"}, "" + bitDepth);
        gd.addCheckbox("Intel byte order", intelByteOrder);
        gd.addNumericField("Pixel width", pixelSizeX, 4);
        gd.addNumericField("Pixel height", pixelSizeY, 4);
        gd.addNumericField("Pixel depth", pixelSizeZ, 4);
        gd.addStringField("Pixel unit", pixelUnit);
        gd.showDialog();
        if (gd.wasOKed()) {
            path = gd.getNextString();
            width = (int)gd.getNextNumber();
            height = (int)gd.getNextNumber();
            depth = (int)gd.getNextNumber();
            numberOfImageStacks = (int)gd.getNextNumber();
            bitDepth = Integer.parseInt(gd.getNextChoice());
            intelByteOrder = gd.getNextBoolean();

            pixelSizeX = gd.getNextNumber();
            pixelSizeY = gd.getNextNumber();
            pixelSizeZ = gd.getNextNumber();
            pixelUnit = gd.getNextString();

            ImagePlus imp = open(path, width, height, depth, numberOfImageStacks, bitDepth, intelByteOrder, pixelSizeX, pixelSizeY, pixelSizeZ, pixelUnit);
            imp.show();
        }

    }

    public static ImagePlus open(String foldername, int width, int height, int depth, int numberOfImageStacks, int bitDepth, boolean intelByteOrder, double pixelSizeX, double pixelSizeY, double pixelSizeZ, String pixelUnit) {
        VirtualRawStack stack = new VirtualRawStack(
                foldername,
                width, height, depth,
                numberOfImageStacks,
                bitDepth,
                intelByteOrder,
                pixelSizeX, pixelSizeY, pixelSizeZ,
                pixelUnit
        );
        ImagePlus imp = new ImagePlus(foldername, stack);
        //System.out.println("stack size " + imp.getStackSize());
        //System.out.println("stack2 size " + stack.getSize());

        ImagePlus imagePlus = HyperStackConverter.toHyperStack(imp, 1, depth, stack.getSize() / depth);
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
                "C:/structure/data/2019-10-28-17-22-59-23-Finsterwalde_Tribolium_nGFP/stacks/C0opticsprefused/",
                512, 1024, 67,
                Integer.MAX_VALUE,
                16,
                true,
                0.3460000, 0.3460000, 3,
                "micron"
        ).show();
    }
}
