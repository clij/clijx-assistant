package net.haesleinhuepf.clincubator;

import ij.IJ;
import ij.IJEventListener;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import ij.plugin.tool.PlugInTool;
import ij.process.ByteProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.utilities.CLIJUtilities;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.gui.InteractiveWindowPosition;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.IncubatorPluginRegistry;
import net.haesleinhuepf.clincubator.interactive.generated.GaussianBlur;
import net.haesleinhuepf.clincubator.interactive.generated.MaximumZProjection;
import net.haesleinhuepf.clincubator.interactive.generated.Mean;
import net.haesleinhuepf.clincubator.interactive.generated.TopHat;
import net.haesleinhuepf.clincubator.interactive.handcrafted.MakeIsotropic;
import net.haesleinhuepf.clincubator.utilities.IncubatorStartingPointTool;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.gui.Utilities.ignoreEvent;

@Plugin(type = SuggestedPlugin.class)
public class CLIncubatorStartingPoint extends AbstractIncubatorPlugin {

    int former_z = -1;
    int former_t = -1;
    int former_c = -1;


    @Override
    public <T extends SuggestedPlugin> Class<T>[] suggestedNextSteps() {
        return new Class[] {
                GaussianBlur.class,
                TopHat.class,
                MakeIsotropic.class,
                MaximumZProjection.class
        };
    }

    @Override
    public <T extends SuggestedPlugin> Class<T>[] suggestedPreviousSteps() {
        return new Class[0];
    }

    @Override
    public void run(String arg) {
        Toolbar.addPlugInTool(new InteractiveWindowPosition());

        if (IJ.getImage().getStack() instanceof CLIJxVirtualStack) {
            IJ.error("This image is managed by CLIncubator already.");
            return;
        }
        IncubatorPluginRegistry.getInstance().register(this);
        ImagePlus.addImageListener(this);

        ImagePlus imp = IJ.getImage();
        setSource(imp);
        former_t = imp.getT();
        former_c = imp.getC();
        former_z = imp.getZ();

        //setTarget(imp);

        //IncubatorUtilities.stamp(CLIJxVirtualStack.imagePlusToBuffer(my_target));
        refresh();

        GenericDialog dialog = buildNonModalDialog(my_target.getWindow());
        if (dialog != null) {
            registerDialogAsNoneModal(dialog);
            //dialog.showDialog();
        }
    }

    ClearCLBuffer result = null;

    public synchronized void refresh() {
        if (result == null) {
            result = CLIJx.getInstance().pushCurrentZStack(my_source);
        } else {
            ClearCLBuffer temp = CLIJx.getInstance().pushCurrentZStack(my_source);
            temp.copyTo(result, true);
            temp.close();
        }
        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
    }

    @Override
    public void imageUpdated(ImagePlus imp) {
        if (imp == my_source) {
            System.out.println("Source updated");
            if (imp.getT() != former_t || imp.getC() != former_c) {
                System.out.println("Target invalidated");
                setTargetInvalid();

                former_t = imp.getT();
                former_c = imp.getC();
            }

            if (imp.getZ() != former_z) {
                refreshView();
                former_z = imp.getZ();
            }
        }
    }
}
