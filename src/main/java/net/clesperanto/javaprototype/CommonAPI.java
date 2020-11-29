package net.clesperanto.javaprototype;

import ij.ImagePlus;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.interfaces.ClearCLImageInterface;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.CLIJx;

public interface CommonAPI {

    public static ClearCLBuffer push(Object object) {
        return CLIJx.getInstance().push(object);
    }
    public static ClearCLBuffer create(ClearCLBuffer object) {
        return CLIJx.getInstance().create(object);
    }
    public static ClearCLBuffer create(long w, long h, long d) {
        return CLIJx.getInstance().create(w, h, d);
    }
    public static ClearCLBuffer create(long[] dimensions, NativeTypeEnum type) {
        return CLIJx.getInstance().create(dimensions, type);
    }
    public static ImagePlus pull(Object object) {
        return CLIJx.getInstance().pull(object);
    }
    public static void clear() {
        CLIJx.getInstance().clear();
    }
    public static String clinfo() {
        return CLIJx.clinfo();
    }
    public static void copy(ClearCLImageInterface image1, ClearCLImageInterface image2) {
        CLIJx.getInstance().copy(image1, image2);
    }
}
