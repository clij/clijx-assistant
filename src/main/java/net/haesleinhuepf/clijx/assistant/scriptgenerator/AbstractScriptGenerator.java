package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.util.HashMap;
import java.util.Locale;

public abstract class AbstractScriptGenerator implements ScriptGenerator {
    protected HashMap<ImagePlus, String> image_map = new HashMap<>();
    public String makeImageID(ImagePlus target) {
        if (!image_map.keySet().contains(target)) {
            String name = "image_" + (image_map.size() + 1);

            AssistantGUIPlugin pluginFromTargetImage = AbstractAssistantGUIPlugin.getPluginFromTargetImage(target);
            if (pluginFromTargetImage != null) {
                name = "image_" + AssistantUtilities.niceName(pluginFromTargetImage.getName()).toLowerCase().replace(" ", "_") + "_" + (image_map.size() + 1);
            }

            image_map.put(target, name);
        }

        return image_map.get(target);
    }

    public String[] makeImageIDs(AssistantGUIPlugin plugin) {
        String[] result = new String[plugin.getNumberOfSources()];
        for (int s = 0; s < plugin.getNumberOfSources(); s++) {
            ImagePlus source = plugin.getSource(s);
            result[s] = makeImageID(source);
        }
        return result;
    }

    public String objectToString(Object arg) {

        if (arg instanceof ClearCLBuffer[][]) {
            ClearCLBuffer[][] arrgs = (ClearCLBuffer[][]) arg;
            for (ClearCLBuffer[] arrg : arrgs) {
                ClearCLBuffer[] imagelist = (ClearCLBuffer[]) arrg;
                for (ClearCLBuffer buffer : imagelist) {
                    String str = objectToString(buffer);
                    if (str != null) {
                        return str;
                    }
                }
            }
            return null;
        } else if (arg instanceof ClearCLBuffer[]) {
            ClearCLBuffer[] imagelist = (ClearCLBuffer[]) arg;
            for (ClearCLBuffer buffer : imagelist) {
                String str = objectToString(buffer);
                if (str != null) {
                    return str;
                }
            }
            return null;
        } else if (arg instanceof ClearCLBuffer) {
            for (ImagePlus image : image_map.keySet()) {
                if (image != null && image.getStack() != null && image.getStack() instanceof CLIJxVirtualStack) {
                    CLIJxVirtualStack cvs = (CLIJxVirtualStack) image.getStack();
                    for (int c = 0; c < cvs.getNumberOfChannels(); c++) {
                        if (arg == cvs.getBuffer(c)) {
                            return objectToString(image);
                        }
                    }
                }
            }
            return null;
        } else if (arg instanceof ImagePlus) {
            return makeImageID((ImagePlus)arg);
        } else if (arg instanceof String) {
            return "\"" + arg + "\"";
        } else {
            return "" + arg;
        }
    }


    @Override
    public String finish(String all ) {
        String output = all;

        for (ImagePlus imp : image_map.keySet()) {
            String name = image_map.get(imp);
            String replace_with = close(name);
            String search_for = replace_with.replace("(", "\\(").replace(")", "\\)");

            System.out.println("Splitting by " + search_for);
            if (search_for.length() == 0) {
                System.out.println("Leave 1");
                continue;
            }

            String[] temp = output.split(search_for);
            if (temp.length == 1) {
                System.out.println("Leave 2");
                continue;
            }
            output = "";
            for (int i = 0; i < temp.length; i++) {
                if (i == temp.length - 1) {
                    output = output + replace_with;
                }
                output = output + temp[i];
            }
        }

        output = output.replace("\n;\n", "\n\n");
        while (output.contains("\n\n\n")) {
            output = output.replace("\n\n\n", "\n\n");
        }
        return output;
    }
}
