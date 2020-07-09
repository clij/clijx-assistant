package net.haesleinhuepf.clincubator.projection;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.plugins.ArgMaximumZProjection;

public enum Projection {
    Maximum_Intensity,
    Minimum_Intensity,
    Sum_Intensity,
    Mean_Average_Intensity,
    Median_Intensity,
    Standard_Deviation_Intensity,
    Arg_Maximum_Projection;

    public static Projection[] all() {
        return new Projection[]{
                Maximum_Intensity,
                Minimum_Intensity,
                Sum_Intensity,
                Mean_Average_Intensity,
                Median_Intensity,
                Standard_Deviation_Intensity,
                Arg_Maximum_Projection};
    }

    public static String[] allToString() {
        String[] result = new String[all().length];
        int i = 0;
        for (Projection projection : all()) {
            result[i] = projection.toString();
            i++;
        }
        return result;
    }
}
