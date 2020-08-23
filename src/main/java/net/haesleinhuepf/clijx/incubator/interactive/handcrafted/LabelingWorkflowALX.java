package net.haesleinhuepf.clijx.incubator.interactive.handcrafted;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.plugins.AutoThresholderImageJ1;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import net.imglib2.ops.parse.token.Int;
import org.scijava.plugin.Plugin;

import java.awt.*;

import static net.haesleinhuepf.clij.macro.AbstractCLIJPlugin.asBoolean;
import static net.haesleinhuepf.clij.macro.AbstractCLIJPlugin.asFloat;

@Plugin(type = IncubatorPlugin.class)
public class LabelingWorkflowALX extends AbstractIncubatorPlugin {

    public LabelingWorkflowALX() {
        super(new net.haesleinhuepf.clijx.plugins.LabelingWorkflowALX());
    }

    @Override
    public void refreshView() {
        super.refreshView();
        if (args != null && asBoolean(args[6])) {
            if (my_target.getStack() instanceof CLIJxVirtualStack) {
                ClearCLBuffer stack = ((CLIJxVirtualStack) my_target.getStack()).getBuffer(my_source.getC() - 1);
                CLIJx clijx = CLIJx.getInstance();
                ClearCLBuffer slice = clijx.create(stack.getWidth(), stack.getHeight());
                clijx.copySlice(stack, slice, my_source.getZ() - 1);

                Roi roi = clijx.pullAsROI(slice);
                slice.close();
                my_source.setRoi(roi);
            }
        }
    }
}
