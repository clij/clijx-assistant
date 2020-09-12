package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.gui.GenericDialog;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import org.scijava.plugin.Plugin;

import java.awt.*;

@Plugin(type = AssistantGUIPlugin.class)
public class Zoom extends AbstractAssistantGUIPlugin {

    private GenericDialog dialog = null;

    public Zoom() {
        super(new net.haesleinhuepf.clijx.plugins.Zoom());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        dialog = super.buildNonModalDialog(parent);
        return dialog;
    }

    @Override
    public synchronized void refresh() {
        float zoom_factor = 1;
        if (dialog != null) {
            try {
                zoom_factor = (float)Double.parseDouble(((TextField)(dialog.getNumericFields().get(0))).getText());
            } catch (Exception e) {
                System.out.println("Reading zoom_factor failed");
                return;
            }
        }
        if (zoom_factor == 0) {
            System.out.println("Zoom_factor = 0");
            return;
        }

        if (result != null) {
            long[] old_dimensions = null;
            if (my_source.getNSlices() > 1) {
                old_dimensions = new long[]{my_source.getWidth(), my_source.getHeight(), my_source.getNSlices()};
            } else {
                old_dimensions = new long[]{my_source.getWidth(), my_source.getHeight()};
            }

            long[] new_dimensions = net.haesleinhuepf.clijx.plugins.Zoom.getNewDimensions(old_dimensions, zoom_factor);

            invalidateResultsIfDimensionsChanged(new_dimensions);
        }

        super.refresh();
    }

}
