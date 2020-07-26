package net.haesleinhuepf.clijx.incubator.interactive.generated;

import net.haesleinhuepf.clijx.incubator.interactive.suggestions.MedianZProjectionSuggestion;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class MedianZProjection extends AbstractIncubatorPlugin implements MedianZProjectionSuggestion {

    public MedianZProjection() {
        super(new net.haesleinhuepf.clij2.plugins.MedianZProjection());
    }

}