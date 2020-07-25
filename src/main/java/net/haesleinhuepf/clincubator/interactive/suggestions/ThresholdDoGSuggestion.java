package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface ThresholdDoGSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.ConnectedComponentsLabeling.class,
net.haesleinhuepf.clincubator.interactive.generated.DetectAndLabelMaxima.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.MaximumZProjection.class,
net.haesleinhuepf.clincubator.interactive.handcrafted.MakeIsotropic.class,
net.haesleinhuepf.clincubator.interactive.generated.TopHat.class
        };
    }
}
