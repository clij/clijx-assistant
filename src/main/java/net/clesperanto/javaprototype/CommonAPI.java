package net.clesperanto.javaprototype;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.interfaces.ClearCLImageInterface;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

abstract class CommonAPI {

    public static ClearCLBuffer push(Object object) {
        return CLIJx.getInstance().push(object);
    }
    public static ClearCLBuffer create(ClearCLBuffer object) {
        return CLIJx.getInstance().create(object);
    }
    public static ClearCLBuffer create_like(ClearCLBuffer object) {
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

    public static ClearCLBuffer imread(String filename) {
        ImagePlus imp = IJ.openImage(filename);
        return CLIJx.getInstance().push(imp);
    }

    public static void imshow(Object object) {
        imshow(object, null, false, null, null);
    }

    public static void imshow(Object object, String title) {
        imshow(object, title, false, null, null);
    }

    public static void imshow(Object object, String title, boolean labels) {
        imshow(object, title, labels, null, null);
    }

    public static void imshow(Object object, String title, boolean labels, Double min_intensity, Double max_intensity) {
        ImagePlus image = CLIJx.getInstance().pull(object);
        if (title != null) {
            image.setTitle(title);
        }

        if (labels) {
            image.resetDisplayRange();
            AssistantUtilities.glasbey(image);
        } else if (min_intensity != null || max_intensity != null) {
            if (min_intensity == null) {
                min_intensity = image.getDisplayRangeMin();
            }
            if (max_intensity == null) {
                max_intensity = image.getDisplayRangeMax();
            }
            image.setDisplayRange(min_intensity, max_intensity);
        }
        image.show();
    }

}
