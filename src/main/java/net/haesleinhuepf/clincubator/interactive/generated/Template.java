package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.suggestions.TemplateSuggestion;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

//@Plugin(type = SuggestedPlugin.class)
public class Template extends AbstractIncubatorPlugin implements TemplateSuggestion {

    public Template() {
        super(new net.haesleinhuepf.clij2.plugins.Mean3DBox());
    }

}
