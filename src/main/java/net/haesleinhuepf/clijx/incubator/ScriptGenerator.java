package net.haesleinhuepf.clijx.incubator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.incubator.IncubatorPluginRegistry;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;

import java.util.HashMap;

public interface ScriptGenerator {

    String push(ImagePlus source);

    String comment(String name);

    String execute(IncubatorPlugin plugin);

    String fileEnding();

    String header();

    HashMap<ImagePlus, String> image_map = new HashMap<>();
    default String makeImageID(ImagePlus target) {
        if (!image_map.keySet().contains(target)) {
            image_map.put(target, "image" + (image_map.size() + 1));
        }

        return image_map.get(target);
    }

    default String overview(IncubatorPlugin plugin) {
        return comment("Overview\n" + overview(plugin, 0));
    }

    default String overview(IncubatorPlugin plugin, int level) {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < level; i++) {
            text.append("  ");
        }
        text.append(" * " + IncubatorUtilities.niceName(plugin.getClass().getSimpleName()) + " \n");
        for (IncubatorPlugin child : IncubatorPluginRegistry.getInstance().getFollowers(plugin)) {
            text.append(overview(child, level + 1));
        }

        return text.toString();
    }
}
