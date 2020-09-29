package net.haesleinhuepf.clijx.assistant.utilities;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Toolbar;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.plugins.PullToROIManager;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.annotation.AnnotationTool;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.gui.*;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.weka.WekaLabelClassifier;
import org.scijava.util.ProcessUtils;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AssistantUtilities {
    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return sdf.format(cal.getTime());
    }

    public static ClearCLBuffer maximum_x_projection(CLIJx clijx, ClearCLBuffer stack) {
        ClearCLBuffer image2D = clijx.create(new long[]{stack.getDepth(), stack.getHeight()});
        image2D.setName("MAX_x_" + stack.getName());
        clijx.maximumXProjection(stack,image2D);
        return image2D;
    }

    public static ClearCLBuffer maximum_y_projection(CLIJx clijx, ClearCLBuffer stack) {
        ClearCLBuffer image2D = clijx.create(new long[]{stack.getWidth(), stack.getDepth()});
        image2D.setName("MAX_y_" + stack.getName());
        clijx.maximumYProjection(stack,image2D);
        return image2D;
    }

    public static ClearCLBuffer maximum_z_projection(CLIJx clijx, ClearCLBuffer stack) {
        ClearCLBuffer image2D = clijx.create(new long[]{stack.getWidth(), stack.getHeight()});
        image2D.setName("MAX_z_" + stack.getName());
        clijx.maximumZProjection(stack,image2D);
        return image2D;
    }

    public static ClearCLBuffer copy_slice(CLIJx clijx, ClearCLBuffer stack, int slice) {
        ClearCLBuffer image2D = clijx.create(new long[]{stack.getWidth(), stack.getHeight()});
        image2D.setName("Slice_" + slice + "_" + stack.getName());
        clijx.copySlice(stack, image2D, slice);
        return image2D;
    }

    public static String stamp(ClearCLBuffer buffer) {
        String timestamp = "" + System.currentTimeMillis();
        buffer.setName(timestamp);
        return timestamp;
    }
    public static boolean checkStamp(ClearCLBuffer buffer, String stamp) {
        return buffer.getName().compareTo(stamp) == 0 && stamp.length() > 0;
    }

    public static void transferCalibration(ImagePlus source, ImagePlus target) {
        target.getCalibration().pixelWidth = source.getCalibration().pixelWidth;
        target.getCalibration().pixelHeight = source.getCalibration().pixelHeight;
        target.getCalibration().pixelDepth = source.getCalibration().pixelDepth;

        target.getCalibration().setXUnit(source.getCalibration().getXUnit());
        target.getCalibration().setYUnit(source.getCalibration().getYUnit());
        target.getCalibration().setZUnit(source.getCalibration().getZUnit());
    }

    public static String shortName(String title) {
        if (title.length() < 25) {
            return title;
        }
        return title.substring(0, 25) + "...";
    }


    public static String niceName(String name) {

        name = name.replace("3D", "");
        name = name.replace("Box", "");

        String result = "";

        for (int i = 0; i < name.length(); i++) {
            String ch = name.substring(i,i+1);
            if (!ch.toLowerCase().equals(ch)) {
                result = result + " ";
            }
            result = result + ch;
        }

        result = result.substring(0, 1).toUpperCase() + result.substring(1);

        result = result.replace("C L", "CL");
        result = result.replace("X Y", "XY");
        result = result.replace("X Z", "XZ");
        result = result.replace("Y Z", "YZ");
        result = result.replace("_ ", " ");
        result = result.replace("I J", "IJ");
        result = result.replace("Do G", "DoG");
        result = result.replace("Lo G", "LoG");
        result = result.replace("Cl Esperanto", "ClEsperanto");
        result = result.replace("Morpho Lib J", "MorphoLibJ");
        result = result.replace("Simple I T K", "SimpleITK");
        result = result.replace("CL IJ", "CLIJ");
        result = result.replace("R O I ", "ROI");

        return result.trim();

    }

    public static void glasbey(ImagePlus imp) {
        String dir = IJ.getDirectory("imagej");
        if (!dir.contains("null") && dir.toLowerCase().contains("fiji")) {


            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            IJ.run(imp, "glasbey_on_dark", "");
                            imp.resetDisplayRange();
                        }
                    },
                    300
            );

        }
    }

    public static void fire(ImagePlus imp) {
        //System.out.println();
        String dir = IJ.getDirectory("imagej");
        if (!dir.contains("null")) {

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            IJ.run(imp, "Green Fire Blue", "");
                            imp.resetDisplayRange();
                        }
                    },
                    300
            );

        }
    }

    public static boolean ignoreEvent = false;


    public static boolean isIncubatablePlugin(CLIJMacroPlugin clijMacroPlugin) {
        if (clijMacroPlugin == null) {
            return false;
        }
        String parameters = clijMacroPlugin.getParameterHelpText();

        // white list
        if (clijMacroPlugin instanceof WekaLabelClassifier) {
            return true;
        }

        //if (!clijMacroPlugin.getName().contains("makeIso")) {
        //    return false;
        //}

        while (parameters.contains(", ")) {
            parameters = parameters.replace(", ", ",");
        }
        if (parameters.contains(",ByRef String ")) {
            // contains String output parameters
            //System.out.println("B");
            return false;
        }
        if (parameters.contains(",Array ") || parameters.contains(",ByRef Array ")) {
            // contains String parameters
            //System.out.println("C");
            return false;
        }

        String[] parameterdefintions = parameters.split(",");
        if (parameterdefintions.length < 2) {
            if (clijMacroPlugin instanceof PullToROIManager) {
                return true;
            }
            return false;
        }

        if (!parameterdefintions[0].startsWith("Image ")) {
            // first parameter is no input image
            //System.out.println("D");
            return false;
        }
        if (!parameterdefintions[1].startsWith("ByRef Image ")) {
            // second parameters is no output image
            //System.out.println("E");
            return false;
        }
        if (parameterdefintions.length > 2) {
            if (parameterdefintions[2].startsWith("Image ") || parameterdefintions[2].startsWith("ByRef Image ")) {
                // second parameters is no output image
                //System.out.println("E");
                return false;
            }
        }
        if (clijMacroPlugin.getClass().getName().contains(".clij2wrappers.")) {
            return false;
        }
        if (clijMacroPlugin.getClass().getName().contains(".tilor.")) {
            return false;
        }

        // blacklist
        ArrayList<Class> blocklist = new ArrayList<>();
        blocklist.add(net.haesleinhuepf.clij2.plugins.AddImageAndScalar.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MedianSliceBySliceBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.NonzeroMinimumDiamond.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Crop3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TopHatBox
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ExtendLabelingViaVoronoi
        blocklist.add(net.haesleinhuepf.clij2.plugins.Crop2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Rotate2D.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.SubtractBackground3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdShanbhag
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Rotate3D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum3DSphere.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumZProjectionBounded.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Blur3DSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumZProjection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LabelSpots
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMinimaBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GaussianBlur3D
        blocklist.add(net.haesleinhuepf.clij2.plugins.Mean3DSphere.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GaussianBlur2D.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.LocalID.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnEdges
        blocklist.add(net.haesleinhuepf.clij2.plugins.MeanXProjection.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMaximaSliceBySliceBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceBottom
        blocklist.add(net.haesleinhuepf.clijx.plugins.SubtractBackground2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMean
        blocklist.add(net.haesleinhuepf.clij2.plugins.NonzeroMinimumBox.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.NonLocalMeans
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdHuang
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Logarithm.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MultiplyImageAndScalar.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.CountTouchingNeighbors.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ImageToStack.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.TopHatSphere.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.Maximum3DBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Invert
        blocklist.add(net.haesleinhuepf.clij2.plugins.BottomHatSphere.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumZProjectionThresholdedBounded.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Scale2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumZProjection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Maximum3DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Scale3D.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LaplacianOfGaussian3D
        blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MeanYProjection.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MedianSliceBySliceSphere.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.LFRecon.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.RotateRight
        blocklist.add(net.haesleinhuepf.clij2.plugins.Histogram.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMinError
        blocklist.add(net.haesleinhuepf.clij2.plugins.LaplaceDiamond.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceTop
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumZProjectionBounded.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumBox.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.Skeletonize.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceRight
        blocklist.add(net.haesleinhuepf.clij2.plugins.Maximum2DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumOctagon.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ClosingBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.LabelledSpotsToPointList.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Sobel
        blocklist.add(net.haesleinhuepf.clij2.plugins.AdjacencyMatrixToTouchMatrix.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.SumXProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Flip3D
        blocklist.add(net.haesleinhuepf.clijx.plugins.BlurSliceBySlice.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Maximum3DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Mean3DBox
        blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumOctagon.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.StackToTiles.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryEdgeDetection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DetectLabelEdges
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumImageAndScalar
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMaxEntropy
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIsoData
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SumZProjection
        //blocklist.add(net.haesleinhuepf.clijx.plugins.FindMaximaPlateaus
        blocklist.add(net.haesleinhuepf.clij2.plugins.Downsample3D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ConvertUInt8.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DilateBoxSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.EntropyBox
        blocklist.add(net.haesleinhuepf.clij2.plugins.Downsample2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ReduceStack.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdTriangle
        blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeBoxSliceBySlice.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Flip2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GreaterConstant
        blocklist.add(net.haesleinhuepf.clij2.plugins.ReplaceIntensity.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIJ_IsoData
        blocklist.add(net.haesleinhuepf.clij2.plugins.Paste3D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.SpotsToPointList.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.TopHatOctagonSliceBySlice.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Copy.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.NonzeroMaximumDiamond.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MultiplyImageAndCoordinate.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Paste2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.PointlistToLabelledSpots.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMinimum
        blocklist.add(net.haesleinhuepf.clijx.plugins.ConvertRGBStackToGraySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DistanceMap.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceRadial.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MeanSliceBySliceSphere.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.GaussianBlur3D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.SumImageSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Median3DBox
        blocklist.add(net.haesleinhuepf.clijx.plugins.BinaryImageMoments3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LabelToMask
        blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceRadialTop.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Median3DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.NotEqualConstant
        blocklist.add(net.haesleinhuepf.clij2.plugins.Median2DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.CloseIndexGapsInLabelMap.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.OpeningDiamond.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ShortestDistances.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdYen
        blocklist.add(net.haesleinhuepf.clij2.plugins.GenerateTouchMatrix.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMoments
        blocklist.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfNFarOffPoints.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SmallerConstant
        blocklist.add(net.haesleinhuepf.clij2.plugins.DilateSphereSliceBySlice.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMaxima2DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.TouchMatrixToAdjacencyMatrix.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.WriteValuesToPositions.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GreaterOrEqualConstant
        blocklist.add(net.haesleinhuepf.clijx.plugins.BlurBuffers3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdOtsu
        //blocklist.add(net.haesleinhuepf.clij2.plugins.EqualizeMeanIntensitiesOfSlices
        //blocklist.add(net.haesleinhuepf.clij2.plugins.OpeningBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.NonzeroMaximumBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.FloodFillDiamond.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MedianZProjection
        blocklist.add(net.haesleinhuepf.clij2.plugins.DilateSphere.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ClosingDiamond.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Translate3D
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingBox
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LabelVoronoiOctagon.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.ReslicePolar.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Translate2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumImageAndScalar.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.Bilateral
        blocklist.add(net.haesleinhuepf.clij2.plugins.SumYProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.StandardDeviationZProjection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.VoronoiLabeling.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Maximum2DSphere.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfNClosestPoints.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.CountNonZeroPixelsSliceBySliceSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BottomHatBox.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.TopHatBox.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.BlurImages3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Exponential.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdDefault
        blocklist.add(net.haesleinhuepf.clij2.plugins.NClosestPoints.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum2DSphere.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GradientY.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GradientZ.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GradientX.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMaxima3DBox.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.TopHatSphere.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumYProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.RotateClockwise
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MeanZProjection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Power
        blocklist.add(net.haesleinhuepf.clij2.plugins.Scale.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Mean2DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ConvertFloat.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumXProjection.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumSliceBySliceSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SubtractImageFromScalar.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Threshold.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.TopHatOctagon.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Mean2DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.RotateCounterClockwise
        blocklist.add(net.haesleinhuepf.clij2.plugins.DilateBox.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LaplaceSphere
        //blocklist.add(net.haesleinhuepf.clij2.plugins.VoronoiOctagon.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.UndefinedToZero.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Blur2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum2DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DownsampleSliceBySliceHalfMedian.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryFillHoles.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIntermodes.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CopySlice.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumSliceBySliceSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum3DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.CentroidsOfLabels.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.Mean3DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMinimaSliceBySliceBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumYProjection.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.ConnectedComponentsLabelingBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryNot
        //blocklist.add(net.haesleinhuepf.clij2.plugins.EqualConstant
        blocklist.add(net.haesleinhuepf.clij2.plugins.NeighborsOfNeighbors.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.MergeTouchingLabels
        blocklist.add(net.haesleinhuepf.clij2.plugins.Median2DSphere.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumDiamond.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMaximaBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MeanZProjectionBounded.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.Presign.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.RotateLeft.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingDiamond.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumXProjection.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.LocalExtremaBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMinima2DBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdPercentile
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TransposeYZ
        blocklist.add(net.haesleinhuepf.clijx.registration.TranslationTimelapseRegistration.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdLi
        blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeSphereSliceBySlice.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Resample.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LaplaceBox
        blocklist.add(net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CountNonZeroVoxels3DSphere
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TransposeXZ
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TransposeXY
        blocklist.add(net.haesleinhuepf.clij2.plugins.ConvertUInt16.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdRenyiEntropy
        blocklist.add(net.haesleinhuepf.clij2.plugins.CountNonZeroPixels2DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceLeft
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Absolute.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Blur3D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabeling.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMinima3DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GenerateTouchCountMatrix.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.SmallerOrEqualConstant.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Watershed.class);

        blocklist.add(net.haesleinhuepf.clij2.plugins.GenerateParametricImageFromResultsTableColumn.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.AffineTransform2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.AffineTransform.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.AffineTransform3D.class);

        blocklist.add(net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox.class);

        blocklist.add(net.haesleinhuepf.clijx.plugins.GreyLevelAtttributeFiltering.class);


        if (blocklist.contains(clijMacroPlugin.getClass())) {
            return false;
        }

        //System.out.println("Z");

        return true;
    }


    public static void installTools() {
        String tool = IJ.getToolName();
        ignoreEvent = true;
        //Toolbar.removeMacroTools();

        Toolbar.addPlugInTool(new AnnotationTool());
        Toolbar.addPlugInTool(new AssistantStartingPointTool());

        ignoreEvent = false;

        IJ.setTool(tool);

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        new MemoryDisplay().run("");
                    }
                },
                1000
        );
    }

    public static boolean resultIsBinaryImage(AssistantGUIPlugin abstractAssistantGUIPlugin) {
        String name = abstractAssistantGUIPlugin.getName().toLowerCase();
        if (abstractAssistantGUIPlugin.getCLIJMacroPlugin() != null && abstractAssistantGUIPlugin.getCLIJMacroPlugin() instanceof IsCategorized) {
            name = name + "," + ((IsCategorized) abstractAssistantGUIPlugin.getCLIJMacroPlugin()).getCategories().toLowerCase();
        }

        return name.contains("threshold") ||
                name.contains("binary") ||
                name.contains("watershed") ||
                name.contains("greater") ||
                name.contains("smaller") ||
                name.contains("equal")
                ;
    }

    public static boolean resultIsLabelImage(AssistantGUIPlugin abstractAssistantGUIPlugin) {
        String name = abstractAssistantGUIPlugin.getName().toLowerCase();
        if (abstractAssistantGUIPlugin.getCLIJMacroPlugin() != null && abstractAssistantGUIPlugin.getCLIJMacroPlugin() instanceof IsCategorized) {
            name = name + "," + ((IsCategorized) abstractAssistantGUIPlugin.getCLIJMacroPlugin()).getCategories().toLowerCase();
        }

        return name.contains("label");
    }

    public static double parmeterNameToStepSizeSuggestion(String parameterName, boolean small_step) {
        if (parameterName.toLowerCase().contains("sigma")) {
            return small_step ? 0.5 : 2;
        }
        if (parameterName.toLowerCase().contains("relative")) {
            return small_step ? 0.05 : 0.2;
        }
        if (parameterName.toLowerCase().contains("micron")) {
            return small_step ? 0.1 : 5;
        }
        if (parameterName.toLowerCase().contains("degree")) {
            return small_step ? 15 : 90;
        }
        if (parameterName.toLowerCase().contains("long range")) {
            return small_step ? 64 : 256;
        }
        if (parameterName.toLowerCase().contains("constant")) {
            return small_step ? 10 : 100;
        }
        if (parameterName.toLowerCase().contains("zoom")) {
            return small_step ? 0.1 : 1;
        }
        return small_step ? 1 : 10;
    }

    public static void addMenuAction(Menu menu, String label, ActionListener listener) {
        MenuItem submenu = new MenuItem(label);
        if (listener != null) {
            submenu.addActionListener(listener);
        }
        menu.add(submenu);
    }

    public static void execute(String directory, String... command) {
        PrintStream out = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                //IJ.log("" + b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                //IJ.log(new String(b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                byte[] a = new byte[len];
                System.arraycopy(b, off, a, 0, len);
                //IJ.log("" + len);
                if (a.length > 2) {
                    IJ.log(new String(a));
                }
            }
        });

        try {
            ProcessUtils.exec(new File(directory), out, out, command);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

}
