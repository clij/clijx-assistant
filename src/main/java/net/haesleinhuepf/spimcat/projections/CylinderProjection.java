package net.haesleinhuepf.spimcat.projections;

import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import net.haesleinhuepf.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.enums.ImageChannelDataType;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.spimcat.transform.MakeIsotropic;
import net.imglib2.realtransform.AffineTransform3D;

public class CylinderProjection extends AbstractIncubatorPlugin {

    int number_of_angles = 360;
    float delta_angle_in_degrees = 1;

    protected void configure() {
        GenericDialogPlus gdp = new GenericDialogPlus("Cylinder projection");
        gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addNumericField("Number of angles", number_of_angles);
        gdp.addNumericField("Angle step in degrees", delta_angle_in_degrees);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return;
        }

        setSource(gdp.getNextImage());
        number_of_angles = (int) gdp.getNextNumber();
        delta_angle_in_degrees = (float) gdp.getNextNumber();

    }

    protected synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = clijx.pushCurrentZStack(my_source);

        int center_x = (int) (pushed.getWidth() / 2);
        int center_y = (int) (pushed.getDepth() / 2);

        int radius = (int) Math.sqrt(Math.pow(center_x, 2) + Math.pow(center_y, 2));

        ClearCLBuffer resliced_from_top = clijx.create(pushed.getWidth(), pushed.getDepth(), pushed.getHeight());
        clijx.resliceTop(pushed, resliced_from_top);
        pushed.close();

        ClearCLBuffer radial_resliced = clijx.create(radius, pushed.getHeight(), number_of_angles);
        clijx.resliceRadial(resliced_from_top, radial_resliced, delta_angle_in_degrees, center_x, center_y);
        //clijx.show(radial_resliced, "radial");
        resliced_from_top.close();

        ClearCLBuffer result = clijx.create(radial_resliced.getHeight(), radial_resliced.getDepth(), radial_resliced.getWidth());
        clijx.resliceLeft(radial_resliced, result);
        radial_resliced.close();

        setTarget(clijx.pull(result));
        my_target.setTitle("Cylinder projection " + my_source.getTitle());

        result.close();
    }



}
