package net.haesleinhuepf.clincubator.interactive.handcrafted;

import ij.IJ;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clijx.plugins.RigidTransform;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clincubator.interactive.suggestions.CylinderTransformSuggestion;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;

@Plugin(type = SuggestedPlugin.class)
public class CylinderTransform extends AbstractIncubatorPlugin implements CylinderTransformSuggestion {

    int number_of_angles = 360;
    float delta_angle_in_degrees = 1;
    float relative_center_x = 0.5f;
    float relative_center_z = 0.5f;

    private TextField center_x_slider;
    private TextField center_z_slider;

    public CylinderTransform() {
        super(new net.haesleinhuepf.clijx.plugins.CylinderTransform());
    }

    protected boolean configure() {
        GenericDialog gdp = new GenericDialog("Cylinder projection");
        gdp.addNumericField("Number_of_angles", number_of_angles);
        gdp.addNumericField("Angle_step_in_degrees",  delta_angle_in_degrees);
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


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Cylinder transform");
        gdp.addNumericField("Center_x (0...1)", relative_center_x);
        addPlusMinusPanel(gdp, "relative_center_x");

        gdp.addNumericField("Center_z (0...1)", relative_center_z);
        addPlusMinusPanel(gdp, "relative_center_z");


        center_x_slider = (TextField) gdp.getNumericFields().get(0);
        center_z_slider = (TextField) gdp.getNumericFields().get(1);

        return gdp;
    }


    @Override
    public void refreshView() {}


    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        if (center_z_slider != null) {
            try {
                relative_center_x = Float.parseFloat(center_x_slider.getText());
                relative_center_z = Float.parseFloat(center_z_slider.getText());
            } catch (Exception e) {
                return;
            }
        }

        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);

        args = new Object[]{pushed, null, number_of_angles, delta_angle_in_degrees, relative_center_x, relative_center_z};
        net.haesleinhuepf.clijx.plugins.CylinderTransform plugin = (net.haesleinhuepf.clijx.plugins.CylinderTransform) getCLIJMacroPlugin();
        plugin.setArgs(args);
        if (result == null) {
            result = plugin.createOutputBufferFromSource(pushed);
        }
        args[1] = result;
        if (plugin instanceof CLIJOpenCLProcessor) {
            plugin.executeCL();
        }
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Cylinder transformed " + my_source.getTitle());
    }




}
