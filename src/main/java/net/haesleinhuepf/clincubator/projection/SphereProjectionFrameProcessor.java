package net.haesleinhuepf.clincubator.projection;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.framor.AbstractFrameProcessor;
import net.haesleinhuepf.clijx.framor.FrameProcessor;
import net.haesleinhuepf.clijx.framor.Framor;
import net.imglib2.realtransform.AffineTransform3D;

public class SphereProjectionFrameProcessor extends HalfStackSphereProjectionFrameProcessor {

    public SphereProjectionFrameProcessor() {
        init();
    }
    public SphereProjectionFrameProcessor(Float scale_in_microns, Float background_subtraction_radius_in_microns, Integer number_of_angles) {
        this.scale_in_microns = scale_in_microns;
        this.background_subtraction_radius_in_microns = background_subtraction_radius_in_microns;
        this.number_of_angles = number_of_angles;

        init();
    }

    private void init() {
        this.full_rotation = 360f;
        this.center_x_relative = 0.5f;
        this.center_y_relative = 0.5f;
    }

    @Override
    public FrameProcessor duplicate() {
        SphereProjectionFrameProcessor frameProcessor = new SphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles);
        frameProcessor.setCLIJ2(getCLIJ2());
        return frameProcessor;
    }

    @Override
    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Sphere-maximum-projection");
        System.out.println("Dialog" + gd);
        gd.addNumericField("scale_in_microns", scale_in_microns);
        gd.addNumericField("background_subtraction_radius_in_microns", background_subtraction_radius_in_microns);
        System.out.println("number_of_angles" + number_of_angles);
        gd.addNumericField("number_of_angles", number_of_angles);
        gd.showDialog();
        if (gd.wasCanceled()) {
            return;
        }
        scale_in_microns = (float)gd.getNextNumber();
        background_subtraction_radius_in_microns = (float)gd.getNextNumber();
        number_of_angles = (int)gd.getNextNumber();

        new Framor(IJ.getImage(), new SphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles)).getResult().show();
    }

    public static void main(String[] args) {
        new ImageJ();
        ImagePlus imp = IJ.openImage("C:/structure/data/Polyclad_2019-10-29_Pc_Tp502.tif");
        imp.show();

        float scale_in_microns = 1;
        float background_subtraction_radius_in_microns = 10;
        int number_of_angles = 360;

        new Framor(imp, new SphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles)).getResult().show();

    }
}

