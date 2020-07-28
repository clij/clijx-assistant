package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clijx/incubator/PluginGenerator.java for details.
public interface MakeIsotropicSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection.class,
net.haesleinhuepf.clijx.incubator.interactive.handcrafted.SphereTransform.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.ThresholdDoG.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.RigidTransform.class,
net.haesleinhuepf.clijx.incubator.interactive.handcrafted.CylinderTransform.class,
net.haesleinhuepf.clijx.incubator.interactive.generated.TopHat.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clijx.incubator.interactive.generated.TopHat.class
        };
    }
}
