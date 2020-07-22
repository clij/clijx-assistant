package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.suggestions.FindAndLabelMaximaSuggestion;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class FindAndLabelMaxima extends AbstractIncubatorPlugin implements FindAndLabelMaximaSuggestion {

    public FindAndLabelMaxima() {
        super(new net.haesleinhuepf.clijx.plugins.FindAndLabelMaxima());
    }

}
