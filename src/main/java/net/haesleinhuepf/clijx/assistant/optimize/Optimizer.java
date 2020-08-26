package net.haesleinhuepf.clijx.assistant.optimize;

import net.haesleinhuepf.clijx.assistant.utilities.Logger;
import org.apache.commons.math3.analysis.MultivariateFunction;

import java.awt.event.ActionListener;

public interface Optimizer {
    double[] optimize(double[] current, Workflow workflow, int[] parameter_index_map, MultivariateFunction fitness, Logger logger);
}
