package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.util.Arrays;

@Plugin(type = AssistantGUIPlugin.class)
public class MakeIsotropic extends AbstractAssistantGUIPlugin {

    float new_voxel_size_in_microns = 0;

    public MakeIsotropic() {
        super(new net.haesleinhuepf.clij2.plugins.MakeIsotropic());
    }

    GenericDialog dialog = null;

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        String unit = "unit";
        if (my_sources != null) {
            unit = my_sources[0].getCalibration().getUnit();
        }

        init();

        dialog = new GenericDialog("Make isotropic");
        dialog.addNumericField("Future voxel size (in " + unit + ")", new_voxel_size_in_microns, 1);
        addPlusMinusPanel(dialog, "voxel size");

        return dialog;
    }

    private void init() {
        if (my_sources != null && new_voxel_size_in_microns == 0) {
            new_voxel_size_in_microns = (float)
                    (
                            my_sources[0].getCalibration().pixelWidth +
                            my_sources[0].getCalibration().pixelHeight +
                            my_sources[0].getCalibration().pixelDepth
                    ) / 3;

            if (new_voxel_size_in_microns == 0) {
                new_voxel_size_in_microns = 1;
            }
        }

    }


    public void refreshDialogFromArguments() {
        if ( args == null || args.length < 6) {
            return;
        }
        ((TextField) dialog.getNumericFields().get(0)).setText("" + args[5]);
    }

    public synchronized void refresh()
    {
        init();

        Calibration calib = my_sources[0].getCalibration();
        float original_voxel_size_x = (float) calib.pixelWidth;
        float original_voxel_size_y = (float) calib.pixelHeight;
        float original_voxel_size_z = (float) calib.pixelDepth;
        System.out.println("voxel size x: " + original_voxel_size_x);
        System.out.println("voxel size y: " + original_voxel_size_y);
        System.out.println("voxel size z: " + original_voxel_size_z);




        if (dialog != null) {
            try {
                new_voxel_size_in_microns = Float.parseFloat(((TextField) dialog.getNumericFields().get(0)).getText());
            } catch (Exception e) {
                System.out.println("Error parsing text (ExtractChannel)");
            }
        }
        if (new_voxel_size_in_microns == 0) {
            return;
        }

        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);

        net.haesleinhuepf.clij2.plugins.MakeIsotropic plugin = (net.haesleinhuepf.clij2.plugins.MakeIsotropic) getCLIJMacroPlugin();
        args = new Object[] {
                pushed[0],
                null,
                original_voxel_size_x,
                original_voxel_size_y,
                original_voxel_size_z,
                new_voxel_size_in_microns
        };
        plugin.setArgs(args);

        System.out.println("Check result: " + result);

        long[] new_dimensions = null;
        ImagePlus source = my_sources[0];

        float scale1X = (float) (original_voxel_size_x / new_voxel_size_in_microns);
        float scale1Y = (float) (original_voxel_size_y / new_voxel_size_in_microns);
        float scale1Z = (float) (original_voxel_size_z / new_voxel_size_in_microns);

        if (source.getNSlices() > 1) {
            new_dimensions = new long[]{(long)(source.getWidth() * scale1X), (long)(source.getHeight() * scale1Y), (long)(source.getNSlices() * scale1Z)};
        } else {
            new_dimensions = new long[]{(long)(source.getWidth() * scale1X), (long)(source.getHeight() * scale1Y)};
        }
        System.out.println("Size: " + Arrays.toString(new_dimensions));

        invalidateResultsIfDimensionsChanged(new_dimensions);

        System.out.println("Checked result: " + result);
        if (result == null) {
            result = createOutputBufferFromSource(pushed[0]);
        }
        args[1] = result[0];
        executeCL(pushed, new ClearCLBuffer[][]{result});
        cleanup(my_sources, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));

        my_target.setTitle("Isotropic " + my_sources[0].getTitle());
        my_target.getCalibration().pixelWidth = new_voxel_size_in_microns;
        my_target.getCalibration().pixelHeight = new_voxel_size_in_microns;
        my_target.getCalibration().pixelDepth = new_voxel_size_in_microns;
        my_target.getCalibration().setUnit("micron");
        my_target.setDisplayRange(my_sources[0].getDisplayRangeMin(), my_sources[0].getDisplayRangeMax());
        my_target.updateAndDraw();
        enhanceContrast();
    }

    @Override
    public void refreshView() {
        if (my_sources == null || my_target == null) {
            return;
        }
        my_target.setZ((int) (my_sources[0].getZ() * my_sources[0].getCalibration().pixelDepth / my_target.getCalibration().pixelDepth));
    }
}
