package net.haesleinhuepf.spimcat.processing;

import ij.IJ;
import ij.gui.GenericDialog;
import net.haesleinhuepf.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mean extends AbstractIncubatorPlugin {

    int former_radius = 1;
    Scrollbar radiusSlider = null;

    protected void configure() {
        GenericDialog gdp = new GenericDialog("Mean filtered");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Radius", 0, 100, former_radius);
        gdp.setModal(false);
        gdp.setOKLabel("Done");
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return;
        }

        setSource(IJ.getImage());



        radiusSlider = (Scrollbar) gdp.getSliders().get(0);

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

        radiusSlider.addMouseListener(mouseAdapter);
        radiusSlider.addKeyListener(keyAdapter);



        //radius = (int) gdp.getNextNumber();
    }

    @Override
    protected boolean parametersWereChanged() {
        return radiusSlider.getValue() != former_radius;
    }

    ClearCLBuffer result = null;
    protected synchronized void refresh() {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed);
        }
        former_radius = radiusSlider.getValue();
        clijx.mean3DBox(pushed, result, former_radius, former_radius, former_radius);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Mean filtered " + my_source.getTitle());
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
    }
}
