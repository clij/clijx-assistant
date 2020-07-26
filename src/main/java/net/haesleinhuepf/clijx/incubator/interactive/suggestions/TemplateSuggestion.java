package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

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
