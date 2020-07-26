package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.Invert;
import net.haesleinhuepf.clijx.incubator.interactive.generated.TransposeXY;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface MultiplyImageAndScalarSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                TransposeXY.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                Invert.class
        };
    }
}
