package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ExtendLabelingViaVoronoiSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.DrawDistanceMeshBetweenTouchingLabels.class,
net.haesleinhuepf.clincubator.interactive.generated.DetectLabelEdges.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.DetectAndLabelMaxima.class,
net.haesleinhuepf.clincubator.interactive.generated.ExcludeLabelsOutsideSizeRange.class
        };
    }
}
