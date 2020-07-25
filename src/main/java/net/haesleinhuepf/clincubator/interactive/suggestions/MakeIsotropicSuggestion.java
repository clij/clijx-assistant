package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface MakeIsotropicSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.MaximumZProjection.class,
net.haesleinhuepf.clincubator.interactive.handcrafted.SphereTransform.class,
net.haesleinhuepf.clincubator.interactive.generated.ThresholdDoG.class,
net.haesleinhuepf.clincubator.interactive.generated.RigidTransform.class,
net.haesleinhuepf.clincubator.interactive.handcrafted.CylinderTransform.class,
net.haesleinhuepf.clincubator.interactive.generated.TopHat.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.TopHat.class
        };
    }
}
