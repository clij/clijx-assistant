package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.*;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ThresholdOtsuSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                ConnectedComponentsLabeling.class,
VoronoiOctagon.class,
BinaryNot.class,
ParametricWatershed.class,
BinaryEdgeDetection.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                SubtractImageFromScalar.class,
GaussianBlur.class
        };
    }
}
