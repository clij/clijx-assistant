package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.plugins.GaussianBlur3D;
import net.haesleinhuepf.clij2.plugins.MaximumZProjection;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;
import net.haesleinhuepf.clijx.assistant.interactive.generic.GenericAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.interactive.handcrafted.Zoom;
import net.haesleinhuepf.clijx.assistant.optimize.Workflow;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

class MavenJavaProjectGenerator {

    static String TEMPLATE_REPOSITORY = "C:/structure/code/clijx-assistant-plugin-generator-template/";
    //"https://github.com/clij/clijx-assistant-plugin-generator-template";
    static String GIT_EXECUTABLE = "git";
    static String MAVEN_EXECUTABLE = "mvn";

    String enter_lower_case_plugin_name_here = "";
    String enter_plugin_name_here = "";
    String enter_plugin_description_here = "";
    String enter_your_id_here = "";
    String enter_your_name_here = "";
    String CLIJx_EnterCLIJPluginIdentifierHere = "";
    String EnterClassNameHere = "";
    String enter_date_here = "";
    String enter_code_here = "";

    String enter_function_name_here = "";
    String enter_parameters_here = "";
    String enter_typed_parameters_here = "";
    String enter_default_values_here = "";
    String enter_value_parsers_here = "";

    public MavenJavaProjectGenerator(Workflow workflow, String plugin_name, String plugin_description, String author_name, String author_id) {

        enter_plugin_name_here = validJavaName(plugin_name);
        enter_lower_case_plugin_name_here = enter_plugin_name_here.toLowerCase();
        enter_plugin_description_here = validJavaText(plugin_description);
        enter_your_id_here = validJavaText(author_id);
        enter_your_name_here = validJavaText(author_name);
        enter_function_name_here = validJavaFunctionName(enter_plugin_name_here);
        CLIJx_EnterCLIJPluginIdentifierHere = "CLIJx_" + enter_function_name_here;
        EnterClassNameHere = enter_plugin_name_here;
        enter_date_here = new Date().toString();

        parseWorkflow(workflow);


    }

    private String validJavaFunctionName(String enter_plugin_name_here) {
        return enter_plugin_name_here.substring(0,1).toLowerCase() + enter_plugin_name_here.substring(1);
    }

    private void parseWorkflow(Workflow workflow) {
        enter_code_here = "";
        enter_parameters_here = "";
        enter_typed_parameters_here = "";
        enter_default_values_here = "";
        enter_value_parsers_here = "";

        /*
        if (enter_parameters_here.length() > 0) {
                    enter_parameters_here = enter_parameters_here + ",";
                }
                enter_parameters_here = enter_parameters_here + variableValueToCLIJParameterString(parameter_value, parameter_name + "_" + parameter_count);

        */


        String clij2_jar = jarFromClass(CLIJ2.class);
        String clijx_jar = jarFromClass(CLIJx.class);

        String current_image = "";
        String next_image = "pushed";
        int plugin_index = 0;
        int parameter_count = 0;

        ArrayList<CLIJMacroPlugin> plugins = workflow.getPlugins();
        for (int p = 0; p < plugins.size(); p++) {
            CLIJMacroPlugin plugin = plugins.get(p);

            // ---------------------------------------------------------------------------------------------------------
            // determine call
            String jar = jarFromClass(plugin.getClass());

            String call = "";
            if (jar.compareTo(clij2_jar) == 0 || jar.compareTo(clijx_jar) == 0) {
                call = "clijx." + validJavaFunctionName(plugin.getClass().getSimpleName()) + "(";
            } else {
                call = "// Note: this operation needs " + jar + "\n";
                call = call + plugin.getClass().getName() + "." + validJavaFunctionName(plugin.getClass().getSimpleName()) + "(clijx, ";
            }

            // ---------------------------------------------------------------------------------------------------------
            // determine parameters

            // count image variables up
            current_image = next_image;
            next_image = "image" + (plugin_index + 1);
            if (p == plugins.size() - 1) {
                next_image = "result";
            }

            call = call + current_image + ", " + next_image;

            Object[] args = workflow.getArgs()[plugin_index];
            String[] parameters = plugin.getParameterHelpText().replace(", ", ",").split(",");

            for (int a = 2; a < args.length; a++ ) {
                parameter_count++;
                Object parameter_value = args[a];
                String parameter = parameters[a].replace("ByRef ", "");
                String parameter_name = parameter.split(" ")[1].trim();

                enter_parameters_here = enter_parameters_here + ", ";
                enter_typed_parameters_here = enter_typed_parameters_here + ", ";
                enter_default_values_here = enter_default_values_here + ", ";
                enter_value_parsers_here = enter_value_parsers_here + ", ";

                enter_parameters_here = enter_parameters_here + variableValueToCLIJParameterString(parameter_value, parameter_name) + "_" + parameter_count;
                enter_typed_parameters_here = enter_typed_parameters_here + variableValueToJavaParameterNameString(parameter_value, parameter_name) + "_" + parameter_count;;
                enter_default_values_here = enter_default_values_here + variableValueToJavaString(parameter_value);
                enter_value_parsers_here = enter_value_parsers_here + variableValueToJavaParser(parameter_value, parameter_count + 1);

                call = call + ", " + parameter_name + "_" + parameter_count;;
            }

            call = call + ");";

            ClearCLBuffer input = (ClearCLBuffer) args[0];
            ClearCLBuffer output = (ClearCLBuffer) args[1];

            // descriptive comment
            enter_code_here = enter_code_here +
                    "\n// " + plugin.getName() + "\n";

            // only generate output image if not the last step
            if (p < plugins.size() - 1) {
                enter_code_here = enter_code_here +
                       "ClearCLBuffer " + next_image + " = clijx.create(new long[]{" + bufferToDimensionsString(output, input, current_image) + "}, " + bufferToTypeString(output, input, current_image) + ");\n";
            }

            enter_code_here = enter_code_here +
                    call + "\n";


            if (plugin_index > 0) {
                enter_code_here = enter_code_here +
                        current_image + ".close();\n";
            }
            enter_code_here = enter_code_here +
                    "\n";

            plugin_index ++;
        }

        enter_code_here = ("\n" + enter_code_here).replace("\n", "\n        ");

    }

    private String variableValueToJavaParser(Object variable, int arg_count) {
        if (variable instanceof Double || variable instanceof Float || variable instanceof Integer) {
            return "asFloat(args[" + arg_count + "])";
        } else if (variable instanceof String) {
            return "(\"\" + args[" + arg_count + "])";
        } else {
            return "0 /* WARNING: unsupported parameter type (" + variable.getClass().getSimpleName() + ") " + variable + "*/";
        }
    }

    private String bufferToDimensionsString(ClearCLBuffer output, ClearCLBuffer input, String buffername) {

        String width = output.getWidth() == input.getWidth() ? buffername + ".getWidth()":("" + output.getWidth());
        String height = output.getHeight() == input.getHeight() ? buffername + ".getHeight()":("" + output.getHeight());
        String depth = output.getDepth() == input.getDepth() ? buffername + ".getDepth()":("" + output.getDepth());


        if (output.getDimension() == 2) {
            return "" + width + ", " + height;
        } else {
            return "" + width + ", " + height + ", " + depth;
        }
    }

    private String bufferToTypeString(ClearCLBuffer output, ClearCLBuffer input, String buffername) {
        return output.getNativeType() == input.getNativeType() ? buffername + ".getNativeType()":("clijx." + output.getNativeType().getClass().getSimpleName());
    }

    private String variableValueToCLIJParameterString(Object variable, String name) {
        if (variable instanceof Double || variable instanceof Float || variable instanceof Integer) {
            return "Number " + name;
        } else if (variable instanceof String) {
            return "String " + name;
        } else {
            return "Number " + name + "/* WARNING: unsupported parameter type (" + variable.getClass().getSimpleName() + ") " + variable + "*/";
        }
    }

    private String variableValueToJavaParameterNameString(Object variable, String name) {
        if (variable instanceof Double || variable instanceof Float || variable instanceof Integer) {
            return "Float " + name;
        } else if (variable instanceof String) {
            return "String " + name;
        } else {
            return "Float " + name + " /* WARNING: unsupported parameter type (" + variable.getClass().getSimpleName() + ") " + variable + "*/";
        }
    }

    private String variableValueToJavaString(Object variable) {
        if (variable instanceof Double || variable instanceof Float || variable instanceof Integer) {
            return "new Float(" + variable + ")";
        } else if (variable instanceof String) {
            return "\"" + variable + "\"";
        } else {
            return "0 /* WARNING: unsupported parameter type (" + variable.getClass().getSimpleName() + ") " + variable + "*/";
        }
    }

    private String jarFromClass(Class klass) {
        return klass.getResource('/' + klass.getName().replace('.', '/') + ".class").toString().split("!")[0];
    }

    private String validJavaText(String text) {
        return text.replace("\"", "\\\"");
    }

    private String validJavaName(String plugin_name) {
        plugin_name = plugin_name.replace(" ", "_");
        plugin_name = plugin_name.replace("*", "_");
        plugin_name = plugin_name.replace("&", "_");
        plugin_name = plugin_name.replace("(", "_");
        plugin_name = plugin_name.replace(")", "_");
        plugin_name = plugin_name.replace("-", "_");
        return plugin_name;
    }


    void parseSubFolders(File folder) {
        for (File file : folder.listFiles()) {
            if (file.getName().startsWith(".") || file.isHidden()) {
                continue;
            }
            //System.out.println(file);
            if (file.isDirectory()) {
                parseSubFolders(file);
            } else {
                parseFile(file);
            }
        }
    }

    private void parseFile(File file) {
        String content = null;
        try {
            content = readFile(file);

            content = replaceContent(content);

            writeFile(file, content);

            String filename = file.toString();
            String new_filename = replaceContent(filename);
            if (filename.compareTo(new_filename) != 0) {
                file.renameTo(new File(new_filename));
            }





        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String replaceContent(String content) {
        content = content.replace("enter_lower_case_plugin_name_here", enter_lower_case_plugin_name_here);
        content = content.replace("enter_plugin_name_here", enter_plugin_name_here);
        content = content.replace("enter_plugin_description_here", enter_plugin_description_here);
        content = content.replace("enter_your_id_here", enter_your_id_here);
        content = content.replace("enter_your_name_here", enter_your_name_here);
        content = content.replace("CLIJx_EnterCLIJPluginIdentifierHere", CLIJx_EnterCLIJPluginIdentifierHere);
        content = content.replace("EnterClassNameHere", EnterClassNameHere);
        content = content.replace("enter_date_here", enter_date_here);
        content = content.replace("enter_code_here", enter_code_here);
        content = content.replace(", enter_parameters_here", enter_parameters_here);
        content = content.replace(", Float enter_typed_parameters_here", enter_typed_parameters_here);
        content = content.replace(", 1/*enter_default_values_here*/", enter_default_values_here);
        content = content.replace(", 1f /*enter_value_parsers_here*/", enter_value_parsers_here);
        content = content.replace("enter_function_name_here", enter_function_name_here);

        return content;
    }

    private static void writeFile(File file, String content) throws IOException {
        FileWriter writer = new FileWriter(file);

        writer.write(content);
        writer.close();
    }

    private static String readFile(File file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file.toURI())));
    }

    public static void git_clone(String url, String target_directory) {
        String[] command = {GIT_EXECUTABLE, "clone", url, target_directory.replace(" ", "\\ ")};
        AssistantUtilities.execute(target_directory, command);
    }

    public static void mvn_package(String target_directory) {
        String[] command = {MAVEN_EXECUTABLE, "package"};
        AssistantUtilities.execute(target_directory, command);

    }

    boolean installTo(String project_dir, String imagej_dir) {
        if (!imagej_dir.endsWith("/")) {
            imagej_dir = imagej_dir + "/";
        }
        if (!project_dir.endsWith("/")) {
            project_dir = imagej_dir + "/";
        }

        String jar_source_file = project_dir + "target/" + getJarFilename();
        String jar_target_file = imagej_dir + "plugins/" + getJarFilename();

        return new File(jar_source_file).renameTo(new File(jar_target_file));
    }

    public String getJarFilename() {
        return "clijx-assistant-" + enter_lower_case_plugin_name_here + "_-0.1.0.0.jar";
    }


}
