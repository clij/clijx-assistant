package net.haesleinhuepf.spimcat.io;

import ij.ImageListener;
import ij.ImagePlus;
import ij.ImageStack;
import ij.VirtualStack;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij2.plugins.Clear;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;

import java.awt.*;

public class CLIJxVirtualStack extends VirtualStack {
    private ClearCLBuffer[] buffer;

    boolean catcher_initialized = false;

    public CLIJxVirtualStack(ClearCLBuffer[] buffer) {
        super((int)buffer[0].getWidth(), (int)buffer[0].getHeight(), (int)buffer[0].getDepth() * buffer.length);
        setBitDepth((int) (buffer[0].getPixelSizeInBytes() * 8));

        this.buffer = buffer;

        if (!catcher_initialized) {
            ImagePlus.addImageListener(new ImageListener() {
                @Override
                public void imageOpened(ImagePlus imp) {

                }

                @Override
                public void imageClosed(ImagePlus imp) {
                    if (imp.getStack() instanceof CLIJxVirtualStack) {
                        CLIJx clijx = CLIJx.getInstance();
                        //
                        ImageStack stack = imp.getStack();
                        if (imp.getNChannels() == 1) {
                            ClearCLBuffer buffer = ((CLIJxVirtualStack) stack).getBuffer(0);
                            imp.setStack(CLIJx.getInstance().pull(buffer).getStack());
                            buffer.close();
                        } else {
                            ClearCLBuffer collection = null;
                            for (int c = 0; c < imp.getNChannels(); c++) {
                                ClearCLBuffer buffer = ((CLIJxVirtualStack) stack).getBuffer(c);
                                if (collection == null) {
                                    collection = clijx.create(new long[]{buffer.getWidth(), buffer.getHeight(), buffer.getDepth() * imp.getNChannels()}, buffer.getNativeType());

                                    // TODO: make c1c2c3 c1c2c3 c1c2c3 instead of c1c1c1 c2c2c2c c3c3c3
                                    clijx.paste(buffer, collection, 0, 0, buffer.getDepth() * c);

                                }
                                buffer.close();
                            }
                            imp.setStack(CLIJx.getInstance().pull(collection).getStack());
                            collection.close();
                        }

                    }
                }

                @Override
                public void imageUpdated(ImagePlus imp) {

                }
            });
            catcher_initialized = true;
        }
    }

    ImageProcessor[] formerSliceProcessors = null;
    int former_z = -1;

    @Override
    public synchronized ImageProcessor getProcessor(int n) {
        System.out.println("Requested processor " + n);
        int index = n - 1;
        int zplane = index / buffer.length;
        int channel = index % buffer.length;
        //int depth = (int) (buffer.getDepth() / number_of_channels);
        System.out.println("z/c " + zplane + " / " + channel );

        if (zplane != former_z || formerSliceProcessors == null) {

            formerSliceProcessors = new ImageProcessor[buffer.length];

            CLIJx clijx = CLIJx.getInstance();
            ClearCLBuffer slice = clijx.create(new long[]{buffer[0].getWidth(), buffer[0].getHeight()}, buffer[0].getNativeType());

            for (int c = 0; c < buffer.length; c++) {
                clijx.copySlice(buffer[c], slice, zplane);
                System.out.println("Mean slice[" + c + "] " + clijx.meanOfAllPixels(slice));
                ImagePlus imp = clijx.pull(slice);
                formerSliceProcessors[c] = imp.getProcessor();
            }

            slice.close();

            former_z = zplane;
        }
        for (int c = 0; c < buffer.length; c++) {
            System.out.println("wtf [" + c + "]" + new ImagePlus("",formerSliceProcessors[c] ).getStatistics().mean);
        }
        System.out.println("Processor returned: " + formerSliceProcessors[channel]);
        System.out.println("Mean [" + channel + "]: " + new ImagePlus("",formerSliceProcessors[channel] ).getStatistics().mean);
        return (ImageProcessor) formerSliceProcessors[channel].clone();
    }

    public ClearCLBuffer getBuffer(int channel) {
        return buffer[channel];
    }

    public int getNumberOfChannels() {
        return buffer.length;
    }

    private static int count = 0;
    public static ImagePlus bufferToImagePlus(ClearCLBuffer[] buffer) {
        CLIJxVirtualStack cvs = new CLIJxVirtualStack(buffer);
        int number_of_channels = cvs.getNumberOfChannels();
        count++;
        ImagePlus imp = new ImagePlus(("CLIJxVirtualStack " + count + " " + buffer[0].getName()).trim(), cvs);
        if (number_of_channels > 1) {
            System.out.println("imp " + imp.getNSlices());
            System.out.println("buf " + buffer[0].getDepth());
            System.out.println("cha " + number_of_channels);
            imp = HyperStackConverter.toHyperStack(imp, number_of_channels, (int) buffer[0].getDepth(), 1);
        }
        return imp;
    }
    public static ClearCLBuffer[] imagePlusToBuffer(ImagePlus imp) {
        CLIJx clijx = CLIJx.getInstance();
        ImageStack stack = imp.getStack();
        if (stack instanceof CLIJxVirtualStack) {
            ClearCLBuffer[] copy = new ClearCLBuffer[((CLIJxVirtualStack) stack).getNumberOfChannels()];
            for (int c = 0; c < copy.length; c++) {
                copy[c] = ((CLIJxVirtualStack) stack).getBuffer(c);
            }
            return copy;
        } else {
            if (imp.getNChannels() > 1) {
                ClearCLBuffer[] all = new ClearCLBuffer[imp.getNChannels()];
                ImageStack imp_stack = imp.getStack();
                int t = imp.getT() - 1;
                for (int c = 0; c < imp.getNChannels(); c++) {
                    all[c] = null;
                    for (int z = 0; z < imp.getNSlices(); z++) {
                        /*
                        z1c1 0 1
                        z1c2 1 2
                        z2c1 2 3
                        z2c1 3 4
                        */
                        ImageProcessor processor = imp_stack.getProcessor(t * imp.getNChannels() * imp.getNSlices() + z * imp.getNChannels() + c + 1);
                        ImagePlus a_slice = new ImagePlus("", processor);
                        ClearCLBuffer b_slice = clijx.push(a_slice);
                        if (all[c] == null) {
                            all[c] = clijx.create(new long[]{b_slice.getWidth(), b_slice.getHeight(), imp.getNSlices()}, b_slice.getNativeType());
                        }
                        clijx.copySlice(b_slice, all[c], z);
                        b_slice.close();
                    }
                }
                return all;
            } else {
                return new ClearCLBuffer[]{clijx.pushCurrentZStack(imp)};
            }
        }
    }
}
