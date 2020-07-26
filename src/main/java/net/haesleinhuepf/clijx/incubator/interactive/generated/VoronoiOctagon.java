package net.haesleinhuepf.clijx.incubator.interactive.generated;

import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.interactive.suggestions.VoronoiOctagonSuggestion;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class VoronoiOctagon extends AbstractIncubatorPlugin implements VoronoiOctagonSuggestion {

    public VoronoiOctagon() {
        super(new net.haesleinhuepf.clij2.plugins.VoronoiOctagon());
    }

}
