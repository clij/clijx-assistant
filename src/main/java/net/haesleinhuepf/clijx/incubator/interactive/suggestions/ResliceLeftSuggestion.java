package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection;
import net.haesleinhuepf.clijx.incubator.interactive.generated.RotateRight;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ResliceLeftSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                MaximumZProjection.class,
RotateRight.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                
        };
    }
}
