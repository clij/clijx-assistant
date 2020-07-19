package net.haesleinhuepf.clincubator.interactive.detection;

import ij.IJ;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.processing.GaussianBlur;
import net.haesleinhuepf.clincubator.interactive.processing.Mean;
import net.haesleinhuepf.clincubator.interactive.processing.Median;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.MakeIsotropic;
import net.haesleinhuepf.clincubator.interactive.transform.RigidTransform3D;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Plugin(type = SuggestedPlugin.class)
public class SpotPlateauDetection extends AbstractIncubatorPlugin {

    int former_tolerance = 1;
    Scrollbar tolerance_slider = null;
    Checkbox invert_checkbox = null;


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Spot / plateau detection");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Tolerance", 0, 100, former_tolerance);
        gdp.addCheckbox("Invert", false);

        tolerance_slider = (Scrollbar) gdp.getSliders().get(0);

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

        tolerance_slider.addMouseListener(mouseAdapter);
        tolerance_slider.addKeyListener(keyAdapter);

        invert_checkbox = (Checkbox) gdp.getCheckboxes().get(0);

        //radius = (int) gdp.getNextNumber();
        return gdp;
    }

    @Override
    protected boolean parametersWereChanged() {
        return tolerance_slider.getValue() != former_tolerance;
    }

    ClearCLBuffer result = null;
    protected synchronized void refresh() {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed);
        }
        if (tolerance_slider != null) {
            former_tolerance = tolerance_slider.getValue();
        }
        clijx.findMaxima(pushed, result, former_tolerance);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Spots / plateaus in " + my_source.getTitle());
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
    }


    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
                // label stuff
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{
                GaussianBlur.class,
                BackgroundSubtraction.class,
                CylinderProjection.class,
                SphereProjection.class,
                MakeIsotropic.class,
                RigidTransform3D.class
        };
    }
}
