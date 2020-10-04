package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

public class MacroMarkdownGenerator extends MacroGenerator {
    @Override
    public String execute(AssistantGUIPlugin plugin) {
        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "/*\n" +
                    "## " + AssistantUtilities.niceNameWithoutDimShape(plugin.getClass().getName()) + "\n*/\n";
        }
        String methodName = clijMacroPlugin.getName();
        methodName = "Ext." + methodName;

        String[] image1s = makeImageIDs(plugin);
        String image2 = makeImageID(plugin.getTarget());
        String program = "/*\n" +
                "## " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName()) + "\n";

        for (int s = 0; s < plugin.getNumberOfSources(); s++) {
            program = program +
                    "" + image1s[s] + ": \"" + plugin.getSource(s).getTitle() + "\";\n";
        }
        program = program +
                "" + image2 + ": \"" + plugin.getTarget().getTitle() + "\"\n" +
                "*/\n";

        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name;
            program = program + name + " = " + objectToString(plugin.getArgs()[i]) + ";\n";
        }
        program = program + methodName + "(" + namesToCommaSeparated(image1s) + ", " + image2 + call + ");\n";
        //program = program + "Ext.CLIJ2_pull(" + image2 + "); // consider removing this line if you don't need to see that image\n";

        return program;
    }

    @Override
    public String header() {
        return  "/*\n" +
                "To make this script run in Fiji, please activate \n" +
                "the clij, clij2 and IJMMD update sites in your Fiji \n" +
                "installation. Read more: https://clij.github.io\n" +
                "*/\n" +
                "// Init GPU\n" +
                "run(\"CLIJ2 Macro Extensions\", \"cl_device=\");\n\n";
    }

    @Override
    public String overview(AssistantGUIPlugin plugin) {
        return "/*\n" +
                super.overview(plugin).replace("// ", "") +
                "*/\n";
    }
}
