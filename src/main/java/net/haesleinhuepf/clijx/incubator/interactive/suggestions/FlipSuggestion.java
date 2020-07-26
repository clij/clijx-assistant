package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.CopySlice;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface FlipSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                CopySlice.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                
        };
    }
}
