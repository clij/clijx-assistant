package net.haesleinhuepf.clincubator.interactive.transform;

import ij.IJ;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clij2.CLIJ2;
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

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Plugin(type = SuggestedPlugin.class)
public class CylinderProjection extends AbstractIncubatorPlugin {

    int number_of_angles = 360;
    float delta_angle_in_degrees = 1;
    float relative_center_x = 0.5f;
    float relative_center_z = 0.5f;

    private TextField center_x_slider;
    private TextField center_y_slider;


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
        GenericDialog gdp = new GenericDialog("Cylinder projection");
        gdp.addNumericField("Center_x (0...1)", relative_center_x);
        gdp.addNumericField("Center_z (0...1)", relative_center_z);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                refresh();
            }
        };
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refresh();
            }
        };

        center_x_slider = (TextField) gdp.getNumericFields().get(0);
        center_y_slider = (TextField) gdp.getNumericFields().get(1);

        center_x_slider.addMouseListener(mouseAdapter);
        center_x_slider.addKeyListener(keyAdapter);

        center_y_slider.addMouseListener(mouseAdapter);
        center_y_slider.addKeyListener(keyAdapter);

        return gdp;
    }


    @Override
    public void refreshView() {}


    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        if (center_y_slider != null) {
            relative_center_x = Float.parseFloat(center_x_slider.getText());
            relative_center_z = Float.parseFloat(center_y_slider.getText());
        }
        System.out.println("number_of_angles = " + number_of_angles);
        System.out.println("delta_angle_in_degrees = " + delta_angle_in_degrees);
        System.out.println("relative_center_x = " + relative_center_x);
        System.out.println("relative_center_z = " + relative_center_z);

        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        int center_x = (int) (pushed.getWidth() * relative_center_x);
        int center_y = (int) (pushed.getDepth() * relative_center_z);

        int radius = (int) Math.sqrt(Math.pow(pushed.getWidth() / 2, 2) + Math.pow(pushed.getDepth() / 2, 2));

        ClearCLBuffer resliced_from_top = clijx.create(pushed.getWidth(), pushed.getDepth(), pushed.getHeight());
        clijx.resliceTop(pushed, resliced_from_top);
        pushed.close();

        ClearCLBuffer radial_resliced = clijx.create(radius, pushed.getHeight(), number_of_angles);

        float start_angle = 0;
        float scale_x = 1f;
        float scale_z = 1f;

        clijx.resliceRadial(resliced_from_top, radial_resliced, delta_angle_in_degrees, start_angle, center_x, center_y, scale_x, scale_z);
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
