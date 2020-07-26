package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.DetectAndLabelMaxima;
import net.haesleinhuepf.clijx.incubator.interactive.generated.DetectLabelEdges;
import net.haesleinhuepf.clijx.incubator.interactive.generated.DrawDistanceMeshBetweenTouchingLabels;
import net.haesleinhuepf.clijx.incubator.interactive.generated.ExcludeLabelsOutsideSizeRange;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ExtendLabelingViaVoronoiSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                DrawDistanceMeshBetweenTouchingLabels.class,
DetectLabelEdges.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                DetectAndLabelMaxima.class,
ExcludeLabelsOutsideSizeRange.class
        };
    }
}
