package net.haesleinhuepf.clijx.plugins;


import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.haesleinhuepf.clijx.weka.ApplyWekaModel;
import net.haesleinhuepf.clijx.weka.GenerateFeatureStack;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_binaryWekaPixelClassifier")
public class BinaryWekaPixelClassifier extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized {

    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, String features, String modelfilename";
    }

    @Override
    public Object[] getDefaultValues() {
        return new Object[]{null, null, "original gaussianblur=1 gaussianblur=5 sobelofgaussian=1 sobelofgaussian=5", "file.model"};
    }

    @Override
    public boolean executeCL() {
        ClearCLBuffer input = (ClearCLBuffer) args[0];
        ClearCLBuffer output = (ClearCLBuffer) args[1];

        String features = (String) args[2];
        String model_filename = (String) args[3];

        return binaryWekaPixelClassifier(getCLIJ2(), input, output, features, model_filename);
    }

    public static boolean binaryWekaPixelClassifier(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, String features, String model_filename)
    {
        if (!new File(model_filename).exists()) {
            clij2.set(output, 0);
            System.out.println("Model " + model_filename + " not found. Cancelling binaryWekaPixelClassifier.");
            return true;
        }
        ClearCLBuffer featureStack = GenerateFeatureStack.generateFeatureStack(clij2, input, features);

        ClearCLBuffer temp = clij2.create(output);

        ApplyWekaModel.applyWekaModel(clij2, featureStack, temp, model_filename);
        featureStack.close();

        clij2.addImageAndScalar(temp, output, -1);
        temp.close();

        return true;
    }

    @Override
    public String getDescription() {
        return "Applies a pre-trained CLIJx-Weka model to a 2D image. \n\n" +
                "You can train your own model using menu Plugins > Segmentation > CLIJx Binary Weka Pixel Classifier" +
                "Make sure that the handed over feature list is the same used while training the model.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D";
    }

    @Override
    public String getCategories() {
        return "Binary,Segmentation";
    }
}
