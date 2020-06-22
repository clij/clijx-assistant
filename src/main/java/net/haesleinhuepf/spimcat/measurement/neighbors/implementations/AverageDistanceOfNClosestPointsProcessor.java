package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;

public class AverageDistanceOfNClosestPointsProcessor implements NeighborProcessor, OffersDocumentation {

    private int n;

    public AverageDistanceOfNClosestPointsProcessor(int n) {
        this.n = n;
    }

    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        int number_of_objects = (int)pointlist.getWidth();
        ClearCLBuffer vector = clij2.create(number_of_objects, 1, 1);
        clij2.averageDistanceOfNClosestPoints(distance_matrix, vector, n);

        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.replaceIntensities(label_map, vector, result);
        return result;
    }

    @Override
    public String getLUTName() {
        return "Fire";
    }

    @Override
    public String getName() {
        return "Average distance of " + n + " closest points";
    }

    @Override
    public boolean getDefaultActivated() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Measures the average distance to the " + n + " closest neighbors.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D / 3D";
    }
}
