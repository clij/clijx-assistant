package net.haesleinhuepf.spimcat.transform;

import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import net.haesleinhuepf.AbstractIncubatorPlugin;
import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class RigidTransform3D extends AbstractIncubatorPlugin implements AdjustmentListener {

    Scrollbar registrationTranslationXSlider = null;
    Scrollbar registrationTranslationYSlider = null;
    Scrollbar registrationTranslationZSlider = null;

    Scrollbar registrationRotationXSlider = null;
    Scrollbar registrationRotationYSlider = null;
    Scrollbar registrationRotationZSlider = null;


    protected void configure() {
        GenericDialogPlus gdp = new GenericDialogPlus("Rigid transformation");
        gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return;
        }
        setSource(gdp.getNextImage());


        gdp = new GenericDialogPlus("Rigid Transform");
        //gdp.addCheckbox("Do noise and background subtraction (Difference of Gaussian)", formerDoNoiseAndBackgroundRemoval);
        //gdp.addSlider("Sigma 1 (in 0.1 pixel)", 0, 100, formerSigma1);
        //gdp.addSlider("Sigma 2 (in 0.1 pixel)", 0, 100, formerSigma2);
        //gdp.addMessage("View transform");
        //gdp.addSlider("View Translation X (in pixel)", -100, 100, formerViewTranslationX);
        //gdp.addSlider("View Translation Y (in pixel)", -100, 100, formerViewTranslationY);
        //gdp.addSlider("View Translation Z (in pixel)", -100, 100, formerViewTranslationZ);
        //gdp.addSlider("View Rotation X (in degrees)", -180, 180, formerViewRotationX);
        //gdp.addSlider("View Rotation Y (in degrees)", -180, 180, formerViewRotationY);
        //gdp.addSlider("View Rotation Z (in degrees)", -180, 180, formerViewRotationZ);

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
        registrationTranslationXSlider.addAdjustmentListener(this);
        registrationTranslationYSlider.addAdjustmentListener(this);
        registrationTranslationZSlider.addAdjustmentListener(this);
        registrationRotationXSlider.addAdjustmentListener(this);
        registrationRotationYSlider.addAdjustmentListener(this);
        registrationRotationZSlider.addAdjustmentListener(this);

        gdp.setModal(false);
        gdp.showDialog();

        System.out.println("Dialog shown");


    }

    ClearCLBuffer result = null;
    String former_transform = "";
    protected synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);//clijx.pushCurrentZStack(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed);
        }

        //double registrationTranslationX = registrationTranslationXSlider.getValue();
        //double registrationTranslationY = registrationTranslationYSlider.getValue();
        //double registrationTranslationZ = registrationTranslationZSlider.getValue();

        //double registrationRotationX = registrationRotationXSlider.getValue() * Math.PI / 180.0;
        //double registrationRotationY = registrationRotationYSlider.getValue() * Math.PI / 180.0;
        //double registrationRotationZ = registrationRotationZSlider.getValue() * Math.PI / 180.0;

        String transform =
                "-center" +
                " translateX=" + registrationTranslationXSlider.getValue() +
                " translateY=" + registrationTranslationYSlider.getValue() +
                " translateZ=" + registrationTranslationZSlider.getValue() +
                " rotateX=" + registrationRotationXSlider.getValue() +
                " rotateY=" + registrationRotationYSlider.getValue() +
                " rotateZ=" + registrationRotationZSlider.getValue() +
                " center";

        if (former_transform == transform && !sourceWasChanged()) {
            return;
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
    public void adjustmentValueChanged(AdjustmentEvent e) {
        refresh();
    }
}
