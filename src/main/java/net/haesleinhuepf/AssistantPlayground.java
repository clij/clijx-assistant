package net.haesleinhuepf;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingBox;
import net.haesleinhuepf.clij2.plugins.GaussianBlur3D;
import net.haesleinhuepf.clij2.plugins.ThresholdOtsu;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.interactive.generic.GenericAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.interactive.handcrafted.WekaLabelClassifier;
import net.haesleinhuepf.clijx.gui.MemoryDisplay;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;
import net.haesleinhuepf.clijx.assistant.interactive.handcrafted.CylinderTransform;
import net.haesleinhuepf.clijx.assistant.interactive.handcrafted.MakeIsotropic;

import java.io.FileNotFoundException;

public class AssistantPlayground implements PlugInFilter {

    public static void main(String... args) throws FileNotFoundException {
        new ImageJ();
        CLIJx.getInstance("RTX");

        new MemoryDisplay().run(null);

        //ImagePlus imp = IJ.openImage("C:/structure/data/spim_TL18_Angle0-1.tif");
        //ImagePlus imp = IJ.openImage("D:/structure/data/Irene/ISB200714_well5_1pos_3h_MyosinGFP-small.tif");
        //ImagePlus imp = IJ.openImage("C:/structure/data/mitosis.tif");
        ImagePlus imp = IJ.openImage("C:/structure/data/blobs.tif");
        imp.show();

        //ImagePlus imp1 = IJ.openImage("C:\\structure\\teaching\\lecture_applied_bioimage_analysis_2020\\12_Volumetric_image_data\\data\\000200.raw.tif");
/*
        VirtualTifStack vts = VirtualTifStack.open("C:\\structure\\data\\2018-05-23-16-18-13-89-Florence_multisample\\processed\\tif\\");
        ImagePlus imp1 = new ImagePlus("Florence", vts);
        HyperStackConverter.toHyperStack(imp1, 1, vts.getDepth(), vts.getSize() / vts.getDepth());
        imp1.show();
        imp1.getCalibration().pixelWidth = 0.52;
        imp1.getCalibration().pixelHeight = 0.52;
        imp1.getCalibration().pixelDepth = 1.98;
        imp1.setZ(imp1.getNSlices() / 2);
        imp1.setDisplayRange(0, 1000);
*/
        //if (true) return;
        new AssistantGUIStartingPoint().run("");
        //new AssistantPlayground().run(null);

        new GenericAssistantGUIPlugin(new GaussianBlur3D()).run("");
        new GenericAssistantGUIPlugin(new ThresholdOtsu()).run("");
        new GenericAssistantGUIPlugin(new ConnectedComponentsLabelingBox()).run("");

        new WekaLabelClassifier().run("");

    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {

        new MakeIsotropic().run("");

        new CylinderTransform().run("");

    }
}
