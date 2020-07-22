package net.haesleinhuepf.clincubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clincubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clincubator.utilities.IncubatorPlugin;

import java.util.HashMap;

public class MacroGenerator implements ScriptGenerator {

    @Override
    public String push(ImagePlus source) {
        return ""+
                "image1 = \"" + source.getTitle() + "\";\n" +
                "Ext.CLIJ2_push(image1);\n";
    }

    @Override
    public String comment(String name) {
        return "// " + name + "\n";
    }

    @Override
    public String execute(IncubatorPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "// " + IncubatorUtilities.niceName(plugin.getClass().getSimpleName());
        }
        String methodName = clijMacroPlugin.getClass().getSimpleName();
        methodName = methodName.substring(0,1).toLowerCase() + methodName.substring(1);
        String pakage = clijMacroPlugin.getClass().getPackage().getName();
        if (pakage.contains(".clij2")) {
            methodName = "Ext.CLIJ2_" + methodName;
        } else if (pakage.contains(".clijx")) {
            methodName = "Ext.CLIJx_" + methodName;
        } else {
            methodName = "Ext.CLIJ_" + methodName;
        }

        String image1 = makeImageID(plugin.getSource());
        String image2 = makeImageID(plugin.getTarget());
        String program = "// " + IncubatorUtilities.niceName(plugin.getClass().getSimpleName()) + "\n" +
                "// " + image1 + " = \"" + plugin.getSource().getTitle() + "\";\n" +
                "// " + image2 + " = \"" + plugin.getTarget().getTitle() + "\";\n";

        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name;
            program = program + name + " = " + plugin.getArgs()[i] + ";\n";
        }
        program = program + methodName + "(" + image1 + ", " + image2 + call + ");\n";

        return program;
    }

    HashMap<ImagePlus, String> image_map = new HashMap<>();
    private String makeImageID(ImagePlus target) {
        if (!image_map.keySet().contains(target)) {
            image_map.put(target, "image" + (image_map.size() + 1));
        }

        return image_map.get(target);
    }

    @Override
    public String fileEnding() {
        return ".ijm";
    }

    @Override
    public String header() {
        return  "// To make this script run in Fiji, please activate \n" +
                "// the clij and clij2 update sites in your Fiji \n" +
                "// installation. Read more: https://clij.github.io\n\n" +
                "// Init GPU\n" +
                "run(\"CLIJ2 Macro Extensions\", \"cl_device=\");\n\n";
    }
}
