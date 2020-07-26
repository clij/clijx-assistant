package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.ExcludeLabelsOutsideSizeRange;
import net.haesleinhuepf.clijx.incubator.interactive.generated.ExtendLabelingViaVoronoi;
import net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection;
import net.haesleinhuepf.clijx.incubator.interactive.generated.ThresholdDoG;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface DetectAndLabelMaximaSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                ExtendLabelingViaVoronoi.class,
ExcludeLabelsOutsideSizeRange.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                MaximumZProjection.class,
ThresholdDoG.class
        };
    }
}
