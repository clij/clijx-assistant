package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class DifferenceOfGaussian extends AbstractIncubatorPlugin implements SuggestedPlugin {

    public DifferenceOfGaussian() {
        super(new net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian3D());
    }

    public Class[] suggestedNextSteps() {
        return new Class[] {
            net.haesleinhuepf.clincubator.interactive.generated.MaximumImageAndScalar.class
        };
    }

    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            
        };
    }
}