package net.haesleinhuepf.clijx.assistant.optimize;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.plugins.Copy;
import net.haesleinhuepf.clij2.plugins.GaussianBlur2D;
import net.haesleinhuepf.clij2.plugins.GreaterConstant;

import java.util.ArrayList;
import java.util.Arrays;

public class Workflow {

    ArrayList<CLIJMacroPlugin> plugins;
    ArrayList<Object[]> parameters;
    Object[][] args;

    public Workflow(CLIJMacroPlugin[] clijPlugins, Object[][] args) {
        plugins = new ArrayList<CLIJMacroPlugin>();
        parameters = new ArrayList<Object[]>();

        for (int i = 0; i < clijPlugins.length; i++) {
            plugins.add(clijPlugins[i]);
            parameters.add(args[i]);
        }
        this.args = args;
    }

    @Deprecated
    public Workflow(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output) {
        plugins = new ArrayList<>();
        parameters = new ArrayList<>();

        // blur
        ClearCLBuffer temp1 = clij2.create(input);

        GaussianBlur2D blur = new GaussianBlur2D();
        blur.setCLIJ2(clij2);

        Object[] blur_parameters = new Object[]{input, temp1, new Double(1), new Double(1)};
        blur.setArgs(blur_parameters);

        plugins.add(blur);
        parameters.add(blur_parameters);

        // threshold
        GreaterConstant threshold = new GreaterConstant();
        threshold.setCLIJ2(clij2);

        Object[] threshold_parameters = new Object[]{temp1, output, new Double(128)};
        threshold.setArgs(threshold_parameters);

        plugins.add(threshold);
        parameters.add(threshold_parameters);
    }


    public void setNumericParameter(int plugin_index, int parameter_index, Double parameter_value) {
        //System.out.println("Set " + plugin_index + "/" + parameter_index + " = " + parameter_value);

        parameters.get(plugin_index)[parameter_index] = Math.abs(parameter_value);
    }

    public Double getNumericParameter(int plugin_index, int parameter_index) {
        return Double.parseDouble("" + parameters.get(plugin_index)[parameter_index]);
    }


    public void compute() {
        int count = 0;
        for (CLIJMacroPlugin plugin : plugins) {
            if (plugin instanceof CLIJOpenCLProcessor && (count > 0)) { // special cases: don't execute initial step (Starting point)
                ((CLIJOpenCLProcessor) plugin).executeCL();
            }
            count ++;
        }
    }

    public ClearCLBuffer getOutput() {
        return (ClearCLBuffer) parameters.get(parameters.size() - 1)[1];
    }

    public String[] getNumericParameterNames() {
        ArrayList<String> parameter_list = new ArrayList<>();
        for (CLIJMacroPlugin plugin : plugins) {
            String[] parameterHelpTexts = plugin.getParameterHelpText().split(",");
            for (String parameterHelpText : parameterHelpTexts) {
                while (parameterHelpText.contains("  ")) {
                    parameterHelpText = parameterHelpText.replace("  ", "");
                }
                String[] temp = parameterHelpText.split(" ");
                if (temp[temp.length - 2].compareTo("Number") == 0) {
                    parameter_list.add(plugin.getName() + "_" + temp[temp.length - 1]);
                }
            }
        }

        String[] result = new String[parameter_list.size()];
        parameter_list.toArray(result);
        return result;
    }

    public int[] getPluginIndices() {
        String[] parameterNames = getNumericParameterNames();
        int[] result = new int[parameterNames.length];

        int count = 0;
        int plugin_count = 0;
        for (CLIJMacroPlugin plugin : plugins) {
            String[] parameterHelpTexts = plugin.getParameterHelpText().split(",");
            for (String parameterHelpText : parameterHelpTexts) {
                while (parameterHelpText.contains("  ")) {
                    parameterHelpText = parameterHelpText.replace("  ", "");
                }
                String[] temp = parameterHelpText.split(" ");
                if (temp[temp.length - 2].compareTo("Number") == 0) {
                    result[count] = plugin_count;
                    count++;
                }
            }
            plugin_count++;
        }
        return result;
    }

    public int[] getParameterIndices() {
        String[] parameterNames = getNumericParameterNames();
        int[] result = new int[parameterNames.length];

        int count = 0;
        for (CLIJMacroPlugin plugin : plugins) {
            String[] parameterHelpTexts = plugin.getParameterHelpText().split(",");

            int parameter_count = 0;
            for (String parameterHelpText : parameterHelpTexts) {
                while (parameterHelpText.contains("  ")) {
                    parameterHelpText = parameterHelpText.replace("  ", "");
                }
                String[] temp = parameterHelpText.split(" ");
                if (temp[temp.length - 2].compareTo("Number") == 0) {
                    result[count] = parameter_count;
                    count++;
                }
                parameter_count++;
            }
        }
        return result;
    }

    public ArrayList<CLIJMacroPlugin> getPlugins() {
        return plugins;
    }

    public Object[][] getArgs() {
        return args;
    }
}
