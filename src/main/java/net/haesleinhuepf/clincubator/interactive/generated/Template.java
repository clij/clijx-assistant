package net.haesleinhuepf.clincubator.interactive.generated;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.plugins.Mean3DBox;
import net.haesleinhuepf.clij2.plugins.MeanZProjection;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.projections.PopularIntensityProjection;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.RigidTransform3D;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;


//@Plugin(type = SuggestedPlugin.class)
public class Template extends AbstractIncubatorPlugin implements SuggestedPlugin {

    public Template() {
        super(new Mean3DBox());
    }

    public Class[] suggestedNextSteps() {
        return new Class[] {
            /*SUGGESTED_NEXT_STEPS*/
        };
    }

    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            /*SUGGESTED_PREVIOUS_STEPS*/
        };
    }
}
