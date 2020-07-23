package net.haesleinhuepf.clincubator;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPluginService;
import net.haesleinhuepf.clij2.plugins.Mean3DBox;
import net.haesleinhuepf.clij2.plugins.Watershed;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import org.scijava.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class GeneratePlugins {
    public static void main(String[] args) throws IOException {
        CombinedUsageStats combinedUsageStats = new CombinedUsageStats(
                "../clij2-docs/src/main/macro/",
                "../clijx/src/main/macro/",
                "../scripts_hidden/",
                "../scripts/");

        CLIJMacroPluginService service = new Context(CLIJMacroPluginService.class).getService(CLIJMacroPluginService.class);
        for (String pluginName : service.getCLIJMethodNames()) {
            //System.out.println("Check " + pluginName);
            CLIJMacroPlugin plugin = service.getCLIJMacroPlugin(pluginName);
            if (isIncubatablePlugin(plugin)) {
                System.out.println(plugin.getClass().getName());

                String methodName = pluginName;//.replace("CLIJ2", "CLIJx");
                String className = pluginName.replace("CLIJ2_", "");
                className = className.replace("CLIJx_", "");
                className = className.substring(0,1).toUpperCase() + className.substring(1);

                String template = new String(Files.readAllBytes(Paths.get("src/main/java/net/haesleinhuepf/clincubator/interactive/generated/Template.java")));

                String betterClassName = className.replace("3D", "").replace("Box","");

                template = template.replace("//@Plugin(type = SuggestedPlugin.class)", "@Plugin(type = SuggestedPlugin.class)\n// this is generated code. See src/test/java/net/haesleinhuepf/clincubator/PluginGenerator.java for details.");
                template = template.replace("net.haesleinhuepf.clij2.plugins.Mean3DBox", plugin.getClass().getName());
                // template = template.replace("Mean3DBox", plugin.getClass().getName());
                template = template.replace("Template", betterClassName);

                if (!new File("src/main/java/net/haesleinhuepf/clincubator/interactive/handcrafted/" + betterClassName + ".java").exists()) {
                    File outputTarget = new File("src/main/java/net/haesleinhuepf/clincubator/interactive/generated/" + betterClassName + ".java");
                    try {
                        FileWriter writer = new FileWriter(outputTarget);
                        writer.write(template);
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                template = new String(Files.readAllBytes(Paths.get("src/main/java/net/haesleinhuepf/clincubator/interactive/suggestions/TemplateSuggestion.java")));
                template = template.replace("Template", betterClassName);

                {
                    HashMap<String, Integer> following = combinedUsageStats.getFollowersOf(methodName.replace("CLIJ2_", "").replace("CLIJ_", "").replace("CLIJx_", ""));
                    String suggestedNextSteps = "";
                    for (String method : following.keySet()) {
                        String nextName = classFromMethodName(service, method);
                        if (nextName.length() > 0 && !suggestedNextSteps.contains(nextName)) {
                            if (suggestedNextSteps.length() > 0 && !suggestedNextSteps.endsWith(",\n")) {
                                suggestedNextSteps = suggestedNextSteps + ",\n";
                            }
                            suggestedNextSteps = suggestedNextSteps + nextName + ".class";
                        }
                    }

                    template = template.replace("/*SUGGESTED_NEXT_STEPS*/", suggestedNextSteps);
                }

                {
                    HashMap<String, Integer> previous = combinedUsageStats.getFollowing(methodName.replace("CLIJ2_", "").replace("CLIJ_", "").replace("CLIJx_", ""));
                    String suggestedNextSteps = "";
                    for (String method : previous.keySet()) {
                        String nextName = classFromMethodName(service, method);
                        if (nextName.length() > 0 && !suggestedNextSteps.contains(nextName)) {

                            if (suggestedNextSteps.length() > 0 && !suggestedNextSteps.endsWith(",\n")) {
                                suggestedNextSteps = suggestedNextSteps + ",\n";
                            }
                            suggestedNextSteps = suggestedNextSteps + nextName + ".class";
                        }
                    }

                    template = template.replace("/*SUGGESTED_PREVIOUS_STEPS*/", suggestedNextSteps);
                }

                {
                    File outputTarget = new File("src/main/java/net/haesleinhuepf/clincubator/interactive/suggestions/" + betterClassName + "Suggestion.java");
                    try {
                        FileWriter writer = new FileWriter(outputTarget);
                        writer.write(template);
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        }
    }

    private static String classFromMethodName(CLIJMacroPluginService service, String method) {
        CLIJMacroPlugin plugin = service.getCLIJMacroPlugin(method);
        if (plugin == null) {
            plugin = service.getCLIJMacroPlugin("CLIJ2_" + method + "3DBox");
        }
        if (plugin == null) {
            plugin = service.getCLIJMacroPlugin("CLIJ2_" + method + "3D");
        }
        if (plugin == null) {
            plugin = service.getCLIJMacroPlugin("CLIJ2_" + method);
        }
        if (plugin == null) {
            plugin = service.getCLIJMacroPlugin("CLIJx_" + method);
        }
        if (plugin == null) {
            System.out.println("ERROR: Can't find plugin for " + method);
            return "";
        }

        String name = plugin.getClass().getName();
        name = name.replace("clij2.plugins", "clincubator.interactive.generated");
        name = name.replace("clijx.plugins", "clincubator.interactive.generated");
        name = name.replace("3D", "").replace("Box","");

        if (new File("src/main/java/" + name.replace(".", "/") + ".java").exists()) {
            return name;
        }

        name = plugin.getClass().getName();
        name = name.replace("clij2.plugins", "clincubator.interactive.handcrafted");
        name = name.replace("clijx.plugins", "clincubator.interactive.handcrafted");
        name = name.replace("3D", "").replace("Box","");

        if (new File("src/main/java/" + name.replace(".", "/") + ".java").exists()) {
            return name;
        }


        return "";
    }

    private static boolean isIncubatablePlugin(CLIJMacroPlugin clijMacroPlugin) {
        if (clijMacroPlugin == null) {
            return false;
        }
        String parameters = clijMacroPlugin.getParameterHelpText();

        //if (!clijMacroPlugin.getName().contains("makeIso")) {
        //    return false;
        //}

        while (parameters.contains(", ")) {
            parameters = parameters.replace(", ", ",");
        }
        if (parameters.contains(",String ") || parameters.contains(",ByRef String ")) {
            // contains String parameters
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
            //System.out.println("A");
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

        ArrayList<Class> blocklist = new ArrayList<>();
        blocklist.add(net.haesleinhuepf.clij2.plugins.AddImageAndScalar.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MedianSliceBySliceBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.NonzeroMinimumDiamond.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Crop3D.class);
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
        blocklist.add(net.haesleinhuepf.clij2.plugins.MaximumZProjectionBounded.class);
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
        blocklist.add(net.haesleinhuepf.clij2.plugins.LabelVoronoiOctagon.class);
        blocklist.add(net.haesleinhuepf.clijx.plugins.ReslicePolar.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Translate2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.MinimumImageAndScalar.class);
        //blocklist.add(net.haesleinhuepf.clijx.plugins.Bilateral
        blocklist.add(net.haesleinhuepf.clij2.plugins.SumYProjection.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.StandardDeviationZProjection
        blocklist.add(net.haesleinhuepf.clij2.plugins.VoronoiLabeling.class);
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
        blocklist.add(net.haesleinhuepf.clij2.plugins.VoronoiOctagon.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.UndefinedToZero.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Blur2D.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.Minimum2DBox.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.DownsampleSliceBySliceHalfMedian.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ErodeSphere.class);
        //blocklist.add(net.haesleinhuepf.clij2.plugins.BinaryFillHoles.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.ThresholdIntermodes.class);
        blocklist.add(net.haesleinhuepf.clij2.plugins.CopySlice.class);
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

        if (blocklist.contains(clijMacroPlugin.getClass())) {
            return false;
        }

        //System.out.println("Z");

        return true;
    }
}
