package net.haesleinhuepf.clincubator.interactive.mesh;

import ij.IJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.analysis.LabelAnalyser;
import net.haesleinhuepf.clincubator.interactive.labeling.ConnectedComponentsLabeling;
import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsUntilTheyTouch;
import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsWithMaximumRadius;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class DistanceMeshNeighbors extends AbstractIncubatorPlugin {

    ClearCLBuffer result = null;
    protected synchronized void refresh()
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

        ClearCLBuffer distance_touch_matrix = clijx.create(distance_matrix);
        clijx.multiplyImages(touch_matrix, distance_matrix, distance_touch_matrix);
        touch_matrix.close();
        distance_matrix.close();

        clijx.touchMatrixToMesh(pointlist, distance_touch_matrix, result);

        pointlist.close();
        distance_touch_matrix.close();
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Distance meshes neighbors " + my_source.getTitle());
        my_target.setDisplayRange(0, 50);
        IJ.run(my_target, "Fire", "");
    }

    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{
                ConnectedComponentsLabeling.class,
                ExtendLabelsUntilTheyTouch.class,
                ExtendLabelsWithMaximumRadius.class
        };
    }

}
