package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import org.scijava.util.VersionUtils;

public class ClEsperantoCamelJythonGenerator extends JythonGenerator {

    @Override
    public String header() {
        return  "# To make this script run in Fiji, please activate the clij, " +
                "# clij2 and clijx-assistant update sites in your Fiji. \n" +
                "# Read more: \n" +
                "# https://clij.github.io/assistant\n\n" +
                "# https://clesperanto.net\n" +
                "# \n" +
                "# Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "# \n" +
                "import net.clesperanto.javaprototype.Camel as cle\n" +
                "from ij import IJ\n\n";
    }

    @Override
    public String finish(String all) {
        return super.finish(all)
                .replace("clijx.", "cle.")
                .replace("clij2.", "cle.");
    }
}
