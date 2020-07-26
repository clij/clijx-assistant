package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.*;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.MakeIsotropic;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface CylinderTransformSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                MeanZProjection.class,
MaximumZProjection.class,
MeanZProjectionAboveThreshold.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                Rotate.class,
MakeIsotropic.class,
RigidTransform.class,
TopHat.class
        };
    }
}
