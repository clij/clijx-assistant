package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface CylinderTransformSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.generated.MeanZProjection.class,
net.haesleinhuepf.clincubator.interactive.generated.MaximumZProjection.class,
net.haesleinhuepf.clincubator.interactive.generated.MeanZProjectionAboveThreshold.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.Rotate.class,
net.haesleinhuepf.clincubator.interactive.handcrafted.MakeIsotropic.class,
net.haesleinhuepf.clincubator.interactive.generated.RigidTransform.class,
net.haesleinhuepf.clincubator.interactive.generated.TopHat.class
        };
    }
}
