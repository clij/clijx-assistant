package net.haesleinhuepf.clincubator.interactive.suggestions;

import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface TopHatSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                net.haesleinhuepf.clincubator.interactive.handcrafted.MakeIsotropic.class,
net.haesleinhuepf.clincubator.interactive.handcrafted.CylinderTransform.class,
net.haesleinhuepf.clincubator.interactive.generated.ResliceTop.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                net.haesleinhuepf.clincubator.interactive.generated.Rotate.class,
net.haesleinhuepf.clincubator.interactive.handcrafted.MakeIsotropic.class
        };
    }
}
