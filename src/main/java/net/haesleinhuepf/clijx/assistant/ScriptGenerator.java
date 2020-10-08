package net.haesleinhuepf.clijx.assistant;

import ij.ImagePlus;
import ij.VirtualStack;
import net.haesleinhuepf.clij.clearcl.ClearCL;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.util.HashMap;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.distributionName;

public interface ScriptGenerator {

    String push(ImagePlus source);
    String pull(AssistantGUIPlugin result);

    String comment(String name);

    String execute(AssistantGUIPlugin plugin);

    String fileEnding();

    String header();

    HashMap<ImagePlus, String> image_map = new HashMap<>();
    default String makeImageID(ImagePlus target) {
        if (!image_map.keySet().contains(target)) {
            image_map.put(target, "image" + (image_map.size() + 1));
        }

        return image_map.get(target);
    }

    default String[] makeImageIDs(AssistantGUIPlugin plugin) {
        String[] result = new String[plugin.getNumberOfSources()];
        for (int s = 0; s < plugin.getNumberOfSources(); s++) {
            ImagePlus source = plugin.getSource(s);
            result[s] = makeImageID(source);
        }
        return result;
    }

    default String namesToCommaSeparated(String[] names) {
        String names_concat = "";
        for (int i = 0; i < names.length; i++) {
            if (i > 0) {
                names_concat = names_concat + ", ";
            }
            names_concat = names_concat + names[i];
        }
        return names_concat;
    }


    default String overview(AssistantGUIPlugin plugin) {
        return comment("Overview\n" + overview(plugin, 0));
    }

    default String overview(AssistantGUIPlugin plugin, int level) {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < level; i++) {
            text.append("  ");
        }
        text.append(" * " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName()) + " (" + distributionName(plugin.getClass()) + ")" + " \n");
        for (AssistantGUIPlugin child : AssistantGUIPluginRegistry.getInstance().getFollowers(plugin)) {
            text.append(overview(child, level + 1));
        }

        return text.toString();
    }

    default String objectToString(Object arg) {

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
                if (image.getStack() instanceof CLIJxVirtualStack) {
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
            return arg.toString();
        }
    }

    default String pythonize(String methodName) {
        methodName = methodName
                .replace("CLIJx_", "")
                .replace("CLIJ2_", "");

        return AssistantUtilities.niceNameWithoutDimShape(methodName).trim()
                .toLowerCase()
                .replace(" ", "_");
    }

    default String getFilename(ImagePlus source)
    {
        String filename = "";
        if (source.getOriginalFileInfo() != null) {
            filename = source.getOriginalFileInfo().directory + source.getOriginalFileInfo().fileName;
        } else if (source.getFileInfo() != null) {
            filename = source.getFileInfo().directory + source.getFileInfo().fileName;
        }
        return filename.replace("\\", "/");
    }

    String finish();

}
