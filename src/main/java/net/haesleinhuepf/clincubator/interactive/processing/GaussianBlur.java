package net.haesleinhuepf.clincubator.interactive.processing;

import ij.IJ;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.MakeIsotropic;
import net.haesleinhuepf.clincubator.interactive.transform.RigidTransform3D;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Plugin(type = SuggestedPlugin.class)
public class GaussianBlur extends AbstractIncubatorPlugin implements Denoiser{

    int former_sigma_xy = 1;
    int former_sigma_z = 0;
    Scrollbar sigma_xy = null;
    Scrollbar sigma_z = null;

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Gaussian blur");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Sigma in XY (in pixels)", 0, 100, former_sigma_xy);
        gdp.addSlider("Sigma in Z (in pixels)", 0, 100, former_sigma_z);

        sigma_xy = (Scrollbar) gdp.getSliders().get(0);
        sigma_z = (Scrollbar) gdp.getSliders().get(0);

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

        sigma_xy.addMouseListener(mouseAdapter);
        sigma_z.addKeyListener(keyAdapter);

        return gdp;
    }

    @Override
    protected boolean parametersWereChanged() {
        return sigma_xy.getValue() != former_sigma_xy;
    }

    ClearCLBuffer result = null;
    public synchronized void refresh() {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed);
        }
        if (sigma_xy != null && sigma_z != null) {
            former_sigma_xy = sigma_xy.getValue();
            former_sigma_z = sigma_z.getValue();
        }
        clijx.gaussianBlur(pushed, result, former_sigma_xy, former_sigma_xy, former_sigma_z);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Gaussian blur filtered " + my_source.getTitle());
    }

    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
                BackgroundSubtraction.class,
                MakeIsotropic.class
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[0];
    }
}
