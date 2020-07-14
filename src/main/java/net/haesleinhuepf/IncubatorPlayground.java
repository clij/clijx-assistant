package net.haesleinhuepf;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.HyperStackConverter;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.spimcat.io.VirtualTifStack;
import net.haesleinhuepf.spimcat.processing.BackgroundSubtraction;
import net.haesleinhuepf.spimcat.processing.Median;
import net.haesleinhuepf.spimcat.projections.CylinderProjection;
import net.haesleinhuepf.spimcat.projections.MaximumZProjection;
import net.haesleinhuepf.spimcat.transform.MakeIsotropic;
import net.haesleinhuepf.spimcat.transform.RigidTransform3D;

import java.io.FileNotFoundException;

public class IncubatorPlayground implements PlugInFilter {

    public static void main(String... args) throws FileNotFoundException {
        new ImageJ();
        CLIJx.getInstance("RTX");

        //ImagePlus imp1 = IJ.openImage("C:\\structure\\teaching\\lecture_applied_bioimage_analysis_2020\\12_Volumetric_image_data\\data\\000200.raw.tif");

        VirtualTifStack vts = VirtualTifStack.open("C:\\structure\\data\\2018-05-23-16-18-13-89-Florence_multisample\\processed\\tif\\");
        ImagePlus imp1 = new ImagePlus("Florence", vts);
        HyperStackConverter.toHyperStack(imp1, 1, vts.getDepth(), vts.getSize() / vts.getDepth());
        imp1.show();
        imp1.getCalibration().pixelWidth = 0.52;
        imp1.getCalibration().pixelHeight = 0.52;
        imp1.getCalibration().pixelDepth = 1.98;
        imp1.setZ(imp1.getNSlices() / 2);
        imp1.setDisplayRange(0, 1000);

        //if (true) return;
        new IncubatorPlayground();
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        new Median().run("");

        new MakeIsotropic().run("");

        new RigidTransform3D().run("");

        new BackgroundSubtraction().run("");

        new CylinderProjection().run("");

        new MaximumZProjection().run("");
    }
}
