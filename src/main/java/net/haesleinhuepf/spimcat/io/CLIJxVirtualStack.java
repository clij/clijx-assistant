package net.haesleinhuepf.spimcat.io;

import ij.ImageListener;
import ij.ImagePlus;
import ij.ImageStack;
import ij.VirtualStack;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;

public class CLIJxVirtualStack extends VirtualStack {

    private ClearCLBuffer buffer;

    boolean catcher_initialized = false;

    public CLIJxVirtualStack(ClearCLBuffer buffer) {
        super((int)buffer.getWidth(), (int)buffer.getHeight(), (int)buffer.getDepth());
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

    @Override
    public ImageProcessor getProcessor(int n) {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer slice = clijx.create(new long[]{buffer.getWidth(), buffer.getHeight()}, buffer.getNativeType());
        clijx.copySlice(buffer, slice, n);
        ImagePlus imp = clijx.pull(slice);
        slice.close();
        return imp.getProcessor();
    }

    public ClearCLBuffer getBuffer() {
        return buffer;
    }

    private static int count = 0;
    public static ImagePlus bufferToImagePlus(ClearCLBuffer buffer) {
        CLIJxVirtualStack cvs = new CLIJxVirtualStack(buffer);
        count++;
        return new ImagePlus(("CLIJxvs " + count + " " + buffer.getName()).trim(), cvs);
    }
    public static ClearCLBuffer imagePlusToBuffer(ImagePlus imp) {
        CLIJx clijx = CLIJx.getInstance();
        ImageStack stack = imp.getStack();
        if (stack instanceof CLIJxVirtualStack) {
            ClearCLBuffer copy = clijx.create(((CLIJxVirtualStack) stack).getBuffer());
            clijx.copy(((CLIJxVirtualStack) stack).getBuffer(), copy);
            return copy;
        } else {
            return clijx.pushCurrentZStack(imp);
        }
    }
}
