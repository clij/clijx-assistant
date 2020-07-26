package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.*;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface BinaryNotSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                ConnectedComponentsLabeling.class,
DistanceMap.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                VoronoiOctagon.class,
ThresholdHuang.class,
ThresholdDefault.class,
ThresholdOtsu.class
        };
    }
}
