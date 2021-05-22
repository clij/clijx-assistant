package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.gui.GenericDialog;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.util.Arrays;

@Plugin(type = AssistantGUIPlugin.class)
public class SphereTransform extends AbstractAssistantGUIPlugin {

    int number_of_angles = 360;

    float delta_angle_in_degrees = 1;
    float relative_center_x = 0.5f;
    float relative_center_y = 0.5f;
    float relative_center_z = 0.5f;

    private TextField num_angles_slider;
    private TextField num_angles_slider_y;
    private TextField angle_step_in_degrees_slider;

    private TextField center_x_slider;
    private TextField center_y_slider;
    private TextField center_z_slider;

    public SphereTransform() {
        super(new net.haesleinhuepf.clij2.plugins.SphereTransform());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Sphere transform");
        gdp.addNumericField("Number of angles", number_of_angles);
        addPlusMinusPanel(gdp, "number_of_angles");

        gdp.addNumericField("Angle step in degrees", delta_angle_in_degrees);
        addPlusMinusPanel(gdp, "delta_angle_in_degrees");

        gdp.addNumericField("Relative_center_x (0...1)", relative_center_x);
        addPlusMinusPanel(gdp, "relative_center_x");
        gdp.addNumericField("Relativce_center_y (0...1)", relative_center_y);
        addPlusMinusPanel(gdp, "relative_center_y");
        gdp.addNumericField("Relative_center_z (0...1)", relative_center_z);
        addPlusMinusPanel(gdp, "relative_center_z");

        num_angles_slider = (TextField) gdp.getNumericFields().get(0);
        angle_step_in_degrees_slider = (TextField) gdp.getNumericFields().get(1);

        center_x_slider = (TextField) gdp.getNumericFields().get(2);
        center_y_slider = (TextField) gdp.getNumericFields().get(3);
        center_z_slider = (TextField) gdp.getNumericFields().get(4);

        return gdp;
    }

    public synchronized void refresh()
    {
        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);

        if (center_y_slider != null) {
            try {
                number_of_angles = (int)Float.parseFloat(num_angles_slider.getText());
                delta_angle_in_degrees = Float.parseFloat(angle_step_in_degrees_slider.getText());

                relative_center_x = Float.parseFloat(center_x_slider.getText());
                relative_center_y = Float.parseFloat(center_y_slider.getText());
                relative_center_z = Float.parseFloat(center_z_slider.getText());
            } catch (Exception e) {
                System.out.println("Error parsing text (ExtractChannel)");
            }
        }

        args = new Object[]{pushed[0], null, number_of_angles, delta_angle_in_degrees, relative_center_x * pushed[0][0].getWidth(), relative_center_y * pushed[0][0].getHeight(), relative_center_z * pushed[0][0].getDepth()};
        net.haesleinhuepf.clij2.plugins.SphereTransform plugin = (net.haesleinhuepf.clij2.plugins.SphereTransform) getCLIJMacroPlugin();
        plugin.setArgs(args);


        int radius = (int) Math.sqrt(Math.pow(pushed[0][0].getWidth() / 2, 2) + Math.pow(pushed[0][0].getHeight() / 2, 2)  + Math.pow(pushed[0][0].getDepth() / 2, 2) );

        long[] new_dimensions = {number_of_angles, number_of_angles / 2, radius};
        System.out.println("new dims " + Arrays.toString(new_dimensions));
        invalidateResultsIfDimensionsChanged(new_dimensions);

        if (result == null) {
            result = createOutputBufferFromSource(pushed[0]);
        }
        args[1] = result[0];

        executeCL(pushed, new ClearCLBuffer[][]{result});
        cleanup(my_sources, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Sphere transformed " + my_sources[0].getTitle());
        my_target.setDisplayRange(my_sources[0].getDisplayRangeMin(), my_sources[0].getDisplayRangeMax());
        my_target.updateAndDraw();
        enhanceContrast();

    }


    @Override
    public void refreshView() {}

}
