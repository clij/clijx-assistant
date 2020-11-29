package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.clesperanto.macro.api.ClEsperantoMacroAPIGenerator;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.scijava.util.VersionUtils;

public class ClEsperantoMacroGenerator extends AbstractScriptGenerator {

    @Override
    public String push(ImagePlus source) {
        String output = "";
        String imageID = makeImageID(source);
        //makeImageID
        output = output +
                imageID + " = \"" + source.getTitle() + "\";\n" +
                "push(" + imageID + ");\n\n";

        return output;
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        String imageID = makeImageID(result.getTarget());
        return "pull(" + imageID + ");\n" +
                (AssistantUtilities.resultIsLabelImage(result)?"run(\"glasbey_on_dark\");\n":"") +
                close(imageID) + "\n\n";
    }

    @Override
    public String comment(String text) {
        return "// " + text.replace("\n", "\n// ") + "\n";
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "// " + AssistantUtilities.niceNameWithoutDimShape(plugin.getClass().getName());
        }
        String methodName = clijMacroPlugin.getName();
        methodName = ClEsperantoMacroAPIGenerator.pythonize(methodName);

        String[] image1s = makeImageIDs(plugin);
        String image2 = makeImageID(plugin.getTarget());
        String program = "// " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName()) + "\n";

        for (int s = 0; s < plugin.getNumberOfSources(); s++) {
            program = program +
                    image1s[s] + " = \"" + plugin.getSource(s).getTitle() + "\";\n";
        }
        program = program +
                image2 + " = \"" + plugin.getTarget().getTitle() + "\";\n";

        String call = "";
        String after_call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        /*for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name;

            program = program + name + " = " + objectToString(plugin.getArgs()[i]) + ";\n";
        }*/
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
        //program = program + methodName + "(" + namesToCommaSeparated(image1s) + ", " + image2 + call + ");\n";
        //program = program + "Ext.CLIJ2_pull(" + image2 + "); // consider removing this line if you don't need to see that image\n";

        return program;
    }


    @Override
    public String fileEnding() {
        return ".ijm";
    }

    @Override
    public String header() {
        return  "// This is an experimentally generated ImageJ Macro using clEsperanto.\n" +
                "// To make this script run in Fiji, please activate \n" +
                "// the clij, clij2, clijx-assistant update sites in your Fiji \n" +
                "// installation. Before executing it, activate 'clEspereanto Macro' in\n" +
                "// the language menu of the Script Editor.\n" +
                "// Read more: \n" +
                "// https://clesperanto.github.io/\n" +
                "// https://clij.github.io/assistant/\n" +
                "\n" +
                "// Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n";
    }

    @Override
    public String close(String image) {
        return "release(" + image + ");";
    }
}
