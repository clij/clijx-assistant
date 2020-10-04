package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
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
import net.haesleinhuepf.clijx.weka.GenerateFeatureStack;
import net.haesleinhuepf.clijx.weka.TrainWekaModelWithOptions;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;
import org.scijava.util.VersionUtils;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.addMenuAction;

@Plugin(type = AssistantGUIPlugin.class)
public class BinaryWekaPixelClassifier extends AbstractAssistantGUIPlugin {

    String features = "";
    String filename = "pixel_classification.model";

    GenericDialog dialog;

    public BinaryWekaPixelClassifier() {
        super(new net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = new GenericDialog(AssistantUtilities.niceNameWithoutDimShape(this.getName()));
        dialog = gd;

        features = (String)((net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier)(getCLIJMacroPlugin())).getDefaultValues()[2];
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
                sub_dialog.addMessage("Enter radii");
                String[] allFeatures = GenerateFeatureStack.allFeatures();
                for (String entry : allFeatures) {
                    if (entry.startsWith("*")) {
                        sub_dialog.addCheckbox(entry.substring(1), (" " + features + " ").toLowerCase().contains(" " + entry.substring(1).toLowerCase() + " "));
                    } else {
                        sub_dialog.addStringField(entry, extractRadii(features, entry), 30);
                    }
                }
                sub_dialog.showDialog();
                if (sub_dialog.wasCanceled()) {
                    return;
                }

                String new_features = " ";
                for (String entry : allFeatures) {
                    //if (sub_dialog.getNextBoolean()) {
                    //    new_features = new_features + entry + " ";
                    //}
                    if (entry.startsWith("*")) {
                        if (sub_dialog.getNextBoolean()) {
                            new_features = new_features.trim() + " " + entry.substring(1) + " ";
                        }
                        //sub_dialog.addCheckbox(entry, (" " + features + " ").toLowerCase().contains(" " + entry.toLowerCase() + " "));
                    } else {
                        //sub_dialog.addStringField(entry, extractRadii(features, entry), 30);
                        new_features = new_features.trim() + " " + compressRadii(sub_dialog.getNextString(), entry);
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

        //gd.addToSameRow();
        {
            Panel panel = new Panel();
            Button button = new Button("File...");
            button.addActionListener((a) -> {
                String file = IJ.getFilePath("Model location");
                if (file.length() > 0) {
                    ((TextField) gd.getStringFields().get(1)).setText(file);
                }
            });
            panel.add(button);
            gd.addPanel(panel);
        }

        gd.addNumericField("Number of trees", 200, 0);
        gd.addNumericField("Number of features", 2, 0);
        gd.addNumericField("Max depth", 0, 0);

        /*{
            Panel panel = new Panel();
            Button button = new Button("Train");
            button.addActionListener((a) -> {
                train(new IJLogger());
            });
            panel.add(button);
            gd.addPanel(panel);
        }*/
        gd.addMessage("Note: This only works for 2D images for now.");

        //for (int i = 0; i < 2; i++) {
        //    TextField t = ((TextField) gd.getStringFields().get(i));
        //    t.setMinimumSize(new Dimension(200, t.getHeight()));
        //}

        return gd;
    }

    private String extractRadii(String features, String searchfor) {
        String result = "";
        for (String entry : features.split(" ")) {
            if (entry.startsWith(searchfor)) {
                if (result.length() == 0) {
                    result = entry.split("=")[1];
                } else {
                    result = result + ", " + entry.split("=")[1];
                }
            }
        }
        return result;
    }

    private String compressRadii(String input, String parameter) {
        String result = "";
        for (String entry : input.replace(" ", "").split(",")) {
            if (entry.trim().length() > 0) {
                result = result + parameter + "=" + entry.trim() + " ";
            }
        }
        return result.trim();
    }


    private void train(Logger logger) {
        logger.log("Train Weka");
        logger.log("----------");

        CLIJ2 clij2 = CLIJx.getInstance();
        logger.log("GPU: " + clij2.getGPUName() + " (OCLv: " + clij2.getOpenCLVersion() + ", AssistantV: " + VersionUtils.getVersion(this.getClass()) + ")");

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
        ClearCLBuffer ground_truth = OptimizationUtilities.makeGroundTruth(clij2, my_target.getWidth(), my_target.getHeight(), my_target.getNSlices(), rm);

        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);
        ClearCLBuffer input = pushed[0][my_sources[0].getC() - 1];

        features = ((TextField) dialog.getStringFields().get(0)).getText();
        filename = ((TextField) dialog.getStringFields().get(1)).getText();

        int num_trees = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(0)).getText());
        int num_features = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(1)).getText());
        int max_depth = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(2)).getText());

        ClearCLBuffer featureStack = GenerateFeatureStack.generateFeatureStack(clij2, input, features);

        net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier.invalidateCache();

        TrainWekaModelWithOptions.trainWekaModelWithOptions(clij2, featureStack, ground_truth, filename, num_trees, num_features, max_depth);

        cleanup(my_sources, pushed);

        ground_truth.close();
        featureStack.close();
        saveFeatures(filename + ".features.txt", features);
        logger.log("Model saved to " + filename);
        setTargetInvalid();
        logger.log("Bye.");
    }

    @Override
    protected void addMoreActions(Menu more_actions) {
        addMenuAction(more_actions, "Train classifier", (a) -> {
            train(new IJLogger());
        });
    }

    static String loadFeatures(String filename) {
        String features = "";
        try {
            if (new File(filename).exists()) {
                features = new String(Files.readAllBytes(Paths.get(filename)));
            } else {
                System.out.println(filename + " doesn't exist.");
            }
        } catch (NoSuchFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return features;
    }

    static void saveFeatures(String filename, String features) {
        File outputTarget = new File(filename);
        try {
            FileWriter writer = new FileWriter(outputTarget);
            writer.write(features);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}