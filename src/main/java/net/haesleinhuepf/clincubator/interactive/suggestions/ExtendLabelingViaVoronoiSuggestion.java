package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface ExtendLabelingViaVoronoiSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.DrawMeshBetweenNeighboringLabels.class,
net.haesleinhuepf.clincubator.interactive.generated.DetectLabelEdges.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.DetectAndLabelMaxima.class
        };
    }
}
