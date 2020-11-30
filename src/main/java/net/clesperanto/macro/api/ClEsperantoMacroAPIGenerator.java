package net.clesperanto.macro.api;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPluginService;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.scijava.Context;

import java.util.ArrayList;

public class ClEsperantoMacroAPIGenerator {

    public static void main(String ... args ) {
        System.out.println(new ClEsperantoMacroAPIGenerator().toString());
    }

    @Override
    public String toString() {


        CLIJMacroPluginService service = new Context(CLIJMacroPluginService.class).getService(CLIJMacroPluginService.class);

        StringBuilder macro_builder = new StringBuilder();
        macro_builder.append(
                "var __clesperanto_initialized = false;" +
                "function cle_init(cl_device) {\n" +
                "    run(\"CLIJ2 Macro Extensions\", \"cl_device=[\" + cl_device + \"]\");\n" +
                "    __clesperanto_initialized = true;\n"+
                "}\n" +
                "function __cle_init() {\n" +
                "    if (!__clesperanto_initialized) {\n" +
                "        cle_init(\"\");\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "function cle_clear() {\n" +
                "    Ext.CLIJ2_clear();\n"+
                "}\n" +
                "\n"
        );

        int macro_methodCount = 0;
        ArrayList<String> macro_methods = new ArrayList<>();
        for (String method_name : service.getCLIJMethodNames()) {
            //System.out.println(method_name);

            boolean take_it = false;

            // take all clij2 methods
            if (method_name.startsWith("CLIJ2_")) {
                take_it = true;
            }

            // take only clijx methods if there is no clij2 counter part
            if (method_name.startsWith("CLIJx_")) {
                if (service.getCLIJMacroPlugin(method_name.replace("CLIJx_", "CLIJ2_")) == null) {
                    take_it = true;
                }
            }

            if (take_it) {
                CLIJMacroPlugin plugin = service.getCLIJMacroPlugin(method_name);

                // -----------------------------------------------------------------------------------------
                // macro
                if (plugin == null) {
                    macro_builder.append("// No plugin found for " + method_name + "\n\n");
                } else {
                    String macro_parameters = plugin.getParameterHelpText();
                    macro_parameters = macro_parameters.replace(", ", ",");
                    macro_parameters = macro_parameters.replace(" ", "_");
                    macro_parameters = macro_parameters.replace(",", ", ");

                    if (plugin instanceof OffersDocumentation) {
                        macro_builder.append("// " + ((OffersDocumentation) plugin).getDescription().replace("\n", "\n// ") + "\n");
                    }

                    String pythonized_method_name = pythonize(method_name);
                    macro_builder.append(
                            "function " + pythonized_method_name + "(" + macro_parameters + ") {\n" +
                                    "    __cle_init();\n"
                    );

                    macro_builder.append(
                            "    Ext." + method_name + "(" + macro_parameters + ");\n"
                    );

                    macro_builder.append("}\n\n");


                    macro_methodCount++;
                }




            }
        }
        macro_builder.append("// " + macro_methodCount + " methods generated.\n");
        //IJ.log(macro_builder.toString());

        return macro_builder.toString();
    }



    public static String pythonize(String methodName) {
        String new_name = AssistantUtilities.niceName(methodName).trim()
                .toLowerCase()
                .replace(" ", "_")
                .replace("clij2_", "")
                .replace("clij_", "")
                .replace("clijx_", "")
                ;

        // special cases
        if (new_name.compareTo("print") == 0) {
            new_name = "print_image";
        }

        return new_name;
    }

}
