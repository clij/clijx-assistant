package net.haesleinhuepf.clijx.incubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorPlugin;

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

}
