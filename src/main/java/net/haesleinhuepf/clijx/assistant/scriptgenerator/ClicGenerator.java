package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.scijava.util.VersionUtils;

public class ClicGenerator extends AbstractScriptGenerator {

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
                "cle::Buffer " + image1 + " = cle.Push<" + bitDepthToType(imp.getBitDepth()) + ">(raw_image); \n\n";


        return program;
    }

    @Override
    public String pull(AssistantGUIPlugin plugin) {
        ImagePlus imp = plugin.getTarget();
        String image1 = makeImageID(imp);

        String program = "";

        program = program + comment(" Pull result from GPU and save it to disc");
        program = program + "Image<" + bitDepthToType(imp.getBitDepth()) +  "> result" + call_count + " = cle.Pull<" + bitDepthToType(imp.getBitDepth()) + ">(" + image1 + ");\n";
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
            return "# " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName());
        }
        String methodName = clijMacroPlugin.getName();
        methodName = methodName.replace("CLIJ2_", "").replace("CLIJx_", "");
        methodName = methodName.substring(0,1).toUpperCase() + methodName.substring(1);
        //methodName = pythonize(methodName);


        String[] image1s = makeImageIDs(plugin);
        String image2 = makeImageID(plugin.getTarget());
        String program = comment(" " + AssistantUtilities.niceNameWithoutDimShape(plugin.getName()));

        ImagePlus imp = plugin.getTarget();
        program = program +
                "// Create buffer \n";

        if (imp.getNSlices() > 1) {
            program = program +
                    "std::array<unsigned int, 3> dimensions = {" + imp.getWidth() + ", " + imp.getHeight() + ", " + imp.getNSlices() + "}; \n";
        } else {
            program = program +
                    "std::array<unsigned int, 2> dimensions = {" + imp.getWidth() + ", " + imp.getHeight() + "}; \n";
        }
        program = program +
                "cle::Buffer " + image2 + " = cle.Create<" +  bitDepthToType(imp.getBitDepth()) + ">(dimensions.data(), \"" + bitDepthToType(imp.getBitDepth()) + "\"); \n";
        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 0; i < parameters.length; i++) {
            if (plugin.getArgs()[i] instanceof ClearCLBuffer || plugin.getArgs()[i] instanceof ClearCLBuffer[]) {

            } else {
                String temp[] = parameters[i].trim().split(" ");
                String name = temp[temp.length - 1];
                call = call + ", " + name;
                program = program + name + " = " + objectToString(plugin.getArgs()[i]) + "; \n";
            }
        }

        program = program +
                "cle." + methodName +  "(" + namesToCommaSeparated(image1s) + ", " + image2 + call + ");\n\n";

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
        return  "// This is generated experimental code which is supposed to run using CLIc, \n" +
                "// the C++ implementation of clesperanto. Copy this code and enter it into \n" +
                "// that template: \n" +
                "// https://github.com/StRigaud/CLIc_project_template/blob/main/script.cpp\n" +
                "// \n" +
                "// Read more: \n" +
                "// https://github.com/clEsperanto/CLIc_prototype \n" +
                "// The project is work in progress. Stay tuned! \n" +
                "// \n" +
                "// Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "// Initialize GPU\n" +
                "cle::GPU gpu;\n" +
                "cle::CLE cle(gpu);\n\n";


    }

    @Override
    public String finish(String all) {
        return all + "\n";
    }

    @Override
    public String close(String image) {
        return image + ".Close();";
    }
}
