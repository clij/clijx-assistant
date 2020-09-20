package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.cleasperanto.macro.api.ClEsperantoMacroAPIGenerator;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

public class ClEsperantoMacroGenerator implements ScriptGenerator {

    @Override
    public String push(AssistantGUIPlugin plugin) {
        ImagePlus source = plugin.getSource();
        String imageID = makeImageID(source);
        //makeImageID
        return ""+
                imageID + " = \"" + source.getTitle() + "\";\n" +
                "push(" + imageID + ");\n";
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        String imageID = makeImageID(result.getTarget());
        return "pull(" + imageID + ");\n";
    }

    @Override
    public String comment(String text) {
        return "// " + text.replace("\n", "\n// ") + "\n";
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "// " + AssistantUtilities.niceName(plugin.getClass().getName());
        }
        String methodName = clijMacroPlugin.getName();
        methodName = ClEsperantoMacroAPIGenerator.pythonize(methodName);

        String image1 = makeImageID(plugin.getSource());
        String image2 = makeImageID(plugin.getTarget());
        String program = "// " + AssistantUtilities.niceName(plugin.getName()) + "\n" +
                image1 + " = \"" + plugin.getSource().getTitle() + "\";\n" +
                image2 + " = \"" + plugin.getTarget().getTitle() + "\";\n";

        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name;
            program = program + name + " = " + objectToString(plugin.getArgs()[i]) + ";\n";
        }
        program = program + methodName + "(" + image1 + ", " + image2 + call + ");\n";
        //program = program + "Ext.CLIJ2_pull(" + image2 + "); // consider removing this line if you don't need to see that image\n";

        return program;
    }


    @Override
    public String fileEnding() {
        return ".ijm";
    }

    @Override
    public String header() {
        return  "// This is an experimentally generated ImageJ Macro using clEsperanto." +
                "// To make this script run in Fiji, please activate \n" +
                "// the clij and clij2 update sites in your Fiji \n" +
                "// installation. Furthermore add a custom update site:\n" +
                "// https://site.imagej.net/clincubator/\n\n" +
                "// Read more: \n" +
                "// https://clesperanto.github.io/\n" +
                "// https://clij.github.io/assistant/\n" +
                "\n";
    }

    @Override
    public String finish() {
        return "";
    }
}