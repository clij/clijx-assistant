package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface BinaryNotSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.ConnectedComponentsLabeling.class,
net.haesleinhuepf.clincubator.interactive.generated.DistanceMap.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.VoronoiOctagon.class,
net.haesleinhuepf.clincubator.interactive.generated.ThresholdHuang.class,
net.haesleinhuepf.clincubator.interactive.generated.ThresholdDefault.class,
net.haesleinhuepf.clincubator.interactive.generated.ThresholdOtsu.class
        };
    }
}
