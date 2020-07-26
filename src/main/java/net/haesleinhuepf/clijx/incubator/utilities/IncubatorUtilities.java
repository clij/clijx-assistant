package net.haesleinhuepf.clijx.incubator.utilities;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Toolbar;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.gui.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IncubatorUtilities {
    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return sdf.format(cal.getTime());
    }

    public static ClearCLBuffer maximum_x_projection(CLIJx clijx, ClearCLBuffer stack) {
        ClearCLBuffer image2D = clijx.create(new long[]{stack.getDepth(), stack.getHeight()});
        image2D.setName("MAX_x_" + stack.getName());
        clijx.maximumXProjection(stack,image2D);
        return image2D;
    }

    public static ClearCLBuffer maximum_y_projection(CLIJx clijx, ClearCLBuffer stack) {
        ClearCLBuffer image2D = clijx.create(new long[]{stack.getWidth(), stack.getDepth()});
        image2D.setName("MAX_y_" + stack.getName());
        clijx.maximumYProjection(stack,image2D);
        return image2D;
    }

    public static ClearCLBuffer maximum_z_projection(CLIJx clijx, ClearCLBuffer stack) {
        ClearCLBuffer image2D = clijx.create(new long[]{stack.getWidth(), stack.getHeight()});
        image2D.setName("MAX_z_" + stack.getName());
        clijx.maximumZProjection(stack,image2D);
        return image2D;
    }

    public static ClearCLBuffer copy_slice(CLIJx clijx, ClearCLBuffer stack, int slice) {
        ClearCLBuffer image2D = clijx.create(new long[]{stack.getWidth(), stack.getHeight()});
        image2D.setName("Slice_" + slice + "_" + stack.getName());
        clijx.copySlice(stack, image2D, slice);
        return image2D;
    }

    public static String stamp(ClearCLBuffer buffer) {
        String timestamp = "" + System.currentTimeMillis();
        buffer.setName(timestamp);
        return timestamp;
    }
    public static boolean checkStamp(ClearCLBuffer buffer, String stamp) {
        return buffer.getName().compareTo(stamp) == 0 && stamp.length() > 0;
    }

    public static void transferCalibration(ImagePlus source, ImagePlus target) {
        target.getCalibration().pixelWidth = source.getCalibration().pixelWidth;
        target.getCalibration().pixelHeight = source.getCalibration().pixelHeight;
        target.getCalibration().pixelDepth = source.getCalibration().pixelDepth;

        target.getCalibration().setXUnit(source.getCalibration().getXUnit());
        target.getCalibration().setYUnit(source.getCalibration().getYUnit());
        target.getCalibration().setZUnit(source.getCalibration().getZUnit());
    }

    public static String niceName(String name) {
        if (name.compareTo(MenuSeparator.class.getSimpleName()) == 0) {
            name = "-";
        }

        String result = "";

        for (int i = 0; i < name.length(); i++) {
            String ch = name.substring(i,i+1);
            if (!ch.toLowerCase().equals(ch)) {
                result = result + " ";
            }
            result = result + ch;
        }

        result = result.substring(0, 1).toUpperCase() + result.substring(1);

        result = result.replace("C L", "CL");
        result = result.replace("X Y", "XY");
        result = result.replace("X Z", "XZ");
        result = result.replace("Y Z", "YZ");
        result = result.replace("_ ", " ");
        result = result.replace("I J", "IJ");
        result = result.replace("Do G", "DoG");
        result = result.replace("Lo G", "LoG");
        result = result.replace("Cl Esperanto", "ClEsperanto");

        return result.trim();

    }

    public static void glasbey(ImagePlus imp) {
        //System.out.println();
        String dir = IJ.getDirectory("imagej");
        if (!dir.contains("null")) {

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            IJ.run(imp, "glasbey_on_dark", "");
                            imp.resetDisplayRange();
                        }
                    },
                    300
            );

        }
    }


    public static boolean ignoreEvent = false;

    public static void installTools() {
        String tool = IJ.getToolName();
        ignoreEvent = true;
        //Toolbar.removeMacroTools();


        Toolbar.addPlugInTool(new IncubatorStartingPointTool());

        ignoreEvent = false;

        IJ.setTool(tool);

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        new MemoryDisplay().run("");
                    }
                },
                1000
        );
    }

}
