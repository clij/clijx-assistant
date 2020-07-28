package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clijx/incubator/PluginGenerator.java for details.
public interface TopHatSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clijx.incubator.interactive.generated.ThresholdDoG.class,
net.haesleinhuepf.clijx.incubator.interactive.handcrafted.MakeIsotropic.class,
net.haesleinhuepf.clijx.incubator.interactive.handcrafted.CylinderTransform.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ResliceTop.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clijx.incubator.interactive.generated.Rotate.class,
net.haesleinhuepf.clijx.incubator.interactive.handcrafted.MakeIsotropic.class
        };
    }
}
