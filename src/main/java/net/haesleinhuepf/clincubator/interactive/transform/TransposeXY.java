package net.haesleinhuepf.clincubator.interactive.transform;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.enums.ImageChannelDataType;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.processing.*;
import net.haesleinhuepf.clincubator.utilities.MenuSeparator;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import net.imglib2.realtransform.AffineTransform3D;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class TransposeXY extends AbstractIncubatorPlugin {

    ClearCLBuffer result = null;
    protected synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();

        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            if (pushed.getDimension() == 2) {
                result = clijx.create(new long[]{
                        pushed.getHeight(),
                        pushed.getWidth()}, pushed.getNativeType());
            } else {
                result = clijx.create(new long[]{
                        pushed.getHeight(),
                        pushed.getWidth(),
                        pushed.getDepth()}, pushed.getNativeType());
            }
        }

        clijx.transposeXZ(pushed, result);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Transposed XY " + my_source.getTitle());
        my_target.getCalibration().pixelWidth = my_source.getCalibration().pixelHeight;
        my_target.getCalibration().pixelHeight = my_source.getCalibration().pixelWidth;
        my_target.getCalibration().pixelDepth = my_source.getCalibration().pixelDepth;
        my_target.getCalibration().setUnit(my_source.getCalibration().getUnit());
    }

    @Override
    protected void refreshView() {
        my_target.setZ((int) (my_source.getZ() * my_source.getCalibration().pixelDepth / my_target.getCalibration().pixelDepth));
    }


    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{
        };
    }
}
