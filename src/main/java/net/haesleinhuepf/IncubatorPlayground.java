package net.haesleinhuepf;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.plugin.HyperStackConverter;
import net.haesleinhuepf.spimcat.io.VirtualTifStack;
import net.haesleinhuepf.spimcat.projections.CylinderProjection;
import net.haesleinhuepf.spimcat.projections.MaximumZProjection;
import net.haesleinhuepf.spimcat.transform.MakeIsotropic;

import java.io.FileNotFoundException;

public class IncubatorPlayground {

    public static void main(String... args) throws FileNotFoundException {
        new ImageJ();

        //ImagePlus imp1 = IJ.openImage("C:\\structure\\teaching\\lecture_applied_bioimage_analysis_2020\\12_Volumetric_image_data\\data\\000200.raw.tif");

        VirtualTifStack vts = VirtualTifStack.open("C:\\structure\\data\\2018-05-23-16-18-13-89-Florence_multisample\\processed\\tif\\");
        ImagePlus imp1 = new ImagePlus("Florence", vts);
        HyperStackConverter.toHyperStack(imp1, 1, vts.getDepth(), vts.getSize() / vts.getDepth());
        imp1.show();

        //if (true) return;

        new MakeIsotropic().run("");

        new CylinderProjection().run("");

        new MaximumZProjection().run("");
    }
}
