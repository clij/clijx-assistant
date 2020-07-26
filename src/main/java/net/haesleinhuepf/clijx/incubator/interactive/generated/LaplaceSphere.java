package net.haesleinhuepf.clijx.incubator.interactive.generated;

import net.haesleinhuepf.clijx.incubator.interactive.suggestions.LaplaceSphereSuggestion;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class LaplaceSphere extends AbstractIncubatorPlugin implements LaplaceSphereSuggestion {

    public LaplaceSphere() {
        super(new net.haesleinhuepf.clijx.plugins.LaplaceSphere());
    }

}
