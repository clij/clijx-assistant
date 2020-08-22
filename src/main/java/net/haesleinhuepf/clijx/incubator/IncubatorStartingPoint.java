package net.haesleinhuepf.clijx.incubator;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.plugins.Copy;
import net.haesleinhuepf.clijx.gui.InteractiveWindowPosition;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.services.MenuService;
import net.haesleinhuepf.clijx.incubator.services.SuggestionService;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Plugin;

@Plugin(type = IncubatorPlugin.class)
public class IncubatorStartingPoint extends AbstractIncubatorPlugin {

    public IncubatorStartingPoint(){
        super(new Copy());
    }

    int former_z = -1;
    int former_t = -1;
    int former_c = -1;

    @Override
    public void run(String arg) {
        Toolbar.addPlugInTool(new InteractiveWindowPosition());

        if (IJ.getImage().getStack() instanceof CLIJxVirtualStack) {
            IJ.error("This image is managed by CLIJx-Incubator already.");
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
        my_target.setFileInfo(my_source.getOriginalFileInfo());
        System.out.println("Fileinfo: ");
        System.out.println(my_target.getFileInfo());

        GenericDialog dialog = buildNonModalDialog(my_target.getWindow());
        if (dialog != null) {
            registerDialogAsNoneModal(dialog);
            //dialog.showDialog();
        }
        SuggestionService.getInstance();
    }

    ClearCLBuffer[] result = null;

    int former_refreshed_t = -1;
    public synchronized void refresh() {
        if (my_source.getT() == former_refreshed_t) {
            return;
        }
        former_refreshed_t = my_source.getT();

        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                result[i].close();
            }
        }
        result = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("CLIJx Image of " + my_source.getTitle());
        refreshView();
    }

    @Override
    public void imageUpdated(ImagePlus imp) {
        if (imp == my_source) {
            System.out.println("Source updated");
            if (imp.getT() != former_t) {
                System.out.println("Target invalidated");
                setTargetInvalid();

                former_t = imp.getT(); }

            if (imp.getZ() != former_z || imp.getC() != former_c) {
                System.out.println("Calling refresh view");
                refreshView();
                former_z = imp.getZ();
                former_c = imp.getC();
            }
        }
    }

    @Override
    public boolean canManage(CLIJMacroPlugin plugin) {
        return plugin instanceof Copy;
    }
}
