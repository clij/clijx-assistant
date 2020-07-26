package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.ConnectedComponentsLabeling;
import net.haesleinhuepf.clijx.incubator.interactive.generated.DrawDistanceMeshBetweenTouchingLabels;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ExtendLabelsWithMaximumRadiusSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                DrawDistanceMeshBetweenTouchingLabels.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                ConnectedComponentsLabeling.class
        };
    }
}
