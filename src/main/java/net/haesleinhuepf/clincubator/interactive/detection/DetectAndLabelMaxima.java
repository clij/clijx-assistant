package net.haesleinhuepf.clincubator.interactive.detection;

import ij.gui.GenericDialog;
import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.plugins.FindMaximaPlateaus;
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
public class DetectAndLabelMaxima extends AbstractIncubatorPlugin implements Detector {

    int former_sigma = 1;
    boolean invert = false;

    Scrollbar sigma_slider = null;
    Checkbox invert_checkbox = null;


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Detect and label maxima");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Sigma", 0, 100, former_sigma);
        gdp.addCheckbox("Invert", invert);

        sigma_slider = (Scrollbar) gdp.getSliders().get(0);

        invert_checkbox = (Checkbox) gdp.getCheckboxes().get(0);

        return gdp;
    }

    @Override
    protected boolean parametersWereChanged() {
        return sigma_slider.getValue() != former_sigma;
    }

    ClearCLBuffer result = null;
    public synchronized void refresh() {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed.getDimensions(), NativeTypeEnum.Float);
        }
        if (sigma_slider != null) {
            former_sigma = sigma_slider.getValue();
        }
        if (invert_checkbox != null) {
            invert = invert_checkbox.getState();
        }

        ClearCLBuffer blurred = clijx.create(pushed.getDimensions(), NativeTypeEnum.Float);
        clijx.gaussianBlur3D(pushed, blurred, former_sigma, former_sigma, former_sigma);
        pushed.close();

        if (invert) {
            ClearCLBuffer inverted = clijx.create(blurred);
            clijx.invert(blurred, inverted);
            blurred.close();
            blurred = inverted;
        }

        ClearCLBuffer maxima = clijx.create(blurred.getDimensions(), NativeTypeEnum.UnsignedByte);
        FindMaximaPlateaus.findMaximaPlateaus(clijx, blurred, maxima);
        blurred.close();

        clijx.connectedComponentsLabelingBox(maxima, result);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Labeled maxima in " + my_source.getTitle());
        my_target.setDisplayRange(0, 1);
        IncubatorUtilities.glasbey(my_target);
    }
}
