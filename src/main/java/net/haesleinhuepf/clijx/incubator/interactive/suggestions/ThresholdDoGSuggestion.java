package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.ConnectedComponentsLabeling;
import net.haesleinhuepf.clijx.incubator.interactive.generated.DetectAndLabelMaxima;
import net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection;
import net.haesleinhuepf.clijx.incubator.interactive.generated.TopHat;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.MakeIsotropic;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface ThresholdDoGSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                ConnectedComponentsLabeling.class,
DetectAndLabelMaxima.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                MaximumZProjection.class,
MakeIsotropic.class,
TopHat.class
        };
    }
}
