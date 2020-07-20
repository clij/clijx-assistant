package net.haesleinhuepf.clincubator.interactive.analysis;

import ij.IJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class AverageNeighborDistance extends AbstractIncubatorPlugin implements LabelAnalyser {

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

        ClearCLBuffer pointlist = clijx.create(number_of_labels, pushed.getDimension());
        clijx.centroidsOfLabels(pushed, pointlist);

        ClearCLBuffer distance_matrix = clijx.create(number_of_labels + 1, number_of_labels + 1);
        clijx.generateDistanceMatrix(pointlist, pointlist, distance_matrix);

        ClearCLBuffer distance_vector = clijx.create(number_of_labels + 1, 1, 1);
        clijx.averageDistanceOfTouchingNeighbors(distance_matrix, touch_matrix, distance_vector);
        touch_matrix.close();
        distance_matrix.close();
        pointlist.close();

        clijx.replaceIntensities(pushed, distance_vector, result);
        distance_vector.close();
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Average neighbor distance " + my_source.getTitle());
        my_target.setDisplayRange(0, 50);
        IJ.run(my_target, "Fire", "");
    }

}
