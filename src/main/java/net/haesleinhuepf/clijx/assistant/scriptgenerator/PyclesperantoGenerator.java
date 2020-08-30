package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import ij.measure.Calibration;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

public class PyclesperantoGenerator implements ScriptGenerator {

    private String line_start = "";
    private boolean use_napari;

    public PyclesperantoGenerator(boolean use_napari) {
        this.use_napari = use_napari;
    }

    @Override
    public String push(AssistantGUIPlugin plugin) {
        ImagePlus source = plugin.getSource();
        String filename = "";
        if (source.getOriginalFileInfo() != null) {
            filename = source.getOriginalFileInfo().directory + source.getOriginalFileInfo().fileName;
        } else if (source.getFileInfo() != null) {
            filename = source.getFileInfo().directory + source.getFileInfo().fileName;
        }

        String program = "\n"+
                "from tifffile import imread\n\n" +

                "# Load image\n" +
                "image = imread(\"" + filename.replace("\\", "/") + "\")\n\n"+

                "# Push " + source.getTitle() + " to GPU memory\n" +
                makeImageID(source) + " = cle.push(image)\n";


        program = program.replace("\n", "\n" + line_start );
        return program;
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        return "";
    }

    @Override
    public String comment(String text) {
        String program = line_start + "# " + text.replace("\n", "\n# ") + "\n";
        program = program.replace("\n", "\n" + line_start );
        return program;
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "# " + AssistantUtilities.niceName(plugin.getName());
        }
        Calibration calibration = plugin.getTarget().getCalibration();

        String methodName = clijMacroPlugin.getName();
        methodName = methodName.substring(0,1).toLowerCase() + methodName.substring(1);
        String pakage = clijMacroPlugin.getClass().getPackage().getName();

        methodName = "cle." + pythonize(methodName);


        String image1 = makeImageID(plugin.getSource());
        String image2 = makeImageID(plugin.getTarget());
        String program = "# " + AssistantUtilities.niceName(plugin.getName()) + "\n";
                //image1 + " = \"" + plugin.getSource().getTitle() + "\";\n" +

        program = program +
           image2 + " = cle.create_like(" + image1 + ");\n";

        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name;
            program = program + name + " = " + plugin.getArgs()[i] + "\n";
        }
        program = program + methodName + "(" + image1 + ", " + image2 + call + ")\n";

        if (use_napari) {
            String scale = calibration.pixelHeight + ", " + calibration.pixelWidth;
            if (plugin.getTarget().getNSlices() > 1) {
                scale = calibration.pixelDepth + ", " + calibration.pixelHeight + ", " + calibration.pixelWidth;
            }

            program = program +
                    "# show result\n\n" +
                    "viewer.add_image(cle.pull(" + image2 + "), scale=(" + scale + "))\n\n";
        } else {
            program = program +
            "# show result\n\n" +
            "io.imshow(" + image2 + ")\n" +
            "io.show()\n\n";
        }

        program = line_start + program.replace("\n", "\n" + line_start );

        return program;
    }

    private String pythonize(String methodName) {
        return AssistantUtilities.niceName(methodName).trim()
                .toLowerCase()
                .replace(" ", "_")
                .replace("clij2_", "")
                .replace("clij_", "")
                .replace("clijx_", "")
                ;
    }

    @Override
    public String fileEnding() {
        return ".py";
    }

    @Override
    public String header() {
        String header = "# This is an experimentally generated python script. Not all commands are supposed to be executable yet.\n" +
                "# If you want to give it a try, create conda environment named te_oki:\n" +
                "#  > conda create --name te_oki \n" +
                "# activate the environment: \n" +
                "#  > conda activate te_oki \n" +
                "# install dependencies: \n" +
                "#  > pip install pyopencl napari ipython matplotlib numpy pyclesperanto_prototype scikit-image \n" +
                "# Also make sure conda is part of the PATH variable.\n" +
                "# \n" +
                "# If you want to run it from Fiji and you're using a different conda environment, you can configure it in Fijis main menu \n" +
                "# Plugins > ImageJ on GPU (CLIJx-Incubator) > Options >Conda configuration (Te Oki) \n" +
                "# Furthermore, activate the scripting language Te Oki in Fijis script editor to run this script.\n\n" +
                "# Stay tuned and check out http://clesperanto.net to learn more." +
                "\n\n" +
                "import pyclesperanto_prototype as cle\n";
        if (use_napari) {
            line_start = "    ";
            header = header +
                    "import napari\n\n" +
                    "# Start napari viewer\n" +
                    "with napari.gui_qt():\n" +
                    line_start + "viewer = napari.Viewer()\n\n";
        } else {
            line_start = "";
            header = header +
                    "from skimage import io\n\n";
        }

        return header;
    }

    @Override
    public String finish() {
        return "";
    }
}
