package net.haesleinhuepf.clincubator.interactive.projections;

import ij.IJ;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.processing.GaussianBlur;
import net.haesleinhuepf.clincubator.interactive.processing.Mean;
import net.haesleinhuepf.clincubator.interactive.processing.Median;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.MakeIsotropic;
import net.haesleinhuepf.clincubator.interactive.transform.RigidTransform3D;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class MeanZProjection extends AbstractIncubatorPlugin implements PopularIntensityProjection{

    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed.getWidth(), pushed.getHeight());
        }
        clijx.meanZProjection(pushed, result);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Mean Z projected " + my_source.getTitle());
    }



}
