package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

public class CLIJPyGenerator extends JythonGenerator {

    @Override
    public String push(AssistantGUIPlugin plugin) {
        ImagePlus source = plugin.getSource();
        String image1 = makeImageID(source);

        String filename = "";
        if (source.getOriginalFileInfo() != null) {
            filename = source.getOriginalFileInfo().directory + source.getOriginalFileInfo().fileName;
        } else if (source.getFileInfo() != null) {
            filename = source.getFileInfo().directory + source.getFileInfo().fileName;
        }

        return "" +
                "# Push " + source.getTitle() + " to GPU memory\n" +
                "# load image data\n" +
                "from skimage import io\n" +
                "image = io.imread(\"" + filename.replace("\\", "/") + "\")\n" +
                "\n" +
                "# convert and array to an ImageJ2 img:\n" +
                "import numpy as np\n" +
                "np_arr = np.array(image)\n" +
                "ij_img = ij.py.to_java(np_arr)\n" +
                "\n" +
                "# push the image to the GPU\n" +
                image1 + " = clijx.push(ij_img)\n";
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        String image1 = makeImageID(result.getTarget());

        String program = "";
        program = program + comment("consider calling these methods to retrieve the image");
        program = program + comment("result_ij = clijx.pull(" + image1 + ")");
        program = program + comment("result_np = ij.py.rai_to_numpy(result_ij);");
        program = program + comment("consider calling these methods to save the image");
        program = program + comment("clijx.saveAsTif(" + image1 + ", 'filename.tif')");

        return program;

//        return "" +
               // "result = clijx.pull(" + image1 + "))\n" +
               // "# show result\n\n" +
               // "io.imshow(result)\n" +
               // "io.show()\n\n";
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
        String pakage = clijMacroPlugin.getClass().getPackage().getName();

        methodName = "clijx." + pythonize(methodName);


        String image1 = makeImageID(plugin.getSource());
        String image2 = makeImageID(plugin.getTarget());
        String program = "# " + AssistantUtilities.niceName(plugin.getName()) + "\n";

        ImagePlus imp = plugin.getTarget();
        if (imp.getNSlices() > 1) {
            program = program +
                image2 + " = clijx.create([" + imp.getWidth() + ", " + imp.getHeight() + ", "  + imp.getNSlices() + "], clijx." + bitDepthToType(imp.getBitDepth()) + ")\n";
        } else {
            program = program +
                    image2 + " = clijx.create([" + imp.getWidth() + ", " + imp.getHeight() + "], clijx." + bitDepthToType(imp.getBitDepth()) + ")\n";
        }
        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name + "=" + name;
            program = program + name + " = " + objectToString(plugin.getArgs()[i]) + "  \n";
        }
        program = program + methodName + "(" + image1 + ", " + image2 + call + ")\n";


        return program;
    }

    @Override
    public String header() {
        return  "# This is an experimentally generated python script based on pyimagej and clijpy. \n" +
                "# https://github.com/imagej/pyimagej \n" +
                "# https://github.com/clij/clijpy \n" +
                "# Not all commands are supposed to be executable yet.\n" +
                "# If you want to give it a try, create conda environment named te_oki:\n" +
                "#  > conda create --name te_oki \n" +
                "# activate the environment: \n" +
                "#  > conda activate te_oki \n" +
                "# install dependencies: \n" +
                "#  > pip install pyjnius=1.2.0 ipython napari matplotlib numpy pyimagej openjdk=8 scikit-image \n" +
                "# Also make sure conda is part of the PATH variable.\n" +
                "# Please activate the clij and clij2 update sites in your Fiji \n" +
                "# installation. Read more: https://clij.github.io\n\n" +
                "# \n" +
                "# If you want to run it from Fiji and you're using a different conda environment, you can configure it in Fijis main menu \n" +
                "# Plugins > ImageJ on GPU (CLIJx-Incubator) > Options >Conda configuration (Te Oki) \n" +
                "# Furthermore, activate the scripting language Te Oki in Fijis script editor to run this script.\n\n" +

                "# init pyimagej to get access to jar files\n" +
                "import imagej\n" +
                "ij = imagej.init('" + IJ.getDirectory("imagej").replace("\\", "/") + "')\n" +
                "\n" +
                "# init clijpy to get access to the GPU\n" +
                "from jnius import autoclass\n" +
                "CLIJx = autoclass('net.haesleinhuepf.clijx.CLIJx')\n" +
                "clijx = CLIJx.getInstance()\n\n";
    }
}
