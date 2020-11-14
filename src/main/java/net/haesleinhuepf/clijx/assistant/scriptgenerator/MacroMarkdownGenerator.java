package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.annotation.ClassificationClass;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.scijava.util.VersionUtils;

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

        makeImageIDs(plugin);
        makeImageID(plugin.getTarget());
        String program = "\n/*\n" +
                "## " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName()) + "\n";

        //for (int s = 0; s < plugin.getNumberOfSources(); s++) {
        //    program = program +
        //            "" + image1s[s] + ": \"" + plugin.getSource(s).getTitle() + "\";\n";
        //}
        program = program +
        //        "" + image2 + ": \"" + plugin.getTarget().getTitle() + "\"\n" +
                "*/\n";

        String call = "";
        String after_call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 0; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            if (i > 0) {
                call = call + ", ";
            }

            if (plugin.getArgs()[i] instanceof ClearCLBuffer ||
                    plugin.getArgs()[i] instanceof ClearCLBuffer[] ||
                    plugin.getArgs()[i] instanceof ClearCLBuffer[][] ||
                    plugin.getArgs()[i] instanceof ImagePlus
            ) {

                String image_id = objectToString(plugin.getArgs()[i]);
                if (image_id == null && i < plugin.getNumberOfSources()) {
                    image_id = objectToString(plugin.getSource(i));
                }
                call = call + image_id;
                after_call = after_call + close(image_id) + "\n";
            } else {
                call = call + name;
                program = program + name + " = " + objectToString(plugin.getArgs()[i]) + ";\n";
            }
        }
        program = program + methodName + "(" + call + ");\n" +
                after_call + "";
        //program = program + "Ext.CLIJ2_pull(" + image2 + "); // consider removing this line if you don't need to see that image\n";

        return program;
    }

    @Override
    public String header() {
        return  "/*\n" +
                "To make this script run in Fiji, please activate \n" +
                "the clij, clij2 and IJMMD update sites in your Fiji \n" +
                "installation. Read more: https://clij.github.io\n\n" +
                "Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n" +
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
