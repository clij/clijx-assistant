package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
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
import org.scijava.plugin.Plugin;
import org.scijava.util.VersionUtils;

import java.awt.*;
import java.util.HashMap;

import static net.haesleinhuepf.clijx.assistant.interactive.handcrafted.BinaryWekaPixelClassifier.loadFeatures;
import static net.haesleinhuepf.clijx.assistant.interactive.handcrafted.BinaryWekaPixelClassifier.saveFeatures;
import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.addMenuAction;

@Plugin(type = AssistantGUIPlugin.class)
public class WekaLabelClassifier extends AbstractAssistantGUIPlugin {

    GenericDialog dialog;

    String features = GenerateLabelFeatureImage.defaultFeatures();
    String filename = "label_classification.model";

    int num_trees = 200;
    int num_features = 2;
    int max_depth = 0;

    int radius_of_maximum = 0;
    int radius_of_minimum = 0;
    int radius_of_mean = 1;
    int radius_of_standard_deviation = 0;

    boolean show_table = false;

    public WekaLabelClassifier() {
        super(new net.haesleinhuepf.clijx.weka.WekaLabelClassifier());
    }
    public WekaLabelClassifier(CLIJMacroPlugin pass_on) {
        super(pass_on);
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

        gd.addNumericField("Number of trees", num_trees, 0);
        gd.addNumericField("Number of features", num_features, 0);
        gd.addNumericField("Max depth", max_depth, 0);

        if (getCLIJMacroPlugin() instanceof net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier) {
            gd.addNumericField("Radius_of_maximum", radius_of_maximum);
            gd.addNumericField("Radius_of_minimum", radius_of_minimum);
            gd.addNumericField("Radius_of_mean", radius_of_mean);
            gd.addNumericField("Radius_of_standard_deviation", radius_of_standard_deviation);
        }

        gd.addCheckbox("Show table while training", show_table);

        return gd;
    }

/*
    @Override
    public void refresh() {
        CLIJx clijx = CLIJx.getInstance();

        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);
        ClearCLBuffer image = pushed[0][my_sources[0].getC() - 1];
        ClearCLBuffer labelmap = pushed[1][my_sources[1].getC() - 1];



        readDialog();


        CLIJMacroPlugin plugin = getCLIJMacroPlugin();

        args = new Object[plugin.getParameterHelpText().split(",").length];
        args[0] = pushed[0]; // todo: potentially store the whole array here
        plugin.setArgs(args);
        if (result == null) {
            result = createOutputBufferFromSource(pushed[0]);
        }
        //args[1] = result[0]; // todo: potentially store the whole array here
        //args = new Object[]{image, labelmap, result[0], features, filename};
        //plugin.setArgs(args);

        executeCL(pushed, new ClearCLBuffer[][]{result});

        cleanup(my_sources, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle(AssistantUtilities.niceNameWithoutDimShape(this.getName()) + " of " + my_sources[0].getTitle());
        enhanceContrast();

    }*/

    private void readDialog() {
        if (dialog != null) {
            features = ((TextField) dialog.getStringFields().get(0)).getText();
            filename = ((TextField) dialog.getStringFields().get(1)).getText();

            num_trees = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(0)).getText());
            num_features = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(1)).getText());
            max_depth = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(2)).getText());
            show_table = ((Checkbox) dialog.getCheckboxes().get(0)).getState();

            //feature_field.setMinimumSize(new Dimension(500, 10));
            //feature_field.setMaximumSize(new Dimension(500, 10));
            //feature_field.setSize(500, feature_field.getHeight());
            if (getCLIJMacroPlugin() instanceof net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier) {
                radius_of_maximum = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(3)).getText());
                radius_of_minimum = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(4)).getText());
                radius_of_mean = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(5)).getText());
                radius_of_standard_deviation = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(6)).getText());
            }
        }
    }

    private void train(Logger logger) {

        if (getCLIJMacroPlugin() instanceof net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier) {
            logger.log("Train Weka regional label classifier");
            logger.log("------------------------------------");
        } else {
            logger.log("Train Weka label classifier");
            logger.log("---------------------------");
        }

        CLIJ2 clij2 = CLIJx.getInstance();
        logger.log("GPU: " + clij2.getGPUName() + " (OCLv: " + clij2.getOpenCLVersion() + ", AssistantV: " + VersionUtils.getVersion(this.getClass()) + ")");


        readDialog();

        // -------------------------------------------------------------------------------------------------------------
        // determine ground truth
        RoiManager rm = RoiManager.getRoiManager();
        if (rm.getCount() == 0) {
            IJ.log("Please define reference ROIs in the ROI Manager.\n\n" +
                    "These ROIs should have names starting with 'p' for positive and 'n' for negative.\n\n" +
                    "The just activated annotation tool can help you with that.");
            Toolbar.addPlugInTool(new AnnotationTool());
            return;
        }

        HashMap<Integer, Integer> ground_truth = OptimizationUtilities.makeLabelClassificationGroundTruth(clij2, my_sources[1], rm);

        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);

        ClearCLBuffer input_image = pushed[0][my_sources[0].getC() - 1];
        ClearCLBuffer label_map = pushed[1][my_sources[1].getC() - 1];

        IJ.log("Intensity image: " + my_sources[0].getTitle());
        IJ.log("Label image: " + my_sources[1].getTitle());
        if (my_sources[0].getC() != my_sources[1].getC()) {
            IJ.log("Warning: intensity and label image have different selected channels.");
        }

        String feature_definitions = ((TextField) dialog.getStringFields().get(0)).getText();

        ResultsTable table = new ResultsTable();
        {
            ClearCLBuffer featureImage;
            if (getCLIJMacroPlugin() instanceof net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier) {
                featureImage = net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier.generateRegionalLabelFeatureImage(clij2, input_image, label_map, feature_definitions, radius_of_maximum, radius_of_minimum, radius_of_mean, radius_of_standard_deviation);
            } else {
                featureImage = GenerateLabelFeatureImage.generateLabelFeatureImage(clij2, input_image, label_map, feature_definitions);
            }
            clij2.pullToResultsTable(featureImage, table);
            featureImage.close();
        }
        //input_image.close();

        String model_filename = ((TextField) dialog.getStringFields().get(1)).getText();

        for (Integer label : ground_truth.keySet()) {
            int klass = ground_truth.get(label);
            System.out.println("Label " + label + ", class " + klass);
            if (label > 0) {
                table.setValue("CLASS", label - 1, klass);
            }
        }


        table = filterTable(table, "CLASS");

        if (show_table) {
            table.show("TRAINING");
        }

        net.haesleinhuepf.clijx.weka.WekaLabelClassifier.invalidateCache();

        TrainWekaFromTable.trainWekaFromTable(clij2, table, "CLASS", model_filename, num_trees, num_features, max_depth);

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