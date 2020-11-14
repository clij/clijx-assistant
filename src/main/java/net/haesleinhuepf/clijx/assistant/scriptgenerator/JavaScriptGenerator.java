package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import org.scijava.util.VersionUtils;

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
    public String execute(AssistantGUIPlugin plugin) {
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
                "// Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "importClass(Packages.ij.IJ);\n" +
                "importClass(Packages.ij.WindowManager);\n" +
                "importClass(Packages.net.haesleinhuepf.clijx.CLIJx);\n\n" +
                "// Init GPU\n" +
                "clijx = CLIJx.getInstance();\n";
    }

    @Override
    public String finish(String all) {
        return pyToJs(super.finish(all));
    }

    protected String pyToJs(String text) {
        return text.replace("#", "//").replace(")\n", ");\n").replace("  \n", ";\n").replace("[","new long[]{").replace("]", "}");
    }
}
