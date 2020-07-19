package net.haesleinhuepf.clincubator.interactive.segmentation;

import ij.IJ;
import ij.ImageJ;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.labeling.ConnectedComponentsLabeling;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Plugin(type = SuggestedPlugin.class)
public class LocalThreshold extends AbstractIncubatorPlugin  implements Segmenter {

    int former_sigma1 = 1;
    int former_sigma2 = 5;
    float former_threshold = 0;
    boolean former_above_threshold = true;

    Scrollbar sigma_slider1 = null;
    Scrollbar sigma_slider2 = null;
    Scrollbar threshold_slider = null;
    Checkbox above_threshold_checkbox = null;

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Local threshold (DoG based)");
        gdp.addSlider("Sigma_1", 0, 100, former_sigma1);
        gdp.addSlider("Sigma_2", 0, 100, former_sigma2);
        gdp.addSlider("Threshold", 0, 100, former_threshold);
        gdp.addCheckbox("Above threshold", former_above_threshold);

        sigma_slider1 = (Scrollbar) gdp.getSliders().get(0);
        sigma_slider2 = (Scrollbar) gdp.getSliders().get(1);
        threshold_slider = (Scrollbar) gdp.getSliders().get(2);

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

        sigma_slider1.addMouseListener(mouseAdapter);
        sigma_slider1.addKeyListener(keyAdapter);

        sigma_slider2.addMouseListener(mouseAdapter);
        sigma_slider2.addKeyListener(keyAdapter);

        threshold_slider.addMouseListener(mouseAdapter);
        threshold_slider.addKeyListener(keyAdapter);

        above_threshold_checkbox = (Checkbox) gdp.getCheckboxes().get(0);
        above_threshold_checkbox.addMouseListener(mouseAdapter);
        above_threshold_checkbox.addKeyListener(keyAdapter);

        //radius = (int) gdp.getNextNumber();
        return gdp;
    }

    @Override
    protected boolean parametersWereChanged() {
        return sigma_slider1.getValue() != former_sigma1 ||
                sigma_slider2.getValue() != former_sigma2 ||
                threshold_slider.getValue() != former_threshold ||
                above_threshold_checkbox.getState() != former_above_threshold;
    }

    ClearCLBuffer result = null;
    protected synchronized void refresh() {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed.getDimensions(), NativeTypeEnum.UnsignedByte);
        }
        if (sigma_slider1 != null && sigma_slider2 != null && threshold_slider != null && above_threshold_checkbox != null) {
            former_sigma1 = sigma_slider1.getValue();
            former_sigma2 = sigma_slider2.getValue();
            former_threshold = threshold_slider.getValue();
            former_above_threshold = above_threshold_checkbox.getState();
        }

        ClearCLBuffer temp = clijx.create(pushed.getDimensions(), NativeTypeEnum.Float);
        clijx.differenceOfGaussian(pushed, temp, former_sigma1, former_sigma1, former_sigma1, former_sigma2, former_sigma2, former_sigma2);
        pushed.close();


        if (former_above_threshold) {
            clijx.greaterConstant(temp, result, former_threshold);
        } else {
            clijx.smallerOrEqualConstant(temp, result, former_threshold);
        }

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Local thresholded " + my_source.getTitle());
        my_target.setDisplayRange(0, 1);
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
    }


    public static void main(String[] args) {
        new ImageJ();
        IJ.open("C:\\structure\\data\\clincubator_data\\Sphere_ISB200714_well5_1pos_ON_t000000.tif");

        new LocalThreshold().run("");
    }
}
