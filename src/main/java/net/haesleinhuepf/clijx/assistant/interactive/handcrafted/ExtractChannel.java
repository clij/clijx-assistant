package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.plugins.Copy;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;

@Plugin(type = AssistantGUIPlugin.class)
public class ExtractChannel extends AbstractAssistantGUIPlugin {

    TextField channel_number = null;

    public ExtractChannel() {
        super(new Copy());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = new GenericDialog(AssistantUtilities.niceNameWithoutDimShape(this.getClass().getSimpleName()));
        gd.addNumericField("Channel (0-indexed)", 0);
        addPlusMinusPanel(gd, "channel");

        channel_number = (TextField) gd.getNumericFields().get(0);

        return gd;
    }

    public synchronized void refresh()
    {
        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);
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

        if (channel >= pushed[0].length) {
            channel = pushed[0].length - 1;
            channel_number.setText("" + channel);
            return;
        }

        args = new Object[] {
                pushed[0][channel],
                null
        };
        plugin.setArgs(args);
        System.out.println("Check result: " + result);
        checkResult();
        System.out.println("Checked result: " + result);
        if (result == null) {
            result = createOutputBufferFromSource(new ClearCLBuffer[]{pushed[0][0]});
        }
        args[1] = result[0];
        plugin.executeCL();
        cleanup(my_sources, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle("Extracted channel " + my_sources[0].getTitle());
        my_target.setDisplayRange(my_sources[0].getDisplayRangeMin(), my_sources[0].getDisplayRangeMax());
        my_target.updateAndDraw();
        enhanceContrast();
    }

}
