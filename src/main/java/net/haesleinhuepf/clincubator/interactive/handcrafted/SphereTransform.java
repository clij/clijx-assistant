package net.haesleinhuepf.clincubator.interactive.handcrafted;

import ij.IJ;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clijx.plugins.RigidTransform;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.plugins.ReslicePolar;
import net.haesleinhuepf.clincubator.interactive.suggestions.SphereTransformSuggestion;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;

@Plugin(type = SuggestedPlugin.class)
public class SphereTransform extends AbstractIncubatorPlugin implements SphereTransformSuggestion {

    int number_of_angles = 360;
    float delta_angle_in_degrees = 1;
    float relative_center_x = 0.5f;
    float relative_center_y = 0.5f;
    float relative_center_z = 0.5f;


    private TextField center_x_slider;
    private TextField center_y_slider;
    private TextField center_z_slider;

    public SphereTransform() {
        super(new net.haesleinhuepf.clijx.plugins.SphereTransform());
    }

    protected boolean configure() {
        GenericDialog gdp = new GenericDialog("Sphere transform");
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


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Cylinder projection");
        gdp.addNumericField("Center_x (0...1)", relative_center_x);
        addPlusMinusPanel(gdp, "relative_center_x");
        gdp.addNumericField("Center_y (0...1)", relative_center_y);
        addPlusMinusPanel(gdp, "relative_center_y");
        gdp.addNumericField("Center_z (0...1)", relative_center_z);
        addPlusMinusPanel(gdp, "relative_center_z");

        center_x_slider = (TextField) gdp.getNumericFields().get(0);
        center_y_slider = (TextField) gdp.getNumericFields().get(1);
        center_z_slider = (TextField) gdp.getNumericFields().get(2);

        return gdp;
    }

    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);

        if (center_y_slider != null) {
            relative_center_x = Float.parseFloat(center_x_slider.getText());
            relative_center_y = Float.parseFloat(center_y_slider.getText());
            relative_center_z = Float.parseFloat(center_z_slider.getText());
        }

        args = new Object[]{pushed, null, number_of_angles, delta_angle_in_degrees, relative_center_x * pushed.getWidth(), relative_center_y * pushed.getHeight(), relative_center_z * pushed.getDepth()};
        net.haesleinhuepf.clijx.plugins.SphereTransform plugin = (net.haesleinhuepf.clijx.plugins.SphereTransform) getCLIJMacroPlugin();
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
        my_target.setTitle("Sphere transformed " + my_source.getTitle());
    }


    @Override
    public void refreshView() {}



}
