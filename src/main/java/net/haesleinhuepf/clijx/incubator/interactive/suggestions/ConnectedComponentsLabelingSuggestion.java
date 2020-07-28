package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clijx/incubator/PluginGenerator.java for details.
public interface ConnectedComponentsLabelingSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ExcludeLabelsOnEdges.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ExtendLabelsWithMaximumRadius.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clijx.incubator.interactive.generated.BinaryNot.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ThresholdDoG.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ParametricWatershed.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ThresholdOtsu.class
        };
    }
}
