package net.haesleinhuepf.clijx.assistant.optimize;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;

public class GradientOfMultivariateFunction implements MultivariateVectorFunction {
    private MultivariateFunction function;
    private double[] steps;

    public GradientOfMultivariateFunction(MultivariateFunction function, double[] steps) {
        this.function = function;
        this.steps = steps;
    }

    @Override
    public double[] value(double[] point) throws IllegalArgumentException {
        double[] result = new double[point.length];
        for (int i = 0; i < result.length; i++) {
            double[] input = new double[point.length];

            System.arraycopy(point, 0, input, 0, input.length);
            input[i] -= steps[i];
            double a = function.value(input);

            System.arraycopy(point, 0, input, 0, input.length);
            input[i] += steps[i];
            double b = function.value(input);

            result[i] = (a - b) / steps[i] / 2;
        }
        return result;
    }
}
