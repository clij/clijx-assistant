package net.haesleinhuepf.clincubator.interactive.mesh;

import ij.IJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.labeling.ConnectedComponentsLabeling;
import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsUntilTheyTouch;
import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsWithMaximumRadius;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.RigidTransform3D;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

@Plugin(type = SuggestedPlugin.class)
public class MeshNeighbors extends AbstractIncubatorPlugin {

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
        //System.out.println("Labels count " + number_of_labels);
        ClearCLBuffer touch_matrix = clijx.create(number_of_labels + 1, number_of_labels + 1);
        clijx.generateTouchMatrix(pushed, touch_matrix);

        ClearCLBuffer pointlist = clijx.create(number_of_labels, pushed.getDimension());
        clijx.centroidsOfLabels(pushed, pointlist);
        pushed.close();

        clijx.touchMatrixToMesh(pointlist, touch_matrix, result);
        touch_matrix.close();
        pointlist.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Meshed neighbors " + my_source.getTitle());
        my_target.setDisplayRange(0, 10);
        IJ.run("Fire");
    }

    @Override
    protected void refreshView() {
        my_target.setZ(my_source.getZ());
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
