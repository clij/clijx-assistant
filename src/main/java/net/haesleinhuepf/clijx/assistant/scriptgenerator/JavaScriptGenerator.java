package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;

public class JavaScriptGenerator extends JythonGenerator {

    @Override
    public String comment(String text) {
        return "// " + text.replace("\n", "\n// ") + "\n";
    }

    @Override
    public String push(AssistantGUIPlugin plugin) {
        return pyToJs(super.push(plugin));
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
