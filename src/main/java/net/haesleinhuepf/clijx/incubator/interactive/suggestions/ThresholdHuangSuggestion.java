package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.BinaryNot;
import net.haesleinhuepf.clijx.incubator.interactive.generated.Mean;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ThresholdHuangSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                BinaryNot.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                Mean.class
        };
    }
}
