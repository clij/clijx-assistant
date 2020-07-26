package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection;
import net.haesleinhuepf.clijx.incubator.interactive.generated.RigidTransform;
import net.haesleinhuepf.clijx.incubator.interactive.generated.ThresholdDoG;
import net.haesleinhuepf.clijx.incubator.interactive.generated.TopHat;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.CylinderTransform;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.SphereTransform;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface MakeIsotropicSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                MaximumZProjection.class,
SphereTransform.class,
ThresholdDoG.class,
RigidTransform.class,
CylinderTransform.class,
TopHat.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                TopHat.class
        };
    }
}
