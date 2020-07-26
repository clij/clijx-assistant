package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.ConnectedComponentsLabeling;
import net.haesleinhuepf.clijx.incubator.interactive.generated.ThresholdOtsu;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ParametricWatershedSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                ConnectedComponentsLabeling.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                ThresholdOtsu.class
        };
    }
}
