package net.haesleinhuepf.clijx.incubator.interactive.handcrafted;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.Duplicator;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.plugins.AutoThresholderImageJ1;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;

@Plugin(type = IncubatorPlugin.class)
public class PullToROIManager extends AbstractIncubatorPlugin {

    Choice choice = null;
    String[] titles = null;

    public PullToROIManager() {
        super(new net.haesleinhuepf.clij2.plugins.PullToROIManager());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        titles = WindowManager.getImageTitles();
        GenericDialog gd = new GenericDialog(IncubatorUtilities.niceName(this.getClass().getSimpleName()));
        gd.addChoice("", titles, titles[titles.length - 1] );

        choice = (Choice) gd.getChoices().get(0);

        return gd;
    }

    ClearCLBuffer[] result = null;
    public synchronized void refresh()
    {
        ClearCLBuffer[] pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        if (pushed[0].getDepth() > 1 || pushed.length > 1) {
            IJ.log("Warning: Show in ROIM Manager is only supported for single channel 2D Images.");
        }

        String algorithm = "";
        if (choice != null) {
            int index = choice.getSelectedIndex();
            if (index >= 0) {
                algorithm = AutoThresholderImageJ1.getMethods()[index];
            }
        }

        // we just do that so that the recorder has something to analyse
        net.haesleinhuepf.clij2.plugins.PullToROIManager plugin = (net.haesleinhuepf.clij2.plugins.PullToROIManager) getCLIJMacroPlugin();
        args = new Object[] {
                pushed[0]
        };
        plugin.setArgs(args);


        CLIJx clijx = CLIJx.getInstance();

        if (result == null) {
            result = new ClearCLBuffer[]{clijx.create(new long[]{pushed[0].getWidth(), pushed[0].getHeight()}, pushed[0].getNativeType())};
        }

        RoiManager rm = RoiManager.getInstance();
        if (rm == null) {
            rm = new RoiManager();
        }

        rm.reset();
        clijx.pullLabelsToROIManager(pushed[0], rm);

        ImagePlus view = null;
        if (choice != null) {
            view = WindowManager.getImage(choice.getSelectedItem());
            if (view != null) {
                ClearCLBuffer buffer = clijx.pushCurrentSlice(view);
                clijx.copy(buffer, result[0]);
                buffer.close();
            }
        }

        cleanup(my_source, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("ROIs of " + my_source.getTitle());
        rm.runCommand(my_target, "Show all with labels");
        enhanceContrast();

    }

}
