package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.gui.Roi;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clij.macro.AbstractCLIJPlugin.asBoolean;

@Plugin(type = AssistantGUIPlugin.class)
public class LabelingWorkflowALX extends AbstractAssistantGUIPlugin {

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
