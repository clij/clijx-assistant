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

public class HalfStackCylinderProjectionFrameProcessor extends AbstractFrameProcessor implements PlugInFilter {

    protected Float scale_in_microns = 1f;
    protected Float background_subtraction_radius_in_microns = 10f;
    protected Integer number_of_angles = 180;

    protected float start_angle = -90f;
    protected float full_rotation = 180f;
    protected float center_x_relative = 0.5f;
    protected float center_y_relative = 0f;

    protected Projection projection = Projection.Maximum_Intensity;

    public HalfStackCylinderProjectionFrameProcessor() {}
    public HalfStackCylinderProjectionFrameProcessor(Float scale_in_microns, Float background_subtraction_radius_in_microns, Integer number_of_angles, Projection projection) {
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
        float scale1X = (float) (calib.pixelWidth / scale_in_microns);
        float scale1Y = (float) (calib.pixelHeight / scale_in_microns);
        float scale1Z = (float) (calib.pixelDepth / scale_in_microns);

        ClearCLBuffer isotropic = clij2.create(new long[]{
                    (long) (imp.getWidth() * scale1X),
                    (long) (imp.getHeight() * scale1Y),
                    (long) (imp.getNSlices() * scale1Z)}, clij2.Float);

        AffineTransform3D scaleTransform = new AffineTransform3D();
        scaleTransform.scale(1.0 / scale1X, 1.0 / scale1Y, 1.0 / scale1Z);
        clij2.affineTransform3D(input, isotropic, scaleTransform);
        input.close();

        //clij2.show(isotropic, "isotropic");

        // -------------------------------------------------------------------------------------------------------------
        // background subtraction
        ClearCLBuffer background_subtracted = isotropic;
        if (background_subtraction_radius_in_microns > 0) {
            background_subtracted = clij2.create(isotropic);
            System.out.println(clij2.reportMemory());
            clij2.topHatBox(isotropic, background_subtracted, background_subtraction_radius_in_microns / scale_in_microns, background_subtraction_radius_in_microns / scale_in_microns, background_subtraction_radius_in_microns / scale_in_microns);
            isotropic.close();
        }

        // -------------------------------------------------------------------------------------------------------------
        // spatial transforms, including radial projection
        int center_x = (int)(background_subtracted.getWidth() * this.center_x_relative);
        int center_y = (int)(background_subtracted.getDepth() * this.center_y_relative);

        int radius = (int) Math.sqrt(Math.pow(center_x, 2) + Math.pow(center_y, 2));
        double delta_angle_in_degrees = full_rotation / number_of_angles;

        ClearCLBuffer resliced_from_top = clij2.create(background_subtracted.getWidth(), background_subtracted.getDepth(), background_subtracted.getHeight());
        clij2.resliceTop(background_subtracted, resliced_from_top);
        background_subtracted.close();

        //clij2.show(resliced_from_top, "resliced_from_top");

        ClearCLBuffer radial_resliced = clij2.create(radius, background_subtracted.getHeight(), number_of_angles);
        clij2.resliceRadial(resliced_from_top, radial_resliced, delta_angle_in_degrees, center_x, center_y);
        clij2.resliceRadial(resliced_from_top, radial_resliced, delta_angle_in_degrees, start_angle, center_x, center_y, 1.0, 1.0);
        resliced_from_top.close();
        //clij2.show(radial_resliced, "radial_resliced");

        ClearCLBuffer transposed = clij2.create(radial_resliced.getDepth(), radial_resliced.getHeight(), radial_resliced.getWidth());

        clij2.transposeXZ(radial_resliced, transposed);
        radial_resliced.close();

        //clij2.show(transposed, "transposed");

        // -------------------------------------------------------------------------------------------------------------
        // Projection

        ClearCLBuffer output = Utilities.project(clij2, transposed, projection);
        transposed.close();


        //clij2.show(output, "output");

        // -------------------------------------------------------------------------------------------------------------

        ImagePlus result = clij2.pull(output);
        output.close();

        result.setTitle("Half-stack-cylinder-maximum-projection of " + imp.getTitle());
        result.getCalibration().pixelWidth = 180.0 / number_of_angles;
        result.getCalibration().setXUnit("degrees");
        result.getCalibration().pixelHeight = scale_in_microns;
        result.getCalibration().setYUnit("microns");
        result.getCalibration().pixelDepth = 1;
        result.getCalibration().setZUnit("voxel");

        result.getCalibration().frameInterval = imp.getCalibration().frameInterval;
        result.getCalibration().setTimeUnit(imp.getCalibration().getTimeUnit());

        return result;
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
    public FrameProcessor duplicate() {
        HalfStackCylinderProjectionFrameProcessor frameProcessor = new HalfStackCylinderProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection);
        frameProcessor.setCLIJ2(getCLIJ2());
        return frameProcessor;
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Half-stack-cylinder-maximum-projection");
        gd.addNumericField("scale_in_microns", scale_in_microns);
        gd.addNumericField("background_subtraction_radius_in_microns", background_subtraction_radius_in_microns);
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

        new Framor(IJ.getImage(), new HalfStackCylinderProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection)).getResult().show();
    }

    public static void main(String[] args) {
        new ImageJ();
        ImagePlus imp = IJ.openImage("C:/structure/data/Lund_001457.tif");
        imp.show();

        float scale_in_microns = 1;
        float background_subtraction_radius_in_microns = 10;
        int number_of_angles = 720;
        Projection projection = Projection.Maximum_Intensity;

        new Framor(imp, new HalfStackCylinderProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, projection)).getResult().show();

    }
}

