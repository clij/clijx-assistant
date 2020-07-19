package net.haesleinhuepf.clincubator.interactive.labeling;

import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.RigidTransform3D;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class ExtendLabelsUntilTheyTouch extends AbstractIncubatorPlugin {

    ClearCLBuffer result = null;
    protected synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed);
        }
        clijx.extendLabelingViaVoronoi(pushed, result);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Extended labels " + my_source.getTitle());
        IncubatorUtilities.glasbey(my_target);
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
    }


    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
               // trhesholding
                // spot detection
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{

        };
    }
}
