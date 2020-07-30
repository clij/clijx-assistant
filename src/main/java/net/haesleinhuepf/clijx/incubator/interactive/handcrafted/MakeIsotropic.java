package net.haesleinhuepf.clijx.incubator.interactive.handcrafted;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clijx.incubator.interactive.suggestions.MakeIsotropicSuggestion;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;

@Plugin(type = SuggestedPlugin.class)
public class MakeIsotropic extends AbstractIncubatorPlugin implements MakeIsotropicSuggestion {

    float new_voxel_size_in_microns = 1;

    public MakeIsotropic() {
        super(new net.haesleinhuepf.clijx.plugins.MakeIsotropic());
    }

    protected boolean configure() {
        GenericDialog gdp = new GenericDialog("Make isotropic");
        gdp.addNumericField("Future voxel size (in microns)", 1.0, 1);
        gdp.showDialog();

        System.out.println("First dialog done");
        if (gdp.wasCanceled()) {
            System.out.println("First dialog cancelled");
            return false;
        }

        setSource(IJ.getImage());
        new_voxel_size_in_microns = (float) gdp.getNextNumber();
        return true;
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = new GenericDialog(IncubatorUtilities.niceName(this.getClass().getSimpleName()));
        return gd;
    }

    ClearCLBuffer[] result = null;
    public synchronized void refresh()
    {
        Calibration calib = my_source.getCalibration();
        float original_voxel_size_x = (float) calib.pixelWidth;
        float original_voxel_size_y = (float) calib.pixelHeight;
        float original_voxel_size_z = (float) calib.pixelDepth;
        System.out.println("voxel size x: " + original_voxel_size_x);
        System.out.println("voxel size y: " + original_voxel_size_y);
        System.out.println("voxel size z: " + original_voxel_size_z);

        ClearCLBuffer[] pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);

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
            result = createOutputBufferFromSource(pushed);
        }
        args[1] = result[0];
        executeCL(pushed, result);
        cleanup(my_source, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));

        my_target.setTitle("Isotropic " + my_source.getTitle());
        my_target.getCalibration().pixelWidth = new_voxel_size_in_microns;
        my_target.getCalibration().pixelHeight = new_voxel_size_in_microns;
        my_target.getCalibration().pixelDepth = new_voxel_size_in_microns;
        my_target.getCalibration().setUnit("micron");
        //my_target.setDisplayRange(my_source.getDisplayRangeMin(), my_source.getDisplayRangeMax());
        my_target.updateAndDraw();
    }

    @Override
    public void refreshView() {
        if (my_source == null || my_target == null) {
            return;
        }
        my_target.setZ((int) (my_source.getZ() * my_source.getCalibration().pixelDepth / my_target.getCalibration().pixelDepth));
    }



}
