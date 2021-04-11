package net.haesleinhuepf.clijx.assistant;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.plugins.Copy;
import net.haesleinhuepf.clijx.assistant.scriptgenerator.MacroGenerator;
import net.haesleinhuepf.clijx.gui.InteractiveWindowPosition;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.SuggestionService;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin(type = AssistantGUIPlugin.class)
public class AssistantGUIStartingPoint extends AbstractAssistantGUIPlugin {

    public AssistantGUIStartingPoint(){
        super(new Copy());
    }

    int former_z = -1;
    int former_t = -1;
    int former_c = -1;

    @Override
    public void run(String arg) {
        Toolbar.addPlugInTool(new InteractiveWindowPosition());

        if (IJ.getImage().getStack() instanceof CLIJxVirtualStack) {
            IJ.error("This image is managed by CLIJx-Assistant already.");
            return;
        }
        AssistantGUIPluginRegistry.getInstance().register(this);
        ImagePlus.addImageListener(this);

        ImagePlus imp = IJ.getImage();

        if (!new File(new MacroGenerator().getFilename(imp)).exists()) {
            String name_before = imp.getTitle();
            IJ.saveAs(imp, "tif", System.getProperty("java.io.tmpdir") + "/temp" + System.currentTimeMillis() + ".tif");
            imp.setTitle(name_before);
        }


        setSources(new ImagePlus[]{imp});
        former_t = imp.getT();
        former_c = imp.getC();
        former_z = imp.getZ();

        //setTarget(imp);

        //AssistantUtilities.stamp(CLIJxVirtualStack.imagePlusToBuffer(my_target));
        refresh();
        my_target.setFileInfo(my_sources[0].getOriginalFileInfo());
        System.out.println("Fileinfo: ");
        System.out.println(my_target.getFileInfo());

        GenericDialog dialog = buildNonModalDialog(my_target.getWindow());
        if (dialog != null) {
            registerDialogAsNoneModal(dialog);
            //dialog.showDialog();
        }
        SuggestionService.getInstance();
    }

    //ClearCLBuffer[] result = null;

    int former_refreshed_t = -1;
    public synchronized void refresh() {
        if (my_sources[0].getT() == former_refreshed_t) {
            return;
        }
        former_refreshed_t = my_sources[0].getT();

        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                result[i].close();
            }
        }
        result = CLIJxVirtualStack.imagePlusToBuffer(my_sources[0]);
        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));

        ClearCLBuffer input = CLIJ2.getInstance().create(result[0]);
        input.close();
        args = new Object[]{input, result[0]};

        my_target.setTitle("CLIJx Image of " + my_sources[0].getTitle());
        refreshView();
        enhanceContrast();
    }

    @Override
    public void imageUpdated(ImagePlus imp) {
        if (my_sources == null) {
            return;
        }
        if (imp == my_sources[0]) {
            //System.out.println("Source updated");
            if (imp.getT() != former_t) {
                //System.out.println("Target invalidated");
                setTargetInvalid();

                former_t = imp.getT(); }

            if (imp.getZ() != former_z || imp.getC() != former_c) {
                //System.out.println("Calling refresh view");
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

    public static AssistantGUIPlugin getCurrentPlugin() {
        ImagePlus imp = IJ.getImage();
        return AssistantGUIPluginRegistry.getInstance().getPlugin(imp);
    }
}
