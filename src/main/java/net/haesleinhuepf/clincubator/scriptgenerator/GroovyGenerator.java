package net.haesleinhuepf.clincubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clincubator.utilities.IncubatorPlugin;

public class GroovyGenerator extends JavaScriptGenerator {

    @Override
    public String fileEnding() {
        return ".groovy";
    }

    @Override
    public String header() {
        return  "// To make this script run in Fiji, please activate \n" +
                "// the clij and clij2 update sites in your Fiji \n" +
                "// installation. Read more: https://clij.github.io\n\n" +
                "\n\n" +
                "import ij.IJ;\n" +
                "import ij.WindowManager;\n" +
                "import net.haesleinhuepf.clijx.CLIJx;\n\n" +
                "// Init GPU\n" +
                "clijx = CLIJx.getInstance();\n";
    }
}
