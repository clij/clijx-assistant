package net.haesleinhuepf.clincubator.interactive.detection;

import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Plugin(type = SuggestedPlugin.class)
public class FindAndLabeledMaxima extends AbstractIncubatorPlugin implements Detector {

    int former_tolerance = 1;
    boolean invert = false;

    Scrollbar tolerance_slider = null;
    Checkbox invert_checkbox = null;


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Find and label maxima");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Tolerance", 0, 100, former_tolerance);
        gdp.addCheckbox("Invert", invert);

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

        invert_checkbox.addMouseListener(mouseAdapter);
        invert_checkbox.addKeyListener(keyAdapter);

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
        if (invert_checkbox != null) {
            invert = invert_checkbox.getState();
        }

        if (invert) {
            ClearCLBuffer inverted = clijx.create(pushed.getDimensions(), NativeTypeEnum.Float);
            clijx.invert(pushed, inverted);
            pushed.close();
            pushed = inverted;
        }

        clijx.findMaxima(pushed, result, former_tolerance);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Labeled maxima in " + my_source.getTitle());
        my_target.setDisplayRange(0, 1);
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
    }


}
