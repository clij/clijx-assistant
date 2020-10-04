package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

public class JythonGenerator implements ScriptGenerator {

    @Override
    public String push(ImagePlus source) {
        String output = "";

        String filename = getFilename(source);


        if (filename != null && filename.length() > 0) {
            output = output + "" +
                    "# Load image from disc \n" +
                    "imp = IJ.open(\"" + filename + "\")\n" +
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
                "result = clijx.pull(" + image1 + ");\n" +
                "result.show();\n\n";
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
        methodName = methodName.replace("CLIJ2_", "").replace("CLIJx_", "");
        methodName = methodName.substring(0,1).toLowerCase() + methodName.substring(1);
        String pakage = clijMacroPlugin.getClass().getPackage().getName();

        methodName = "clijx." + pythonize(methodName);


        String[] image1s = makeImageIDs(plugin);
        String image2 = makeImageID(plugin.getTarget());
        String program = "# " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName()) + "\n";

        ImagePlus target = plugin.getTarget();
        ImagePlus source = plugin.getSource(0);

        if (target != null &&
                source != null &&
                target.getWidth() == source.getWidth() &&
                target.getHeight() == source.getHeight() &&
                target.getNSlices() == source.getNSlices()
        ) {
            if ( target.getBitDepth() == source.getBitDepth()) {
                program = program +
                        image2 + " = clijx.create(" + image1s[0] + ");\n";
            } else {
                program = program +
                        image2 + " = clijx.create(" + image1s[0] + ".getDimensions(), clijx." + bitDepthToType(target.getBitDepth()) + " );\n";
            }
        }else if (target.getNSlices() > 1) {
            program = program +
                image2 + " = clijx.create([" + target.getWidth() + ", " + target.getHeight() + ", "  + target.getNSlices() + "], clijx." + bitDepthToType(target.getBitDepth()) + ");\n";
        } else {
            program = program +
                    image2 + " = clijx.create([" + target.getWidth() + ", " + target.getHeight() + "], clijx." + bitDepthToType(target.getBitDepth()) + ");\n";
        }
        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name;
            program = program + name + " = " + objectToString(plugin.getArgs()[i]) + ";\n";
        }
        program = program + methodName + "(" + namesToCommaSeparated(image1s) + ", " + image2 + call + ");\n";

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
    public String fileEnding() {
        return ".py";
    }

    @Override
    public String header() {
        return  "# To make this script run in Fiji, please activate \n" +
                "# the clij and clij2 update sites in your Fiji \n" +
                "# installation. Read more: https://clij.github.io\n\n" +
                "from ij import IJ;\n" +
                "from ij import WindowManager;\n" +
                "from net.haesleinhuepf.clijx import CLIJx;\n\n" +
                "# Init GPU\n" +
                "clijx = CLIJx.getInstance();\n" +
                "clijx.clear();\n\n";
    }

    @Override
    public String finish() {
        return "# clean up memory \n" +
                "clijx.clear();\n\n";
    }
}
