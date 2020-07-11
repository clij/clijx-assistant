package net.haesleinhuepf.clincubator.preprocessing;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.framor.AbstractFrameProcessor;
import net.haesleinhuepf.clijx.framor.FrameProcessor;
import net.haesleinhuepf.clijx.framor.Framor;

public class DriftCorrectionFrameProcessor extends AbstractFrameProcessor implements PlugInFilter {


    private double[] reference_center_of_mass;

    public DriftCorrectionFrameProcessor() {}
    public DriftCorrectionFrameProcessor(double[] reference_center_of_mass) {
        this.reference_center_of_mass = reference_center_of_mass;
    }

    @Override
    public ImagePlus process(ImagePlus imp) {
        CLIJ2 clij2 = getCLIJ2();
        ClearCLBuffer input = clij2.push(imp);
        ClearCLBuffer output = clij2.create(input);

        double[] center_of_mass = clij2.centerOfMass(input);

        if (center_of_mass.length == 2) {
            clij2.translate2D(input, output, center_of_mass[0] - reference_center_of_mass[0], center_of_mass[1] - reference_center_of_mass[1]);
        } else {
            clij2.translate3D(input, output, center_of_mass[0] - reference_center_of_mass[0], center_of_mass[1] - reference_center_of_mass[1], center_of_mass[2] - reference_center_of_mass[2]);
        }

        ImagePlus result = clij2.pull(output);
        input.close();
        output.close();

        return result;
    }

    @Override
    public FrameProcessor duplicate() {
        DriftCorrectionFrameProcessor frameProcessor = new DriftCorrectionFrameProcessor(reference_center_of_mass);
        frameProcessor.setCLIJ2(getCLIJ2());
        return frameProcessor;
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Drift correction (CLIJxf)");
        gd.addNumericField("Reference_frame (0-indiced)", 0);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return;
        }
        int frame = (int) gd.getNextNumber();
        ImagePlus imp = IJ.getImage();
        imp.setT(frame + 1);

        CLIJ2 clij2 = CLIJ2.getInstance();
        ClearCLBuffer buffer = clij2.pushCurrentZStack(imp);
        double[] center_of_mass = clij2.centerOfMass(buffer);
        buffer.close();

        new Framor(imp, new DriftCorrectionFrameProcessor(center_of_mass)).getResult().show();
    }

    @Override
    public long getMemoryNeedInBytes(ImagePlus imp) {
        return imp.getBitDepth() / 8 * imp.getWidth() * imp.getHeight() * imp.getNSlices() + imp.getBitDepth() / 8 * imp.getWidth() * imp.getHeight();
    }


    public static void main(String[] args) {
        new ImageJ();
        ImagePlus imp = IJ.openImage("C:/structure/data/Lund_001457.tif");
        imp.show();

        new Framor(imp, new DriftCorrectionFrameProcessor()).getResult().show();
    }
}
