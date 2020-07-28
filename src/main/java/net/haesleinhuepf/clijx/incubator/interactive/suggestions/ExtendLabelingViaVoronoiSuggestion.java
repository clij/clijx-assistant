package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clijx/incubator/PluginGenerator.java for details.
public interface ExtendLabelingViaVoronoiSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clijx.incubator.interactive.generated.DrawDistanceMeshBetweenTouchingLabels.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.DetectLabelEdges.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clijx.incubator.interactive.generated.DetectAndLabelMaxima.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ExcludeLabelsOutsideSizeRange.class
        };
    }
}
