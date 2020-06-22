package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;

public class LocalMedianNumberOfTouchingNeighborsProcessor implements NeighborProcessor {
    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        int number_of_objects = (int)pointlist.getWidth();
        ClearCLBuffer vector = clij2.create(number_of_objects, 1, 1);
        clij2.countTouchingNeighbors(touch_matrix, vector);

        ClearCLBuffer mean_vector = clij2.create(number_of_objects, 1, 1);
        clij2.medianOfTouchingNeighbors(vector, touch_matrix, mean_vector);


        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.replaceIntensities(label_map, mean_vector, result);
        mean_vector.close();
        vector.close();

        return result;
    }

    @Override
    public String getLUTName() {
        return "Fire";
    }

    @Override
    public String getName() {
        return "Local median number of touching neighbors";
    }

    @Override
    public boolean getDefaultActivated() {
        return false;
    }
}
