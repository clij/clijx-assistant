package net.haesleinhuepf.clijx.incubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.incubator.ScriptGenerator;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;

public class MacroGenerator implements ScriptGenerator {

    @Override
    public String push(ImagePlus source) {
        return ""+
                "image1 = \"" + source.getTitle() + "\";\n" +
                "Ext.CLIJ2_push(image1);\n";
    }

    @Override
    public String comment(String text) {
        return "// " + text.replace("\n", "\n// ") + "\n";
    }

    @Override
    public String execute(IncubatorPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "// " + IncubatorUtilities.niceName(plugin.getClass().getSimpleName());
        }
        String methodName = clijMacroPlugin.getClass().getSimpleName();
        methodName = methodName.substring(0,1).toLowerCase() + methodName.substring(1);
        String pakage = clijMacroPlugin.getClass().getPackage().getName();
        if (pakage.contains(".clij2")) {
            methodName = "Ext.CLIJ2_" + methodName;
        } else if (pakage.contains(".clijx")) {
            methodName = "Ext.CLIJx_" + methodName;
        } else {
            methodName = "Ext.CLIJ_" + methodName;
        }

        String image1 = makeImageID(plugin.getSource());
        String image2 = makeImageID(plugin.getTarget());
        String program = "// " + IncubatorUtilities.niceName(plugin.getClass().getSimpleName()) + "\n" +
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
}
