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
import org.scijava.plugin.Plugin;
import org.scijava.util.VersionUtils;

import java.awt.*;
import java.util.HashMap;

import static net.haesleinhuepf.clijx.assistant.interactive.handcrafted.BinaryWekaPixelClassifier.loadFeatures;
import static net.haesleinhuepf.clijx.assistant.interactive.handcrafted.BinaryWekaPixelClassifier.saveFeatures;
import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.addMenuAction;

@Plugin(type = AssistantGUIPlugin.class)
public class WekaRegionalLabelClassifier extends WekaLabelClassifier {


    public WekaRegionalLabelClassifier() {
        super(new net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier());
    }

}