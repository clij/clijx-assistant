package net.haesleinhuepf.clijx.incubator.utilities;

import org.scijava.plugin.SciJavaPlugin;

public interface SuggestedPlugin extends SciJavaPlugin {

    <T extends SuggestedPlugin> Class<T>[] suggestedNextSteps();
    <T extends SuggestedPlugin> Class<T>[] suggestedPreviousSteps();

    void run(String command);
}
