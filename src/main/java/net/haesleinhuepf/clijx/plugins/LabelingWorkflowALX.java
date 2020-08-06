package net.haesleinhuepf.clijx.plugins;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_labelingWorkflowALX")
public class LabelingWorkflowALX extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number sigma_x, Number sigma_y, Number sigma_z, Number threshold, Boolean show_circles";
    }

    @Override
    public Object[] getDefaultValues() {
        return new Object[]{null, null, 3, 3, 1, 1000, true};
    }

    @Override
    public boolean executeCL() {
        return labelingWorkflowALX(getCLIJ2(), (ClearCLBuffer) args[0], (ClearCLBuffer) args[1], asFloat(args[2]), asFloat(args[3]), asFloat(args[4]), asFloat(args[5]), asBoolean(args[6]));
    }

    public static boolean labelingWorkflowALX(CLIJ2 clijx, ClearCLBuffer pushed, ClearCLBuffer result, Float sigma_x, Float sigma_y, Float sigma_z, Float threshold, Boolean show_circles) {
        ClearCLBuffer blurred = clijx.create(pushed.getDimensions(), NativeTypeEnum.Float);
        clijx.gaussianBlur3D(pushed, blurred, sigma_x, sigma_y, sigma_z);

        ClearCLBuffer thresholded = clijx.create(pushed.getDimensions(), NativeTypeEnum.UnsignedByte);
        clijx.greaterConstant(blurred, thresholded, threshold);

        ClearCLBuffer maxima = clijx.create(pushed.getDimensions(), NativeTypeEnum.UnsignedByte);
        clijx.detectMaximaBox(blurred, maxima, 1, 1, 1);
        blurred.close();

        ClearCLBuffer maxima_above_threshold = clijx.create(pushed.getDimensions(), NativeTypeEnum.UnsignedByte);
        clijx.binaryAnd(thresholded, maxima, maxima_above_threshold);
        thresholded.close();
        maxima.close();

        if (!show_circles) {
            clijx.labelSpots(maxima_above_threshold, result);
            return true;
        }

        ClearCLBuffer labelmap = clijx.create(pushed.getDimensions(), NativeTypeEnum.Float);
        clijx.labelSpots(maxima_above_threshold, labelmap);

        clijx.maximum3DSphere(labelmap, result, sigma_x * 2, sigma_y * 2, sigma_z * 2);
        labelmap.close();

        return true;
    }

    @Override
    public String getDescription() {
        return "A segmentation workflow using maxima detection, thresholding, maximum filters and label edge detection.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return getCLIJ2().create(input.getDimensions(), NativeTypeEnum.Float);
    }
}
