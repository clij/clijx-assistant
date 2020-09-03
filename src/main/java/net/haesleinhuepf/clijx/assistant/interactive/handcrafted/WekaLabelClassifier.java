package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.optimize.BinaryAnnotationTool;
import net.haesleinhuepf.clijx.assistant.optimize.OptimizationUtilities;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.clijx.assistant.utilities.IJLogger;
import net.haesleinhuepf.clijx.assistant.utilities.Logger;
import net.haesleinhuepf.clijx.plugins.GenerateLabelFeatureImage;
import net.haesleinhuepf.clijx.weka.GenerateFeatureStack;
import net.haesleinhuepf.clijx.weka.TrainWekaFromTable;
import net.haesleinhuepf.clijx.weka.TrainWekaModelWithOptions;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;
import org.scijava.util.VersionUtils;

import java.awt.*;
import java.lang.reflect.GenericArrayType;
import java.util.HashMap;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.addMenuAction;

@Plugin(type = AssistantGUIPlugin.class)
public class WekaLabelClassifier extends AbstractAssistantGUIPlugin {

    Choice choice = null;
    GenericDialog dialog;
    String[] titles;

    String features = GenerateLabelFeatureImage.defaultFeatures();
    String filename = "label_classification.model";

    int num_trees = 200;
    int num_features = 2;
    int max_depth = 0;

    public WekaLabelClassifier() {
        super(new net.haesleinhuepf.clijx.plugins.WekaLabelClassifier());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {

        GenericDialog gd = new GenericDialog(AssistantUtilities.niceName(this.getName()));
        dialog = gd;

        String[] titles = WindowManager.getImageTitles();
        gd.addChoice("Intensity image", titles, titles[titles.length - 2] );
        choice = (Choice) gd.getChoices().get(0);


        gd.addStringField("Feature definition", features);

        gd.addStringField("Model file", filename);

        TextField filename_field = ((TextField) gd.getStringFields().get(1));
        gd.addToSameRow();
        {
            Panel panel = new Panel();
            Button button = new Button("File...");
            button.addActionListener((a) -> {
                String file = IJ.getFilePath("Model location");
                if (file.length() > 0) {
                    filename_field.setText(file);
                }
            });
            panel.add(button);
            gd.addPanel(panel);
        }

        gd.addNumericField("Number of trees", 200, 0);
        gd.addNumericField("Number of features", 2, 0);
        gd.addNumericField("Max depth", 0, 0);

        return gd;
    }

    @Override
    public void refresh() {
        CLIJx clijx = CLIJx.getInstance();

        ClearCLBuffer[] pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        ClearCLBuffer labelmap = pushed[my_source.getC() - 1];

        ClearCLBuffer image = null;
        if (choice != null) {
            image = clijx.pushCurrentZStack(WindowManager.getImage(choice.getSelectedItem()));
        } else {
            image = pushed[0];
        }

        readDialog();


        CLIJMacroPlugin plugin = getCLIJMacroPlugin();

        args = new Object[plugin.getParameterHelpText().split(",").length];
        args[0] = pushed[0]; // todo: potentially store the whole array here
        plugin.setArgs(args);
        if (result == null) {
            result = createOutputBufferFromSource(pushed);
        }
        //args[1] = result[0]; // todo: potentially store the whole array here
        args = new Object[]{image, labelmap, result[0], features, filename};
        plugin.setArgs(args);

        executeCL(new ClearCLBuffer[][]{pushed, new ClearCLBuffer[]{image} ,result});
        if (image != pushed[0]) {
            image.close();
        }
        cleanup(my_source, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle(AssistantUtilities.niceName(this.getName()) + " of " + my_source.getTitle());
        enhanceContrast();

    }

    private void readDialog() {
        if (dialog != null) {
            features = ((TextField) dialog.getStringFields().get(0)).getText();
            filename = ((TextField) dialog.getStringFields().get(1)).getText();

            num_trees = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(0)).getText());
            num_features = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(1)).getText());
            max_depth = (int) Double.parseDouble(((TextField) dialog.getNumericFields().get(2)).getText());

        }
    }

    private void train(Logger logger) {
        logger.log("Train Weka label classifier");
        logger.log("---------------------------");

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
            Toolbar.addPlugInTool(new BinaryAnnotationTool());
            return;
        }

        HashMap<Integer, Integer> ground_truth = OptimizationUtilities.makeLabelClassificationGroundTruth(clij2, my_source, rm);

        ClearCLBuffer[] pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        ClearCLBuffer input_image = clij2.pushCurrentZStack(WindowManager.getImage(choice.getSelectedItem()));
        ClearCLBuffer label_map = pushed[my_source.getC() - 1];

        String feature_definitions = ((TextField) dialog.getStringFields().get(0)).getText();

        ResultsTable table = new ResultsTable();
        {
            ClearCLBuffer featureImage = GenerateLabelFeatureImage.generateLabelFeatureImage(clij2, input_image, label_map, feature_definitions);
            clij2.pullToResultsTable(featureImage, table);
            featureImage.close();
        }
        input_image.close();

        String model_filename = ((TextField) dialog.getStringFields().get(1)).getText();

        for (Integer klass : ground_truth.keySet()) {
            table.setValue("CLASS", klass, ground_truth.get(klass));
        }


        table = filterTable(table, "CLASS");

        table.show("TRAINING");

        TrainWekaFromTable.trainWekaFromTable(clij2, table, "CLASS", model_filename, num_trees, num_features, max_depth);

        cleanup(my_source, pushed);


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