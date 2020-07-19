package net.haesleinhuepf.clincubator.projection;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clijx.framor.FrameProcessor;
import net.haesleinhuepf.clijx.framor.Framor;

public class TrashSphereProjectionFrameProcessor extends SphereProjectionFrameProcessor {

    public TrashSphereProjectionFrameProcessor() {
        init();
    }
    public TrashSphereProjectionFrameProcessor(Float scale_in_microns, Float background_subtraction_radius_in_microns, Integer number_of_angles, Projection projection) {
        this.scale_in_microns = scale_in_microns;
        this.background_subtraction_radius_in_microns = background_subtraction_radius_in_microns;
        this.number_of_angles = number_of_angles;
        this.projection = projection;

        init();
    }

    private void init() {
        this.full_rotation = 360f;
        this.center_x_relative = 0.5f;
        this.center_y_relative = 0.5f;
    }

    @Override
    public FrameProcessor duplicate() {
        TrashSphereProjectionFrameProcessor frameProcessor = new TrashSphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection);
        frameProcessor.setCLIJ2(getCLIJ2());
        return frameProcessor;
    }

    @Override
    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Sphere projection");
        System.out.println("Dialog" + gd);
        gd.addNumericField("scale_in_microns", scale_in_microns);
        gd.addNumericField("background_subtraction_radius_in_microns", background_subtraction_radius_in_microns);
        System.out.println("number_of_angles" + number_of_angles);
        gd.addNumericField("number_of_angles", number_of_angles);
        gd.addChoice("Projection", Projection.allToString(), projection.toString());
        gd.showDialog();
        if (gd.wasCanceled()) {
            return;
        }
        scale_in_microns = (float)gd.getNextNumber();
        background_subtraction_radius_in_microns = (float)gd.getNextNumber();
        number_of_angles = (int)gd.getNextNumber();
        projection = Projection.all()[gd.getNextChoiceIndex()];

        new Framor(IJ.getImage(), new TrashSphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection)).getResult().show();
    }

    public static void main(String[] args) {
        new ImageJ();
        ImagePlus imp = IJ.openImage("C:/structure/data/clincubator_data/ISB200714_well5_1pos_ON_t000000.tif");
        //ImagePlus imp = IJ.openImage("C:/structure/data/Polyclad_2019-10-29_Pc_Tp502.tif");
        imp.show();

        float scale_in_microns = 1f;
        float background_subtraction_radius_in_microns = 10;
        int number_of_angles = 720;
        Projection projection = Projection.Maximum_Intensity;

        new Framor(imp, new TrashSphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection)).getResult().show();

    }
}

