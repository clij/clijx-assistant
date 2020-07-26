package net.haesleinhuepf.clijx.incubator.interactive.generated;

import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.interactive.suggestions.GreaterOrEqualConstantSuggestion;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class GreaterOrEqualConstant extends AbstractIncubatorPlugin implements GreaterOrEqualConstantSuggestion {

    public GreaterOrEqualConstant() {
        super(new net.haesleinhuepf.clij2.plugins.GreaterOrEqualConstant());
    }

}
