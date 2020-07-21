package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class Maximum extends AbstractIncubatorPlugin implements SuggestedPlugin {

    public Maximum() {
        super(new net.haesleinhuepf.clij2.plugins.Maximum3DBox());
    }

    public Class[] suggestedNextSteps() {
        return new Class[] {
            
        };
    }

    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            net.haesleinhuepf.clincubator.interactive.generated.Minimum.class
        };
    }
}
