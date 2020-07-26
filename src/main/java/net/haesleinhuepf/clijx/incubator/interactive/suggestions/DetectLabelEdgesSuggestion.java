package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.ExtendLabelingViaVoronoi;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface DetectLabelEdgesSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                ExtendLabelingViaVoronoi.class
        };
    }
}
