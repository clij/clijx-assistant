package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.
public class DetectAndLabelMaxima extends AbstractIncubatorPlugin implements SuggestedPlugin {

    public DetectAndLabelMaxima() {
        super(new net.haesleinhuepf.clijx.plugins.DetectAndLabelMaxima());
    }

    public Class[] suggestedNextSteps() {
        return new Class[] {
            net.haesleinhuepf.clincubator.interactive.generated.ExtendLabelingViaVoronoi.class
        };
    }

    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            net.haesleinhuepf.clincubator.interactive.generated.MaximumZProjection.class
        };
    }
}
