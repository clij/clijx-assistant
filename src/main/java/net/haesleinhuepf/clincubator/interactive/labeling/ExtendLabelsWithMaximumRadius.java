package net.haesleinhuepf.clincubator.interactive.labeling;

import ij.gui.GenericDialog;
import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLKernel;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
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
public class ExtendLabelsWithMaximumRadius extends AbstractIncubatorPlugin {

    int former_radius = 1;

    Scrollbar radius_slider = null;


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Extend labels with maximum radius");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Radius", 0, 100, former_radius);

        radius_slider = (Scrollbar) gdp.getSliders().get(0);

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

        radius_slider.addMouseListener(mouseAdapter);
        radius_slider.addKeyListener(keyAdapter);

        //radius = (int) gdp.getNextNumber();
        return gdp;
    }

    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed);
        }

        if (radius_slider != null) {
            former_radius = radius_slider.getValue();
        }

        ClearCLBuffer temp = clijx.create(result);
        clijx.copy(pushed, temp);
        pushed.close();

        ClearCLBuffer flag = clijx.create(1,1,1);

        ClearCLKernel flip_kernel = null;
        ClearCLKernel flop_kernel = null;

        for (int i = 0; i < former_radius; i++) {
            if (i % 2 == 0) {
                flip_kernel = clijx.onlyzeroOverwriteMaximumBox(temp, flag, result, flip_kernel);
            } else {
                flop_kernel = clijx.onlyzeroOverwriteMaximumDiamond(result, flag, temp, flop_kernel);
            }
        }
        if (former_radius % 2 == 0) {
            clijx.copy(temp, result);
        }

        if (flip_kernel != null) {
            flip_kernel.close();
        }
        if (flop_kernel != null) {
            flop_kernel.close();
        }
        flag.close();
        temp.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Extended labels (r = " + former_radius + ") " + my_source.getTitle());
        IncubatorUtilities.glasbey(my_target);
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
    }

    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
               // trhesholding
                // spot detection
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{
                BackgroundSubtraction.class,
                CylinderProjection.class,
                SphereProjection.class,
                RigidTransform3D.class
        };
    }
}
