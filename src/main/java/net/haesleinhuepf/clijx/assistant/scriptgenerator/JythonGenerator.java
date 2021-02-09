package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.scijava.util.VersionUtils;

public class JythonGenerator extends AbstractScriptGenerator {

    protected boolean clijx_used = false;

    @Override
    public String push(ImagePlus source) {
        String output = "";

        String filename = getFilename(source);


        if (filename != null && filename.length() > 0) {
            output = output + "" +
                    "# Load image from disc \n" +
                    "imp = IJ.openImage(\"" + filename + "\")\n" +
                    "# Push " + source.getTitle() + " to GPU memory\n" +
                    makeImageID(source) + " = clijx.push(imp);\n\n";
        } else {
            output = output +
                    "# Push " + source.getTitle() + " to GPU memory\n" +
                    makeImageID(source) + " = clijx.push(WindowManager.getImage(\"" + source.getTitle() + "\"));\n\n";
        }

        return output;
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        String image1 = makeImageID(result.getTarget());

        return "" +
                "result = clijx.pull(" + image1 + ")\n" +
                "result.setDisplayRange(" + result.getTarget().getDisplayRangeMin() + ", " + result.getTarget().getDisplayRangeMax() + ")\n" +
                (AssistantUtilities.resultIsLabelImage(result)?"IJ.run(result, \"glasbey_on_dark\", \"\")\n":"") +
                "result.show()\n" +
                close(image1) + "\n\n";


    }

    @Override
    public String comment(String text) {
        return "# " + text.replace("\n", "\n# ") + "\n";
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "# " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName());
        }
        String methodName = clijMacroPlugin.getName();
        if (methodName.contains("CLIJx")) {
            clijx_used = true;
        }
        methodName = methodName.replace("CLIJ2_", "").replace("CLIJx_", "");
        methodName = methodName.substring(0,1).toLowerCase() + methodName.substring(1);
        //String pakage = clijMacroPlugin.getClass().getPackage().getName();

        methodName = "clijx." + pythonize(methodName);


        String[] image1s = makeImageIDs(plugin);
        String image2 = makeImageID(plugin.getTarget());
        String program = "# " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName()) + "\n";

        ImagePlus target = plugin.getTarget();
        ImagePlus source = plugin.getSource(0);

        if (target != null) {
            if (source != null &&
                            target.getWidth() == source.getWidth() &&
                            target.getHeight() == source.getHeight() &&
                            target.getNSlices() == source.getNSlices()
            ) {
                if (target.getBitDepth() == source.getBitDepth()) {
                    program = program +
                            image2 + " = clijx.create(" + image1s[0] + ")\n";
                } else {
                    program = program +
                            image2 + " = clijx.create(" + image1s[0] + ".getDimensions(), clijx." + bitDepthToType(target.getBitDepth()) + " )\n";
                }
            } else if (target.getNSlices() > 1) {
                program = program +
                        image2 + " = clijx.create([" + target.getWidth() + ", " + target.getHeight() + ", " + target.getNSlices() + "], clijx." + bitDepthToType(target.getBitDepth()) + ")\n";
            } else {
                program = program +
                        image2 + " = clijx.create([" + target.getWidth() + ", " + target.getHeight() + "], clijx." + bitDepthToType(target.getBitDepth()) + ")\n";
            }
        }
        String call = "";
        String after_call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 0; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            //call = call + ", " + name;
            //program = program + name + " = " + objectToString(plugin.getArgs()[i]) + ";\n";
            if (i > 0) {
                call = call + ", ";
            }
            if (plugin.getArgs()[i] instanceof ClearCLBuffer || plugin.getArgs()[i] instanceof ClearCLBuffer[]) {
                String image_id = objectToString(plugin.getArgs()[i]);
                if (image_id == null && i < plugin.getNumberOfSources()) {
                    image_id = objectToString(plugin.getSource(i));
                }
                call = call + image_id;
                after_call = after_call + close(image_id) + "\n";
            } else {
                call = call + name;
                program = program + name + " = " + objectToString(plugin.getArgs()[i]) + "\n";
            }
        }
        program = program + methodName + "(" + call + ")\n" +
                after_call;

        //program = program + comment("consider removing this line if you don't need to see that image");
        //program = program + "clijx.show(" + image2 + ", \"" + plugin.getTarget().getTitle() + "\")\n";

        return program;
    }

    protected String bitDepthToType(int bitDepth) {
        if (bitDepth == 8) {
            return "UnsignedByte";
        } else if (bitDepth == 16) {
            return "UnsignedShort";
        } else
            return "Float";
    }

    public String pythonize(String methodName) {
        return methodName; // AssistantUtilities.niceNameWithoutDimShape(methodName).trim().replace(" ", "_").toLowerCase();
    }

    @Override
    public String close(String image) {
        return image + ".close()";
    }

    @Override
    public String fileEnding() {
        return ".py";
    }

    @Override
    public String header() {
        return  "# To make this script run in Fiji, please activate \n" +
                "# the clij and clij2 update sites in your Fiji \n" +
                "# installation. Read more: https://clij.github.io\n\n" +
                "# Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "from ij import IJ\n" +
                "from ij import WindowManager\n" +
                "from net.haesleinhuepf.clijx import CLIJx\n\n" +
                "# Init GPU\n" +
                "clijx = CLIJx.getInstance()\n\n";
    }

    @Override
    public String finish(String all) {
        String output = super.finish(all);
        if (!clijx_used) {
            output = output.replace("CLIJx.getInstance", "CLIJ2.getInstance");
            output = output.replace("net.haesleinhuepf.clijx.CLIJx", "net.haesleinhuepf.clij2.CLIJ2");
            output = output.replace("from net.haesleinhuepf.clijx import CLIJx", "from net.haesleinhuepf.clij2 import CLIJ2");
            output = output.replace("clijx", "clij2");
            output = output.replace("CLIJx", "CLIJ2");
        }
        return output;
    }
}
