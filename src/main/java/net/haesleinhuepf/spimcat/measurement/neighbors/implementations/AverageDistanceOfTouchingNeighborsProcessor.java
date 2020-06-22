package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;

public class AverageDistanceOfTouchingNeighborsProcessor implements NeighborProcessor, OffersDocumentation {
    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        int number_of_objects = (int)pointlist.getWidth();
        ClearCLBuffer vector = clij2.create(number_of_objects, 1, 1);
        clij2.averageDistanceOfTouchingNeighbors(distance_matrix, touch_matrix, vector);

        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.replaceIntensities(label_map, vector, result);
        vector.close();
        return result;
    }

    @Override
    public String getLUTName() {
        return "Fire";
    }

    @Override
    public String getName() {
        return "Average distance of touching neighbors";
    }

    @Override
    public boolean getDefaultActivated() {
        return true;
    }


    @Override
    public String getDescription() {
        return "Measures the average distance to all touching neighbors.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D / 3D";
    }

}
