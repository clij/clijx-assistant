package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.util.Arrays;

@Plugin(type = AssistantGUIPlugin.class)
public class ZPositionRangeProjection extends AbstractAssistantGUIPlugin {

    public ZPositionRangeProjection() {
        super(new net.haesleinhuepf.clij2.plugins.ZPositionRangeProjection());
    }


    @Override
    public void refreshView() {}

    GenericDialog my_dialog = null;

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
         my_dialog = super.buildNonModalDialog(parent);
         return my_dialog;
    }

    @Override
    protected void checkResult() {
        if (result == null || my_dialog == null) {
            return;
        }
        //
        TextField start_x_slider = (TextField) my_dialog.getNumericFields().get(0);
        TextField end_x_slider = (TextField) my_dialog.getNumericFields().get(1);

        int start_x;
        int end_x;

        try {
            start_x = (int) (Double.parseDouble(start_x_slider.getText()));
            end_x = (int) (Double.parseDouble(end_x_slider.getText()));
        } catch (Exception e) {
            System.out.println("Error parsing text (ExtractChannel)");
            return;
        }

        long[] new_dimensions = null;
        new_dimensions = new long[]{result[0].getWidth(), result[0].getHeight(), end_x - start_x + 1};
        System.out.println("Size: " + Arrays.toString(new_dimensions));

        invalidateResultsIfDimensionsChanged(new_dimensions);
    }
}
