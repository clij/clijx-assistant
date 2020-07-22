package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.suggestions.ThresholdPercentileSuggestion;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class ThresholdPercentile extends AbstractIncubatorPlugin implements ThresholdPercentileSuggestion {

    public ThresholdPercentile() {
        super(new net.haesleinhuepf.clij2.plugins.ThresholdPercentile());
    }

}
