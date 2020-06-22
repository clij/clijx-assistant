package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;

public class VoronoiProcessor implements NeighborProcessor {
    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        ClearCLBuffer temp = clij2.create(pointlist.getWidth(), pointlist.getHeight() + 1);
        clij2.set(temp, 1);
        clij2.paste(pointlist, temp, 0, 0);


        ClearCLBuffer spots = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.writeValuesToPositions(temp, spots);
        temp.close();

        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.voronoiOctagon(spots, result);
        spots.close();

        return result;
    }

    @Override
    public String getLUTName() {
        return "Grays";
    }

    @Override
    public String getName() {
        return "Voronoi diagram";
    }

    @Override
    public boolean getDefaultActivated() {
        return false;
    }
}
