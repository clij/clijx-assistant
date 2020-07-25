package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface ConnectedComponentsLabelingSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.ExcludeLabelsOnEdges.class,
net.haesleinhuepf.clincubator.interactive.generated.ExtendLabelsWithMaximumRadius.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.BinaryNot.class,
net.haesleinhuepf.clincubator.interactive.generated.ThresholdDoG.class,
net.haesleinhuepf.clincubator.interactive.generated.ThresholdOtsu.class
        };
    }
}
