package net.haesleinhuepf.clincubator.utilities;

import ij.IJ;
import ij.IJEventListener;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.tool.PlugInTool;
import ij.process.ByteProcessor;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.generated.MaximumZProjection;
import net.haesleinhuepf.clincubator.interactive.generated.Mean;
import net.haesleinhuepf.clincubator.interactive.generated.TopHat;
import net.haesleinhuepf.clincubator.interactive.handcrafted.MakeIsotropic;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import static net.haesleinhuepf.clijx.gui.Utilities.ignoreEvent;

@Plugin(type = SuggestedPlugin.class)
public class IncubatorStartingPoint extends AbstractIncubatorPlugin {

    int former_t = -1;
    int former_c = -1;


    @Override
    public <T extends SuggestedPlugin> Class<T>[] suggestedNextSteps() {
        return new Class[] {
                Mean.class,
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
        if (IJ.getImage().getStack() instanceof CLIJxVirtualStack) {
            IJ.error("This image is managed by CLIncubator already.");
            return;
        }
        ImagePlus voidSource = new ImagePlus("pixel", new ByteProcessor(1,1));
        ImagePlus imp = IJ.getImage();

        IncubatorUtilities.transferCalibration(imp, voidSource);
        setSource(voidSource);
        former_t = imp.getT();
        former_c = imp.getC();
        setTarget(imp);

        GenericDialog dialog = buildNonModalDialog(my_target.getWindow());
        if (dialog != null) {
            registerDialogAsNoneModal(dialog);
            //dialog.showDialog();
        }
    }


    @Override
    public void imageUpdated(ImagePlus imp) {
        if (imp == my_target) {
            if (imp.getT() != former_t || imp.getC() != former_c) {
                setTargetInvalid();
            }
        }
    }
}
