package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

import java.util.Random;

public class HumanReadibleProtocolGenerator implements ScriptGenerator {

    Random random = new Random();

    @Override
    public String push(ImagePlus source) {
        String image1 = makeImageID(source);

        String output =
                    "We work with the image \"" + source.getTitle() + "\" for simplicity, we call it " + image1 + ".\n";

        return output;
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        return "";
    }

    @Override
    public String comment(String name) {
        return name + "\n";
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {
        String[] startings = {"Then, ", "Afterwards, ", "Following, ", "As the next step "};
        String[] image1s = makeImageIDs(plugin);
        String image2 = makeImageID(plugin.getTarget());

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();

        String text = "\n" + startings[random.nextInt(startings.length)] +
                "we applied \"" +
                AssistantUtilities.niceName(plugin.getName()) + "\"";

        if (clijMacroPlugin instanceof HasAuthor) {
            text = text +
                    ", a CLIJ plugin programmed by " + ((HasAuthor) clijMacroPlugin).getAuthorName() + ", ";
        }

        text = text +
                " on " + namesToCommaSeparated(image1s) +
                " and got a new image out, \n" + image2 + ", also titled \"" + plugin.getTarget().getTitle() + "\".\n";

        String[] midparts = {"In order to do so, ", "Therefore, ", "In detail, ", "While doing that, "};

        if (clijMacroPlugin != null) {
            String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");

            if (parameters.length > 2) {
                text = text + midparts[random.nextInt(midparts.length)] +
                        " we used the parameter" + ((parameters.length > 3) ? "s" : "") + " ";

                for (int i = 2; i < parameters.length; i++) {
                    String temp[] = parameters[i].trim().split(" ");
                    String name = temp[temp.length - 1];
                    if (i > 2) {
                        if (i == parameters.length - 1) {
                            text = text + " and ";
                        } else {
                            text = text + ", ";
                        }
                    }
                    text = text + name + " = " + plugin.getArgs()[i] + "";
                }
                text = text + ".";
            }
        }
        return text;
    }



    @Override
    public String fileEnding() {
        return ".txt";
    }

    @Override
    public String header() {
        return  "This protocol documents an image processing workflow using CLIJx-Incubator.\n" +
                "Read more about it online: https://clij.github.io/incubator/ \n\n";
    }

    @Override
    public String finish() {
        return "";
    }
}
