package net.haesleinhuepf.spimcat.measurement.neighbors;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

public interface NeighborProcessor {
    ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix);

    String getLUTName();
    String getName();
    boolean getDefaultActivated();
}
