package net.haesleinhuepf.clijx.assistant;

import ij.ImagePlus;
import ij.VirtualStack;
import net.haesleinhuepf.clij.clearcl.ClearCL;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.util.HashMap;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.distributionName;

public interface ScriptGenerator {

    String push(ImagePlus source);
    String pull(AssistantGUIPlugin result);

    String comment(String name);

    String execute(AssistantGUIPlugin plugin);

    String fileEnding();

    String header();

    String makeImageID(ImagePlus target);

    String[] makeImageIDs(AssistantGUIPlugin plugin);

    default String namesToCommaSeparated(String[] names) {
        String names_concat = "";
        for (int i = 0; i < names.length; i++) {
            if (i > 0) {
                names_concat = names_concat + ", ";
            }
            names_concat = names_concat + names[i];
        }
        return names_concat;
    }


    default String overview(AssistantGUIPlugin plugin) {
        return comment("Overview\n" + overview(plugin, 0));
    }

    default String overview(AssistantGUIPlugin plugin, int level) {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < level; i++) {
            text.append("  ");
        }
        text.append(" * " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName()) + " (" + distributionName(plugin.getClass()) + ")" + " \n");
        for (AssistantGUIPlugin child : AssistantGUIPluginRegistry.getInstance().getFollowers(plugin)) {
            text.append(overview(child, level + 1));
        }

        return text.toString();
    }

    String objectToString(Object arg);

    default String pythonize(String methodName) {
        methodName = methodName
                .replace("CLIJx_", "")
                .replace("CLIJ2_", "");

        return AssistantUtilities.niceName(methodName).trim()
                .toLowerCase()
                .replace(" ", "_");
    }

    default String getFilename(ImagePlus source)
    {
        String filename = "";
        if (source.getOriginalFileInfo() != null) {
            filename = source.getOriginalFileInfo().directory + source.getOriginalFileInfo().fileName;
        } else if (source.getFileInfo() != null) {
            filename = source.getFileInfo().directory + source.getFileInfo().fileName;
        }
        return filename.replace("\\", "/");
    }

    String finish(String all);

    String close(String image);

}
