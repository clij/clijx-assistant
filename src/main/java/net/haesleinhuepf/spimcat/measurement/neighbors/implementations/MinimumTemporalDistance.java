package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;
import net.haesleinhuepf.spimcat.measurement.neighbors.TakesFormerLabelMap;
import net.haesleinhuepf.spimcat.measurement.neighbors.TakesFormerPointlist;

public class MinimumTemporalDistance implements NeighborProcessor, TakesFormerLabelMap, TakesFormerPointlist {
    private ClearCLBuffer former_labelMap;
    private ClearCLBuffer former_pointlist;

    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        if (former_labelMap == null || former_pointlist == null) {
            clij2.set(result, 0);
            return result;
        }

        long number_of_labels = pointlist.getWidth();
        long number_of_former_labels = former_pointlist.getWidth();
        ClearCLBuffer displacement_matrix = clij2.create(number_of_labels + 1, number_of_former_labels + 1);
        clij2.generateDistanceMatrix(pointlist, former_pointlist, displacement_matrix);

        //clij2.show(displacement_matrix, "ds");
        //new WaitForUserDialog("dd").show();

        clij2.setRow(displacement_matrix, 0, Float.MAX_VALUE);
        clij2.setColumn(displacement_matrix, 0, Float.MAX_VALUE);

        ClearCLBuffer minimum_displacement_vector = clij2.create(number_of_labels, 1, 1 );
        clij2.minimumYProjection(displacement_matrix, minimum_displacement_vector);



        clij2.replaceIntensities(label_map, minimum_displacement_vector, result);
        return result;
    }

    @Override
    public String getLUTName() {
        return "Fire";
    }

    @Override
    public String getName() {
        return "Minimum temporal distance";
    }

    @Override
    public boolean getDefaultActivated() {
        return false;
    }

    @Override
    public void setFormerLabelMap(ClearCLBuffer labelMap) {
        this.former_labelMap = labelMap;
    }

    @Override
    public void setFormerPointlist(ClearCLBuffer pointlist) {
        this.former_pointlist = pointlist;
    }
}
