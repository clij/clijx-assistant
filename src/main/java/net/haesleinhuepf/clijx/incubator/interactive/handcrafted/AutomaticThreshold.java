package net.haesleinhuepf.clijx.incubator.interactive.handcrafted;

import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.plugins.AutoThresholderImageJ1;
import net.haesleinhuepf.clij2.plugins.Copy;
import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;

@Plugin(type = IncubatorPlugin.class)
public class AutomaticThreshold extends AbstractIncubatorPlugin {

    Choice choice = null;

    public AutomaticThreshold() {
        super(new net.haesleinhuepf.clij2.plugins.AutomaticThreshold());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = new GenericDialog(IncubatorUtilities.niceName(this.getClass().getSimpleName()));
        gd.addChoice("Threshold algorithm", AutoThresholderImageJ1.getMethods(), "Default" );

        choice = (Choice) gd.getChoices().get(0);

        return gd;
    }

    ClearCLBuffer[] result = null;
    public synchronized void refresh()
    {
        ClearCLBuffer[] pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        String algorithm = "";
        if (choice != null) {
            int index = choice.getSelectedIndex();
            if (index >= 0) {
                algorithm = AutoThresholderImageJ1.getMethods()[index];
            }
        }

        net.haesleinhuepf.clij2.plugins.AutomaticThreshold plugin = (net.haesleinhuepf.clij2.plugins.AutomaticThreshold) getCLIJMacroPlugin();

        args = new Object[] {
                pushed[0],
                null,
                algorithm
        };
        plugin.setArgs(args);

        if (result == null) {
            result = createOutputBufferFromSource(new ClearCLBuffer[]{pushed[0]});
        }
        args[1] = result[0];
        executeCL(pushed, result);
        cleanup(my_source, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Thresholded (" + algorithm + ") " + my_source.getTitle());
        my_target.setDisplayRange(0, 1);
        my_target.updateAndDraw();
    }

}
