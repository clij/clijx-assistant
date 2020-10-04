package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;

@Plugin(type = AssistantGUIPlugin.class)
public class MakeIsotropic extends AbstractAssistantGUIPlugin {

    float new_voxel_size_in_microns = 1;

    public MakeIsotropic() {
        super(new net.haesleinhuepf.clijx.plugins.MakeIsotropic());
    }

    protected boolean configure() {
        String unit = "unit";
        if (my_sources != null) {
            unit = my_sources[0].getCalibration().getUnit();
        }
        GenericDialog gdp = new GenericDialog("Make isotropic");
        gdp.addNumericField("Future voxel size (in " + unit + ")", 1.0, 1);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return false;
        }

        setSources(new ImagePlus[]{IJ.getImage()});
        new_voxel_size_in_microns = (float) gdp.getNextNumber();
        return true;
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = new GenericDialog(AssistantUtilities.niceNameWithoutDimShape(this.getClass().getSimpleName()));
        return gd;
    }

    ClearCLBuffer[] result = null;
    public synchronized void refresh()
    {
        Calibration calib = my_sources[0].getCalibration();
        float original_voxel_size_x = (float) calib.pixelWidth;
        float original_voxel_size_y = (float) calib.pixelHeight;
        float original_voxel_size_z = (float) calib.pixelDepth;
        System.out.println("voxel size x: " + original_voxel_size_x);
        System.out.println("voxel size y: " + original_voxel_size_y);
        System.out.println("voxel size z: " + original_voxel_size_z);

        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);

        net.haesleinhuepf.clijx.plugins.MakeIsotropic plugin = (net.haesleinhuepf.clijx.plugins.MakeIsotropic) getCLIJMacroPlugin();
        args = new Object[] {
                pushed[0],
                null,
                original_voxel_size_x,
                original_voxel_size_y,
                original_voxel_size_z,
                new_voxel_size_in_microns
        };
        plugin.setArgs(args);

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
