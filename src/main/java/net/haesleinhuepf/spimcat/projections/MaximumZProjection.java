package net.haesleinhuepf.spimcat.projections;

import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import net.haesleinhuepf.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.spimcat.transform.MakeIsotropic;

public class MaximumZProjection extends AbstractIncubatorPlugin {

    protected void configure() {
        GenericDialogPlus gdp = new GenericDialogPlus("Maximum Z projection");
        gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return;
        }

        setSource(gdp.getNextImage());

    }

    protected synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = clijx.pushCurrentZStack(my_source);

        ClearCLBuffer result = clijx.create(pushed.getWidth(), pushed.getHeight());
        clijx.maximumZProjection(pushed, result);
        pushed.close();

        setTarget(clijx.pull(result));
        my_target.setTitle("Maximum Z projection " + my_source.getTitle());

        result.close();
    }


}
