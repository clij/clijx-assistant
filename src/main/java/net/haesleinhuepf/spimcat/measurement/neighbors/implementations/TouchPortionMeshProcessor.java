package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;

public class TouchPortionMeshProcessor implements NeighborProcessor {
    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        ClearCLBuffer touch_count_matrix = clij2.create(distance_matrix);
        clij2.generateTouchCountMatrix(label_map, touch_count_matrix);

        //ClearCLBuffer edge_image = clij2.create(label_map);
        //clij2.detectLabelEdges(label_map, edge_image);
        //ResultsTable table = new ResultsTable();
        //clij2.statisticsOfBackgroundAndLabelledPixels(edge_image, label_map, table);

        ClearCLBuffer vector = clij2.create(touch_count_matrix.getWidth(), 1);
        clij2.sumYProjection(touch_count_matrix, vector);

        //clij2.pushResultsTableColumn(vector, table, StatisticsOfLabelledPixels.STATISTICS_ENTRY.SUM_INTENSITY.toString());

        ClearCLBuffer touch_portion_matrix = clij2.create(distance_matrix);
        clij2.divideImages(touch_count_matrix, vector, touch_portion_matrix);

        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.touchMatrixToMesh(pointlist, touch_portion_matrix, result);
        touch_count_matrix.close();
        touch_portion_matrix.close();
        vector.close();

        return result;
    }

    @Override
    public String getLUTName() {
        return "Fire";
    }

    @Override
    public String getName() {
        return "Touch Portion Mesh";
    }

    @Override
    public boolean getDefaultActivated() {
        return false;
    }
}
