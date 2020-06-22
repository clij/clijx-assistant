package net.haesleinhuepf.spimcat.transform;

import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import ij.ImageJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.AbstractIncubatorPlugin;
import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCL;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.enums.ImageChannelDataType;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import net.imglib2.realtransform.AffineTransform3D;

public class MakeIsotropic extends AbstractIncubatorPlugin {

    float zoom = 1;

    protected void configure() {
        GenericDialogPlus gdp = new GenericDialogPlus("Make isotropic");
        gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addNumericField("Future voxel size (in microns)", 1.0, 1);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return;
        }

        setSource(gdp.getNextImage());
        zoom = (float) gdp.getNextNumber();
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

}
