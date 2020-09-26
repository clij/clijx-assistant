package net.haesleinhuepf.clijx.plugins;


import ij.measure.ResultsTable;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.haesleinhuepf.clijx.weka.ApplyWekaModel;
import net.haesleinhuepf.clijx.weka.ApplyWekaToTable;
import net.haesleinhuepf.clijx.weka.CLIJxWeka2;
import net.haesleinhuepf.clijx.weka.GenerateFeatureStack;
import org.scijava.plugin.Plugin;

import java.io.File;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_wekaLabelClassifier")
public class WekaLabelClassifier extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized {

    private static CLIJxWeka2 clijxWeka2 = null;
    private static String last_loaded_filename = "";

    @Override
    public String getParameterHelpText() {
        return "Image input, Image label_map, ByRef Image destination, String features, String modelfilename";
    }

    @Override
    public Object[] getDefaultValues() {
        return new Object[]{null, null, null, GenerateLabelFeatureImage.defaultFeatures(), "object_classifier.model"};
    }

    @Override
    public boolean executeCL() {
        ClearCLBuffer input = (ClearCLBuffer) args[0];
        ClearCLBuffer labelmap = (ClearCLBuffer) args[1];
        ClearCLBuffer output = (ClearCLBuffer) args[2];

        String features = (String) args[3];
        String model_filename = (String) args[4];

        return wekaLabelClassifier(getCLIJ2(), input, labelmap, output, features, model_filename);
    }

    public static boolean wekaLabelClassifier(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer labelMap, ClearCLBuffer output, String features, String model_filename)
    {
        if (!new File(model_filename).exists()) {
            clij2.set(output, 0);
            System.out.println("Model " + model_filename + " not found. Cancelling WekaLabelClassifier.");
            return true;
        }

        ClearCLBuffer featureImage = GenerateLabelFeatureImage.generateLabelFeatureImage(clij2, input, labelMap, features);
        clij2.print(featureImage);

        ResultsTable table = new ResultsTable();
        clij2.pullToResultsTable(featureImage, table);
        featureImage.close();


        if (!(clijxWeka2 != null && last_loaded_filename.compareTo(model_filename) == 0)) {
            clijxWeka2 = new CLIJxWeka2(clij2, null, model_filename);
            last_loaded_filename = model_filename;
            System.out.println("Load model");
        }

        ApplyWekaToTable.applyWekaToTable(clij2, table, "CLASS", clijxWeka2);

        //table.show("PREDICTION");

        ClearCLBuffer vector = clij2.create(table.size(), 1, 1);
        clij2.pushResultsTableColumn(vector, table, "CLASS");

        ClearCLBuffer vector_with_background = clij2.create(table.size() + 1, 1, 1);
        clij2.set(vector_with_background, 0);
        clij2.paste(vector, vector_with_background, 1, 0, 0);

        //System.out.println("Vector");
        //clij2.print(vector);
        //System.out.println("Vector with bg");
        //clij2.print(vector_with_background);

        clij2.replaceIntensities(labelMap, vector_with_background, output);

        //clij2.show(labelMap, "labels");
        //clij2.show(output, "output");

        vector.close();
        vector_with_background.close();

        return true;
    }

    @Override
    public String getDescription() {
        return "Applies a pre-trained CLIJx-Weka model to an image and a corresponding label map. \n\n" +
                "" +
                "Make sure that the handed over feature list is the same used while training the model.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Label,Segmentation";
    }

    public static void invalidateCache() {
        last_loaded_filename = "";
    }

}
