package net.haesleinhuepf.clincubator.interactive.analysis;

import ij.IJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.labeling.Labeler;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.RigidTransform3D;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class CountNeighbors extends AbstractIncubatorPlugin implements LabelAnalyser {

    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();



        if (result == null) {
            result = clijx.create(pushed.getWidth(), pushed.getHeight());
        }

        int number_of_labels = (int)clijx.maximumOfAllPixels(pushed);
        ClearCLBuffer touch_matrix = clijx.create(number_of_labels + 1, number_of_labels + 1);
        clijx.generateTouchMatrix(pushed, touch_matrix);

        ClearCLBuffer touch_count_vector = clijx.create(number_of_labels + 1, 1, 1);
        clijx.countTouchingNeighbors(touch_matrix, touch_count_vector);
        touch_matrix.close();

        clijx.replaceIntensities(pushed, touch_count_vector, result);
        touch_count_vector.close();
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Extended labels " + my_source.getTitle());
        my_target.setDisplayRange(0, 10);
        IJ.run(my_target, "Fire", "");
    }

}
