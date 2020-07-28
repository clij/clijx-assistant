package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clijx/incubator/PluginGenerator.java for details.
public interface DrawDistanceMeshBetweenTouchingLabelsSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clijx.incubator.interactive.generated.ExtendLabelingViaVoronoi.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ExtendLabelsWithMaximumRadius.class
        };
    }
}
