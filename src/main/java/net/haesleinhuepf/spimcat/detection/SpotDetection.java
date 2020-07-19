package net.haesleinhuepf.spimcat.detection;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.measure.Calibration;
import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.utilities.CLIJUtilities;

import java.awt.*;

@Deprecated
public class SpotDetection {
    static CLIJx clijx ;
    public static void main(String[] args) throws InterruptedException {
        new ImageJ();

        ImagePlus timePointStack = IJ.getImage();
        timePointStack.show();

        double zoomFactor = 1.5;

        GenericDialog gdp = new GenericDialog("SpotDetection");
        gdp.addSlider("Blur", 0, 1000, 3);
        gdp.addSlider("Background Subtraction", 0, 100, 0, 1);
        gdp.addSlider("Threshold", 0, 2000, 400, 1);
        gdp.setModal(false);
        gdp.showDialog();

        clijx = CLIJx.getInstance();
        System.out.println(clijx.getGPUName());

        Scrollbar blurSlider = (Scrollbar)gdp.getSliders().get(0);
        Scrollbar backgroundSubtractionSlider = (Scrollbar)gdp.getSliders().get(1);
        Scrollbar thresholdSlider = (Scrollbar)gdp.getSliders().get(2);

        ClearCLBuffer pushedImage = null;

        int formerTimePoint = -1;
        int formerPlane = -1;
        double formerBlurSigma = -1;
        double formerThreshold = -1;
        double formerBackgroundBlurSigma = -10;

        System.out.println("Start");
        while ((!gdp.wasCanceled()) && (!gdp.wasOKed())) {
            System.out.println("loop");
            double blurSigma = blurSlider.getValue();
            double backgroundBlurSigma = backgroundSubtractionSlider.getValue();
            double threshold = thresholdSlider.getValue();
            int plane = timePointStack.getZ() - 1;
            int timepoint = timePointStack.getFrame() - 1;

            if (formerPlane == plane &&
                formerBackgroundBlurSigma == backgroundBlurSigma &&
                    formerBlurSigma == blurSigma &&
                    formerThreshold == threshold &&
                    formerTimePoint == timepoint
            ) {
                Thread.sleep(100);
                System.out.println("wait");
                continue;
            }
            System.out.println("draw");

            clijx.stopWatch("");
            formerPlane = plane;
            formerBackgroundBlurSigma = backgroundBlurSigma;
            formerBlurSigma = blurSigma;
            formerThreshold = threshold;
            formerTimePoint = timepoint;

            pushedImage = clijx.pushCurrentZStack(timePointStack);

            Calibration calibration = timePointStack.getCalibration();
            double factorX = calibration.pixelWidth * zoomFactor;
            double factorY = calibration.pixelHeight * zoomFactor;
            double factorZ = calibration.pixelDepth * zoomFactor;

            // -----------------------------------------------------------------------
            // resampling
            long w = (long)(pushedImage.getWidth() * factorX);
            long h = (long)(pushedImage.getHeight() * factorY);
            long d = (long)(pushedImage.getDepth() * factorZ);

            System.out.println(new long[]{w, h, d});

            ClearCLImage resampledImage = clijx.create(new long[]{w, h, d}, CLIJUtilities.nativeToChannelType(pushedImage.getNativeType()));
            resampledImage.setName("resampledImage");

            System.out.println("PushedImage: " + pushedImage);

            clijx.resample(pushedImage, resampledImage, factorX, factorY, factorZ, true);


            clijx.stopWatch("resample");

            ClearCLBuffer backgroundSubtractedImage = clijx.create(resampledImage.getDimensions(), pushedImage.getNativeType());
            if (backgroundBlurSigma > 0) {
                clijx.subtractBackground3D(resampledImage, backgroundSubtractedImage, backgroundBlurSigma, backgroundBlurSigma, backgroundBlurSigma);
            } else {
                clijx.copy(resampledImage, backgroundSubtractedImage);
            }

            ClearCLBuffer spots = spot_detection(backgroundSubtractedImage, blurSigma, threshold);

            ClearCLBuffer image_max = IncubatorUtilities.maximum_z_projection(clijx, backgroundSubtractedImage);
            ClearCLBuffer spots_max = IncubatorUtilities.maximum_z_projection(clijx, spots);
            clijx.showRGB(image_max, spots_max, image_max, "max");

            ClearCLBuffer image_slice = IncubatorUtilities.copy_slice(clijx, backgroundSubtractedImage, plane);
            ClearCLBuffer spots_slice = IncubatorUtilities.copy_slice(clijx, spots, plane);
            clijx.showRGB(image_slice, spots_slice, image_slice, "slice");

            clijx.clear();
            clijx.stopWatch("all");
        }
    }


    private static ClearCLBuffer spot_detection (ClearCLBuffer inputImage, double blurSigma, double threshold) {
        // blur a bit and detect maxima
        ClearCLBuffer blurred = clijx.create(inputImage);
        blurred.setName("blurred");
        ClearCLBuffer thresholded = clijx.create(inputImage);
        thresholded.setName("thresholded");
        ClearCLBuffer detected_spots = clijx.create(inputImage.getDimensions(), clijx.UnsignedByte);
        detected_spots.setName("detected_spots");
        ClearCLBuffer masked = clijx.create(inputImage);
        masked.setName("masked");

        // -----------------------------------------------------------------------
        // background / noise removal

        // clijx.differenceOfGaussian(inputImage,blurred,3,3,0,15,15,0);
        // clijx.absoluteInplace(blurred);

        //if (backgroundBlurSigma > 0) {
        //    clijx.differenceOfGaussian(inputImage,blurred, blurSigma, blurSigma, blurSigma, backgroundBlurSigma, backgroundBlurSigma, backgroundBlurSigma);
        //} else {
        clijx.blur(inputImage, blurred, blurSigma, blurSigma, blurSigma);
        //}
        //clijx.show(blurred, "blurred");
        // ----------------------------------------------------------------------
        // spot detection
        clijx.detectMaximaBox(blurred, detected_spots, 1);

        // remove spots in background
        //clijx.automaticThreshold(blurred, thresholded, thresholdAlgorithm);
        clijx.threshold(blurred, thresholded, threshold);
        //clijx.show(blurred, "blurred");
        clijx.mask(detected_spots, thresholded, masked);


        clijx.copy(masked, detected_spots);
        // clijx.show(detected_spots,"t");

        // -----------------------------------------------------------------------
        // clean up
        clijx.release(thresholded);
        clijx.release(masked);
        clijx.release(blurred);
        return detected_spots;
    }


}
