package net.haesleinhuepf.clincubator.projection;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.Duplicator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.framor.AbstractFrameProcessor;
import net.haesleinhuepf.clijx.framor.FrameProcessor;
import net.haesleinhuepf.clijx.framor.Framor;
import net.haesleinhuepf.clijx.plugins.ReslicePolar;

import java.awt.*;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

public class SphereProjectionFrameProcessor extends AbstractFrameProcessor implements PlugInFilter {

    protected Float scale_in_microns = 1f;
    protected Float background_subtraction_radius_in_microns = 10f;
    protected float denosing_xy_radius_in_pixels = 1;
    protected Integer number_of_angles = 360;
    protected float radius_in_microns = 1000;

    protected float start_angle1 = 180f;
    protected float start_angle2 = 0f;
    protected float full_rotation = 360f;
    protected float center_x_relative = 0.5f;
    protected float center_y_relative = 0.5f;
    protected float center_z_relative = 0.5f;
    protected Projection projection = Projection.Maximum_Intensity;

    public SphereProjectionFrameProcessor() {}
    public SphereProjectionFrameProcessor(Float scale_in_microns, Float background_subtraction_radius_in_microns, Integer number_of_angles, Projection projection) {
        this.scale_in_microns = scale_in_microns;
        this.background_subtraction_radius_in_microns = background_subtraction_radius_in_microns;
        this.number_of_angles = number_of_angles;
        this.projection = projection;
    }

    @Override
    public ImagePlus process(ImagePlus imp) {
        CLIJ2 clij2 = getCLIJ2();
        ClearCLBuffer input = clij2.push(imp);

        // -------------------------------------------------------------------------------------------------------------
        // make isotropic
        Calibration calib = imp.getCalibration();
        float scale_x = (float) (calib.pixelWidth / scale_in_microns);
        float scale_y = (float) (calib.pixelHeight / scale_in_microns);
        float scale_z = (float) (calib.pixelDepth / scale_in_microns);

        //ClearCLBuffer isotropic = clij2.create(new long[]{
        //            (long) (imp.getWidth() * scale1X),
        //            (long) (imp.getHeight() * scale1Y),
        //            (long) (imp.getNSlices() * scale1Z)}, clij2.Float);

        //AffineTransform3D scaleTransform = new AffineTransform3D();
        //scaleTransform.scale(1.0 / scale1X, 1.0 / scale1Y, 1.0 / scale1Z);
        //clij2.affineTransform3D(input, isotropic, scaleTransform);
        //input.close();

        //clij2.show(isotropic, "isotropic");


        //ClearCLBuffer isotropic = input;
        // -------------------------------------------------------------------------------------------------------------
        // denoising
        if (denosing_xy_radius_in_pixels > 0) {
            ClearCLBuffer denoised = clij2.create(input);
            clij2.median3DBox(input, denoised, denosing_xy_radius_in_pixels, denosing_xy_radius_in_pixels, 0);
            input.close();
            input = denoised;
        }

        // -------------------------------------------------------------------------------------------------------------
        // background subtraction
        if (background_subtraction_radius_in_microns > 0) {
            ClearCLBuffer background_subtracted = clij2.create(input);
            clij2.topHatBox(input, background_subtracted, background_subtraction_radius_in_microns / scale_x, background_subtraction_radius_in_microns / scale_y, background_subtraction_radius_in_microns / scale_z);
            input.close();
            input = background_subtracted;
        }


        // -------------------------------------------------------------------------------------------------------------
        // spatial transforms, including radial projection
        float center_x = (float)(input.getWidth() * this.center_x_relative);
        float center_y = (float)(input.getHeight() * this.center_y_relative);
        float center_z = (float)(input.getDepth() * this.center_z_relative);

        float delta_angle_in_degrees = full_rotation / number_of_angles;

        ClearCLBuffer polar = clij2.create((long)(full_rotation / delta_angle_in_degrees), (long)(full_rotation / 2 / delta_angle_in_degrees), (long)radius_in_microns);

        ReslicePolar.reslicePolar(clij2, input, polar, delta_angle_in_degrees, start_angle1, start_angle2,
                center_x, center_y, center_z,
                scale_x, scale_y, scale_z,
                0,0,0,
                0,0,0);



        //ClearCLBuffer resliced_from_top = clij2.create(background_subtracted.getWidth(), background_subtracted.getDepth(), background_subtracted.getHeight());
        //clij2.resliceTop(background_subtracted, resliced_from_top);
        //background_subtracted.close();

        //clij2.show(resliced_from_top, "resliced_from_top");

        //ClearCLBuffer radial_resliced = clij2.create(radius, background_subtracted.getHeight(), number_of_angles);
        //clij2.resliceRadial(resliced_from_top, radial_resliced, delta_angle_in_degrees, start_angle1, center_x, center_y, 1.0, 1.0);
        //resliced_from_top.close();
        //clij2.show(radial_resliced, "radial_resliced");


        //ClearCLBuffer radial_resliced2 = clij2.create(radius, number_of_angles, number_of_angles);
        //clij2.resliceRadial(radial_resliced, radial_resliced2, delta_angle_in_degrees, start_angle2, 0, radial_resliced.getHeight() / 2, 1.0, 1.0);
        //radial_resliced.close();




        //ClearCLBuffer transposed = clij2.create(radial_resliced2.getDepth(), radial_resliced2.getHeight(), radial_resliced2.getWidth());

        //clij2.transposeXZ(radial_resliced2, transposed);
        //radial_resliced2.close();

        //clij2.show(transposed, "transposed");

        // -------------------------------------------------------------------------------------------------------------
        // projection
        ClearCLBuffer output = Utilities.project(clij2, polar, projection);

        polar.close();
        // -------------------------------------------------------------------------------------------------------------

        ImagePlus result = clij2.pull(output);
        output.close();

        result.setTitle("Half-stack-cylinder-maximum-projection of " + imp.getTitle());
        result.getCalibration().pixelWidth = full_rotation / number_of_angles;
        result.getCalibration().setXUnit("degrees");
        result.getCalibration().pixelHeight = full_rotation / number_of_angles;
        result.getCalibration().setXUnit("degrees");
        result.getCalibration().pixelDepth = 1;
        result.getCalibration().setZUnit("voxel");

        result.getCalibration().frameInterval = imp.getCalibration().frameInterval;
        result.getCalibration().setTimeUnit(imp.getCalibration().getTimeUnit());

        return result;
    }

    @Override
    public FrameProcessor duplicate() {
        SphereProjectionFrameProcessor frameProcessor = new SphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection);
        frameProcessor.setCLIJ2(getCLIJ2());
        return frameProcessor;
    }


    @Override
    public long getMemoryNeedInBytes(ImagePlus imp) {
        Calibration calib = imp.getCalibration();
        float scale1X = (float) (calib.pixelWidth / scale_in_microns);
        float scale1Y = (float) (calib.pixelHeight / scale_in_microns);
        float scale1Z = (float) (calib.pixelDepth / scale_in_microns);


        return imp.getBitDepth() / 8 * imp.getWidth() * imp.getHeight() * imp.getNSlices() +
                (long) (imp.getWidth() * scale1X) *
                        (long) (imp.getHeight() * scale1Y) *
                        (long) (imp.getNSlices() * scale1Z) * 4 * 3;

    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        ImagePlus imp = IJ.getImage();
        previewImp = new Duplicator().run(imp, imp.getC(), imp.getC(), 1, imp.getNSlices(), imp.getT(), imp.getT());


        showPreview();

        GenericDialog gd = new GenericDialog("Half-stack cylinder projection");
        gd.addNumericField("scale_in_microns", scale_in_microns);
        gd.addNumericField("background_subtraction_radius_in_microns", background_subtraction_radius_in_microns);
        gd.addNumericField("number_of_angles", number_of_angles);

        TextField tf1 = (TextField) gd.getNumericFields().get(0);
        TextField tf2 = (TextField) gd.getNumericFields().get(1);
        TextField tf3 = (TextField) gd.getNumericFields().get(2);

        TextListener listener = new TextListener() {
            @Override
            public void textValueChanged(TextEvent e) {
                System.out.println("Text changed: " + tf1.getText());
                try
                {
                    double value = Integer.parseInt(tf1.getText());
                    if (value >= 0) {
                        scale_in_microns = (float)value;
                    }

                    value = Integer.parseInt(tf2.getText());
                    if (value >= 0) {
                        background_subtraction_radius_in_microns = (float)value;
                    }

                    value = Integer.parseInt(tf3.getText());
                    if (value >= 0) {
                        number_of_angles = (int)value;
                    }
                    showPreview();

                } catch (Exception ex) {
                    System.out.println("Exception: " + ex);
                }
            }
        };

        tf1.addTextListener(listener);
        tf2.addTextListener(listener);
        tf3.addTextListener(listener);







        gd.addChoice("Projection", Projection.allToString(), projection.toString());
        gd.showDialog();

        if (gd.wasCanceled()) {
            return;
        }
        scale_in_microns = (float)gd.getNextNumber();
        background_subtraction_radius_in_microns = (float)gd.getNextNumber();
        number_of_angles = (int)gd.getNextNumber();
        projection = Projection.all()[gd.getNextChoiceIndex()];

        new Framor(imp, new SphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection)).getResult().show();
    }

    ImagePlus previewImp;
    private void showPreview() {
        System.out.println("start");
        setCLIJ2(CLIJ2.getInstance());
        ImagePlus result = process(previewImp);
        CLIJx clijx = CLIJx.getInstance();
        clijx.showGrey(clijx.push(result), "preview");
        clijx.clear();
        System.out.println("end");
    }

    /*
    public static void main(String[] args) {
        new ImageJ();
        ImagePlus imp = IJ.openImage("C:/structure/data/Half_Polyclad_2019-10-29_Pc_Tp502.tif");
        imp.show();

        float scale_in_microns = 0.5f;
        float background_subtraction_radius_in_microns = 10;
        int number_of_angles = 360;
        Projection projection = Projection.Maximum_Intensity;

        new Framor(imp, new SphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection)).getResult().show();

    }*/

    public static void main(String[] args) {
        new ImageJ();
        ImagePlus imp = IJ.openImage("C:/structure/data/clincubator_data/ISB200714_well5_1pos_ON_t000000.tif");
        //ImagePlus imp = IJ.openImage("C:/structure/data/Polyclad_2019-10-29_Pc_Tp502.tif");
        imp.show();

        //float scale_in_microns = 1f;
        //float background_subtraction_radius_in_microns = 10;
        //int number_of_angles = 720;
        //Projection projection = Projection.Maximum_Intensity;

        //new Framor(imp, new TrashSphereProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection)).getResult().show();

        new SphereProjectionFrameProcessor().run(null);
    }
}

