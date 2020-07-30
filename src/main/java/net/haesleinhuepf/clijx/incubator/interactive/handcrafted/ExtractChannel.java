package net.haesleinhuepf.clijx.incubator.interactive.handcrafted;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.plugins.Copy;
import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.interactive.suggestions.MakeIsotropicSuggestion;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.clijx.incubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;

@Plugin(type = SuggestedPlugin.class)
public class ExtractChannel extends AbstractIncubatorPlugin implements MakeIsotropicSuggestion {

    TextField channel_number = null;

    public ExtractChannel() {
        super(new Copy());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = new GenericDialog(IncubatorUtilities.niceName(this.getClass().getSimpleName()));
        gd.addNumericField("Channel (0-indexed)", 0);
        addPlusMinusPanel(gd, "channel");

        channel_number = (TextField) gd.getNumericFields().get(0);

        return gd;
    }

    ClearCLBuffer[] result = null;
    public synchronized void refresh()
    {
        ClearCLBuffer[] pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);
        int channel = 0;
        if (channel_number != null) {
            try {
                channel = (int)Double.parseDouble(channel_number.getText());
            } catch (Exception e) {
                System.out.println("Error parsing text (ExtractChannel)");
            }
        }
        if (channel < 0) {
            channel = 0;
            channel_number.setText("" + channel);
            return;
        }

        net.haesleinhuepf.clij2.plugins.Copy plugin = (net.haesleinhuepf.clij2.plugins.Copy) getCLIJMacroPlugin();

        if (channel >= pushed.length) {
            channel = pushed.length - 1;
            channel_number.setText("" + channel);
            return;
        }

        args = new Object[] {
                pushed[channel],
                null
        };
        plugin.setArgs(args);

        if (result == null) {
            result = createOutputBufferFromSource(new ClearCLBuffer[]{pushed[0]});
        }
        args[1] = result[0];
        plugin.executeCL();
        cleanup(my_source, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Extracted channel " + my_source.getTitle());
        my_target.setDisplayRange(my_source.getDisplayRangeMin(), my_source.getDisplayRangeMax());
        my_target.updateAndDraw();
        enhanceContrast();
    }

}
