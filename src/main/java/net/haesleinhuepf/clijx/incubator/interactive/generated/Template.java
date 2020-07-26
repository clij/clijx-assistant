package net.haesleinhuepf.clijx.incubator.interactive.generated;

import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.interactive.suggestions.TemplateSuggestion;

//@Plugin(type = SuggestedPlugin.class)
public class Template extends AbstractIncubatorPlugin implements TemplateSuggestion {

    public Template() {
        super(new net.haesleinhuepf.clij2.plugins.Mean3DBox());
    }

}
