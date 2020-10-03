package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;

public class MatlabGenerator extends JythonGenerator {

    @Override
    public String comment(String text) {
        return "% " + text.replace("\n", "\n% ") + "\n";
    }

    @Override
    public String push(ImagePlus source) {
        return pyToMatlab(super.push(source));
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {
        return pyToMatlab(super.execute(plugin));
    }

    @Override
    public String finish() {
        return pyToMatlab(super.finish());
    }

    @Override
    public String fileEnding() {
        return ".m.ijm";
    }

    @Override
    public String header() {
        return  "% To make this script run in Matlab, please install \n" +
                "% clatlab. Read more: https://clij.github.io/clatlab/\n\n" +
                "\n\n" +
                "% Init GPU\n" +
                "\n" +
                "% initialize CLATLAB\n" +
                "clij2 = init_clatlab();\n";
    }

    protected String pyToMatlab(String text) {
        return text.replace("# ", "% ").replace(")\n", ");\n");
    }
}
