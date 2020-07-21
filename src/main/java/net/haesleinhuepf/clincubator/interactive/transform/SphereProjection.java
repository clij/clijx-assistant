package net.haesleinhuepf.clincubator.interactive.transform;

import ij.IJ;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clijx.plugins.RigidTransform;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.plugins.ReslicePolar;
import net.haesleinhuepf.clincubator.interactive.generated.MaximumZProjection;
import net.haesleinhuepf.clincubator.interactive.generated.MeanZProjection;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class SphereProjection extends AbstractIncubatorPlugin {

    int number_of_angles = 360;
    float delta_angle_in_degrees = 1;

    protected boolean configure() {
        GenericDialog gdp = new GenericDialog("Sphere projection");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addNumericField("Number of angles", number_of_angles);
        gdp.addNumericField("Angle step in degrees", delta_angle_in_degrees);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return false;
        }

        setSource(IJ.getImage());
        number_of_angles = (int) gdp.getNextNumber();
        delta_angle_in_degrees = (float) gdp.getNextNumber();

        return true;
    }

    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        float center_x = (float) (pushed.getWidth() / 2);
        float center_y = (float) (pushed.getHeight() / 2);
        float center_z = (float) (pushed.getDepth() / 2);

        int radius = (int) Math.sqrt(Math.pow(center_x, 2) + Math.pow(center_y, 2));


        if (result == null) {
            result = clijx.create(number_of_angles, number_of_angles / 2, radius);
        }
        ReslicePolar.reslicePolar(clijx, pushed, result,
                delta_angle_in_degrees, 0f, 0f,
                center_x, center_y, center_z,
                1f, 1f, 1f,
                0f, 0f, 0f,
                0f, 0f, 0f);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Sphere projected " + my_source.getTitle());
    }


    @Override
    public void refreshView() {}

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
                RigidTransform.class
        };
    }

}
