package net.haesleinhuepf.clijx.incubator.scriptgenerator;

import ij.ImageListener;
import ij.ImagePlus;
import net.haesleinhuepf.clij.converters.implementations.ImagePlusToClearCLBufferConverter;
import net.haesleinhuepf.clijx.incubator.ScriptGenerator;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;

public class MacroGenerator implements ScriptGenerator {

    @Override
    public String push(IncubatorPlugin plugin) {
        ImagePlus source = plugin.getSource();
        String imageID = makeImageID(source);
        //makeImageID
        return ""+
                imageID + " = \"" + source.getTitle() + "\";\n" +
                "Ext.CLIJ2_push(" + imageID + ");\n";
    }

    @Override
    public String pull(IncubatorPlugin result) {
        String imageID = makeImageID(result.getTarget());
        return "Ext.CLIJ2_pull(" + imageID + ");\n";
    }

    @Override
    public String comment(String text) {
        return "// " + text.replace("\n", "\n// ") + "\n";
    }

    @Override
    public String execute(IncubatorPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "// " + IncubatorUtilities.niceName(plugin.getClass().getName());
        }
        String methodName = clijMacroPlugin.getName();
        methodName = "Ext." + methodName;

        String image1 = makeImageID(plugin.getSource());
        String image2 = makeImageID(plugin.getTarget());
        String program = "// " + IncubatorUtilities.niceName(plugin.getName()) + "\n" +
                "// " + image1 + " = \"" + plugin.getSource().getTitle() + "\";\n" +
                "// " + image2 + " = \"" + plugin.getTarget().getTitle() + "\";\n";

        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name;
            program = program + name + " = " + plugin.getArgs()[i] + ";\n";
        }
        program = program + methodName + "(" + image1 + ", " + image2 + call + ");\n";
        program = program + "Ext.CLIJ2_pull(" + image2 + "); // consider removing this line if you don't need to see that image\n";

        return program;
    }

    @Override
    public String fileEnding() {
        return ".ijm";
    }

    @Override
    public String header() {
        return  "// To make this script run in Fiji, please activate \n" +
                "// the clij and clij2 update sites in your Fiji \n" +
                "// installation. Read more: https://clij.github.io\n\n" +
                "// Init GPU\n" +
                "run(\"CLIJ2 Macro Extensions\", \"cl_device=\");\n\n";
    }

    @Override
    public String finish() {
        return "";
    }
}
