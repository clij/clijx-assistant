package net.haesleinhuepf.clincubator.interactive.labeling;

import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLKernel;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.plugins.StatisticsOfLabelledPixels;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.RigidTransform3D;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Plugin(type = SuggestedPlugin.class)
public class ExcludeLabelsOutsideSizeRange extends AbstractIncubatorPlugin {

    int former_min_pixel_count = 10;
    int former_max_pixel_count = 1000;

    Scrollbar min_pixel_count_slider = null;
    Scrollbar max_pixel_count_slider = null;


    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gdp = new GenericDialog("Exclude labels outside size range");
        //gdp.addImageChoice("Image", IJ.getImage().getTitle());
        gdp.addSlider("Minimum_pixel_count", 0, 100, former_min_pixel_count);
        gdp.addSlider("Maximum_pixel_count", 0, 10000, former_max_pixel_count);

        min_pixel_count_slider = (Scrollbar) gdp.getSliders().get(0);
        max_pixel_count_slider = (Scrollbar) gdp.getSliders().get(1);

        return gdp;
    }

    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        validateSource();

        if (result == null) {
            result = clijx.create(pushed);
        }

        if (min_pixel_count_slider != null && max_pixel_count_slider != null) {
            former_min_pixel_count = min_pixel_count_slider.getValue();
            former_max_pixel_count = max_pixel_count_slider.getValue();
        }

        int number_of_labels = (int)clijx.maximumOfAllPixels(pushed);
        ClearCLBuffer size_array = clijx.create(number_of_labels + 1,1, 1);

        ResultsTable table = new ResultsTable();
        clijx.statisticsOfBackgroundAndLabelledPixels(pushed, pushed, table);

        clijx.pushResultsTableColumn(size_array, table, StatisticsOfLabelledPixels.STATISTICS_ENTRY.PIXEL_COUNT.toString());

        ClearCLBuffer below_min = clijx.create(new long[]{number_of_labels + 1,1, 1}, NativeTypeEnum.UnsignedByte);
        clijx.smallerConstant(size_array, below_min, former_min_pixel_count);

        ClearCLBuffer above_max = clijx.create(new long[]{number_of_labels + 1,1, 1}, NativeTypeEnum.UnsignedByte);
        clijx.greaterConstant(size_array, above_max, former_max_pixel_count);

        clijx.binaryOr(above_max, below_min, size_array);
        clijx.excludeLabels(size_array, pushed, result);

        above_max.close();
        below_min.close();
        size_array.close();
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Labels with size within [" + former_min_pixel_count + "..." + former_max_pixel_count + "] " + my_source.getTitle());
        IncubatorUtilities.glasbey(my_target);
    }

    @Override
    public Class[] suggestedNextSteps() {
        return new Class[] {
            ExtendLabelsWithMaximumRadius.class,
            ExtendLabelsUntilTheyTouch.class
        };
    }

    @Override
    public Class[] suggestedPreviousSteps() {
        return new Class[]{
            ConnectedComponentsLabeling.class
        };
    }
}
