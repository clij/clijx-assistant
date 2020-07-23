package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.suggestions.MeanZProjectionAboveThresholdSuggestion;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class MeanZProjectionAboveThreshold extends AbstractIncubatorPlugin implements MeanZProjectionAboveThresholdSuggestion {

    public MeanZProjectionAboveThreshold() {
        super(new net.haesleinhuepf.clijx.plugins.MeanZProjectionAboveThreshold());
    }

}
