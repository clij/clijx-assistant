package net.haesleinhuepf;

import ij.ImageListener;
import ij.ImagePlus;
import ij.plugin.PlugIn;

public abstract class AbstractIncubatorPlugin implements ImageListener, PlugIn {

    protected ImagePlus my_source = null;
    int former_t = -1;
    int former_c = -1;
    protected ImagePlus my_target = null;


    @Override
    public void run(String arg) {
        configure();
        ImagePlus.addImageListener(this);
        refresh();
    }

    protected abstract void configure();
    protected abstract void refresh();
    protected void refreshView() {}

    protected void setSource(ImagePlus input) {
        my_source = input;
        my_target = null;
    }

    protected void setTarget(ImagePlus result) {
        if (my_target == null) {
            my_target = result;
            my_target.setDisplayRange(my_source.getDisplayRangeMin(), my_source.getDisplayRangeMax());
            my_target.show();

        } else {
            ImagePlus output = result;
            double min = my_target.getDisplayRangeMin();
            double max = my_target.getDisplayRangeMax();
            my_target.setStack(output.getStack());

            my_target.setDisplayRange(min, max);
        }
    }

    @Override
    public void imageOpened(ImagePlus imp) {

    }

    @Override
    public void imageClosed(ImagePlus imp) {
        if (imp != null && (imp == my_source || imp != my_target)) {
            ImagePlus.removeImageListener(this);
        }
    }

    @Override
    public void imageUpdated(ImagePlus imp) {
        if (imp == my_source) {
            //if (imp.getT() != former_t || imp.getC() != former_c) {
                former_c = imp.getC();
                former_t = imp.getT();
                refresh();
            //}
            refreshView();
            //
        }
    }
}
