package net.haesleinhuepf.clincubator.interactive.transform;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.enums.ImageChannelDataType;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.interactive.processing.*;
import net.haesleinhuepf.clincubator.utilities.MenuSeparator;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import net.haesleinhuepf.clincubator.interactive.projections.MaximumZProjection;
import net.haesleinhuepf.clincubator.interactive.projections.MeanZProjection;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class MakeIsotropic extends AbstractIncubatorPlugin {

    float zoom = 1;

    protected boolean configure() {
        GenericDialog gdp = new GenericDialog("Make isotropic");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addNumericField("Future voxel size (in microns)", 1.0, 1);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return false;
        }

        setSource(IJ.getImage());
        zoom = (float) gdp.getNextNumber();
        return true;
    }

    ClearCLBuffer result = null;
    protected synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();

        Calibration calib = my_source.getCalibration();
        float scale1X = (float) (calib.pixelWidth / zoom);
        float scale1Y = (float) (calib.pixelHeight / zoom);
        float scale1Z = (float) (calib.pixelDepth / zoom);

        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(new long[]{
                    (long) (my_source.getWidth() * scale1X),
                    (long) (my_source.getHeight() * scale1Y),
                    (long) (my_source.getNSlices() * scale1Z)}, clijx.Float);
        }

        ClearCLImage temp = clijx.create(pushed.getDimensions(), ImageChannelDataType.Float);

        clijx.copy(pushed, temp);
        pushed.close();

        AffineTransform3D scaleTransform = new AffineTransform3D();
        scaleTransform.scale(1.0 / scale1X, 1.0 / scale1Y, 1.0 / scale1Z);
        clijx.affineTransform3D(temp, result, scaleTransform);

        temp.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Isotropic " + my_source.getTitle());
        my_target.getCalibration().pixelWidth = zoom;
        my_target.getCalibration().pixelHeight = zoom;
        my_target.getCalibration().pixelDepth = zoom;
        my_target.getCalibration().setUnit("micron");
    }

    @Override
    protected void refreshView() {
        my_target.setZ((int) (my_source.getZ() * my_source.getCalibration().pixelDepth / my_target.getCalibration().pixelDepth));
    }


    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
                RigidTransform3D.class,
                MenuSeparator.class,
                SphereProjection.class,
                CylinderProjection.class
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{
                GaussianBlur.class,
                Mean.class,
                Median.class,
                DifferenceOfGaussian.class,
                BackgroundSubtraction.class,
                LaplacianOfGaussian.class
        };
    }
}
