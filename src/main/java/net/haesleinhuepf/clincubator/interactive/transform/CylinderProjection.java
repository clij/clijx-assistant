package net.haesleinhuepf.clincubator.interactive.transform;

import ij.IJ;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.processing.GaussianBlur;
import net.haesleinhuepf.clincubator.interactive.processing.Mean;
import net.haesleinhuepf.clincubator.interactive.processing.Median;
import net.haesleinhuepf.clincubator.interactive.projections.MaximumZProjection;
import net.haesleinhuepf.clincubator.interactive.projections.MeanZProjection;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class CylinderProjection extends AbstractIncubatorPlugin {

    int number_of_angles = 360;
    float delta_angle_in_degrees = 1;

    protected void configure() {
        GenericDialog gdp = new GenericDialog("Cylinder projection");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addNumericField("Number of angles", number_of_angles);
        gdp.addNumericField("Angle step in degrees", delta_angle_in_degrees);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return;
        }

        setSource(IJ.getImage());
        number_of_angles = (int) gdp.getNextNumber();
        delta_angle_in_degrees = (float) gdp.getNextNumber();

    }

    ClearCLBuffer result = null;
    protected synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        int center_x = (int) (pushed.getWidth() / 2);
        int center_y = 0; //(int) (pushed.getDepth() / 2);

        int radius = (int) Math.sqrt(Math.pow(center_x, 2) + Math.pow(center_y, 2));

        ClearCLBuffer resliced_from_top = clijx.create(pushed.getWidth(), pushed.getDepth(), pushed.getHeight());
        clijx.resliceTop(pushed, resliced_from_top);
        pushed.close();

        ClearCLBuffer radial_resliced = clijx.create(radius, pushed.getHeight(), number_of_angles);
        clijx.resliceRadial(resliced_from_top, radial_resliced, delta_angle_in_degrees + 180, center_x, center_y);
        resliced_from_top.close();

        if (result == null) {
            result = clijx.create(radial_resliced.getDepth(), radial_resliced.getHeight(), radial_resliced.getWidth());
        }
        clijx.transposeXZ(radial_resliced, result);
        radial_resliced.close();
        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Cylinder projected " + my_source.getTitle());
    }



    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
                MaximumZProjection.class,
                MeanZProjection.class
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{
                MakeIsotropic.class,
                RigidTransform3D.class
        };
    }
}
