package net.haesleinhuepf.clijx.assistant;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

import java.util.HashMap;

public interface ScriptGenerator {

    String push(AssistantGUIPlugin source);
    String pull(AssistantGUIPlugin result);

    String comment(String name);

    String execute(AssistantGUIPlugin plugin);

    String fileEnding();

    String header();

    HashMap<ImagePlus, String> image_map = new HashMap<>();
    default String makeImageID(ImagePlus target) {
        if (!image_map.keySet().contains(target)) {
            image_map.put(target, "image" + (image_map.size() + 1));
        }

        return image_map.get(target);
    }

    default String overview(AssistantGUIPlugin plugin) {
        return comment("Overview\n" + overview(plugin, 0));
    }

    default String overview(AssistantGUIPlugin plugin, int level) {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < level; i++) {
            text.append("  ");
        }
        text.append(" * " + AssistantUtilities.niceName(plugin.getName()) + " \n");
        for (AssistantGUIPlugin child : AssistantGUIPluginRegistry.getInstance().getFollowers(plugin)) {
            text.append(overview(child, level + 1));
        }

        return text.toString();
    }

    default String objectToString(Object arg) {
        if (arg instanceof String) {
            return "\"" + arg + "\"";
        } else {
            return arg.toString();
        }
    }

    default String pythonize(String methodName) {
        return AssistantUtilities.niceName(methodName).trim()
                .toLowerCase()
                .replace(" ", "_")
                .replace("clij2_", "")
                .replace("clij_", "")
                .replace("clijx_", "")
                ;
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

    String finish();

}
