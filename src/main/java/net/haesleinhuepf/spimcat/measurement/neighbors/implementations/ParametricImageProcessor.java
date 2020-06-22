package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import ij.measure.ResultsTable;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;
import net.haesleinhuepf.spimcat.measurement.neighbors.TakesPropertyTable;

public class ParametricImageProcessor implements NeighborProcessor, TakesPropertyTable {

    private ResultsTable table = null;
    private String column;

    public ParametricImageProcessor(String column) {
        this.column = column;
    }

    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        ClearCLBuffer column_buffer = clij2.create(pointlist.getWidth() + 1, 1, 1);

        clij2.pushResultsTableColumn(column_buffer, table, column);

        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.replaceIntensities(label_map, column_buffer, result);
        column_buffer.close();
        return result;
    }

    @Override
    public String getLUTName() {
        return "Fire";
    }

    @Override
    public String getName() {
        return column;
    }

    @Override
    public boolean getDefaultActivated() {
        return false;
    }

    @Override
    public void setTable(ResultsTable table) {
        this.table = table;
    }
}
