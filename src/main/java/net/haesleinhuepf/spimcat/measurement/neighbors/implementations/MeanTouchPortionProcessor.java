package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;

public class MeanTouchPortionProcessor implements NeighborProcessor {
    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        ClearCLBuffer touch_count_matrix = clij2.create(distance_matrix);
        clij2.generateTouchCountMatrix(label_map, touch_count_matrix);

        //ClearCLBuffer edge_image = clij2.create(label_map);
        //clij2.detectLabelEdges(label_map, edge_image);
        //ResultsTable table = new ResultsTable();
        //clij2.statisticsOfBackgroundAndLabelledPixels(edge_image, label_map, table);

        ClearCLBuffer sum_vector = clij2.create(touch_count_matrix.getWidth(), 1);
        clij2.sumYProjection(touch_count_matrix, sum_vector);

        ClearCLBuffer count_vector = clij2.create(touch_count_matrix.getWidth(), 1);
        clij2.countTouchingNeighbors(touch_matrix, count_vector);

        ClearCLBuffer average_vector = clij2.create(touch_count_matrix.getWidth(), 1);
        clij2.divideImages(count_vector, sum_vector, average_vector);

        //ClearCLBuffer vector = clij2.create(touch_count_matrix.getWidth(), 1);
        //clij2.power(average_vector, vector, -1);

        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.replaceIntensities(label_map, average_vector, result);
        touch_count_matrix.close();
        //vector.close();
        sum_vector.close();
        count_vector.close();
        average_vector.close();

        return result;
    }

    @Override
    public String getLUTName() {
        return "Fire";
    }

    @Override
    public String getName() {
        return "Mean touch portion of touching neighbors";
    }

    @Override
    public boolean getDefaultActivated() {
        return false;
    }
}
