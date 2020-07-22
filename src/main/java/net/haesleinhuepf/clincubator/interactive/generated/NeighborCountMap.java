package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.suggestions.NeighborCountMapSuggestion;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class NeighborCountMap extends AbstractIncubatorPlugin implements NeighborCountMapSuggestion {

    public NeighborCountMap() {
        super(new net.haesleinhuepf.clijx.plugins.NeighborCountMap());
    }

}
