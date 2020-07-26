package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.ResliceTop;
import net.haesleinhuepf.clijx.incubator.interactive.generated.Rotate;
import net.haesleinhuepf.clijx.incubator.interactive.generated.ThresholdDoG;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.CylinderTransform;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.MakeIsotropic;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface TopHatSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                ThresholdDoG.class,
MakeIsotropic.class,
CylinderTransform.class,
ResliceTop.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                Rotate.class,
MakeIsotropic.class
        };
    }
}
