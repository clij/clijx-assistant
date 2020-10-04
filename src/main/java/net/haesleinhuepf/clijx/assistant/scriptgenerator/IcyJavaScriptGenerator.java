package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;

public class IcyJavaScriptGenerator extends JythonGenerator {


    @Override
    public String comment(String text) {
        return "// " + text.replace("\n", "\n// ") + "\n";
    }

    @Override
    public String push(ImagePlus source) {
        String program =
                "// get current image from Icy\n" +
                "sequence = getSequence();\n" +
                "// push image to GPU\n" +
                makeImageID(source) + " = clijx.pushSequence(sequence);\n\n";

        return program;
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
        return  "// To make this script run in Icy Bioimaging, please install the clIcy plugin in Icy first. \n" +
                "// Read more: http://icy.bioimageanalysis.org/plugin/clicy-blocks/\n\n" +
                "importClass(Packages.icy.sequence.Sequence);\n" +
                "importClass(net.haesleinhuepf.clicy.CLICY);\n" +
                "importClass(Packages.icy.main.Icy);\n\n" +
                "// Init GPU\n" +
                "clijx = CLICY.getInstance();\n\n";
    }


    @Override
    public String finish() {
        return pyToJs(super.finish());
    }

    protected String pyToJs(String text) {
        return text.
                replace("#", "//").
                replace(")\n", ");\n");
    }
}
