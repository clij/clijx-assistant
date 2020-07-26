package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.*;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ConnectedComponentsLabelingSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                ExcludeLabelsOnEdges.class,
ExtendLabelsWithMaximumRadius.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                BinaryNot.class,
ThresholdDoG.class,
ParametricWatershed.class,
ThresholdOtsu.class
        };
    }
}
