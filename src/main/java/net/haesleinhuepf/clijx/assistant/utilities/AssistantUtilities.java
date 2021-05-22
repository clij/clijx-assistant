package net.haesleinhuepf.clijx.assistant.utilities;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import ij.gui.Toolbar;
import ij.process.LUT;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.util.StringUtils;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.plugins.*;
import net.haesleinhuepf.clij2.plugins.AffineTransform;
import net.haesleinhuepf.clij2.plugins.Blur3DSliceBySlice;
import net.haesleinhuepf.clij2.utilities.HasClassifiedInputOutput;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;
import net.haesleinhuepf.clijx.assistant.annotation.AnnotationTool;
import net.haesleinhuepf.clijx.assistant.interactive.generic.GenericAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.options.AssistantOptions;
import net.haesleinhuepf.clijx.assistant.scriptgenerator.PyclesperantoGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.MenuService;
import net.haesleinhuepf.clijx.assistant.services.SuggestionService;
import net.haesleinhuepf.clijx.gui.*;
import net.haesleinhuepf.clijx.plugins.*;
import net.haesleinhuepf.clijx.weka.ApplyWekaModel;
import net.haesleinhuepf.clijx.weka.WekaLabelClassifier;
import net.haesleinhuepf.clijx.weka.autocontext.ApplyAutoContextWekaModel;
import net.haesleinhuepf.clijx.weka.autocontext.TrainAutoContextWekaModel;
import org.scijava.util.ProcessUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

public class AssistantUtilities {
    public static Comparator<? super String> niceNameComparator = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            o1 = niceName(o1);
            o2 = niceName(o2);
            return o1.compareTo(o2);
        }
    };

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


    @Deprecated // use niceName instead
    public static String niceNameWithoutDimShape(String name) {

        //name = name.replace("3D", "");
        //name = name.replace("Box", "");

        return niceName(name);
    }

    public static void glasbey(ImagePlus imp) {
        String dir = IJ.getDirectory("imagej");
        if (!dir.contains("null") && dir.toLowerCase().contains("fiji")) {
            IJ.run(imp, "glasbey_on_dark", "");
            // ensure that the LUT is really applied: TODO: check if the following is really necessary
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

    public static void hi(ImagePlus imp) {
        String dir = IJ.getDirectory("imagej");
        if (!dir.contains("null") && dir.toLowerCase().contains("fiji")) {

            IJ.run(imp, "hi", "");

            // ensure that the LUT is really applied: TODO: check if the following is really necessary
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            IJ.run(imp, "hi", "");
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

            IJ.run(imp, "Green Fire Blue", "");

            // ensure that the LUT is really applied: TODO: check if the following is really necessary
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

    public static ImagePlus openImage(String filename) {
        if (new File(filename).exists()) {
            return IJ.openImage(filename);
        }
        IJ.log("CLIJ-Assistance couldn't find file\n" + filename + "\n" +
                "Please select its location");
        return IJ.openImage();
    }


    public static boolean ignoreEvent = false;


    public static boolean isReasonable(CLIJMacroPlugin clijPlugin, AssistantGUIPlugin plugin) {
        if (plugin == null || plugin.getTarget() == null) {
            return false;
        }

        CLIJMacroPlugin predecessorPlugin = plugin.getCLIJMacroPlugin();
        if (clijPlugin instanceof HasClassifiedInputOutput && predecessorPlugin instanceof HasClassifiedInputOutput) {
            if (((HasClassifiedInputOutput) clijPlugin).getInputType().contains(((HasClassifiedInputOutput) predecessorPlugin).getOutputType())) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isSuitable(CLIJMacroPlugin clijPlugin, AssistantGUIPlugin plugin) {
        if (plugin == null || plugin.getTarget() == null) {
            return false;
        }

        if (clijPlugin instanceof OffersDocumentation) {
            boolean imageIs3D = plugin.getTarget().getNSlices() > 1;

            String supportedDimensionality = ((OffersDocumentation) clijPlugin).getAvailableForDimensions().replace(" ", "");
            boolean operationTakes3DImages = supportedDimensionality.contains("3D");
            boolean operationTakes2DImages = supportedDimensionality.compareTo("3D->2D") != 0 &&
                                             supportedDimensionality.contains("2D");

            if ((!operationTakes2DImages) || (!operationTakes3DImages)) {
                if (operationTakes3DImages && !imageIs3D ||
                        operationTakes2DImages && imageIs3D) {
                    return false; // image has wrong dimensionality
                }
            }
        }
/*
        if (clijPlugin instanceof IsCategorized) {
            String categories = ((IsCategorized) clijPlugin).getCategories().toLowerCase();

            // check label
            if (resultIsLabelImage(plugin)) {
                if (!categories.contains("label")) {
                    return false;
                }
            }

            // check binary
            if (resultIsBinaryImage(plugin)) {
                if (!categories.contains("binary") || categories.contains("threshold") || categories.contains("segmentation")) {
                    return false;
                }
            }
        }*/
        return true;
    }

    static ArrayList<Class> blocklist = new ArrayList<>();
    static ArrayList<Class> advanced_list = new ArrayList<>();
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
            return false;
        }
        if (parameters.contains(",ByRef Number ")) {
            // contains numberic output parameters
            return false;
        }
        if (parameters.contains(",Array ") || parameters.contains(",ByRef Array ")) {
            // contains array parameters
            return false;
        }

        String[] parameterdefintions = parameters.split(",");
        if (parameterdefintions.length < 2) {
            if (!(clijMacroPlugin instanceof net.haesleinhuepf.clijx.clij2wrappers.PullToROIManager) &&
                    (clijMacroPlugin instanceof net.haesleinhuepf.clij2.plugins.PullToROIManager)
            ) {
                return true;
            }
            return false;
        }

        if (!parameterdefintions[0].startsWith("Image ")) {
            // first parameter is no input image
            //System.out.println("D");
            return false;
        }
        if (!parameterdefintions[1].startsWith("ByRef Image ") && !parameterdefintions[1].startsWith("Image ")) {
            // second parameters is no image
            //System.out.println("E");
            return false;
        }
        /*
        if (parameterdefintions.length > 2) {
            if (parameterdefintions[2].startsWith("Image ") || parameterdefintions[2].startsWith("ByRef Image ")) {
                // second parameters is no output image
                //System.out.println("E");
                return false;
            }
        }*/
        if (clijMacroPlugin.getClass().getName().contains(".clij2wrappers.")) {
            return false;
        }
        if (clijMacroPlugin.getClass().getName().contains(".tilor.")) {
            return false;
        }
        if (clijMacroPlugin.getClass().getName().contains(".macro.")) { // clij1
            return false;
        }
        if (clijMacroPlugin.getClass().getName().contains(".customconvolutionplugin.")) { // clij1
            return false;
        }

        // blacklist
        if (blocklist.size() == 0 || advanced_list.size() == 0) {
            initLists();
        }

        if (blocklist.contains(clijMacroPlugin.getClass())) {
            return false;
        }
        if ((!AbstractAssistantGUIPlugin.show_advanced) && isAdvancedPlugin(clijMacroPlugin)) {
            return false;
        }

        //System.out.println("Z");

        return true;
    }

    public static boolean isAdvancedPlugin(CLIJMacroPlugin clijMacroPlugin) {
        return advanced_list.contains(clijMacroPlugin.getClass());
    }

    private static void initLists() {
        //blocklist.add(net.haesleinhuepf.clij2.plugins.AddImageAndScalar.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MedianSliceBySliceBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.NonzeroMinimumDiamond.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Crop3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TopHatBox
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ExtendLabelingViaVoronoi
        blocklist.add(net.haesleinhuepf.clij2.plugins.Crop2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Rotate2D.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.SubtractBackground3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdShanbhag
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Rotate3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum3DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumZProjectionBounded.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Blur3DSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumZProjection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LabelSpots
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMinimaBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GaussianBlur3D
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Mean3DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GaussianBlur2D.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.LocalID.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnEdges
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MeanXProjection.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMaximaSliceBySliceBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceBottom
        blocklist.add(net.haesleinhuepf.clijx.plugins.SubtractBackground2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMean
        blocklist.add(net.haesleinhuepf.clij2.plugins.NonzeroMinimumBox.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.NonLocalMeans
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdHuang
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Logarithm.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MultiplyImageAndScalar.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.CountTouchingNeighbors.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ImageToStack.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TopHatSphere.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.Maximum3DBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Invert
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BottomHatSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumZProjectionThresholdedBounded.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Scale2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumZProjection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Maximum3DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Scale3D.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LaplacianOfGaussian3D
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MeanYProjection.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MedianSliceBySliceSphere.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.LFRecon.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.RotateRight
        advanced_list.add(net.haesleinhuepf.clij2.plugins.Histogram.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMinError
        blocklist.add(net.haesleinhuepf.clij2.plugins.LaplaceDiamond.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceTop
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumZProjectionBounded.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumBox.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.Skeletonize.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceRight
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Maximum2DBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumOctagon.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ClosingBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.LabelledSpotsToPointList.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Sobel
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AdjacencyMatrixToTouchMatrix.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SumXProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Flip3D
        blocklist.add(net.haesleinhuepf.clijx.plugins.BlurSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Maximum3DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Mean3DBox
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MinimumOctagon.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.StackToTiles.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryEdgeDetection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DetectLabelEdges
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumImageAndScalar
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMaxEntropy
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIsoData
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SumZProjection
        //blocklist.add(net.haesleinhuepf.clijx.plugins.FindMaximaPlateaus
        blocklist.add(net.haesleinhuepf.clij2.plugins.Downsample3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ConvertUInt8.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DilateBoxSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.EntropyBox
        blocklist.add(net.haesleinhuepf.clij2.plugins.Downsample2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ReduceStack.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdTriangle
        blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeBoxSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Flip2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GreaterConstant
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ReplaceIntensity.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIJ_IsoData
        blocklist.add(net.haesleinhuepf.clij2.plugins.Paste3D.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.SpotsToPointList.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.TopHatOctagonSliceBySlice.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Copy.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.NonzeroMaximumDiamond.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MultiplyImageAndCoordinate.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Paste2D.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.PointlistToLabelledSpots.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMinimum
        blocklist.add(net.haesleinhuepf.clijx.plugins.ConvertRGBStackToGraySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DistanceMap.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceRadial.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MeanSliceBySliceSphere.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.GaussianBlur3D.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.SumImageSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Median3DBox
        blocklist.add(net.haesleinhuepf.clijx.plugins.BinaryImageMoments3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LabelToMask
        blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceRadialTop.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Median3DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.NotEqualConstant
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Median2DBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CloseIndexGapsInLabelMap.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.OpeningDiamond.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ShortestDistances.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdYen
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateTouchMatrix.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMoments
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfNFarOffPoints.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SmallerConstant
        blocklist.add(net.haesleinhuepf.clij2.plugins.DilateSphereSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMaxima2DBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.TouchMatrixToAdjacencyMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.WriteValuesToPositions.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GreaterOrEqualConstant
        blocklist.add(net.haesleinhuepf.clijx.plugins.BlurBuffers3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdOtsu
        //blocklist.add(net.haesleinhuepf.clij2.plugins.EqualizeMeanIntensitiesOfSlices
        //blocklist.add(net.haesleinhuepf.clij2.plugins.OpeningBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.NonzeroMaximumBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.FloodFillDiamond.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MedianZProjection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DilateSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ClosingDiamond.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Translate3D
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingBox
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LabelVoronoiOctagon.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ReslicePolar.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Translate2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumImageAndScalar.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.Bilateral
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SumYProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.StandardDeviationZProjection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.VoronoiLabeling.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Maximum2DSphere.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfNClosestPoints.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CountNonZeroPixelsSliceBySliceSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BottomHatBox.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.TopHatBox.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.BlurImages3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Exponential.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdDefault
        advanced_list.add(net.haesleinhuepf.clij2.plugins.NClosestPoints.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum2DSphere.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GradientY.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GradientZ.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GradientX.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMaxima3DBox.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.TopHatSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumYProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.RotateClockwise
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MeanZProjection
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Power
        blocklist.add(net.haesleinhuepf.clij2.plugins.Scale.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Mean2DBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ConvertFloat.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumXProjection.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MinimumSliceBySliceSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SubtractImageFromScalar.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Threshold.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.TopHatOctagon.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Mean2DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.RotateCounterClockwise
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DilateBox.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LaplaceSphere
        //blocklist.add(net.haesleinhuepf.clij2.plugins.VoronoiOctagon.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.UndefinedToZero.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Blur2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum2DBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.DownsampleSliceBySliceHalfMedian.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryFillHoles.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIntermodes.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CopySlice.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumSliceBySliceSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum3DBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.CentroidsOfLabels.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.Mean3DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMinimaSliceBySliceBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumYProjection.class);
        blocklist.add(net.haesleinhuepf.clijx.tilor.implementations.ConnectedComponentsLabelingBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryNot
        //blocklist.add(net.haesleinhuepf.clij2.plugins.EqualConstant
        advanced_list.add(net.haesleinhuepf.clij2.plugins.NeighborsOfNeighbors.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.MergeTouchingLabels
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Median2DSphere.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumDiamond.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMaximaBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MeanZProjectionBounded.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.Presign.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.RotateLeft.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingDiamond.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumXProjection.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.LocalExtremaBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMinima2DBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdPercentile
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TransposeYZ
        blocklist.add(net.haesleinhuepf.clijx.registration.TranslationTimelapseRegistration.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdLi
        blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeSphereSliceBySlice.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Resample.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LaplaceBox
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian2D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CountNonZeroVoxels3DSphere
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TransposeXZ
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TransposeXY
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ConvertUInt16.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdRenyiEntropy
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CountNonZeroPixels2DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceLeft
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Absolute.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Blur3D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabeling.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMinima3DBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateTouchCountMatrix.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SmallerOrEqualConstant.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Watershed.class);

        blocklist.add(net.haesleinhuepf.clij2.plugins.GenerateParametricImageFromResultsTableColumn.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.AffineTransform2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.AffineTransform.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.AffineTransform3D.class);

        blocklist.add(net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfDiamond.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.GenerateIntegerGreyValueCooccurrenceCountMatrixHalfBox.class);

        blocklist.add(net.haesleinhuepf.clijx.plugins.GreyLevelAtttributeFiltering.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.FlagLabelsOnEdges.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.PullToResultsTableColumn.class);

        blocklist.add(CrossCorrelation.class);
        blocklist.add(TrainAutoContextWekaModel.class);
        blocklist.add(ApplyAutoContextWekaModel.class);
        //blocklist.add(ApplyVectorField2D.class);
        //blocklist.add(ApplyVectorField3D.class);
        blocklist.add(ApplyWekaModel.class);

///
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Absolute.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.AbsoluteDifference.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.AddImageAndScalar.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.AddImages.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.AddImagesWeighted.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ArgMaximumZProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.AutomaticThreshold.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.AverageDistanceOfNClosestNeighborsMap.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfTouchingNeighbors.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.AverageNeighborDistanceMap.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.Bilateral.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryAnd.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryEdgeDetection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryFillHoles.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.BinaryFillHolesSliceBySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryIntersection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryNot.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryOr.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinarySubtract.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryUnion.class);
        //blocklist.add(net.haesleinhuepf.clijx.weka.BinaryWekaPixelClassifier.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryXOr.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BottomHatBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.CentroidsOfBackgroundAndLabels.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ClosingBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CombineHorizontally.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CombineVertically.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ConcatenateStacks.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ConnectedComponentsLabelingBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Convolve.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CopySlice.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.CountNonZeroVoxels3DSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Crop3D.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.CustomBinaryOperation.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.CylinderTransform.class);
        blocklist.add(net.haesleinhuepf.clijx.registration.DeformableRegistration2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DepthColorProjection.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.DetectAndLabelMaxima.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.DetectAndLabelMaximaAboveThreshold.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DetectLabelEdges.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DetectMaxima3DBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DifferenceOfGaussian3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DilateBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.DistanceMap.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.DistanceMatrixToMesh.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.DivideByGaussianBackground.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.DivideImages.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.DrawDistanceMeshBetweenTouchingLabels.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.DrawMeshBetweenTouchingLabels.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.DrawTouchCountMeshBetweenTouchingLabels.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.DrawTouchPortionMeshBetweenTouchingLabels.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.DriftCorrectionByCenterOfMassFixation.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.DriftCorrectionByCentroidFixation.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.EntropyBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Equal.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.EqualConstant.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.EqualizeMeanIntensitiesOfSlices.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ExcludeLabels.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnEdges.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ExcludeLabelsOnSurface.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.ExcludeLabelsOutsideSizeRange.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ExcludeLabelsSubSurface.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesOutOfRange.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesWithinRange.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Exponential.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ExtendLabelingViaVoronoi.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.ExtendLabelsWithMaximumRadius.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.Extrema.class);
        blocklist.add(net.haesleinhuepf.clijx.piv.FastParticleImageVelocimetry.class);
        blocklist.add(net.haesleinhuepf.clijx.piv.FastParticleImageVelocimetry3D.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.FindAndLabelMaxima.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.FindMaxima.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.FindMaximaPlateaus.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Flip3D.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.GaussJordan.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GaussianBlur3D.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateBinaryOverlapMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateDistanceMatrix.class);
        blocklist.add(net.haesleinhuepf.clijx.weka.GenerateFeatureStack.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.GenerateGreyValueCooccurrenceMatrixBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateJaccardIndexMatrix.class);
        blocklist.add(net.haesleinhuepf.clijx.weka.GenerateLabelFeatureImage.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GenerateParametricImage.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GetJaccardIndex.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GetMeanOfMaskedPixels.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GetMeanSquaredError.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.GetSorensenDiceCoefficient.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Greater.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GreaterConstant.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GreaterOrEqual.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.GreaterOrEqualConstant.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.IntensityCorrection.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.IntensityCorrectionAboveThresholdOtsu.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Invert.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.JaccardIndex.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionMap.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LabelMaximumExtensionRatioMap.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LabelPixelCountMap.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LabelSpots.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LabelStandardDeviationIntensityMap.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LabelToMask.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LabelVoronoiOctagon.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LabelingWorkflowALX.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.LaplaceBox.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.LaplaceSphere.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.LaplacianOfGaussian3D.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.LocalThreshold.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Logarithm.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.MakeIsotropic.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Mask.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaskLabel.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaskStackWithPlane.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MatrixEqual.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Maximum3DBox.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumImageAndScalar.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumImages.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumOfMaskedPixels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MaximumOfTouchingNeighbors.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumXProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumYProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumZProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumZProjectionBounded.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Mean3DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MeanClosestSpotDistance.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MeanOfMaskedPixels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MeanOfTouchingNeighbors.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MeanSquaredError.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MeanXProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MeanYProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MeanZProjection.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.MeanZProjectionAboveThreshold.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Median3DBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MedianOfTouchingNeighbors.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MedianZProjection.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.MergeTouchingLabels.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum3DBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MinimumDistanceOfTouchingNeighbors.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumImageAndScalar.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumImages.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumOfMaskedPixels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MinimumOfTouchingNeighbors.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumXProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumYProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumZProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MultiplyImageAndScalar.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MultiplyImages.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MultiplyMatrix.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.MultiplyStackWithPlane.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.NClosestDistances.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.NonLocalMeans.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.NotEqual.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.NotEqualConstant.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.OpeningBox.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.ParametricWatershed.class);
        blocklist.add(net.haesleinhuepf.clijx.piv.ParticleImageVelocimetry.class);
        blocklist.add(net.haesleinhuepf.clijx.piv.ParticleImageVelocimetryTimelapse.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.PointIndexListToMesh.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Power.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.PowerImages.class);
        blocklist.add(net.haesleinhuepf.clijx.clij2wrappers.PullToROIManager.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ReplaceIntensities.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ReplacePixelsIfZero.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceBottom.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceLeft.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceRight.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ResliceTop.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.RigidTransform.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Rotate3D.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.RotateClockwise.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.RotateCounterClockwise.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.RotateRight.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.SeededWatershed.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.SetNonZeroPixelsToPixelIndex.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.ShowGlasbeyOnGrey.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.ShowRGB.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Smaller.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SmallerConstant.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SmallerOrEqual.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Sobel.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.SorensenDiceCoefficient.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.SphereTransform.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SquaredDifference.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.StandardDeviationOfMaskedPixels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.StandardDeviationOfTouchingNeighbors.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.StandardDeviationZProjection.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.StatisticsOfBackgroundAndLabelledPixels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.StatisticsOfLabelledPixels.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.SubtractGaussianBackground.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SubtractImageFromScalar.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SubtractImages.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SumXProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SumYProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.SumZProjection.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusion.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf10.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf11.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf12.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf2.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf3.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf4.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf5.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf6.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf7.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf8.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf9.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdDefault.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.ThresholdDoG.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdHuang.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIJ_IsoData.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIntermodes.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIsoData.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdLi.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMaxEntropy.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMean.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMinError.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMinimum.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdMoments.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdOtsu.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdPercentile.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdRenyiEntropy.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdShanbhag.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdTriangle.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdYen.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TopHatBox.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.TouchMatrixToMesh.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.TouchingNeighborCountMap.class);
        blocklist.add(net.haesleinhuepf.clijx.weka.TrainWekaModel.class);
        blocklist.add(net.haesleinhuepf.clijx.weka.TrainWekaModelWithOptions.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.Translate3D.class);
        blocklist.add(net.haesleinhuepf.clijx.registration.TranslationRegistration.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TransposeXY.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TransposeXZ.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.TransposeYZ.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.UndefinedToZero.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.VarianceOfMaskedPixels.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.VoronoiLabeling.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.VoronoiOctagon.class);
        //blocklist.add(net.haesleinhuepf.clijx.weka.WekaLabelClassifier.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.WithinIntensityRange.class);
        blocklist.add(net.haesleinhuepf.clijx.io.WriteVTKLineListToDisc.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.Zoom.class);

        advanced_list.add(net.haesleinhuepf.clijx.plugins.MeanZProjectionBelowThreshold.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.Tenengrad.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.TenengradSliceBySlice.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.SobelSliceBySlice.class);

        advanced_list.add(net.haesleinhuepf.clijx.plugins.GenerateDistanceMatrixAlongAxis.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MaximumDistanceOfTouchingNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.GenerateAngleMatrix.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.FlagLabelsOnEdges.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.PullToResultsTableColumn.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.KMeansLabelClusterer.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ModeOfTouchingNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateProximalNeighborsMatrix.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.ReadIntensitiesFromMap.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.PointIndexListToTouchMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateNNearestNeighborsMatrix.class);

        advanced_list.add(net.haesleinhuepf.clijx.plugins.GenerateDistanceMatrixAlongAxis.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.GenerateAngleMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.SetNonZeroPixelsToPixelIndex.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MinimumZProjectionBounded.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.MeanZProjectionBelowThreshold.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.CountTouchingNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ImageToStack.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MinimumZProjectionThresholdedBounded.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.ReadIntensitiesFromMap.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MaximumZProjectionBounded.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumBox.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.Skeletonize.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MaximumOctagon.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf11.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf12.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf10.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.LabelledSpotsToPointList.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.TouchMatrixToMesh.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AdjacencyMatrixToTouchMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.NClosestDistances.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.StackToTiles.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.FindMaximaPlateaus.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.StatisticsOfBackgroundAndLabelledPixels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateJaccardIndexMatrix.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.DetectAndLabelMaxima.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.SpotsToPointList.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MultiplyImageAndCoordinate.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.DistanceMatrixToMesh.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.PointlistToLabelledSpots.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.DistanceMap.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ModeOfTouchingNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ReplaceIntensities.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.SumImageSliceBySlice.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateBinaryOverlapMatrix.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.SubtractGaussianBackground.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.ShortestDistances.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateTouchMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfNFarOffPoints.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.TouchMatrixToAdjacencyMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.WriteValuesToPositions.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.FloodFillDiamond.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.LabelVoronoiOctagon.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.Sinus.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.Cosinus.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.Bilateral.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.StatisticsOfLabelledPixels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MedianOfTouchingNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfNClosestPoints.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.NClosestPoints.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf9.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf8.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf7.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf6.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf5.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf4.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf3.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusionOf2.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.PointIndexListToMesh.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MultiplyMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MaximumDistanceOfTouchingNeighbors.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.SeededWatershed.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateNNearestNeighborsMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.PointIndexListToTouchMatrix.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.tenengradfusion.TenengradFusion.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.CentroidsOfLabels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MaximumOfTouchingNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.NeighborsOfNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.StandardDeviationOfTouchingNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MinimumDistanceOfTouchingNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.OnlyzeroOverwriteMaximumDiamond.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateProximalNeighborsMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.MeanZProjectionBounded.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.Presign.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.MeanZProjectionAboveThreshold.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateDistanceMatrix.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.FindAndLabelMaxima.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.GenerateTouchCountMatrix.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfNClosestPoints.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfNFarOffPoints.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AverageDistanceOfTouchingNeighbors.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.AverageNeighborDistanceMap.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.Bilateral.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.CentroidsOfBackgroundAndLabels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.CentroidsOfLabels.class);
        advanced_list.add(net.haesleinhuepf.clij2.plugins.CloseIndexGapsInLabelMap.class);

        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMaximumAverageDistanceOfNClosestNeighborsMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMaximumAverageNeighborDistanceMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMaximumTouchingNeighborCountMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMeanAverageDistanceOfNClosestNeighborsMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMeanAverageNeighborDistanceMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMeanTouchPortionMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMeanTouchingNeighborCountMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMedianAverageDistanceOfNClosestNeighborsMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMedianAverageNeighborDistanceMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMedianTouchingNeighborCountMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMinimumAverageDistanceOfNClosestNeighborsMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMinimumAverageNeighborDistanceMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalMinimumTouchingNeighborCountMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageDistanceOfNClosestNeighborsMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalStandardDeviationAverageNeighborDistanceMap.class);
        advanced_list.add(net.haesleinhuepf.clijx.plugins.LocalStandardDeviationTouchingNeighborCountMap.class);

    }

    public static void installTools() {
        if (AssistantUtilities.class.getPackage().toString().contains(".clij2.")) {
            if (AssistantUtilities.CLIJxAssistantInstalled()) {
                return;
            }
        }

        String tool = IJ.getToolName();
        ignoreEvent = true;
        //Toolbar.removeMacroTools();


        Toolbar.addPlugInTool(new AssistantStartingPointTool());
        Toolbar.addPlugInTool(new InteractiveZoom());
        Toolbar.addPlugInTool(new InteractiveWindowPosition());
        Toolbar.addPlugInTool(new AnnotationTool());

        ignoreEvent = false;

        IJ.setTool(tool);

        /*
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        new MemoryDisplay().run("");
                    }
                },
                1000
        );
      
         */
    }

    public static boolean resultIsBinaryImage(AssistantGUIPlugin abstractAssistantGUIPlugin) {
        if (abstractAssistantGUIPlugin.getCLIJMacroPlugin() instanceof HasClassifiedInputOutput) {
            if (((HasClassifiedInputOutput) abstractAssistantGUIPlugin.getCLIJMacroPlugin()).getOutputType().contains("Binary Image")) {
                return true;
            }
        }

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
        if (abstractAssistantGUIPlugin.getCLIJMacroPlugin() instanceof HasClassifiedInputOutput) {
            if (((HasClassifiedInputOutput) abstractAssistantGUIPlugin.getCLIJMacroPlugin()).getOutputType().contains("Label Image")) {
                return true;
            }
        }

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
        if (parameterName.toLowerCase().contains("gamma")) {
            return small_step ? 0.1 : 1;
        }
        if (parameterName.toLowerCase().contains("relative")) {
            return small_step ? 0.05 : 0.2;
        }
        if (parameterName.toLowerCase().contains("micron")) {
            return small_step ? 0.1 : 5;
        }
        if (parameterName.toLowerCase().contains("angles")) {
            return small_step ? 15 : 90;
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
        if (parameterName.toLowerCase().contains("size")) {
            return small_step ? 0.05 : 0.1;
        }
        if (parameterName.toLowerCase().contains("error")) {
            return small_step ? 0.01 : 0.1;
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

    public static String jarFromClass(Class klass) {
        return klass.getResource('/' + klass.getName().replace('.', '/') + ".class").toString().split("!")[0];
    }

    private static String cle_compatible = null;

    public static boolean isCleCompatible(String function_name)
    {
        if (cle_compatible == null) {
            InputStream resourceAsStream = SuggestionService.class.getClassLoader().getResourceAsStream("cle_compatibility.config");
            try {
                cle_compatible = "\n" + StringUtils.streamToString(resourceAsStream, "UTF-8").replace("\r\n", "\n") + "\n";
            } catch (Exception e) {
                return false;
            }
        }
        //System.out.println("Checking " + function_name + " = " + new PyclesperantoGenerator(false).pythonize(function_name));
        return cle_compatible.contains("\n" + new PyclesperantoGenerator(false).pythonize(function_name) + "\n");
    }


    private static String clic_compatible = null;

    public static boolean isClicCompatible(String function_name)
    {
        function_name = function_name.toLowerCase();
        function_name = function_name.replace("clijx_","");
        function_name = function_name.replace("clij2_","");
        if (clic_compatible == null) {
            InputStream resourceAsStream = SuggestionService.class.getClassLoader().getResourceAsStream("clic_compatibility.config");
            try {
                clic_compatible = "\n" + StringUtils.streamToString(resourceAsStream, "UTF-8").replace("\r\n", "\n").toLowerCase() + "\n";
            } catch (Exception e) {
                return false;
            }
        }
        //System.out.println(clic_compatible);
        //System.out.println("Checking " + function_name);
        return clic_compatible.contains("\n" + function_name + "\n");
    }


    private static String has_online_reference = null;

    public static boolean hasOnlineReference(String plugin_name)
    {
        String function_name = pluginNameToFunctionName(plugin_name);
        if (has_online_reference == null) {
            InputStream resourceAsStream = SuggestionService.class.getClassLoader().getResourceAsStream("online_reference.config");
            try {
                has_online_reference = "\n" + StringUtils.streamToString(resourceAsStream, "UTF-8").replace("\r\n", "\n") + "\n";
            } catch (Exception e) {
                return false;
            }
        }
        //System.out.println("Checking " + function_name + " = " + new PyclesperantoGenerator(false).pythonize(function_name));
        return has_online_reference.contains("\n" + function_name + "\n");
    }
    public static void callOnlineReference(String plugin_name) {
        String function_name = pluginNameToFunctionName(plugin_name);
        try {
            Desktop.getDesktop().browse(new URI("https://clij.github.io/clij2-docs/reference_" + function_name));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String pluginNameToFunctionName(String plugin_name) {
        return plugin_name.replace("CLIJ2_", "").replace("CLIJx_", "");
    }

    public static String getCompatibilityString(String function_name) {
        return "ijm" +
                (isJavaCompatible(function_name)?", java":"") +
                (isCleCompatible(function_name)?", py":"") +
                (isClicCompatible(function_name)?", c++":"");
    }

    private static boolean isJavaCompatible(String function_name) {
        function_name = function_name.toLowerCase().trim();
        return !(
                function_name.startsWith("imagej") ||
                function_name.startsWith("morpholibj") ||
                function_name.startsWith("simpleitk") ||
                function_name.startsWith("bonej") ||
                function_name.startsWith("imglib2")
                );
    }

    public static String distributionName(Class klass) {

        String full_class_name = klass.toString().replace("class ", "");
        //System.out.println("PKG " + full_class_name);
        if (full_class_name.startsWith("net.clesperanto")) {
            return "clEsperanto";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clij.")) {
            return "CLIJ";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clij2.")) {
            return "CLIJ2";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clijx.weka.")) {
            return "CLIJxWEKA";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clijx.simpleitk.")) {
            return "SimpleITK";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clijx.morpholibj.")) {
            return "MorpholibJ";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clijx.imglib2.")) {
            return "Imglib2";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clijx.imagej3dsuite.")) {
            return "ImageJ 3D Suite";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clijx.imagej2.")) {
            return "ImageJ2";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clijx.bonej.")) {
            return "BoneJ";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clijx.imagej.")) {
            return "ImageJ";
        }
        if (full_class_name.startsWith("net.haesleinhuepf.clijx.")) {
            return "CLIJx";
        }
        return "unknown";
    }

    public static String niceName(String name) {

        //name = name.replace("3D", "");
        //name = name.replace("Box", "");

        String result = name;

        result = result.replace("SimpleITK", "");
        result = result.replace("ImageJ2", "");
        result = result.replace("imageJ2", "");
        result = result.replace("MorphoLibJ", "");
        result = result.replace("ImageJ3DSuite", "");
        result = result.replace("BoneJ", "");
        result = result.replace("simpleITK", "");
        result = result.replace("morphoLibJ", "");
        result = result.replace("imageJ3DSuite", "");
        result = result.replace("boneJ", "");
        result = result.replace("CLIJxWEKA", "");
        result = result.replace("CLIJx", "");
        result = result.replace("CLIJ2", "");
        result = result.replace("CLIJ", "");
        result = result.replace("ImageJ", "");
        result = result.replace("_", " ");
        result = result.replace("  ", " ");

        result = result.trim();

        name = result;
        result = "";
        for (int i = 0; i < name.length(); i++) {
            String ch = name.substring(i,i+1);
            if (!ch.toLowerCase().equals(ch)) {
                result = result + " ";
            }
            result = result + ch;
        }

        result = result.replace("C L", "CL");
        result = result.replace("2 D", "2D");
        result = result.replace("3 D", "3D");
        result = result.replace("X Y", "XY");
        result = result.replace("X Z", "XZ");
        result = result.replace("Y Z", "YZ");
        //result = result.replace("_ ", " ");
        result = result.replace("I J", "IJ");
        result = result.replace("Do G", "DoG");
        result = result.replace("Lo G", "LoG");
        result = result.replace("Cl Esperanto", "clEsperanto");
        result = result.replace("Morpho Lib J", "MorphoLibJ");
        result = result.replace("Simple I T K", "SimpleITK");
        result = result.replace("D Suite", "DSuite");
        result = result.replace("Bone J", "BoneJ");
        result = result.replace("CL IJ", "CLIJ");
        result = result.replace("R O I ", "ROI");
        result = result.replace("F F T", "FFT");
        result = result.replace("X Or", "XOr");
        result = result.replace("W E K A", "WEKA");

        result = result.substring(0, 1).toUpperCase() + result.substring(1);

        result = result.trim();

        //System.out.println("Name out: " + result);

        return result;
    }

    public static void main(String[] args) {
//        AbstractAssistantGUIPlugin.show_advanced = true;
//        System.out.println(isIncubatablePlugin(new GenerateTouchMatrix()));
        //System.out.println(niceName("CLIJx_SimpleITKWhateverFilter"));
        //System.out.println(isCleCompatible("thresholdOtsu"));
        /*
        System.out.println(isIncubatablePlugin(new SeededWatershed()));

        new ImageJ();
        CLIJx.getInstance("RTX");

        ImagePlus imp = IJ.openImage("C:/structure/data/blobs.tif");
        imp.show();

        AssistantGUIPlugin agp = new GenericAssistantGUIPlugin(new GaussianBlur2D());
        agp.run("");

        System.out.println(isSuitable(new SeededWatershed(), agp));
*/

        for (AssistantGUIPlugin p : MenuService.getInstance().getPluginsInCategory("All", new ThresholdOtsu())) {
            System.out.println(p.getName());
        }

    }

    public static void openJupyterNotebook(String file) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isWindows = System.getProperty("os.name")
                        .toLowerCase().startsWith("windows");

                String teOkiDirectory = IJ.getDirectory("imagej");
                System.out.println(teOkiDirectory);
                File directory = new File(teOkiDirectory);

                String conda_code_attempt1;
                String conda_code_attempt2;

                if (isWindows) {
                    conda_code_attempt1 = //"call " + conda_directory + "\\Scripts\\activate.bat " + conda_directory + "\n" +
                            "call " + AssistantOptions.getInstance().getCondaPath() + "conda activate " + AssistantOptions.getInstance().getCondaEnv() + "\n" +
                                    "cd " + directory + "\n" +
                                    "jupyter nbconvert --execute --to notebook " + file + "\n" +
                                    "jupyter notebook " + file.replace(".ipynb", ".nbconvert.ipynb") + "\n";

                    conda_code_attempt2 =
                            "call " + AssistantOptions.getInstance().getCondaPath() + "conda activate " + AssistantOptions.getInstance().getCondaEnv() + "\n" +
                                    "cd " + directory + "\n" +
                                    "jupyter notebook " + file + "\n";
                } else {
                    conda_code_attempt1 = AssistantOptions.getInstance().getCondaPath() + "conda activate " + AssistantOptions.getInstance().getCondaEnv() + "\n" +
                            "cd " + directory + "\n" +
                            "jupyter nbconvert --execute --to notebook " + file + "\n" +
                            "jupyter notebook " + file.replace(".ipynb", ".nbconvert.ipynb");

                    conda_code_attempt2 = AssistantOptions.getInstance().getCondaPath() + "conda activate " + AssistantOptions.getInstance().getCondaEnv() + "\n" +
                            "cd " + directory + "\n" +
                            "jupyter notebook " + file;

                }

                System.out.println(conda_code_attempt1);

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

                boolean failed = false;
                try {
                    System.out.println("Attempt 1");
                    Files.write(Paths.get(directory + "/temp.bat"), conda_code_attempt1.getBytes());
                    ProcessUtils.exec(directory, out, out, directory + "/temp.bat");
                    //IJ.log(exec);
                    //parent.errorHandler = handler.errorHandler;
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                    failed = true;
                } catch (RuntimeException e) {
                    e.printStackTrace(System.out);
                    failed = true;
                }
                if (!new File(file.replace(".ipynb", ".nbconvert.ipynb")).exists()) {
                    failed = true;
                }

                if (failed) {
                    System.out.println("Attempt 2");
                    try {
                        Files.write(Paths.get(directory + "/temp.bat"), conda_code_attempt2.getBytes());
                        ProcessUtils.exec(directory, out, out, directory + "/temp.bat");
                        //IJ.log(exec);
                        //parent.errorHandler = handler.errorHandler;
                    } catch (IOException e) {
                        e.printStackTrace(System.out);
                        failed = true;
                    } catch (RuntimeException e) {
                        e.printStackTrace(System.out);
                        failed = true;
                    }
                }

                IJ.log("Te Oki: Bye.");
            }
        }).start();

    }

    public static void openIcyProtocol(String protocol_filename) {
        System.out.println("Opening ICY: " + protocol_filename);
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isWindows = System.getProperty("os.name")
                        .toLowerCase().startsWith("windows");

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

                File directory = new File(protocol_filename).getParentFile();

                try {
                    ProcessUtils.exec(directory, out, out, AssistantOptions.getInstance().getIcyExecutable(),
                            "-x", "plugins.adufour.protocols.Protocols", "protocol=" + protocol_filename);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static File getNewSelfDeletingTempDir() {
        String location = System.getProperty("java.io.tmpdir") + "/temp" + System.currentTimeMillis() + "/";
        File dir = new File(location);
        dir.mkdirs();
        dir.deleteOnExit();

        return dir;
    }

    public static void attachCloseListener(ImagePlus my_target) {
        /*ImageWindow frame = my_target.getWindow();
        if (frame == null) {
            return;
        }

        WindowListener[] list = frame.getWindowListeners();
        for (WindowListener listener : list) {
            frame.removeWindowListener(listener);
        }*/
    }

    static Boolean isCLIJxAssistantInstalled = null;
    public static boolean CLIJxAssistantInstalled() {
        if (isCLIJxAssistantInstalled != null) {
            return isCLIJxAssistantInstalled;
        }
        isCLIJxAssistantInstalled = true;
        try {
            String dir = IJ.getDirectory("imagej");
            if (!dir.contains("null") && dir.toLowerCase().contains("fiji")) {
                // we're in a Fiji folder
                File plugins_dir = new File(dir + "/plugins");
                if (!jarExists(plugins_dir, "clijx-assistant_")) {
                    isCLIJxAssistantInstalled = false;
                }
            }
        }catch (Exception e) {
            System.out.println("Error while checking the CLIJ2 installation:");
            System.out.println(e.getMessage());
        }

        return isCLIJxAssistantInstalled;
    }

    private static boolean jarExists(File folder, String name) {
        return folder.list((dir, name1) -> name1.contains(name)).length > 0;
    }

/*
    class CloseListener implements WindowListener {

        @Override
        public void windowClosing(WindowEvent e) {

        }
    }*/
}
