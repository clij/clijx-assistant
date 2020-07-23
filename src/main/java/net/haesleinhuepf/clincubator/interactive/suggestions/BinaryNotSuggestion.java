package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface BinaryNotSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.ConnectedComponentsLabeling.class,
net.haesleinhuepf.clincubator.interactive.generated.DistanceMap.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.ThresholdHuang.class,
net.haesleinhuepf.clincubator.interactive.generated.ThresholdDefault.class,
net.haesleinhuepf.clincubator.interactive.generated.ThresholdOtsu.class
        };
    }
}
