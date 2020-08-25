package net.haesleinhuepf.clijx.assistant.optimize;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import org.apache.commons.math3.analysis.MultivariateFunction;

import java.util.Arrays;

public class BinaryImageFitnessFunction implements MultivariateFunction {

    private CLIJ2 clij2;
    private Workflow workflow;
    private final int[] plugin_indices;
    private final int[] parameter_indices;
    private final int[] parameter_index_map;
    private final ClearCLBuffer ground_truth;
    private ClearCLBuffer mask;

    private int num_dimensions = 0;

    public BinaryImageFitnessFunction(CLIJ2 clij2, Workflow workflow, int[] parameter_index_map, ClearCLBuffer ground_truth, ClearCLBuffer mask) {
        this.clij2 = clij2;
        this.workflow = workflow;
        this.plugin_indices = workflow.getPluginIndices();
        this.parameter_indices = workflow.getParameterIndices();
        this.parameter_index_map = parameter_index_map;
        this.ground_truth = ground_truth;
        this.mask = mask;

        for (int i = 0; i < parameter_index_map.length; i++) {
            //System.out.println("S: " + parameter_index_map[i]);
            if (num_dimensions < parameter_index_map[i] + 1) {
                num_dimensions = parameter_index_map[i] + 1;
            }
        }
        //System.out.println("Num dim: " + num_dimensions);
    }

    @Override
    public double value(double[] doubles) {
        System.out.print("Try: " + Arrays.toString(doubles));
        for (int i = 0; i < doubles.length; i++) {
            for (int j = 0; j < plugin_indices.length; j++) {
                if (i == parameter_index_map[j]) {
                    workflow.setNumericParameter(
                            plugin_indices[j],
                            parameter_indices[j],
                            doubles[i]
                    );
                }
            }
        }
        workflow.compute();

        ClearCLBuffer temp1 = clij2.create(ground_truth);
        ClearCLBuffer temp2 = clij2.create(ground_truth);

        //clij2.show(workflow.getOutput(), "output");

        clij2.copy(workflow.getOutput(), temp1);
        clij2.addImageAndScalar(temp1, temp2, 1);
        clij2.mask(temp2, mask, temp1);

        //clij2.show(temp1, "temp1");
        //clij2.show(ground_truth, "grout");

        double result_mse = clij2.meanSquaredError(temp1, ground_truth);

        temp1.close();
        temp2.close();

        System.out.println(" -> " + result_mse);
        return result_mse;
    }

    public double[] getCurrent() {
        double[] doubles = new double[num_dimensions];
        for (int i = 0; i < doubles.length; i++) {
            for (int j = 0; j < plugin_indices.length; j++) {
                if (i == parameter_index_map[j]) {
                    doubles[i] = workflow.getNumericParameter(
                            plugin_indices[j],
                            parameter_indices[j]
                            );
                }
            }
        }
        //System.out.println("Current: " + Arrays.toString(doubles));
        return doubles;
    }

    public int getNumDimensions() {
        return num_dimensions;
    }
}
