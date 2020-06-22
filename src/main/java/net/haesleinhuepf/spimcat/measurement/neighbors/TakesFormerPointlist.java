package net.haesleinhuepf.spimcat.measurement.neighbors;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;

public interface TakesFormerPointlist {
    void setFormerPointlist(ClearCLBuffer pointlist);
}
