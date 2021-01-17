package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.annotation.AnnotationTool;
import net.haesleinhuepf.clijx.assistant.optimize.OptimizationUtilities;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.clijx.assistant.utilities.IJLogger;
import net.haesleinhuepf.clijx.assistant.utilities.Logger;
import net.haesleinhuepf.clijx.weka.GenerateLabelFeatureImage;
import net.haesleinhuepf.clijx.weka.TrainWekaFromTable;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.apache.commons.math3.ml.clustering.*;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.scijava.plugin.Plugin;
import org.scijava.util.VersionUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.haesleinhuepf.clijx.assistant.interactive.handcrafted.BinaryWekaPixelClassifier.loadFeatures;
import static net.haesleinhuepf.clijx.assistant.interactive.handcrafted.BinaryWekaPixelClassifier.saveFeatures;
import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.addMenuAction;
import static net.haesleinhuepf.clijx.plugins.KMeansLabelClusterer.tableToList;

@Plugin(type = AssistantGUIPlugin.class)
public class LabelClusterer extends AbstractAssistantGUIPlugin {

    GenericDialog dialog;

    String features = GenerateLabelFeatureImage.defaultFeatures();
    String filename = ".model";

    int num_classes = 2;
    int neighbor_radius = 0;

    boolean show_table = false;

    public LabelClusterer() {
        super(new net.haesleinhuepf.clijx.plugins.KMeansLabelClusterer());
        filename = getCLIJMacroPlugin().getName() + ".model";
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {

        GenericDialog gd = new GenericDialog(AssistantUtilities.niceNameWithoutDimShape(this.getName()));
        dialog = gd;

        String temp = loadFeatures(filename + ".features.txt");
        if (temp.length() > 0) {
            features = temp;
        }
        gd.addStringField("Feature definition", features, 30);
        TextField feature_field = ((TextField) gd.getStringFields().get(0));

        //gd.addToSameRow();
        {
            Panel panel = new Panel();
            Button button = new Button("Features...");
            button.addActionListener((a) -> {
                GenericDialog sub_dialog = new GenericDialog("Features");
                String[] allFeatures = GenerateLabelFeatureImage.allFeatures();
                for (String entry : allFeatures) {
                    sub_dialog.addCheckbox(entry, (" " + features + " ").toLowerCase().contains(" " + entry.toLowerCase() + " "));
                }
                sub_dialog.showDialog();
                if (sub_dialog.wasCanceled()) {
                    return;
                }

                String new_features = " ";
                for (String entry : allFeatures) {
                    if (sub_dialog.getNextBoolean()) {
                        new_features = new_features + entry + " ";
                    }
                }
                if (new_features.length() > 1) {
                    feature_field.setText(new_features.trim());
                }
            });
            panel.add(button);
            //gd.add(new Panel());
            //gd.addToSameRow();
            gd.addPanel(panel);
        }


        gd.addStringField("Model file", filename, 30);

        TextField filename_field = ((TextField) gd.getStringFields().get(1));
        //gd.addToSameRow();
        {
            Panel panel = new Panel();
            Button button = new Button("File...");
            button.addActionListener((a) -> {
                String file = IJ.getFilePath("Model location");
                if (file.length() > 0) {
                    filename_field.setText(file);

                    loadFeatures(filename + ".features.txt");
                    feature_field.setText(features);
                }
            });
            panel.add(button);
            gd.addPanel(panel);
        }

        gd.addNumericField("Number of classes", num_classes, 0);
        addPlusMinusPanel(gd, "num_classes");

        gd.addNumericField("Neighbor radius", neighbor_radius, 0);
        addPlusMinusPanel(gd, "num_classes");

        gd.addCheckbox("Show table while training", show_table);

        return gd;
    }


    private void readDialog() {
        if (dialog != null) {
            features = ((TextField) dialog.getStringFields().get(0)).getText();
            filename = ((TextField) dialog.getStringFields().get(1)).getText();

            num_classes = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(0)).getText());
            neighbor_radius = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(1)).getText());
            show_table = ((Checkbox)dialog.getCheckboxes().get(0)).getState();
        }
    }

    private void train(Logger logger) {
        logger.log("Train clustering");
        logger.log("----------------");
        logger.log("Plugin: " + getCLIJMacroPlugin().getName());

        CLIJ2 clij2 = CLIJx.getInstance();
        logger.log("GPU: " + clij2.getGPUName() + " (OCLv: " + clij2.getOpenCLVersion() + ", AssistantV: " + VersionUtils.getVersion(this.getClass()) + ")");


        readDialog();

        // -------------------------------------------------------------------------------------------------------------
        // determine ground truth
        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);

        ClearCLBuffer input_image = pushed[0][my_sources[0].getC() - 1];
        ClearCLBuffer label_map = pushed[1][my_sources[1].getC() - 1];

        IJ.log("Intensity image: " + my_sources[0].getTitle());
        IJ.log("Label image: " + my_sources[1].getTitle());
        if (my_sources[0].getC() != my_sources[1].getC()) {
            IJ.log("Warning: intensity and label image have different selected channels.");
        }

        String feature_definitions = ((TextField) dialog.getStringFields().get(0)).getText();

        /*ResultsTable table = new ResultsTable();
        {
            ClearCLBuffer featureImage = GenerateLabelFeatureImage.generateLabelFeatureImage(clij2, input_image, label_map, feature_definitions);
            clij2.pullToResultsTable(featureImage, table);
            featureImage.close();
        }*/
        //input_image.close();

        String model_filename = ((TextField) dialog.getStringFields().get(1)).getText();



        ClearCLBuffer temp = clij2.create(label_map);

        if (getCLIJMacroPlugin() instanceof net.haesleinhuepf.clijx.plugins.KMeansLabelClusterer) {
            net.haesleinhuepf.clijx.plugins.KMeansLabelClusterer.kMeansLabelClusterer(clij2, input_image, label_map, temp, feature_definitions, model_filename, num_classes, neighbor_radius, true);
        } // else ...
        //trainKMeansClustering(clij2, table, "CLASS", model_filename, num_classes);
        //TrainWekaFromTable.trainWekaFromTable(clij2, table, "CLASS", model_filename, num_trees, num_features, max_depth);

        temp.close();

        cleanup(my_sources, pushed);


        logger.log("Model saved to " + model_filename);
        saveFeatures(model_filename + ".features.txt", features);
        logger.log("Featurelist saved to " + model_filename + ".features.txt");
        setTargetInvalid();
        logger.log("Bye.");
    }




    @Override
    protected void addMoreActions(Menu more_actions) {
        addMenuAction(more_actions, "Train classifier", (a) -> {
            train(new IJLogger());
        });
    }

    public static ResultsTable filterTable(ResultsTable table, String class_header) {
        ResultsTable collection = new ResultsTable();
        for (int j = 0; j < table.size(); j++) {
            if (table.getValue(class_header, j) > 0) {
                collection.incrementCounter();
                for (String header : table.getHeadings()) {
                    collection.addValue(header, table.getValue(header, j));
                }
            }
        }
        return collection;
    }

}