package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface FlipSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.CopySlice.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                
        };
    }
}
