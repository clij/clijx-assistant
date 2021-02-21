package net.haesleinhuepf.spimcat.io;

import ij.
        *;
import ij.plugin.Duplicator;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;

public class CLIJxVirtualStack extends VirtualStack {
    public enum ProjectionStyle {
        SINGLE_SLICE,
        MAXIMUM_INTENSITY,
        MAXIMUM_INTENSITY_WITH_SLICE_HIGHLIGHT,
        MINIMUM_INTENSITY,
        MINIMUM_INTENSITY_WITH_SLICE_HIGHLIGHT,
        MEAN_INTENSITY,
        SUM_INTENSITY,
        MEDIAN_INTENSITY,
        STANDARD_DEVIATION_INTENSITY,
        EXTENDED_DEPTH_OF_FOCUS_VARIANCE;

        public static ProjectionStyle[] all() {
            return new ProjectionStyle[]{
                SINGLE_SLICE,
                MAXIMUM_INTENSITY,
                MAXIMUM_INTENSITY_WITH_SLICE_HIGHLIGHT,
                MINIMUM_INTENSITY,
                MINIMUM_INTENSITY_WITH_SLICE_HIGHLIGHT,
                MEAN_INTENSITY,
                SUM_INTENSITY,
                MEDIAN_INTENSITY,
                STANDARD_DEVIATION_INTENSITY,
                EXTENDED_DEPTH_OF_FOCUS_VARIANCE
            };
        }
    }

    private ProjectionStyle projectionStyle = ProjectionStyle.SINGLE_SLICE;

    public ProjectionStyle getProjectionStyle() {
        return projectionStyle;
    }

    public void setProjectionStyle(ProjectionStyle projectionStyle) {
        this.projectionStyle = projectionStyle;
        formerSliceProcessors = null;
    }

    private ClearCLBuffer[] buffer;

    private CLIJxVirtualStack(ClearCLBuffer[] buffer) {
        super((int)buffer[0].getWidth(), (int)buffer[0].getHeight(), (int)buffer[0].getDepth() * buffer.length);
        setBitDepth((int) (buffer[0].getPixelSizeInBytes() * 8));

        this.buffer = buffer;
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
            ClearCLBuffer backup = null;

            //System.out.println("Buffer " + buffer);
            //System.out.println("Buffer[0] " + buffer[0]);
            //System.out.println("Buffer[0] pointer " + buffer[0].getPeerPointer());
            //System.out.println("Buffer slice " + slice.getPeerPointer());

            for (int c = 0; c < buffer.length; c++) {
                System.out.println("Channel " + c);
                if (buffer[c].getPeerPointer() != null) { // Workaround: This can happen if visualization happens during reset
                    if (projectionStyle == ProjectionStyle.MAXIMUM_INTENSITY_WITH_SLICE_HIGHLIGHT || projectionStyle == ProjectionStyle.MINIMUM_INTENSITY_WITH_SLICE_HIGHLIGHT) {
                        backup = clijx.create(new long[]{buffer[0].getWidth(), buffer[0].getHeight()}, buffer[0].getNativeType());
                        clijx.copySlice(buffer[c], backup, zplane);
                        clijx.multiplyImageAndScalar(backup, slice, projectionStyle == ProjectionStyle.MAXIMUM_INTENSITY_WITH_SLICE_HIGHLIGHT?2:0.5);
                        clijx.copySlice(slice, buffer[c], zplane);
                    }
                    switch (projectionStyle) {
                        case MAXIMUM_INTENSITY:
                        case MAXIMUM_INTENSITY_WITH_SLICE_HIGHLIGHT:
                            clijx.maximumZProjection(buffer[c], slice);
                            break;
                        case MINIMUM_INTENSITY:
                        case MINIMUM_INTENSITY_WITH_SLICE_HIGHLIGHT:
                            clijx.minimumZProjection(buffer[c], slice);
                            break;
                        case MEAN_INTENSITY:
                            clijx.meanZProjection(buffer[c], slice);
                            break;
                        case SUM_INTENSITY:
                            clijx.sumZProjection(buffer[c], slice);
                            break;
                        case MEDIAN_INTENSITY:
                            clijx.medianZProjection(buffer[c], slice);
                            break;
                        case STANDARD_DEVIATION_INTENSITY:
                            clijx.standardDeviationZProjection(buffer[c], slice);
                            break;
                        case EXTENDED_DEPTH_OF_FOCUS_VARIANCE:
                            clijx.extendedDepthOfFocusVarianceProjection(buffer[c], slice, 10);
                            break;
                        case SINGLE_SLICE:
                        default:
                            clijx.copySlice(buffer[c], slice, zplane);
                    }
                    if (backup != null) {
                        clijx.copySlice(backup, buffer[c], zplane);
                    }
                }
                ImagePlus imp = clijx.pull(slice);
                formerSliceProcessors[c] = imp.getProcessor();
            }


            slice.close();
            //backup.close();

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
        //CLIJxVirtualStackRegistry.getInstance().register(imp);
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
                            if (imp.getNSlices() > 1) {
                                all[c] = clijx.create(new long[]{b_slice.getWidth(), b_slice.getHeight(), imp.getNSlices()}, b_slice.getNativeType());
                            } else {
                                all[c] = clijx.create(new long[]{b_slice.getWidth(), b_slice.getHeight()}, b_slice.getNativeType());
                            }
                        }
                        if (imp.getNSlices() > 1) {
                            clijx.copySlice(b_slice, all[c], z);
                        } else {
                            clijx.copy(b_slice, all[c]);
                        }
                        b_slice.close();
                    }
                }
                return all;
            } else {
                //Roi roi = imp.getRoi();
                //imp.killRoi();
                ClearCLBuffer[] buffer = new ClearCLBuffer[]{clijx.pushCurrentZStack(imp)};
                //imp.setRoi(roi);
                return buffer;
            }
        }
    }
}
