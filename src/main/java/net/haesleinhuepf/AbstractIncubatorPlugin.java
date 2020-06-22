package net.haesleinhuepf;

import ij.ImageListener;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.ui.swing.script.SyntaxHighlighter;

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

    private boolean paused = false;
    protected void setTarget(ImagePlus result) {
        paused = true;
        long timeStamp = System.currentTimeMillis();
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
        //System.out.println(my_target.getTitle() + " Pulling took " + (System.currentTimeMillis() - timeStamp) + " ms");
        paused = false;
        invalidateTarget();
        imageUpdated(my_target);
        validateTarget();
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
        if (paused) {
            return;
        }
        if (imp == my_source) {
            if (sourceWasChanged()) {
                //System.out.println("Updating " + imp.getTitle());
                refresh();
            }

            refreshView();
        }
    }

    String stamp = "";
    protected boolean sourceWasChanged() {
        if (my_source.getT() != former_t || my_source.getC() != former_c) {
            //System.out.println(my_source.getTitle() + " t or c were changed");
            return true;
        }
        if (my_source.getStack() instanceof  CLIJxVirtualStack) {
            if (IncubatorUtilities.checkStamp(((CLIJxVirtualStack) my_source.getStack()).getBuffer(), stamp)) {
                return false;
            } else {
                //System.out.println(my_source.getTitle() + " changed stamp " + stamp);
            }
        }
        return true;
    }

    protected void validateSource() {
        former_c = my_source.getC();
        former_t = my_source.getT();
        if (my_source.getStack() instanceof  CLIJxVirtualStack) {
            stamp = ((CLIJxVirtualStack) my_source.getStack()).getBuffer().getName();
        }
    }

    protected void validateTarget() {
        if (my_target.getStack() instanceof CLIJxVirtualStack) {
            IncubatorUtilities.stamp(((CLIJxVirtualStack) my_target.getStack()).getBuffer());
        } else {
            //System.out.println("Cannot mark " + my_target);
        }
    }

    protected void invalidateTarget() {
        if (my_target.getStack() instanceof CLIJxVirtualStack) {
            ((CLIJxVirtualStack) my_target.getStack()).getBuffer().setName("");
        }
    }
}
