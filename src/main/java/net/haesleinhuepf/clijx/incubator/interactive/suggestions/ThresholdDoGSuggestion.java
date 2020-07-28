package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clijx/incubator/PluginGenerator.java for details.
public interface ThresholdDoGSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clijx.incubator.interactive.generated.ConnectedComponentsLabeling.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.DetectAndLabelMaxima.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ParametricWatershed.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection.class,
net.haesleinhuepf.clijx.incubator.interactive.handcrafted.MakeIsotropic.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.TopHat.class
        };
    }
}
