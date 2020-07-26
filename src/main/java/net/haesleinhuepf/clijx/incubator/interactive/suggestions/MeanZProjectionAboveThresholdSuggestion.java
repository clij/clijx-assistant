package net.haesleinhuepf.clijx.incubator.interactive.suggestions;

import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.CylinderTransform;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.SphereTransform;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;

// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public interface MeanZProjectionAboveThresholdSuggestion extends SuggestedPlugin {
    default Class[] suggestedNextSteps() {
        return new Class[] {
                
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                SphereTransform.class,
CylinderTransform.class
        };
    }
}
