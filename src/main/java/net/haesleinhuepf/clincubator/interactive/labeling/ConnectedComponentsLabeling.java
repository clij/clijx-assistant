package net.haesleinhuepf.clincubator.interactive.labeling;

import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class ConnectedComponentsLabeling extends AbstractIncubatorPlugin implements Labeler {

    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed.getDimensions(), NativeTypeEnum.Float);
        }
        clijx.connectedComponentsLabelingBox(pushed, result);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Connected components labelled " + my_source.getTitle());
        IncubatorUtilities.glasbey(my_target);
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
    }

}
