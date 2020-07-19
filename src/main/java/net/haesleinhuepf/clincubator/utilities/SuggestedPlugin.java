package net.haesleinhuepf.clincubator.utilities;

import ij.process.ImageProcessor;
import org.scijava.plugin.SciJavaPlugin;

public interface SuggestedPlugin extends SciJavaPlugin {

    <T extends SuggestedPlugin> Class<T>[] suggestedNextSteps();
    <T extends SuggestedPlugin> Class<T>[] suggestedPreviousSteps();

    void run(String command);
}
