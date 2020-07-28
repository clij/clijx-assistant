package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clijx/incubator/PluginGenerator.java for details.
public interface ThresholdOtsuSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clijx.incubator.interactive.generated.ConnectedComponentsLabeling.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.VoronoiOctagon.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.BinaryNot.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ParametricWatershed.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.BinaryEdgeDetection.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clijx.incubator.interactive.generated.SubtractImageFromScalar.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.GaussianBlur.class
        };
    }
}
