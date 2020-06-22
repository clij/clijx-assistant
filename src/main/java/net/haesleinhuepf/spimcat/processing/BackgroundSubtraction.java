package net.haesleinhuepf.spimcat.processing;

import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import net.haesleinhuepf.AbstractIncubatorPlugin;
import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

public class BackgroundSubtraction extends AbstractIncubatorPlugin {

    int radius = 10;

    protected void configure() {
        GenericDialogPlus gdp = new GenericDialogPlus("Background subtraction");
        gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addNumericField("Radius", radius);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return;
        }

        setSource(gdp.getNextImage());
        radius = (int) gdp.getNextNumber();
    }

    ClearCLBuffer result = null;
    protected synchronized void refresh() {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed);
        }
        clijx.topHatBox(pushed, result, radius, radius, radius);
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Background subtracted " + my_source.getTitle());
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
    }
}
