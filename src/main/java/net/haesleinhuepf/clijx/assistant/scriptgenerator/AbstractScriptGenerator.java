package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.util.HashMap;

public abstract class AbstractScriptGenerator implements ScriptGenerator {
    protected HashMap<ImagePlus, String> image_map = new HashMap<>();
    public String makeImageID(ImagePlus target) {
        if (!image_map.keySet().contains(target)) {
            image_map.put(target, "image" + (image_map.size() + 1));
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

}
