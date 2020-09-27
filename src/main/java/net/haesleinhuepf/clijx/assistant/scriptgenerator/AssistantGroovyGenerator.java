package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.interactive.generic.GenericAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;

public class AssistantGroovyGenerator implements ScriptGenerator {

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


        return ""+
                "// Load image from disc \n"+
                "IJ.open(\"" + filename.replace("\\", "/") +  "\");\n";
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
        if( target.getWindow() != null) {
            output.append("// set window position and size\n");
            output.append("window = node.getTarget().getWindow();\n");
            output.append("window.setLocation(" + target.getWindow().getX() + ", " + target.getWindow().getY() + ");\n");
            output.append("window.setSize(" + target.getWindow().getWidth() + ", " + target.getWindow().getHeight() + ");\n");
        }

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
                "// Read more: https://clij.github.io/assistant/save_and_load\n\n" +
                "\n\n" +
                "import ij.IJ;\n" +
                "import ij.WindowManager;\n" +
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
    public String finish() {
        return
                "// reset auto-positioning\n" +
                "IJ.wait(500);\n" +
                "net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin.setAutoPosition(was_auto_position);\n\n";
    }

}
