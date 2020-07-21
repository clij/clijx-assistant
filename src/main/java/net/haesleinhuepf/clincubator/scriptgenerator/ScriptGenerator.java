package net.haesleinhuepf.clincubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clincubator.utilities.IncubatorPlugin;

public interface ScriptGenerator {

    String push(ImagePlus source);

    String comment(String name);

    String execute(IncubatorPlugin plugin);

    String fileEnding();

    String header();
}
