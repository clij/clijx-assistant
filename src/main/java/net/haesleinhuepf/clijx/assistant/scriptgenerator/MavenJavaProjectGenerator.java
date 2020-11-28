package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCL;
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
import net.haesleinhuepf.clijx.assistant.options.AssistantOptions;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.jocl.CL;
import org.scijava.util.VersionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.jarFromClass;

class MavenJavaProjectGenerator {

    static String TEMPLATE_REPOSITORY = //"C:/structure/code/clijx-assistant-plugin-generator-template/";
                                        "https://github.com/clij/clijx-assistant-plugin-generator-template";


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
    String enter_custom_dependencies_here = "";

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
        enter_custom_dependencies_here = "";

        String clij2_jar = jarFromClass(CLIJ2.class);
        String clijx_jar = jarFromClass(CLIJx.class);

        int plugin_index = 0;
        int parameter_count = 0;

        String enter_image_parameters_here = "";
        String enter_image_typed_parameters_here = "";
        String enter_image_default_values_here = "";
        String enter_image_value_parsers_here = "";

        ArrayList<CLIJMacroPlugin> plugins = workflow.getPlugins();
        for (int p = 0; p < plugins.size(); p++) {
            CLIJMacroPlugin plugin = plugins.get(p);

            // ---------------------------------------------------------------------------------------------------------
            // determine call
            String jar = jarFromClass(plugin.getClass());

            String after_call = "";
            String call = "";
            if (jar.compareTo(clij2_jar) == 0 || jar.compareTo(clijx_jar) == 0) {
                call = "clijx." + validJavaFunctionName(plugin.getClass().getSimpleName()) + "(";
            } else {
                call = "// Note: this operation needs " + jar + "\n";
                call = call + plugin.getClass().getName() + "." + validJavaFunctionName(plugin.getClass().getSimpleName()) + "(clijx, ";
                enter_custom_dependencies_here = enter_custom_dependencies_here + dependency(jar);
            }

            // descriptive comment
            enter_code_here = enter_code_here +
                    "\n// " + plugin.getName() + "\n";

            // ---------------------------------------------------------------------------------------------------------
            // determine parameters

            Object[] args = workflow.getArgs()[plugin_index];
            String[] parameters = plugin.getParameterHelpText().replace(", ", ",").split(",");

            ClearCLBuffer first_input_image;
            if ( args[0] instanceof ClearCLBuffer) {
                first_input_image = (ClearCLBuffer) args[0];
            } else { // if (args[0] instanceof ImagePlus){
                first_input_image = CLIJ2.getInstance().push(args[0]);
                first_input_image.close();
            }
            //ClearCLBuffer last_input_image = (ClearCLBuffer) args[0];
            //int i = 0;
            //while(i < args.length && args[i] instanceof ClearCLBuffer) {
            //    last_input_image = (ClearCLBuffer) args[i];
            //    i++;
            //}

            for (int a = 0; a < args.length; a++ ) {


                parameter_count++;

                Object parameter_value = args[a];
                boolean byref = parameters[a].trim().startsWith("ByRef");
                String parameter = parameters[a].replace("ByRef ", "");
                String parameter_name = parameter.split(" ")[1].trim();

                if (parameter_value instanceof ClearCLBuffer[]) {
                    parameter_value = ((ClearCLBuffer[])parameter_value)[0];
                }

                // if image parameter
                if (parameter_value instanceof ClearCLBuffer) {
                    boolean image_exists_already = idExists((ClearCLBuffer)parameter_value);
                    String image_id = makeImageID((ClearCLBuffer) parameter_value);

                    // if initial input image or final output image
                    if (
                            (!image_exists_already && !byref) ||  // input image parameters
                            (p == plugins.size() -1 && byref) // output image parameters of last plugin
                    ) {
                        if (a > 0) {
                            enter_image_parameters_here = enter_image_parameters_here + ", ";
                            enter_image_typed_parameters_here = enter_image_typed_parameters_here + ", ";
                            enter_image_default_values_here = enter_image_default_values_here + ", ";
                            enter_image_value_parsers_here = enter_image_value_parsers_here + ", ";
                        }

                        //System.out.println("--->");
                        if (byref) {
                            enter_image_parameters_here = enter_image_parameters_here + "ByRef ";
                        }
                        enter_image_parameters_here = enter_image_parameters_here + "Image " + image_id;

                        enter_image_typed_parameters_here = enter_image_typed_parameters_here + "ClearCLBuffer " + image_id;
                        enter_image_default_values_here = enter_image_default_values_here + "null";
                        enter_image_value_parsers_here = enter_image_value_parsers_here + "(ClearCLBuffer) args[" + parameter_count + "]";
                    } else { // any intermediate image
                        if (
                                !enter_code_here.contains("ClearCLBuffer " + image_id) &&
                                !enter_image_typed_parameters_here.contains("ClearCLBuffer " + image_id)
                        ) {
                            enter_code_here = enter_code_here +
                                    "ClearCLBuffer " + image_id + " = clijx.create(new long[]{" + bufferToDimensionsString((ClearCLBuffer) parameter_value, first_input_image, makeImageID(first_input_image)) + "}, " + bufferToTypeString((ClearCLBuffer) parameter_value, first_input_image, makeImageID(first_input_image)) + ");\n";
                        }
                    }
                    if (a > 0) {
                        call = call + ", ";
                    }
                    call = call + image_id;
                    after_call = after_call + "// " + image_id + ".close;\n";
                } else { // if parrameter of any type but image
                    enter_parameters_here = enter_parameters_here + ", ";
                    enter_typed_parameters_here = enter_typed_parameters_here + ", ";
                    enter_default_values_here = enter_default_values_here + ", ";
                    enter_value_parsers_here = enter_value_parsers_here + ", ";
                    call = call + ", ";

                    enter_parameters_here = enter_parameters_here + variableValueToCLIJParameterString(parameter_value, parameter_name) + "_" + parameter_count;
                    enter_typed_parameters_here = enter_typed_parameters_here + variableValueToJavaParameterNameString(parameter_value, parameter_name) + "_" + parameter_count;
                    enter_default_values_here = enter_default_values_here + variableValueToJavaString(parameter_value);
                    enter_value_parsers_here = enter_value_parsers_here + variableValueToJavaParser(parameter_value, parameter_count + 1);
                    call = call + parameter_name + "_" + parameter_count;
                }
            }

            call = call + ");";

            enter_code_here = enter_code_here +
                    call + "\n" +
                    after_call + "\n";

            enter_code_here = enter_code_here +
                    "\n";

            plugin_index ++;
        }

        enter_parameters_here = enter_image_parameters_here + enter_parameters_here;
        enter_typed_parameters_here = enter_image_typed_parameters_here + enter_typed_parameters_here;
        enter_default_values_here = enter_image_default_values_here + enter_default_values_here;
        enter_value_parsers_here = enter_image_value_parsers_here + enter_value_parsers_here;

        // fix order of parameters when parsing
        String[] temp1 = enter_value_parsers_here.split("\\[");
        enter_value_parsers_here = "";
        for (int i = 0; i < temp1.length; i++) {
            System.out.println("Text: " + temp1[i]);
            if (i == 0) {
                enter_value_parsers_here = temp1[i];
            } else {
                enter_value_parsers_here = enter_value_parsers_here + "[" + (i - 1) + "]";
                String[] temparr = temp1[i].split("\\]");
                if (temparr.length > 1) {
                    enter_value_parsers_here = enter_value_parsers_here + temparr[1];
                }
            }
        }

        // release temporary memory
        String parameters = ", " + enter_typed_parameters_here;
        for (ClearCLBuffer buffer : image_map.keySet()) {
            String image_id = image_map.get(buffer);
            String[] temp = enter_code_here.split("// " + image_id + ".close;\n");
            if (!parameters.contains(", ClearCLBuffer " + image_id)) {
                temp[temp.length - 1] = image_id + ".close();\n" + temp[temp.length - 1];
            }
            enter_code_here = String.join("", temp);
        }

        enter_code_here = ("\n" + enter_code_here).replace("\n", "\n        ");

    }

    private String dependencies = "";
    private String dependency(String jar) {
        if (dependencies.contains(jar)) {
            return "";
        }
        dependencies = dependencies + jar + "\n";

        return
        "\t\t<dependency>\n" +
        "\t\t\t<artifactId>.dep" + dependencies.split(",").length + ".</artifactId>\n" +
        "\t\t\t<groupId>..</groupId>\n" +
        "\t\t\t<version>1</version>\n" +
        "\t\t\t<scope>system</scope>\n" +
        "\t\t\t<systemPath>" + jar.replace("file:/", "").replace("jar:", "") + "</systemPath>\n" +
        "\t\t</dependency>";
    }

    public String getDependencies() {
        return dependencies;
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
        return output.getNativeType() == input.getNativeType() ? buffername + ".getNativeType()":("clijx." + output.getNativeType().toString());
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
                IJ.log("Parsing directory... " + file);
                parseSubFolders(file);
            } else {
                IJ.log("Parsing file... " + file);
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
        content = content.replace("Image input, ByRef Image destination, enter_parameters_here", enter_parameters_here);
        content = content.replace("ClearCLBuffer pushed, ClearCLBuffer result, Float enter_typed_parameters_here", enter_typed_parameters_here);
        content = content.replace("null, null, 1/*enter_default_values_here*/", enter_default_values_here);
        content = content.replace("(ClearCLBuffer) args[0], (ClearCLBuffer) args[1], 1f /*enter_value_parsers_here*/", enter_value_parsers_here);
        content = content.replace("enter_function_name_here", enter_function_name_here);
        content = content.replace("<!-- enter_custom_dependencies_here -->", enter_custom_dependencies_here);

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
        String[] command = {AssistantOptions.getInstance().getGitExecutable(), "clone", "--depth", "1", "--branch", VersionUtils.getVersion(MavenJavaProjectGenerator.class) , url, target_directory.replace(" ", "\\ ")};
        AssistantUtilities.execute(target_directory, command);
    }

    public static void mvn_package(String target_directory) {
        String[] command = {AssistantOptions.getInstance().getMavenExecutable(), "-Dmaven.compiler.fork=true", "-Dmaven.compiler.executable=" + AssistantOptions.getInstance().getJdkHome() + "bin/javac", "package"};
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
        IJ.log("Source: " + jar_source_file);
        IJ.log("Target: " + jar_target_file);

        return new File(jar_source_file).renameTo(new File(jar_target_file));
    }

    public String getJarFilename() {
        return "clijx-assistant-" + enter_lower_case_plugin_name_here + "_-" + VersionUtils.getVersion(this.getClass()) + ".jar";
    }


    HashMap<ClearCLBuffer, String> image_map = new HashMap<>();
    private String makeImageID(ClearCLBuffer target) {
        if (!image_map.keySet().contains(target)) {
            image_map.put(target, "image" + (image_map.size() + 1));
        }
        return image_map.get(target);
    }
    private boolean idExists(ClearCLBuffer target) {
        return image_map.keySet().contains(target);
    }
}
