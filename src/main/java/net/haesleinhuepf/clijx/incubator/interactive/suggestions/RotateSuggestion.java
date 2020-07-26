package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.generated.MaximumZProjection;
import net.haesleinhuepf.clijx.incubator.interactive.generated.TopHat;
import net.haesleinhuepf.clijx.incubator.interactive.generated.Translate;
import net.haesleinhuepf.clijx.incubator.interactive.generated.TransposeXY;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.CylinderTransform;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface RotateSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                MaximumZProjection.class,
CylinderTransform.class,
TopHat.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                Translate.class,
TransposeXY.class
        };
    }
}
