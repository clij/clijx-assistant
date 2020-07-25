package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface LabelSpotsSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.LabelVoronoiOctagon.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                
        };
    }
}
