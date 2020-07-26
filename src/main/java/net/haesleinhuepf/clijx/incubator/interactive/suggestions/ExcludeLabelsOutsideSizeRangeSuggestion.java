package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.DetectAndLabelMaxima;
import net.haesleinhuepf.clijx.incubator.interactive.generated.ExtendLabelingViaVoronoi;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ExcludeLabelsOutsideSizeRangeSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                ExtendLabelingViaVoronoi.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                DetectAndLabelMaxima.class
        };
    }
}
