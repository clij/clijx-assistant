package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface TemplateSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                /*SUGGESTED_NEXT_STEPS*/
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                /*SUGGESTED_PREVIOUS_STEPS*/
        };
    }
}
