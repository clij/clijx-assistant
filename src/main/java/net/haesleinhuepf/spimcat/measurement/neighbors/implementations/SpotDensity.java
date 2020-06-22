package net.haesleinhuepf.spimcat.measurement.neighbors.implementations;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.spimcat.measurement.neighbors.NeighborProcessor;

public class SpotDensity implements NeighborProcessor, OffersDocumentation {


    private int radius;

    public SpotDensity(int radius){

        this.radius = radius;
    }

    @Override
    public ClearCLBuffer process(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer pointlist, ClearCLBuffer label_map, ClearCLBuffer touch_matrix, ClearCLBuffer distance_matrix) {
        ClearCLBuffer temp = clij2.create(pointlist.getWidth(), pointlist.getHeight() + 1);
        clij2.set(temp, 1);
        clij2.paste(pointlist, temp, 0, 0);


        ClearCLBuffer spots = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        clij2.writeValuesToPositions(temp, spots);
        temp.close();



        ClearCLBuffer result = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
        if (input.getDimension() == 2) {
            clij2.countNonZeroPixels2DSphere(spots, result, radius, radius);
        } else {
            clij2.countNonZeroVoxels3DSphere(spots, result, radius, radius, 0);
        }
        spots.close();
        return result;
    }

    @Override
    public String getLUTName() {
        return "Fire";
    }

    @Override
    public String getName() {
        return "Spot density locally (r = " + radius + ")";
    }

    @Override
    public boolean getDefaultActivated() {
        return false;
    }


    @Override
    public String getDescription() {
        return "Measures the number of label center points (spots) in a given radius around every pixel. This measure is a surrogate parameter for object density.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D/3D";
    }
}
