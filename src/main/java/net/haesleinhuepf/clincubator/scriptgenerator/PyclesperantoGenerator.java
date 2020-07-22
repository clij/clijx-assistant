package net.haesleinhuepf.clincubator.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clincubator.utilities.IncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.IncubatorUtilities;

import java.util.HashMap;

public class PyclesperantoGenerator implements ScriptGenerator {

    @Override
    public String push(ImagePlus source) {
        return ""+
                "# Push " + source.getTitle() + " to GPU memory\n" +
                "image1 = cle.push(<enter initial image variable name>)\n";
    }

    @Override
    public String comment(String name) {
        return "# " + name + "\n";
    }

    @Override
    public String execute(IncubatorPlugin plugin) {

        CLIJMacroPlugin clijMacroPlugin = plugin.getCLIJMacroPlugin();
        if (clijMacroPlugin == null) {
            return "# " + IncubatorUtilities.niceName(plugin.getClass().getSimpleName());
        }
        String methodName = clijMacroPlugin.getClass().getSimpleName();
        methodName = methodName.substring(0,1).toLowerCase() + methodName.substring(1);
        String pakage = clijMacroPlugin.getClass().getPackage().getName();

        methodName = "cle." + pythonize(methodName);


        String image1 = makeImageID(plugin.getSource());
        String image2 = makeImageID(plugin.getTarget());
        String program = "# " + IncubatorUtilities.niceName(plugin.getClass().getSimpleName()) + "\n";
                //image1 + " = \"" + plugin.getSource().getTitle() + "\";\n" +
                //image2 + " = \"" + plugin.getTarget().getTitle() + "\";\n";

        String call = "";

        String[] parameters = clijMacroPlugin.getParameterHelpText().split(",");
        for (int i = 2; i < parameters.length; i++) {
            String temp[] = parameters[i].trim().split(" ");
            String name = temp[temp.length - 1];
            call = call + ", " + name + "=" + name;
            program = program + name + " = " + plugin.getArgs()[i] + "\n";
        }
        program = program + image2 + " = " + methodName + "(" + image1 + ", " + call + ")\n";

        return program;
    }

    private String pythonize(String methodName) {
        return IncubatorUtilities.niceName(methodName).trim().replace(" ", "_").toLowerCase();
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
        return ".py";
    }

    @Override
    public String header() {
        return  "# This is experimental script output which is not supposed to be executable yet.\n" +
                "# Stay tuned and check out http://clesperanto.net to learn more." +
                "\n\n" +
                "import pyclesperanto_prototype as cle";
    }
}
