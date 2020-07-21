package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class NonLocalMeans extends AbstractIncubatorPlugin implements SuggestedPlugin {

    public NonLocalMeans() {
        super(new net.haesleinhuepf.clijx.plugins.NonLocalMeans());
    }

    public Class[] suggestedNextSteps() {
        return new Class[] {
            
        };
    }

    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            
        };
    }
}
