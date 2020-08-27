package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.plugins.AutoThresholderImageJ1;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.optimize.BinaryAnnotationTool;
import net.haesleinhuepf.clijx.assistant.optimize.OptimizationUtilities;
import net.haesleinhuepf.clijx.assistant.optimize.SimplexOptimizer;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.clijx.assistant.utilities.IJLogger;
import net.haesleinhuepf.clijx.assistant.utilities.Logger;
import net.haesleinhuepf.clijx.weka.GenerateFeatureStack;
import net.haesleinhuepf.clijx.weka.TrainWekaModel;
import net.haesleinhuepf.clijx.weka.TrainWekaModelWithOptions;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;
import org.scijava.util.VersionUtils;

import javax.swing.*;
import java.awt.*;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.addMenuAction;

@Plugin(type = AssistantGUIPlugin.class)
public class BinaryWekaPixelClassifier extends AbstractAssistantGUIPlugin {

    GenericDialog dialog;

    public BinaryWekaPixelClassifier() {
        super(new net.haesleinhuepf.clijx.plugins.BinaryWekaPixelClassifier());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = super.buildNonModalDialog(parent);
        dialog = gd;

        gd.addToSameRow();
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

        for (int i = 0; i < 2; i++) {
            TextField t = ((TextField) gd.getStringFields().get(i));
            t.setMinimumSize(new Dimension(200, t.getHeight()));
        }

        return gd;
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
            Toolbar.addPlugInTool(new BinaryAnnotationTool());
            return;
        }
        ClearCLBuffer ground_truth = OptimizationUtilities.makeGroundTruth(clij2, my_target.getWidth(), my_target.getHeight(), my_target.getNSlices(), rm);

        ClearCLBuffer[] pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        ClearCLBuffer input = pushed[my_source.getC() - 1];

        String feature_definitions = ((TextField) dialog.getStringFields().get(0)).getText();
        String model_filename = ((TextField) dialog.getStringFields().get(1)).getText();

        int num_trees = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(0)).getText());
        int num_features = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(0)).getText());
        int max_depth = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(0)).getText());

        ClearCLBuffer featureStack = GenerateFeatureStack.generateFeatureStack(clij2, input, feature_definitions);

        TrainWekaModelWithOptions.trainWekaModelWithOptions(clij2, featureStack, ground_truth, model_filename, num_trees, num_features, max_depth);

        cleanup(my_source, pushed);

        ground_truth.close();
        featureStack.close();

        logger.log("Model saved to " + model_filename);
        setTargetInvalid();
        logger.log("Bye.");
    }

    @Override
    protected void addMoreActions(Menu more_actions) {
        addMenuAction(more_actions, "Train classifier", (a) -> {
            train(new IJLogger());
        });
    }

}