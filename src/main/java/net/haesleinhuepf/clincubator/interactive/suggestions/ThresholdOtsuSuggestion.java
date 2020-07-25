package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface ThresholdOtsuSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.ConnectedComponentsLabeling.class,
net.haesleinhuepf.clincubator.interactive.generated.VoronoiOctagon.class,
net.haesleinhuepf.clincubator.interactive.generated.BinaryNot.class,
net.haesleinhuepf.clincubator.interactive.generated.BinaryEdgeDetection.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.SubtractImageFromScalar.class
        };
    }
}
