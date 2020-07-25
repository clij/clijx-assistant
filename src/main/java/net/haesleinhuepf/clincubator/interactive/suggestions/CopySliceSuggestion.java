package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface CopySliceSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.Sobel.class,
net.haesleinhuepf.clincubator.interactive.generated.Flip.class,
net.haesleinhuepf.clincubator.interactive.generated.MaximumZProjection.class
        };
    }
}
