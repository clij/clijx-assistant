package net.haesleinhuepf.clijx.incubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorPlugin;

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
    public String execute(IncubatorPlugin plugin) {
        return pyToMatlab(super.execute(plugin));
    }

    @Override
    public String fileEnding() {
        return ".m";
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
