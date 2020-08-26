package net.haesleinhuepf.clijx.assistant.optimize;

import net.haesleinhuepf.clijx.assistant.utilities.IJLogger;
import net.haesleinhuepf.clijx.assistant.utilities.Logger;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;

import java.util.Arrays;

import static net.haesleinhuepf.clijx.assistant.optimize.OptimizationUtilities.range;

public class SimplexOptimizer implements Optimizer {
    int iterations = 6;
    public SimplexOptimizer() {

    }
    public SimplexOptimizer(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public double[] optimize(double[] current, Workflow workflow, int[] parameter_index_map, MultivariateFunction fitness, Logger logger) {

        logger.log("Start:        " + Arrays.toString(current) + "\t");

        org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer optimizer = new org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer(-1, 1e-5);
        for (int i = 0; i < iterations; i++) {
            NelderMeadSimplex simplex = makeSimplexOptimizer(current.length, workflow.getNumericParameterNames(), parameter_index_map, Math.pow(2, iterations / 2 - i - 1));
            //double[] lowerBounds = new double[simplex.getDimension()];
            //double[] upperBounds = new double[simplex.getDimension()];
            //for (int b = 0; b < upperBounds.length; b++) {
            //    upperBounds[b] = Double.MAX_VALUE;
            //}
            //, new SimpleBounds(lowerBounds, upperBounds)
            PointValuePair solution = optimizer.optimize(new MaxEval(1000), new InitialGuess(current), simplex, new ObjectiveFunction(fitness), GoalType.MINIMIZE);

            current = solution.getKey();
            logger.log("Intermediate: " + Arrays.toString(current) + "\t f = " + solution.getValue());
        }
        logger.log("Final:        " + Arrays.toString(current) + "\t");
        return current;
    }

    private static NelderMeadSimplex makeSimplexOptimizer(int numDimensions, String[] numericParameterNames, int[] parameter_index_map, double factor) {
        double[] steps = range(numDimensions, numericParameterNames, parameter_index_map, factor);

        System.out.println("Step lengths: " + Arrays.toString(steps) );

        NelderMeadSimplex simplex = new NelderMeadSimplex(steps);
        return simplex;
    }

}
