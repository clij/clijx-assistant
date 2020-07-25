package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.suggestions.IntensityCorrectionSuggestion;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class IntensityCorrection extends AbstractIncubatorPlugin implements IntensityCorrectionSuggestion {

    public IntensityCorrection() {
        super(new net.haesleinhuepf.clijx.plugins.IntensityCorrection());
    }

}
