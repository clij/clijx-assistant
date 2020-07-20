package net.haesleinhuepf.clincubator.interactive.processing;

import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.detection.FindAndLabelMaxima;
import net.haesleinhuepf.clincubator.interactive.transform.MakeIsotropic;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Plugin(type = SuggestedPlugin.class)
public class DifferenceOfGaussian extends AbstractIncubatorPlugin {

    int former_sigma1 = 1;
    int former_sigma2 = 5;
    Scrollbar sigma_slider1 = null;
    Scrollbar sigma_slider2 = null;


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Difference of Gaussian");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Sigma 1", 0, 100, former_sigma1);
        gdp.addSlider("Sigma 2", 0, 100, former_sigma2);

        sigma_slider1 = (Scrollbar) gdp.getSliders().get(0);
        sigma_slider2 = (Scrollbar) gdp.getSliders().get(1);

        return gdp;
    }

    @Override
    protected boolean parametersWereChanged() {
        return sigma_slider1.getValue() != former_sigma1 || sigma_slider2.getValue() != former_sigma2;
    }

    ClearCLBuffer result = null;
    public synchronized void refresh() {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed.getDimensions(), NativeTypeEnum.Float);
        }
        if (sigma_slider1 != null && sigma_slider2 != null) {
            former_sigma1 = sigma_slider1.getValue();
            former_sigma2 = sigma_slider2.getValue();
        }
        clijx.differenceOfGaussian(pushed, result, former_sigma1, former_sigma1, former_sigma1, former_sigma2, former_sigma2, former_sigma2);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("DoG " + my_source.getTitle());
    }

    @Override
    public Class[] suggestedNextSteps() {
        return new Class[]{
                FindAndLabelMaxima.class
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{
                MakeIsotropic.class
        };
    }
}
