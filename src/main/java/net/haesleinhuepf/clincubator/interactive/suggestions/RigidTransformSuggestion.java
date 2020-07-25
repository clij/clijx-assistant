package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface RigidTransformSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.handcrafted.SphereTransform.class,
net.haesleinhuepf.clincubator.interactive.handcrafted.CylinderTransform.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.handcrafted.MakeIsotropic.class
        };
    }
}
