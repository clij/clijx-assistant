package net.haesleinhuepf.clincubator.interactive.transform;

import ij.IJ;
import ij.gui.GenericDialog;
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
import java.awt.event.*;

@Plugin(type = SuggestedPlugin.class)
public class RigidTransform3D extends AbstractIncubatorPlugin {

    Scrollbar registrationTranslationXSlider = null;
    Scrollbar registrationTranslationYSlider = null;
    Scrollbar registrationTranslationZSlider = null;

    Scrollbar registrationRotationXSlider = null;
    Scrollbar registrationRotationYSlider = null;
    Scrollbar registrationRotationZSlider = null;


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {

        GenericDialog gdp = new GenericDialog("Rigid Transform");


        gdp.addSlider("Translation X (in pixel)", -100, 100, 0);
        gdp.addSlider("Translation Y (in pixel)", -100, 100, 0);
        gdp.addSlider("Translation Z (in pixel)", -100, 100, 0);
        gdp.addSlider("Rotation X (in degrees)", -180, 180, 0);
        gdp.addSlider("Rotation Y (in degrees)", -180, 180, 0);
        gdp.addSlider("Rotation Z (in degrees)", -180, 180, 0);

        registrationTranslationXSlider = (Scrollbar) gdp.getSliders().get(0);
        registrationTranslationYSlider = (Scrollbar) gdp.getSliders().get(1);
        registrationTranslationZSlider = (Scrollbar) gdp.getSliders().get(2);

        registrationRotationXSlider = (Scrollbar) gdp.getSliders().get(3);
        registrationRotationYSlider = (Scrollbar) gdp.getSliders().get(4);
        registrationRotationZSlider = (Scrollbar) gdp.getSliders().get(5);

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

        for (Scrollbar item : new Scrollbar[]{
            registrationTranslationXSlider,
            registrationTranslationYSlider,
            registrationTranslationZSlider,
            registrationRotationXSlider,
            registrationRotationYSlider,
            registrationRotationZSlider
        }) {
            item.addKeyListener(keyAdapter);
            item.addMouseListener(mouseAdapter);
        }

        return gdp;
    }

    private String getTransform() {
        if (registrationRotationZSlider != null) {
            return
                    "-center" +
                            " translateX=" + registrationTranslationXSlider.getValue() +
                            " translateY=" + registrationTranslationYSlider.getValue() +
                            " translateZ=" + registrationTranslationZSlider.getValue() +
                            " rotateX=" + registrationRotationXSlider.getValue() +
                            " rotateY=" + registrationRotationYSlider.getValue() +
                            " rotateZ=" + registrationRotationZSlider.getValue() +
                            " center";
        } else {
            return "";
        }
    }

    @Override
    protected boolean parametersWereChanged() {
        return former_transform.compareTo(getTransform()) != 0;
    }

    ClearCLBuffer result = null;
    String former_transform = "";
    protected synchronized void refresh()
    {
        String transform = getTransform();

        if (former_transform.compareTo(transform) == 0 && !sourceWasChanged()) {
            System.out.println("Cancel");
            return;
        }

        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);//clijx.pushCurrentZStack(my_source);
        validateSource();

        System.out.println(clijx.reportMemory());

        if (result == null) {
            result = clijx.create(pushed);
        }

        former_transform = transform;

        System.out.println(transform.replace("\n", " "));

        clijx.affineTransform3D(pushed, result, transform);

        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Rigid transformed " + my_source.getTitle());
    }


    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
    }

    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
                MaximumZProjection.class,
                MeanZProjection.class,
                SphereProjection.class,
                CylinderProjection.class
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{
                GaussianBlur.class,
                Mean.class,
                Median.class,
                BackgroundSubtraction.class,
        };
    }

}
