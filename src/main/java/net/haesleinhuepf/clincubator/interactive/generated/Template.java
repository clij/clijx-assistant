package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

//@Plugin(type = SuggestedPlugin.class)
public class Template extends AbstractIncubatorPlugin implements SuggestedPlugin {

    public Template() {
        super(new net.haesleinhuepf.clij2.plugins.Mean3DBox());
    }

    public Class[] suggestedNextSteps() {
        return new Class[] {
            /*SUGGESTED_NEXT_STEPS*/
        };
    }

    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            /*SUGGESTED_PREVIOUS_STEPS*/
        };
    }
}
