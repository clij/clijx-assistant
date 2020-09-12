package net.haesleinhuepf.clijx.plugins;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_zoom")
public class Zoom extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized {

    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number zoom_factor";
    }

    @Override
    public Object[] getDefaultValues() {
        return new Object[]{null, null, 1};
    }

    @Override
    public boolean executeCL() {
        return zoom(getCLIJ2(), (ClearCLBuffer) args[0], (ClearCLBuffer) args[1], asFloat(args[2]));
    }

    public static boolean zoom(CLIJ2 clij2, ClearCLBuffer pushed, ClearCLBuffer result, Float zoom_factor) {

        if (pushed.getDimension() == 2) {
            clij2.scale2D(pushed, result, zoom_factor, zoom_factor);
        } else {
            clij2.scale3D(pushed, result, zoom_factor, zoom_factor, zoom_factor);
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "See Scale2D and Scale3D.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        float zoom_factor = asFloat(args[2]);
        long[] dimensions = getNewDimensions(input.getDimensions(), zoom_factor);
        return getCLIJ2().create(dimensions, input.getNativeType());
    }

    public static long[] getNewDimensions(long[] old_dimensions, float zoom_factor) {
        long[] dimensions = new long[old_dimensions.length];
        for (int d = 0; d < dimensions.length; d++) {
            dimensions[d] = (long) (old_dimensions[d] * zoom_factor);
        }

        return dimensions;
    }



    @Override
    public String getCategories() {
        return "Transform";
    }
}
