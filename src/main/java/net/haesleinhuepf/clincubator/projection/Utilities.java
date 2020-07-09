package net.haesleinhuepf.clincubator.projection;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;

public class Utilities {
    public static ClearCLBuffer project(CLIJ2 clij2, ClearCLBuffer input, Projection projection) {

        ClearCLBuffer output = null;
        if (projection == Projection.Arg_Maximum_Projection) {
            output = clij2.create(input.getWidth(), input.getHeight(), 2);
        } else {
            output = clij2.create(input.getWidth(), input.getHeight());
        }

        switch(projection) {
            case Minimum_Intensity:
                clij2.minimumZProjection(input, output);
                break;
            case Sum_Intensity:
                clij2.sumZProjection(input, output);
                break;
            case Mean_Average_Intensity:
                clij2.meanZProjection(input, output);
                break;
            case Median_Intensity:
                clij2.medianZProjection(input, output);
                break;
            case Standard_Deviation_Intensity:
                clij2.standardDeviationZProjection(input, output);
                break;
            case Maximum_Intensity:
                clij2.maximumZProjection(input, output);
                break;
            case Arg_Maximum_Projection:

                ClearCLBuffer maximum_projection = clij2.create(input.getWidth(), input.getHeight());
                ClearCLBuffer arg_maximum_projection = clij2.create(input.getWidth(), input.getHeight());
                clij2.argMaximumZProjection(input, maximum_projection, arg_maximum_projection);

                // -------------------------------------------------------------------------------------------------------------
                // put results in a stack
                clij2.copySlice(maximum_projection, output, 0);
                clij2.copySlice(arg_maximum_projection, output, 1);

                maximum_projection.close();
                arg_maximum_projection.close();
                break;
            default:
                clij2.set(output, 0);
                break;
        }
        return output;
    }
}
