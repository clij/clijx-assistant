package net.haesleinhuepf.clijx.incubator.optimize;

import org.apache.commons.math3.analysis.MultivariateFunction;

public interface Optimizer {
    double[] optimize(double[] current, Workflow workflow, int[] parameter_index_map, MultivariateFunction fitness);
}
