package net.haesleinhuepf.clijx.assistant.optimize;

import net.haesleinhuepf.clijx.assistant.utilities.Logger;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.*;
import org.apache.commons.math3.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomVectorGenerator;
import org.apache.commons.math3.random.UncorrelatedRandomVectorGenerator;

import java.util.ArrayList;
import java.util.Arrays;

import static net.haesleinhuepf.clijx.assistant.optimize.OptimizationUtilities.range;

public class GradientDescentOptimizer implements Optimizer {
    int iterations = 6;
    public GradientDescentOptimizer() {

    }
    public GradientDescentOptimizer(int iterations) {
        this.iterations = iterations;
    }

    @Override
    public double[] optimize(double[] current, Workflow workflow, int[] parameter_index_map, MultivariateFunction fitness, Logger logger) {
        GradientMultivariateOptimizer underlying = new NonLinearConjugateGradientOptimizer(NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE, new SimpleValueChecker(1e-10, 1e-10));

        logger.log("Start:        " + Arrays.toString(current) + "\t");

        for (int i = 0; i < iterations; i++) {

            double[] stdDev = range(current.length, workflow.getNumericParameterNames(), parameter_index_map, Math.pow(2, iterations / 2 - i - 1));
            System.out.println("Stddevs: " + Arrays.toString(stdDev) );


            RandomVectorGenerator generator = new UncorrelatedRandomVectorGenerator(current, stdDev, new GaussianRandomGenerator(new JDKRandomGenerator()));
            int nbStarts = 10;
            MultiStartMultivariateOptimizer optimizer = new MultiStartMultivariateOptimizer(underlying, nbStarts, generator);

            PointValuePair solution = optimizer.optimize(new MaxEval(1000), new ObjectiveFunction(fitness), new ObjectiveFunctionGradient(new GradientOfMultivariateFunction(fitness, stdDev)), GoalType.MINIMIZE, new InitialGuess(current));

            current = solution.getKey();

            logger.log("Intermediate: " + Arrays.toString(current) + "\t f = " + solution.getValue());
        }
        logger.log("Final:        " + Arrays.toString(current) + "\t");

        return current;
    }

}
