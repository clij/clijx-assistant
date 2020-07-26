package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection;
import net.haesleinhuepf.clijx.incubator.interactive.generated.MeanZProjectionAboveThreshold;
import net.haesleinhuepf.clijx.incubator.interactive.generated.RigidTransform;
import net.haesleinhuepf.clijx.incubator.interactive.generated.TransposeXY;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.MakeIsotropic;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface SphereTransformSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                MaximumZProjection.class,
MeanZProjectionAboveThreshold.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                MakeIsotropic.class,
RigidTransform.class,
TransposeXY.class
        };
    }
}
