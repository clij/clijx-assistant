package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.suggestions.StandardDeviationZProjectionSuggestion;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class StandardDeviationZProjection extends AbstractIncubatorPlugin implements StandardDeviationZProjectionSuggestion {

    public StandardDeviationZProjection() {
        super(new net.haesleinhuepf.clij2.plugins.StandardDeviationZProjection());
    }

}
