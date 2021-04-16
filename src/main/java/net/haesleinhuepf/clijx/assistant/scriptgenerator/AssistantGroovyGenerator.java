package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.interactive.generic.GenericAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import org.scijava.util.VersionUtils;

public class AssistantGroovyGenerator extends AbstractScriptGenerator {

    @Override
    public String push(ImagePlus source) {
        String output = "";

        String image1 = makeImageID(source);

        String filename = "";
        if (source.getOriginalFileInfo() != null) {
            filename = source.getOriginalFileInfo().directory + source.getOriginalFileInfo().fileName;
        } else if (source.getFileInfo() != null) {
            filename = source.getFileInfo().directory + source.getFileInfo().fileName;
        }



        output = output +
                "// Load image from disc \n" +
                image1 + " = net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.openImage(\"" + filename.replace("\\", "/") + "\");\n" +
                image1 + ".setC(" + source.getC() + ");\n" +
                image1 + ".setZ(" + source.getZ() + ");\n" +
                image1 + ".setT(" + source.getT() + ");\n" +
                image1 + ".setTitle(\"" + source.getTitle() + "\");\n" +
                image1 + ".show();\n";

        return output;
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        return "";
    }

    @Override
    public String comment(String name) {
        return "// " + name.replace("\n", "\n//");
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {
        StringBuilder output = new StringBuilder();
        output.append("// " + plugin.getName() + "\n");

        Class klass = plugin.getClass();
        Class pluginClass = plugin.getCLIJMacroPlugin().getClass();

        //String pkg = klass.getPackage().toString();
        //if (pkg.endsWith())
        if (plugin instanceof GenericAssistantGUIPlugin) {
            output.append("node = new " + klass.getName() + "(new " + pluginClass.getName() + "());\n");
        } else {
            output.append("node = new " + klass.getName() + "();\n");
        }
        output.append("node.setSources(" + namesToCommaSeparated(makeImageIDs(plugin)) + ");\n");
        output.append("node.run(\"\");\n");

        Object[] args = plugin.getArgs();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof String || args[i] instanceof Integer || args[i] instanceof Double) {
                    output.append("node.getArgs()[" + i + "] = " + objectToString(args[i]) + ";\n");
                }
            }
        }
        output.append("node.refreshDialogFromArguments();\n");
        output.append("node.setTargetInvalid();\n");

        ImagePlus target = plugin.getTarget();
        if( target != null && target.getWindow() != null) {
            output.append("// set window position and size\n");
            output.append("window = node.getTarget().getWindow();\n");
            output.append("window.setLocation(" + target.getWindow().getX() + ", " + target.getWindow().getY() + ");\n");
            output.append("window.setSize(" + target.getWindow().getWidth() + ", " + target.getWindow().getHeight() + ");\n");
            output.append("window.getCanvas().setMagnification(" + target.getWindow().getCanvas().getMagnification() + ");\n");
        }
        output.append(makeImageID(target) + " = node.getTarget();\n");
        output.append(
                "java.lang.Thread.sleep(500);\n" +
                "IJ.run(\"In [+]\");\n" +
                "IJ.run(\"Out [-]\");\n");

        output.append("\n");
        return output.toString();
    }

    @Override
    public String fileEnding() {
        return ".groovy";
    }

    @Override
    public String header() {
        return  "// This script contains a CLIJx-assistant workflow. You can load this workflow by \n" +
                "// executing this script in Fijis script editor after choosing the Groovy language.\n" +
                "// \n" +
                "//                   This script is not meant to be edited.\n" +
                "// \n" +
                "// For image analysis automation, generate an ImageJ Groovy script for example.\n" +
                "// To make this script run in Fiji, please activate the clij, clij2 and \n" +
                "// clijx-assistant update sites in your Fiji. \n" +
                "// Read more: https://clij.github.io/assistant/save_and_load\n\n" +
                "// Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "\n\n" +
                "import ij.IJ;\n" +
                "import net.haesleinhuepf.clijx.CLIJx;\n\n" +
                "// clean up first\n" +
                "IJ.run(\"Close All\");\n\n" +
                "// Init GPU\n" +
                "clijx = CLIJx.getInstance();\n" +
                "clijx.clear();\n" +
                "\n" +
                "// disable automatic window positioning \n" +
                "was_auto_position = net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin.isAutoPosition();\n" +
                "net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin.setAutoPosition(false);\n" +
                "\n";
    }

    @Override
    public String finish(String all) {
        return all +
                "// reset auto-positioning\n" +
                "IJ.wait(500);\n" +
                "net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin.setAutoPosition(was_auto_position);\n\n";
    }

    @Override
    public String close(String image) {
        return "";
    }

}
