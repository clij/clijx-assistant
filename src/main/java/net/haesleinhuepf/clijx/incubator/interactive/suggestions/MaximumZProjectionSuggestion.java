package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.*;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.CylinderTransform;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.MakeIsotropic;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.SphereTransform;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface MaximumZProjectionSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                DetectAndLabelMaxima.class,
ThresholdDoG.class,
CopySlice.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                Rotate.class,
SphereTransform.class,
MakeIsotropic.class,
ResliceLeft.class,
CylinderTransform.class,
DrawDistanceMeshBetweenTouchingLabels.class
        };
    }
}
