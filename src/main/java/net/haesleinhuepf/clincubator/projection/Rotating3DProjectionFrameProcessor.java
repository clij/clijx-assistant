package net.haesleinhuepf.clincubator.projection;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.enums.ImageChannelDataType;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.framor.AbstractFrameProcessor;
import net.haesleinhuepf.clijx.framor.FrameProcessor;
import net.haesleinhuepf.clijx.framor.Framor;
import net.imglib2.realtransform.AffineTransform3D;

public class Rotating3DProjectionFrameProcessor extends AbstractFrameProcessor implements PlugInFilter {

    protected Float scale_in_microns = 1f;
    protected Float background_subtraction_radius_in_microns = 10f;
    protected Integer number_of_angles = 320;

    protected float start_angle = -90f;
    protected float full_rotation = 360f;
    protected Projection projection = Projection.Maximum_Intensity;

    public Rotating3DProjectionFrameProcessor() {}
    public Rotating3DProjectionFrameProcessor(Float scale_in_microns, Float background_subtraction_radius_in_microns, Integer number_of_angles, Float full_rotation, Projection projection) {
        this.scale_in_microns = scale_in_microns;
        this.background_subtraction_radius_in_microns = background_subtraction_radius_in_microns;
        this.number_of_angles = number_of_angles;
        this.full_rotation = full_rotation;
        this.projection = projection;
    }

    @Override
    public ImagePlus process(ImagePlus imp) {
        CLIJ2 clij2 = getCLIJ2();
        ClearCLBuffer input = clij2.push(imp);

        Calibration calib = imp.getCalibration();
        float scale1X = (float) (calib.pixelWidth / scale_in_microns);
        float scale1Y = (float) (calib.pixelHeight / scale_in_microns);
        float scale1Z = (float) (calib.pixelDepth / scale_in_microns);

        // -------------------------------------------------------------------------------------------------------------
        // subtract background

        int sigma_x = (int)(background_subtraction_radius_in_microns / scale1X + 0.5);
        int sigma_y = (int)(background_subtraction_radius_in_microns / scale1Y + 0.5);
        int sigma_z = (int)(background_subtraction_radius_in_microns / scale1Z + 0.5);
        ClearCLImage background_subtracted = clij2.create(input.getDimensions(), ImageChannelDataType.Float);
        Utilities.topHatBox(clij2, input, background_subtracted, sigma_x, sigma_y, sigma_z);
        input.close();

        // -------------------------------------------------------------------------------------------------------------
        // make isotropic
        ClearCLBuffer isotropic = clij2.create(new long[]{
                    (long) (imp.getWidth() * scale1X),
                    (long) (imp.getHeight() * scale1Y),
                    (long) (imp.getNSlices() * scale1Z)}, clij2.Float);

        // -------------------------------------------------------------------------------------------------------------
        // spatial transforms, including radial projection
        double delta_angle_in_degrees = full_rotation / number_of_angles;

        // -------------------------------------------------------------------------------------------------------------
        // Projection(s)
        ClearCLBuffer output = clij2.create(isotropic.getWidth(), isotropic.getHeight(), number_of_angles);
        ClearCLBuffer slice = clij2.create(isotropic.getWidth(), isotropic.getHeight());

        for (int a = 0; a < number_of_angles; a++) {
            double angle = start_angle + a * delta_angle_in_degrees;

            AffineTransform3D affineTransform = new AffineTransform3D();
            affineTransform.scale(scale1X, scale1Y, scale1Z);
            affineTransform.translate(-isotropic.getWidth() / 2, -isotropic.getHeight() / 2, -isotropic.getDepth() / 2);
            affineTransform.rotate(1, angle * Math.PI / 180.0);
            affineTransform.translate(isotropic.getWidth() / 2, isotropic.getHeight() / 2, isotropic.getDepth() / 2);
            affineTransform = affineTransform.inverse();
            clij2.affineTransform3D(background_subtracted, isotropic, affineTransform);

            Utilities.project(clij2, isotropic, slice, projection);

            clij2.copySlice(slice, output, a);
        }
        isotropic.close();
        input.close();
        //clij2.show(output, "output");

        // -------------------------------------------------------------------------------------------------------------

        ImagePlus result = clij2.pull(output);
        output.close();

        result.setTitle("Rotating 3D projection of " + imp.getTitle());
        result.getCalibration().pixelWidth = scale_in_microns;
        result.getCalibration().setXUnit("microns");
        result.getCalibration().pixelHeight = scale_in_microns;
        result.getCalibration().setYUnit("microns");
        result.getCalibration().pixelDepth = delta_angle_in_degrees;
        result.getCalibration().setZUnit("degrees");

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
                (long) (imp.getNSlices() * scale1Z) * 2 +

                imp.getBitDepth() / 8 * imp.getWidth() * imp.getHeight() * number_of_angles +
                (long) (imp.getWidth() * scale1X) *
                        (long) (imp.getHeight() * scale1Y) *
                        (long) (imp.getNSlices() * scale1Z);
    }

    @Override
    public FrameProcessor duplicate() {
        Rotating3DProjectionFrameProcessor frameProcessor = new Rotating3DProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, full_rotation, projection);
        frameProcessor.setCLIJ2(getCLIJ2());
        return frameProcessor;
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        GenericDialog gd = new GenericDialog("Rotating 3D projection");
        gd.addNumericField("scale_in_microns", scale_in_microns);
        gd.addNumericField("background_subtraction_radius_in_microns", background_subtraction_radius_in_microns);
        gd.addNumericField("number_of_angles", number_of_angles);
        gd.addNumericField("full_rotation_in_degrees", full_rotation);
        gd.addChoice("Projection", Projection.allToString(), projection.toString());
        gd.showDialog();
        if (gd.wasCanceled()) {
            return;
        }
        scale_in_microns = (float)gd.getNextNumber();
        background_subtraction_radius_in_microns = (float)gd.getNextNumber();
        number_of_angles = (int)gd.getNextNumber();
        full_rotation = (float)gd.getNextNumber();
        projection = Projection.all()[gd.getNextChoiceIndex()];

        new Framor(IJ.getImage(), new Rotating3DProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, full_rotation, projection)).getResult().show();
    }

    public static void main(String[] args) {
        new ImageJ();
        ImagePlus imp = IJ.openImage("C:/structure/data/clincubator_data/Florence_000300.tif");
        imp.show();

        float scale_in_microns = 1;
        float background_subtraction_radius_in_microns = 3;
        int number_of_angles = 360;
        float full_rotation = 360;
        Projection projection = Projection.Maximum_Intensity;

        new Framor(imp, new Rotating3DProjectionFrameProcessor(scale_in_microns, background_subtraction_radius_in_microns, number_of_angles, full_rotation, projection)).getResult().show();

    }
}

