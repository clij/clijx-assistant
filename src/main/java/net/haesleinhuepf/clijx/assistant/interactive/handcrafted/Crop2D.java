package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.plugin.Plugin;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

@Plugin(type = AssistantGUIPlugin.class)
public class Crop2D extends AbstractAssistantGUIPlugin {

    private GenericDialog dialog = null;

    int width_in_pixels = 100;
    int height_in_pixels = 100;

    int x_in_pixels = 0;
    int y_in_pixels = 0;

    public Crop2D() {
        super(new net.haesleinhuepf.clij2.plugins.Crop2D());
    }

    @Override
    protected GenericDialog buildNonModalDialog(Frame parent) {
        dialog = super.buildNonModalDialog(parent);
        return dialog;
    }

    public synchronized void refresh()
    {
        if (dialog != null) {
            try {
                x_in_pixels = (int)Double.parseDouble(((TextField)(dialog.getNumericFields().get(0))).getText());
                y_in_pixels = (int)Double.parseDouble(((TextField)(dialog.getNumericFields().get(1))).getText());
                width_in_pixels = (int)Double.parseDouble(((TextField)(dialog.getNumericFields().get(2))).getText());
                height_in_pixels = (int)Double.parseDouble(((TextField)(dialog.getNumericFields().get(3))).getText());
            } catch (Exception e) {
                System.out.println("Reading crop box failed");
                return;
            }
        }

        if (result != null) {
            long[] new_dimensions = new long[]{width_in_pixels, height_in_pixels};

            System.out.println("Size: " + Arrays.toString(new_dimensions));

            invalidateResultsIfDimensionsChanged(new_dimensions);
        }


        net.haesleinhuepf.clij2.plugins.Crop2D plugin = (net.haesleinhuepf.clij2.plugins.Crop2D) getCLIJMacroPlugin();
        ClearCLBuffer[][] pushed;

        if (my_sources[0].getStack() instanceof CLIJxVirtualStack) {

            my_sources[0].killRoi();
            pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);
            my_sources[0].setRoi(x_in_pixels, y_in_pixels, width_in_pixels, height_in_pixels);

            args = new Object[] {
                    pushed[0],
                    null,
                    x_in_pixels,
                    y_in_pixels,
                    width_in_pixels,
                    height_in_pixels
            };
        } else {
            my_sources[0].setRoi(x_in_pixels, y_in_pixels, width_in_pixels, height_in_pixels);
            try {
                pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);
            } catch (Exception e) {
                IJ.log(e.getMessage());
                return;
            }

            args = new Object[]{
                    pushed[0],
                    null,
                    0,
                    0,
                    width_in_pixels,
                    height_in_pixels
            };
        }


        plugin.setArgs(args);

        if (result == null) {
            result = createOutputBufferFromSource(pushed[0]);
        }
        args[1] = result[0];

        executeCL(pushed, new ClearCLBuffer[][]{result});

        cleanup(my_sources, pushed);

        // save correct config for script generators
        args = new Object[] {
                pushed[0],
                null,
                x_in_pixels,
                y_in_pixels,
                width_in_pixels,
                height_in_pixels
        };

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));

        my_target.setTitle("Crop2D of " + my_sources[0].getTitle());
        //ImagePlus.addImageListener(new MyImageListener());
        my_sources[0].getWindow().getCanvas().addMouseListener(new MyListener());

        enhanceContrast();
    }

    class MyListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (my_sources[0] != null && my_sources[0].getWindow() != null && my_sources[0].getWindow().getCanvas() != null && dialog != null) {
                Roi roi = my_sources[0].getRoi();
                if (roi != null) {
                    if(roi.getBounds().x != x_in_pixels || roi.getBounds().y != y_in_pixels || roi.getBounds().width != width_in_pixels || roi.getBounds().getHeight() != height_in_pixels) {
                        ((TextField) dialog.getNumericFields().get(0)).setText("" + roi.getBounds().x);
                        ((TextField) dialog.getNumericFields().get(1)).setText("" + roi.getBounds().y);
                        ((TextField) dialog.getNumericFields().get(2)).setText("" + roi.getBounds().width);
                        ((TextField) dialog.getNumericFields().get(3)).setText("" + roi.getBounds().height);
                        setTargetInvalid();
                    }
                }
            }
        }
    }


    public static void main(String[] args) {
        new ImageJ();
        ImagePlus imp = IJ.openImage("C:/structure/data/t1-head.tif");
        imp.show();

        new Crop2D().run("");
    }

}
