package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ThresholdOtsuSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.ConnectedComponentsLabeling.class,
net.haesleinhuepf.clincubator.interactive.generated.VoronoiOctagon.class,
net.haesleinhuepf.clincubator.interactive.generated.BinaryNot.class,
net.haesleinhuepf.clincubator.interactive.generated.ParametricWatershed.class,
net.haesleinhuepf.clincubator.interactive.generated.BinaryEdgeDetection.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.SubtractImageFromScalar.class,
net.haesleinhuepf.clincubator.interactive.generated.GaussianBlur.class
        };
    }
}
