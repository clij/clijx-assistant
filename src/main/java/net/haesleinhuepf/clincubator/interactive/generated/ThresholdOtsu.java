package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class ThresholdOtsu extends AbstractIncubatorPlugin implements SuggestedPlugin {

    public ThresholdOtsu() {
        super(new net.haesleinhuepf.clij2.plugins.ThresholdOtsu());
    }

    public Class[] suggestedNextSteps() {
        return new Class[] {
            net.haesleinhuepf.clincubator.interactive.generated.ConnectedComponentsLabeling.class,
net.haesleinhuepf.clincubator.interactive.generated.BinaryNot.class,
net.haesleinhuepf.clincubator.interactive.generated.BinaryEdgeDetection.class
        };
    }

    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            
        };
    }
}
