package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class TopHat extends AbstractIncubatorPlugin implements SuggestedPlugin {

    public TopHat() {
        super(new net.haesleinhuepf.clij2.plugins.TopHatBox());
    }

    public Class[] suggestedNextSteps() {
        return new Class[] {
            net.haesleinhuepf.clincubator.interactive.generated.ResliceTop.class
        };
    }

    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            
        };
    }
}