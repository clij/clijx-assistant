package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import org.scijava.util.VersionUtils;

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
                "// Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "import ij.IJ;\n" +
                "import ij.WindowManager;\n" +
                "import net.haesleinhuepf.clijx.CLIJx;\n\n" +
                "// Init GPU\n" +
                "clijx = CLIJx.getInstance();\n\n";
    }
}
