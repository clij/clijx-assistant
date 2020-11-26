package net.haesleinhuepf.spimcat.io;

import ij.*;
import ij.plugin.Duplicator;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import org.jocl.CL;

import java.util.HashMap;

public class CLIJxVirtualStack extends VirtualStack {
    private ClearCLBuffer[] buffer;

    private static boolean catcher_initialized = false;

    @Deprecated // do not use this; it's a workaround because ImageJ closes windows when Composite stacks are replaced
    public static boolean ignore_closing = false;

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
                public synchronized void imageClosed(ImagePlus imp) {
                    System.out.println("img closed " + imp.getTitle());
                    if (ignore_closing) {
                        unregister(imp);
                        return;
                    }
                    //System.out.println("img closed2");
                    //System.out.println("stack " + imp.getStack());
                    //System.out.println("stack " + imp.getStack().getClass());

                    CLIJxVirtualStack cvs;
                    if ((imp.getStack() instanceof CLIJxVirtualStack)) {
                        cvs = (CLIJxVirtualStack) imp.getStack();
                    } else {
                        //System.out.println("Get stack from cache " + imp.getTitle());
                        cvs = getStackFromCache(imp);
                    }
                    //System.out.println("cvs" + cvs);


                    if (cvs != null) {
                        //System.out.println("img closed3");
                        if (imp.getNChannels() == 1) {
                            ClearCLBuffer buffer = (cvs).getBuffer(0);
                            imp.setStack(CLIJx.getInstance().pull(buffer).getStack());
                            System.out.println("Replacing stack of " + imp.getTitle());
                            buffer.close();
                        } else {

                            ImagePlus imp2 = new Duplicator().run(imp, 1, imp.getNChannels(), 1, imp.getNSlices(), 1, imp.getNFrames());
                            imp.setStack(imp2.getStack());
                            System.out.println("Replacing stack of " + imp.getTitle());

                            for (int c = 0; c < imp.getNChannels(); c++) {
                                ClearCLBuffer buffer = (cvs).getBuffer(c);
                                //imp.setStack(CLIJx.getInstance().pull(buffer).getStack());
                                buffer.close();
                            }
                        }
                    }
                    unregister(imp);
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
        int index = n - 1;
        int zplane = index / buffer.length;
        int channel = index % buffer.length;

        if (zplane != former_z || formerSliceProcessors == null) {

            formerSliceProcessors = new ImageProcessor[buffer.length];

            CLIJx clijx = CLIJx.getInstance();
            ClearCLBuffer slice = clijx.create(new long[]{buffer[0].getWidth(), buffer[0].getHeight()}, buffer[0].getNativeType());

            //System.out.println("Buffer " + buffer);
            //System.out.println("Buffer[0] " + buffer[0]);
            //System.out.println("Buffer[0] pointer " + buffer[0].getPeerPointer());
            //System.out.println("Buffer slice " + slice.getPeerPointer());

            for (int c = 0; c < buffer.length; c++) {
                //System.out.println("Channel " + c);
                if (buffer[c].getPeerPointer() != null) { // Workaround: This can happen if visualization happens during reset
                    clijx.copySlice(buffer[c], slice, zplane);
                }
                ImagePlus imp = clijx.pull(slice);
                formerSliceProcessors[c] = imp.getProcessor();
            }

            slice.close();

            former_z = zplane;
        }
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
            //System.out.println("imp " + imp.getNSlices());
            //System.out.println("buf " + buffer[0].getDepth());
            //System.out.println("cha " + number_of_channels);
            imp = HyperStackConverter.toHyperStack(imp, number_of_channels, (int) buffer[0].getDepth(), 1);
        }
        register(imp, cvs);
        return imp;
    }

    public static ClearCLBuffer[][] imagePlusesToBuffers(ImagePlus[] imps) {
        ClearCLBuffer[][] result = new ClearCLBuffer[imps.length][];
        for (int i = 0; i < result.length; i++) {
            result[i] = imagePlusToBuffer(imps[i]);
        }
        return result;
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
                if (imp.getRoi() != null) {
                    imp = (new Duplicator()).run(imp, 1, imp.getNChannels(), 1, imp.getNSlices(), imp.getT(), imp.getT());
                }
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
                //Roi roi = imp.getRoi();
                //imp.killRoi();
                System.out.println("Reading stack of " + imp.getTitle());
                ClearCLBuffer[] buffer = new ClearCLBuffer[]{clijx.pushCurrentZStack(imp)};
                //imp.setRoi(roi);
                return buffer;
            }
        }
    }

    static HashMap<ImagePlus, CLIJxVirtualStack> cache = new HashMap<>();
    public static void register(ImagePlus imp, CLIJxVirtualStack stack) {
        //System.out.println("Registering " + imp);
        unregister(imp);
        cache.put(imp, stack);
    }

    private static void unregister(ImagePlus imp) {
        cache.remove(imp);
    }

    private static CLIJxVirtualStack getStackFromCache(ImagePlus imp) {
        //for (ImagePlus c : cache.keySet()) {
        //    System.out.println("Cached: " + c.getTitle());
        //}
        CLIJxVirtualStack cvs = cache.get(imp);
        //System.out.println("Returning " + cvs);
        return cvs;
    }
}
