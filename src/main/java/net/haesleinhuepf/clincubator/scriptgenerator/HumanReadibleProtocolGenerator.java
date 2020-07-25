package net.haesleinhuepf.clincubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clincubator.utilities.IncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.IncubatorUtilities;

import java.util.HashMap;
import java.util.Random;

public class HumanReadibleProtocolGenerator implements ScriptGenerator {

    Random random = new Random();

    @Override
    public String push(ImagePlus source) {
        String image1 = makeImageID(source);

        return ""+
                "We start by processing the image \"" + source.getTitle() + "\" for simplicity, we call it " + image1 + ".\n";
    }

    @Override
    public String comment(String name) {
        return "# " + name + "\n";
    }

    @Override
    public String execute(IncubatorPlugin plugin) {
        String[] startings = {"Then, ", "Afterwards, ", "Following, ", "As the next step "};
        String image1 = makeImageID(plugin.getSource());
        String image2 = makeImageID(plugin.getTarget());

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();

        String text = "\n" + startings[random.nextInt(startings.length)] +
                "we applied \"" +
                IncubatorUtilities.niceName(plugin.getClass().getSimpleName()) + "\"";

        if (clijMacroPlugin instanceof HasAuthor) {
            text = text +
                    ", a CLIJ plugin programmed by " + ((HasAuthor) clijMacroPlugin).getAuthorName() + ", ";
        }

        text = text +
                " on " + image1 +
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
            }
            text = text + ".";
        }
        return text;
    }



    @Override
    public String fileEnding() {
        return ".txt";
    }

    @Override
    public String header() {
        return  "This protocol documents and image processing workflow using CLIncubator.\n" +
                "Read more about it online: https://clij.github.io/clincubator/";
    }
}
