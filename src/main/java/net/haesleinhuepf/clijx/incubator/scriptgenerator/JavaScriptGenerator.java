package net.haesleinhuepf.clijx.incubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorPlugin;

public class JavaScriptGenerator extends JythonGenerator {

    @Override
    public String comment(String text) {
        return "// " + text.replace("\n", "\n// ") + "\n";
    }

    @Override
    public String push(ImagePlus source) {
        return pyToJs(super.push(source));
    }

    @Override
    public String execute(IncubatorPlugin plugin) {
        return pyToJs(super.execute(plugin));
    }

    @Override
    public String fileEnding() {
        return ".js";
    }

    @Override
    public String header() {
        return  "// To make this script run in Fiji, please activate \n" +
                "// the clij and clij2 update sites in your Fiji \n" +
                "// installation. Read more: https://clij.github.io\n\n" +
                "\n\n" +
                "importClass(Packages.ij.IJ);\n" +
                "importClass(Packages.ij.WindowManager);\n" +
                "importClass(Packages.net.haesleinhuepf.clijx.CLIJx);\n\n" +
                "// Init GPU\n" +
                "clijx = CLIJx.getInstance();\n";
    }

    protected String pyToJs(String text) {
        return text.replace("#", "//").replace(")\n", ");\n").replace("  \n", ";\n");
    }
}
