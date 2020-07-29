package net.haesleinhuepf.spimcat.io;

import ij.ImageListener;
import ij.ImagePlus;
import ij.ImageStack;
import ij.VirtualStack;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;

public class CLIJxVirtualStack extends VirtualStack {
    int number_of_channels = 1;

    private ClearCLBuffer buffer;

    boolean catcher_initialized = false;

    public CLIJxVirtualStack(ClearCLBuffer buffer, int number_of_channels) {
        super((int)buffer.getWidth(), (int)buffer.getHeight(), (int)buffer.getDepth());
        setBitDepth((int) (buffer.getPixelSizeInBytes() * 8));

        this.number_of_channels = number_of_channels;

        this.buffer = buffer;

        if (!catcher_initialized) {
            ImagePlus.addImageListener(new ImageListener() {
                @Override
                public void imageOpened(ImagePlus imp) {

                }

                @Override
                public void imageClosed(ImagePlus imp) {
                    if (imp.getStack() instanceof CLIJxVirtualStack) {
                        //CLIJx clijx = CLIJx.getInstance();
                        //
                        ImageStack stack = imp.getStack();
                        ClearCLBuffer buffer = ((CLIJxVirtualStack) stack).getBuffer();
                        imp.setStack(CLIJx.getInstance().pull(buffer).getStack());
                        buffer.close();
                    }
                }

                @Override
                public void imageUpdated(ImagePlus imp) {

                }
            });
            catcher_initialized = true;
        }
    }

    ImageProcessor formerProcessor = null;
    int former_n = -1;

    @Override
    public synchronized ImageProcessor getProcessor(int n) {
        if (n == former_n && formerProcessor != null) {
            return formerProcessor;
        }

        System.out.println("Requested processor " + n);
        int index = n - 1;
        int zplane = index / number_of_channels;
        int channel = index % number_of_channels;
        int depth = (int) (buffer.getDepth() / number_of_channels);
        System.out.println("z/c " + zplane + " / " + channel );

        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer slice = clijx.create(new long[]{buffer.getWidth(), buffer.getHeight()}, buffer.getNativeType());
        clijx.copySlice(buffer, slice, channel * depth + zplane);
        ImagePlus imp = clijx.pull(slice);
        slice.close();

        former_n = n;
        formerProcessor = imp.getProcessor();
        return formerProcessor;
    }

    public ClearCLBuffer getBuffer() {
        return buffer;
    }

    private static int count = 0;
    public static ImagePlus bufferToImagePlus(ClearCLBuffer buffer, int number_of_channels) {
        CLIJxVirtualStack cvs = new CLIJxVirtualStack(buffer, number_of_channels);
        count++;
        ImagePlus imp = new ImagePlus(("CLIJxVirutalStack " + count + " " + buffer.getName()).trim(), cvs);
        if (number_of_channels > 1) {
            System.out.println("imp " + imp.getNSlices());
            System.out.println("buf " + buffer.getDepth());
            System.out.println("cha " + number_of_channels);
            imp = HyperStackConverter.toHyperStack(imp, number_of_channels, (int) (buffer.getDepth() / number_of_channels), 1);
        }
        return imp;
    }
    public static ClearCLBuffer imagePlusToBuffer(ImagePlus imp) {
        CLIJx clijx = CLIJx.getInstance();
        ImageStack stack = imp.getStack();
        if (stack instanceof CLIJxVirtualStack) {
            ClearCLBuffer copy = clijx.create(((CLIJxVirtualStack) stack).getBuffer());
            clijx.copy(((CLIJxVirtualStack) stack).getBuffer(), copy);
            return copy;
        } else {
            if (imp.getNChannels() > 1) {
                int former_c = imp.getC();
                imp.setC(1);

                // push first to get type and size
                ClearCLBuffer first = clijx.pushCurrentZStack(imp);
                ClearCLBuffer all = IncubatorUtilities.increaseStackSizeWithChannels(clijx, first, imp.getNChannels());
                clijx.paste(first, all, 0, 0, 0);
                first.close();
                for (int c = 1; c < imp.getNChannels(); c++) {
                    imp.setC(c + 1);
                    ClearCLBuffer any = clijx.pushCurrentZStack(imp);
                    clijx.paste(any, all, 0, 0, c * any.getDepth());
                    any.close();
                }
                imp.setC(former_c);
                return all;
            } else {
                return clijx.pushCurrentZStack(imp);
            }
        }
    }
}
