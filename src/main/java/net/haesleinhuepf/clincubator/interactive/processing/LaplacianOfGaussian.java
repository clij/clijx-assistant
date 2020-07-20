package net.haesleinhuepf.clincubator.interactive.processing;

import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.plugins.LaplacianOfGaussian3D;
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
public class LaplacianOfGaussian extends AbstractIncubatorPlugin {

    float former_sigma = 5;
    Scrollbar sigma_slider = null;


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Laplacian of Gaussian");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Sigma", 0, 100, former_sigma);

        sigma_slider = (Scrollbar) gdp.getSliders().get(1);

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

        sigma_slider.addMouseListener(mouseAdapter);
        sigma_slider.addKeyListener(keyAdapter);


        //radius = (int) gdp.getNextNumber();
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
        LaplacianOfGaussian3D.laplacianOfGaussian3D(clijx, pushed, result, former_sigma, former_sigma, former_sigma);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("LoG " + my_source.getTitle());
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
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
