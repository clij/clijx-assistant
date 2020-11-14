package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.scijava.util.VersionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HumanReadibleProtocolGenerator extends AbstractScriptGenerator {

    Random random = new Random();

    @Override
    public String push(ImagePlus source) {
        String image1 = makeImageID(source);

        String output =
                    "We start our image data flow with " + image1 + ".\n";

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
                ", and got a new image out, " + image2 + ".\n";

        String[] midparts = {"In order to do so, ", "Therefore, ", "In detail, ", "While doing that, "};

        if (clijMacroPlugin != null) {
            String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");

            boolean first_parameter_set = false;
            for (int i = 0; i < parameters.length; i++) {
                if (! (
                        plugin.getArgs()[i] instanceof ClearCLBuffer ||
                                plugin.getArgs()[i] instanceof ClearCLBuffer[]
                ) ) {
                    String temp[] = parameters[i].trim().split(" ");
                    String name = temp[temp.length - 1];
                    if (first_parameter_set) {
                        if (i == parameters.length - 1) {
                            text = text + " and ";
                        } else {
                            text = text + ", ";
                        }
                    } else {
                        text = text + midparts[random.nextInt(midparts.length)] +
                                "we used the parameters ";
                    }
                    first_parameter_set = true;
                    text = text + name + " = " + plugin.getArgs()[i] + "";
                }
            }
            if (first_parameter_set) {
                text = text + ".\n";
            }

        }
        return insertLineBreaks(text, 65);
    }

    private String insertLineBreaks(String text, int max_line_length) {
        StringBuilder builder = new StringBuilder();
        int line_length = 0;
        for (String entry : text.split(" ")) {
            line_length = line_length + entry.length() + 1;
            if (line_length > max_line_length) {
                builder.append("\n");
                line_length = 0;
            } else {
                builder.append(" ");
            }
            if (entry.contains("\n")) {
                line_length = 0;
            }
            builder.append(entry);
        }

        return builder.toString();
    }

    public String namesToCommaSeparated(String[] names) {
        String names_concat = "";
        for (int i = 0; i < names.length; i++) {
            if (i > 0) {
                if ( i < names.length - 1) {
                    names_concat = names_concat + ", ";
                } else {
                    names_concat = names_concat + " and ";
                }
            }
            names_concat = names_concat + names[i];
        }
        return names_concat;
    }


    @Override
    public String fileEnding() {
        return ".txt";
    }

    @Override
    public String header() {
        return  "This protocol documents an image data flow using CLIJx-Assistant.\n" +
                "Read more about it online: https://clij.github.io/assistant/ \n\n" +
                "Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n";
    }

    @Override
    public String finish(String all) {

        StringBuilder builder = new StringBuilder();
        builder.append(all);
        builder.append("\nUsed images");

        ArrayList<String> names = new ArrayList<>();
        for (ImagePlus imp : image_map.keySet()) {
            String name = image_map.get(imp);
            names.add("\n * " + name + ": " + imp.getTitle());
        }

        Collections.sort(names);

        for (String name : names) {
            builder.append(name);
        }

        return builder.toString();
    }

    @Override
    public String close(String image) {
        return "";
    }
}
