package net.haesleinhuepf.clincubator.projection;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.interfaces.ClearCLImageInterface;
import net.haesleinhuepf.clij2.CLIJ2;

public class Utilities {
    public static ClearCLBuffer project(CLIJ2 clij2, ClearCLBuffer input, Projection projection) {

        ClearCLBuffer output = null;
        if (projection == Projection.Arg_Maximum_Projection) {
            output = clij2.create(input.getWidth(), input.getHeight(), 2);
        } else {
            output = clij2.create(input.getWidth(), input.getHeight());
        }

        return project(clij2, input, output, projection);
    }
    public static ClearCLBuffer project(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, Projection projection) {
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

    static boolean topHatBox(CLIJ2 clij2, ClearCLBuffer input, ClearCLImageInterface output, Integer radiusX, Integer radiusY, Integer radiusZ) {

        ClearCLBuffer temp1 = clij2.create(input);
        ClearCLBuffer temp2 = clij2.create(input);

        if(input.getDimension() == 3) {
            clij2.minimum3DBox(input, temp1, radiusX, radiusX, radiusZ);
            clij2.maximum3DBox(temp1, temp2, radiusX, radiusY, radiusZ);
        } else {
            clij2.minimum2DBox(input, temp1, radiusX, radiusX);
            clij2.maximum2DBox(temp1, temp2, radiusX, radiusY);
        }

        clij2.subtractImages(input, temp2, output);

        clij2.release(temp1);
        clij2.release(temp2);
        return true;
    }

}
