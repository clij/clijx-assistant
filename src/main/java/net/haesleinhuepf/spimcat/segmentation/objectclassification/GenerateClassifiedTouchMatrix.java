package net.haesleinhuepf.spimcat.segmentation.objectclassification;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import org.scijava.plugin.Plugin;

import java.util.HashMap;

/**
 * Author: @haesleinhuepf
 *         April 2020
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_generateClassifiedTouchMatrix")
public class GenerateClassifiedTouchMatrix extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public boolean executeCL() {
        boolean result = generateClassifiedTouchMatrix(getCLIJ2(), (ClearCLBuffer)( args[0]), (ClearCLBuffer)(args[1]), (ClearCLBuffer)(args[2]));
        return result;
    }

    public static boolean generateClassifiedTouchMatrix(CLIJ2 clij2, ClearCLBuffer src_label_map, ClearCLBuffer src_classification_vector, ClearCLBuffer dst_touch_matrix) {

        clij2.set(dst_touch_matrix, 0f);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("src_label_map", src_label_map);
        parameters.put("src_classification", src_classification_vector);
        parameters.put("dst_matrix", dst_touch_matrix);

        long[] globalSizes = src_label_map.getDimensions();

        clij2.activateSizeIndependentKernelCompilation();
        clij2.execute(GenerateClassifiedTouchMatrix.class, "generate_classified_touch_matrix_" + src_label_map.getDimension() + "d_x.cl", "generate_classified_touch_matrix_" + src_label_map.getDimension() + "d", globalSizes, globalSizes, parameters);
        return true;
    }

    @Override
    public String getParameterHelpText() {
        return "Image label_map, Image classification_vector, ByRef Image touch_matrix_destination";
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input)
    {
        double maxValue = clij.op().maximumOfAllPixels(input) + 1;
        ClearCLBuffer output = clij.createCLBuffer(new long[]{(long)maxValue, (long)maxValue}, NativeTypeEnum.Float);
        return output;
    }

    @Override
    public String getDescription() {
        return "Takes a labelmap with n labels and generates a (n+1)*(n+1) matrix where all pixels are set to 0 exept those where labels are touching. " +
                "Therefor it groups the labels in classes as defined in a given classification vector." +
                "Only half of the matrix is filled (with x < y). For example, if labels 3 and 4 are touching then the pixel (3,4) in the matrix will be set to 1.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }
}
