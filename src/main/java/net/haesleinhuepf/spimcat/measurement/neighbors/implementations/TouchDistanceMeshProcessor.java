package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;

public class TouchDistanceMeshProcessor implements NeighborProcessor {
    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        ClearCLBuffer distance_touch_matrix = clij2.create(distance_matrix);
        clij2.multiplyImages(touch_matrix, distance_matrix, distance_touch_matrix);

        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.touchMatrixToMesh(pointlist, distance_touch_matrix, result);
        distance_touch_matrix.close();

        return result;
    }

    @Override
    public String getLUTName() {
        return "Fire";
    }

    @Override
    public String getName() {
        return "Distance mesh of touching neighbors";
    }

    @Override
    public boolean getDefaultActivated() {
        return true;
    }


}
