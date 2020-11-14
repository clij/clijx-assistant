package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import org.scijava.util.VersionUtils;

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
    public String pull(AssistantGUIPlugin result) {
        String image1 = makeImageID(result.getTarget());

        return "// pull result back from GPU\n" +
                "output = clij2.pullSequence(" + image1 + ");\n" +
                close(image1) + ";\n" +
                "\n" +
                "// Show result\n" +
                "Icy.addSequence(output);\n";
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
                "// Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "importClass(Packages.icy.sequence.Sequence);\n" +
                "importClass(net.haesleinhuepf.clicy.CLICY);\n" +
                "importClass(Packages.icy.main.Icy);\n\n" +
                "// Init GPU\n" +
                "clijx = CLICY.getInstance();\n\n";
    }

    @Override
    public String finish(String all) {
        return pyToJs(super.finish(all));
    }

    protected String pyToJs(String text) {
        return text.
                replace("#", "//").
                replace(")\n", ");\n");
    }
}
