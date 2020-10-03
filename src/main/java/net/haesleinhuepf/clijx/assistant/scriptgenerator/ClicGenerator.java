package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

public class ClicGenerator implements ScriptGenerator {

    int call_count = 0;

    @Override
    public String push(ImagePlus imp) {
        String program = "";


        String image1 = makeImageID(imp);
        program = program +
                "// Push image \n";

        if (imp.getNSlices() > 1) {
            program = program +
                    "Image<" + bitDepthToType(imp.getBitDepth()) + "> raw_image (raw_data, " + imp.getWidth() + ", " + imp.getHeight() + ", " + imp.getNSlices() + ", \"" + bitDepthToType(imp.getBitDepth()) + "\"); \n";
        } else {
            program = program +
                    "Image<" + bitDepthToType(imp.getBitDepth()) + "> raw_image (raw_data, " + imp.getWidth() + ", " + imp.getHeight() + ", \"" + bitDepthToType(imp.getBitDepth()) + "\"); \n";
        }
        program = program +
                "cle::Buffer " + image1 + " = gpu.Push<" + bitDepthToType(imp.getBitDepth()) + ">(raw_image); \n\n";

        return program;
    }

    @Override
    public String pull(AssistantGUIPlugin plugin) {
        ImagePlus imp = plugin.getTarget();
        String image1 = makeImageID(imp);

        String program = "";

        program = program + comment(" Pull output into container");
        program = program + "Image<" + bitDepthToType(imp.getBitDepth()) +  "> result" + call_count + " = gpu.Pull<" + bitDepthToType(imp.getBitDepth()) + ">(" + image1 + ");\n";

        program = program + comment(" Write add image and scalar test result in tiff");
        program = program + "TiffWriter imageWriter" + call_count + " (filename.c_str());\n";
        program = program + "imageWriter" + call_count + ".write(result" + call_count + ".GetData(), result" + call_count + ".GetDimensions()[0], result" + call_count + ".GetDimensions()[1], result" + call_count + ".GetDimensions()[2]);\n";
        program = program + "\n";

        return program;
    }

    @Override
    public String comment(String text) {
        return "// " + text.replace("\n", "\n// ") + "\n";
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "# " + AssistantUtilities.niceName(plugin.getName());
        }
        String methodName = clijMacroPlugin.getName();
        methodName = methodName.replace("CLIJ2_", "").replace("CLIJx_", "");
        methodName = methodName.substring(0,1).toLowerCase() + methodName.substring(1);
        methodName = pythonize(methodName);


        String[] image1s = makeImageIDs(plugin);
        String image2 = makeImageID(plugin.getTarget());
        String program = comment(" " + AssistantUtilities.niceName(plugin.getName()));

        ImagePlus imp = plugin.getTarget();
        program = program +
                "// Create buffer \n";

        if (imp.getNSlices() > 1) {
            program = program +
                    "std::array<unsigned int, 3> dimensions = {" + imp.getWidth() + ", " + imp.getHeight() + ", " + imp.getNSlices() + "}; \n";
        } else {
            program = program +
                    "std::array<unsigned int, 3> dimensions = {" + imp.getWidth() + ", " + imp.getHeight() + "}; \n";
        }
        program = program +
                "cle::Buffer " + image2 + " = gpu.Create<" +  bitDepthToType(imp.getBitDepth()) + ">(dimensions.data(), \"" + bitDepthToType(imp.getBitDepth()) + "\"); \n";
        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name;
            program = program + name + " = " + objectToString(plugin.getArgs()[i]) + "; \n";
        }

        program = program + "cle::" + methodName + " operation" + call_count + "(gpu);\n" +
        "operation" + call_count + ".Execute(" + namesToCommaSeparated(image1s) + ", " + image2 + call + ");\n\n";

        call_count++;

        return program;
    }

    protected String bitDepthToType(int bitDepth) {
        if (bitDepth == 8) {
            return "unsigned char";
        } else if (bitDepth == 16) {
            return "unsigned short";
        } else
            return "float";
    }

    @Override
    public String fileEnding() {
        return ".js";
    }

    @Override
    public String header() {
        return  "// This is generated experimental code which is supposed to run using clic, " +
                "// the C++ implementation of clesperanto: \n" +
                "// https://github.com/clEsperanto/CLIc_prototype \n" +
                "// The project is work in progress. Stay tuned!" +
                "//" +
                "\n" +
                "// Initialise device, context, and CQ.\n" +
                "cle::GPU gpu;\n" +
                "gpu.Initialisation();\n\n";
    }

    @Override
    public String finish() {
        return "\n";
    }
}
