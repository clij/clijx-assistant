package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.util.Arrays;

@Plugin(type = AssistantGUIPlugin.class)
public class CylinderTransform extends AbstractAssistantGUIPlugin {

    int number_of_angles = 360;
    float delta_angle_in_degrees = 1;
    float relative_center_x = 0.5f;
    float relative_center_z = 0.5f;

    private TextField num_angles_slider;
    private TextField angle_step_in_degrees_slider;
    private TextField center_x_slider;
    private TextField center_z_slider;

    public CylinderTransform() {
        super(new net.haesleinhuepf.clij2.plugins.CylinderTransform());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Cylinder transform");
        gdp.addNumericField("Number_of_angles", number_of_angles);
        addPlusMinusPanel(gdp, "Number_of_angles");

        gdp.addNumericField("Angle_step_in_degrees",  delta_angle_in_degrees);
        addPlusMinusPanel(gdp, "Angle_step_in_degrees");

        gdp.addNumericField("Relative_center_x (0...1)", relative_center_x);
        addPlusMinusPanel(gdp, "relative_center_x");

        gdp.addNumericField("Relative_enter_z (0...1)", relative_center_z);
        addPlusMinusPanel(gdp, "relative_center_z");


        num_angles_slider = (TextField) gdp.getNumericFields().get(0);
        angle_step_in_degrees_slider = (TextField) gdp.getNumericFields().get(1);
        center_x_slider = (TextField) gdp.getNumericFields().get(2);
        center_z_slider = (TextField) gdp.getNumericFields().get(3);

        return gdp;
    }


    @Override
    public void refreshView() {}

    public synchronized void refresh()
    {
        if (center_z_slider != null) {
            try {
                number_of_angles = (int)Float.parseFloat(num_angles_slider.getText());
                delta_angle_in_degrees = Float.parseFloat(angle_step_in_degrees_slider.getText());
                relative_center_x = Float.parseFloat(center_x_slider.getText());
                relative_center_z = Float.parseFloat(center_z_slider.getText());
            } catch (Exception e) {
                e.printStackTrace();

                return;
            }
        }

        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);

        args = new Object[]{pushed[0], null, number_of_angles, delta_angle_in_degrees, relative_center_x, relative_center_z};
        net.haesleinhuepf.clij2.plugins.CylinderTransform plugin = (net.haesleinhuepf.clij2.plugins.CylinderTransform) getCLIJMacroPlugin();
        plugin.setArgs(args);

        //
        int radius = (int) Math.sqrt(Math.pow(pushed[0][0].getWidth() / 2, 2) + Math.pow(pushed[0][0].getDepth() / 2, 2));

        long[] new_dimensions = {number_of_angles, pushed[0][0].getHeight(), radius};
        System.out.println("new dims " + Arrays.toString(new_dimensions));
        invalidateResultsIfDimensionsChanged(new_dimensions);

        if (result == null) {
            result = createOutputBufferFromSource(pushed[0]);
        }
        args[1] = result[0];
        executeCL(pushed, new ClearCLBuffer[][]{result});
        cleanup(my_sources, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Cylinder transformed " + my_sources[0].getTitle());
        my_target.setDisplayRange(my_sources[0].getDisplayRangeMin(), my_sources[0].getDisplayRangeMax());
        my_target.updateAndDraw();
        enhanceContrast();

    }



}
