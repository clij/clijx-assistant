package net.haesleinhuepf.spimcat.measurement.neighbors;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.Duplicator;
import ij.plugin.HyperStackConverter;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.ClearCLKernel;
import net.haesleinhuepf.clij.clearcl.enums.ImageChannelDataType;
import net.haesleinhuepf.clij.clearcl.interfaces.ClearCLImageInterface;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.plugins.StatisticsOfLabelledPixels;
import net.haesleinhuepf.clij2.plugins.VoronoiLabeling;
import net.haesleinhuepf.spimcat.measurement.neighbors.implementations.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class NeighborAnalysis implements PlugInFilter {

    static int background_subtraction_radius = 0;
    static double spot_detectection_blur_sigma = 3;
    static double threshold = 0;
    static boolean exclude_labels_on_edges = true;
    static boolean process_just_current_frame = false;
    static boolean sync_results_and_input = true;
    static String input_type = "Raw image";
    static boolean fill_gaps_between_labels = true;

    ArrayList<ImagePlus> synced;

    NeighborProcessor[] processors = new NeighborProcessor[] {
            new AverageDistanceOfTouchingNeighborsProcessor(),      new StandardDeviationDistanceOfTouchingNeighborsProcessor(),
            new AverageDistanceOfNClosestPointsProcessor(1),     new AverageDistanceOfNClosestPointsProcessor(2),
            new SpotDetectionProcessor(),                           new AverageDistanceOfNClosestPointsProcessor(3),
            new LabelMapProcessor(),                                new AverageDistanceOfNClosestPointsProcessor(6),
            new VoronoiProcessor(),                                 new AverageDistanceOfNClosestPointsProcessor(10),
            new TouchMeshProcessor(),                               new SpotDensity(10),
            new TouchDistanceMeshProcessor(),                       new SpotDensity(25),
            new TouchCountMeshProcessor(),                          new SpotDensity(50),
            new TouchPortionMeshProcessor(),                        new SpotDensity(75),

            new MeanTouchPortionProcessor(),                                new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MEAN_INTENSITY.toString()),
            new NumberOfTouchingNeighborsProcessor(),                       new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MINIMUM_INTENSITY.toString()),
            new LocalMeanNumberOfTouchingNeighborsProcessor(),              new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MAXIMUM_INTENSITY.toString()),
            new LocalMedianNumberOfTouchingNeighborsProcessor(),            new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.STANDARD_DEVIATION_INTENSITY.toString()),
            new LocalStandardDeviationNumberOfTouchingNeighborsProcessor(), new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.PIXEL_COUNT.toString()),

            new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MEAN_DISTANCE_TO_CENTROID.toString()),
            new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MEAN_DISTANCE_TO_MASS_CENTER.toString()),
            new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MAX_DISTANCE_TO_CENTROID.toString()),
            new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MAX_DISTANCE_TO_MASS_CENTER.toString()),
            new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MAX_MEAN_DISTANCE_TO_CENTROID_RATIO.toString()),
            new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MAX_MEAN_DISTANCE_TO_MASS_CENTER_RATIO.toString()),

            new MaximumTemporalOverlapProcessor(), new LocalMedianMaximumTemporalOverlapProcessor(),
            new MinimumTemporalDistance(), new LocalMedianMinimumTemporalDistance()
    };

    static boolean[] former_selection = null;

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        ImagePlus input_imp = IJ.getImage();
        int original_z = input_imp.getZ();
        int original_t = input_imp.getT();

        Roi roi = input_imp.getRoi();
        input_imp.killRoi();

        GenericDialog gd = new GenericDialog("Neighbor analyser");
        ArrayList<String> deviceList = CLIJ.getAvailableDeviceNames();
        CLIJ clij = CLIJ.getInstance();

        String[] deviceArray = new String[deviceList.size()];
        deviceList.toArray(deviceArray);
        gd.addChoice("CL_Device", deviceArray, clij.getClearCLContext().getDevice().getName());
        gd.addChoice("Input", new String[]{"Raw image", "Spot image", "Binary image", "Label Map"}, input_type);

        gd.addNumericField("Background_subtraction_radius (top-hat)", background_subtraction_radius, 0);
        gd.addNumericField("Spot_detection_blur_sigma", spot_detectection_blur_sigma, 2);
        gd.addNumericField("Threshold", threshold, 2);
        gd.addCheckbox("Fill gaps between labels", fill_gaps_between_labels);

        gd.addCheckbox("Exclude_labels_on_edges", exclude_labels_on_edges);
        gd.addCheckbox("Process_just_current_frame", process_just_current_frame);
        gd.addCheckbox("Sync_results_and_input", sync_results_and_input);


        int check_box_count = 0;
        for (NeighborProcessor processor : processors) {
            boolean selected = processor.getDefaultActivated();
            if (former_selection != null) {
                selected = former_selection[check_box_count];
            }
            gd.addCheckbox(processor.getName().replace(" ", "_"), selected);
            System.out.println(processor.getName().replace(" ", "_"));


            if (check_box_count % 2 == 0) {
                gd.addToSameRow();
            }
            check_box_count++;
        }

        gd.showDialog();
        if (gd.wasCanceled()) {
            return;
        }

        String cl_device_name = gd.getNextChoice();
        input_type = gd.getNextChoice();
        background_subtraction_radius = (int)gd.getNextNumber();
        spot_detectection_blur_sigma = gd.getNextNumber();
        threshold = gd.getNextNumber();

        HashMap<NeighborProcessor, ImageStack> result_stacks = new HashMap<>();

        fill_gaps_between_labels = gd.getNextBoolean();
        exclude_labels_on_edges = gd.getNextBoolean();
        process_just_current_frame = gd.getNextBoolean();
        sync_results_and_input = gd.getNextBoolean();

        former_selection = new boolean[processors.length];

        int i = 0;
        for (NeighborProcessor processor : processors) {
            if (gd.getNextBoolean()) {
                result_stacks.put(processor, new ImageStack());
                former_selection[i] = true;
            }
            i++;
        }

        CLIJ2 clij2 = CLIJ2.getInstance(cl_device_name);
        ClearCLBuffer former_pointlist = null;
        ClearCLBuffer former_label_map = null;

        for (int t = 0; t < input_imp.getNFrames(); t++) {
            if (!process_just_current_frame) {
                IJ.showProgress(t, input_imp.getNFrames());
                input_imp.setT(t + 1);
            }

            ClearCLBuffer input = clij2.pushCurrentZStack(input_imp);
            ClearCLBuffer label_map = null;
            ClearCLBuffer pointlist = null;

            int number_of_objects = 0;
            ResultsTable table = null;

            if (input_type.compareTo("Raw image") == 0) {
                ClearCLBuffer temp1 = clij2.create(input);
                if (background_subtraction_radius <= 0) {
                    clij2.copy(input, temp1);
                } else {
                    clij2.topHatBox(input, temp1, background_subtraction_radius, background_subtraction_radius, 0);
                }
                ClearCLBuffer temp2 = clij2.create(input);
                clij2.gaussianBlur(temp1, temp2, spot_detectection_blur_sigma, spot_detectection_blur_sigma, 0);

                ClearCLBuffer temp3 = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
                if (temp3.getDimension() == 2) {
                    clij2.detectMaximaBox(temp2, temp3, 1, 1, 0);
                } else {
                    clij2.detectMaximaBox(temp2, temp3, 1, 1, 1);
                }

                if (threshold > 0) {
                    clij2.greaterConstant(temp2, temp1, threshold);
                    clij2.mask(temp3, temp1, temp2);
                } else {
                    clij2.copy(temp3, temp2);
                }

                number_of_objects = (int) clij2.sumOfAllPixels(temp2);
                if (number_of_objects == 0) {
                    IJ.log("Error: No spots found.");
                    //clij2.show(temp1, "temp1");
                    //clij2.show(temp2, "temp2");
                } else if (number_of_objects > 4000) {
                    IJ.log("Warning: More than 4000 spots found. That many points may cause issues in the downstream analysis.\n" +
                            "Consider processing a smaller dataset.");
                }
                System.out.println("Number of objects: " + number_of_objects);
                pointlist = clij2.create(number_of_objects, input.getDimension());
                clij2.spotsToPointList(temp2, pointlist);

                label_map = temp3;
                VoronoiLabeling.voronoiLabeling(clij2, temp2, label_map);

                temp1.close();
                temp2.close();

            } else if (input_type.compareTo("Spot image") == 0) {
                number_of_objects = (int) clij2.sumOfAllPixels(input);
                pointlist = clij2.create(number_of_objects, input.getDimension());
                clij2.spotsToPointList(input, pointlist);

                label_map = clij2.create(input.getDimensions(), clij2.Float);
                VoronoiLabeling.voronoiLabeling(clij2, input, label_map);
            } else if (input_type.compareTo("Binary image") == 0) {
                number_of_objects = (int) clij2.sumOfAllPixels(input);

                ClearCLBuffer temp = clij2.create(input.getDimensions(), clij2.Float);
                clij2.connectedComponentsLabelingBox(input, temp);

                label_map = clij2.create(input.getDimensions(), clij2.Float);
                clij2.labelVoronoiOctagon(temp, label_map);
                temp.close();
            } else {
                label_map = input;
                number_of_objects = (int) clij2.maximumOfAllPixels(label_map);
            }

            if (fill_gaps_between_labels) {
                ClearCLBuffer temp = clij2.create(label_map);
                voronoiLabeling(clij2, label_map, temp);
                clij2.copy(temp, label_map);
                temp.close();

                number_of_objects = (int) clij2.maximumOfAllPixels(label_map);
            }

            if (exclude_labels_on_edges) {
                ClearCLBuffer temp = clij2.create(label_map);
                clij2.excludeLabelsOnEdges(label_map, temp);
                clij2.copy(temp, label_map);
                temp.close();

                number_of_objects = (int) clij2.maximumOfAllPixels(label_map);
            }
            if (pointlist != null) {
                pointlist.close();
            }
            pointlist = clij2.create(number_of_objects, label_map.getDimension());
            clij2.centroidsOfLabels(label_map, pointlist);

            System.out.println("Input " + input);
            System.out.println("Pointlist " + pointlist);
            System.out.println("Label map " + label_map);


            ClearCLBuffer touch_matrix = clij2.create(number_of_objects + 1, number_of_objects + 1);
            clij2.generateTouchMatrix(label_map, touch_matrix);
            //clij2.show(touch_matrix, "TM");

            ClearCLBuffer distance_matrix = clij2.create(number_of_objects + 1, number_of_objects + 1);
            clij2.generateDistanceMatrix(pointlist, pointlist, distance_matrix);
            //clij2.show(distance_matrix, "DM");

            for (NeighborProcessor processor : processors) {
                if (result_stacks.keySet().contains(processor)) {
                    ImageStack stack = result_stacks.get(processor);

                    if (processor instanceof TakesPropertyTable) {
                        if (table == null) {
                            table = new ResultsTable();
                            clij2.statisticsOfBackgroundAndLabelledPixels(input, label_map, table);
                        }
                        ((TakesPropertyTable) processor).setTable(table);
                    }
                    if (processor instanceof TakesFormerPointlist) {
                        ((TakesFormerPointlist) processor).setFormerPointlist(former_pointlist);
                    }
                    if (processor instanceof TakesFormerLabelMap) {
                        ((TakesFormerLabelMap) processor).setFormerLabelMap(former_label_map);
                    }

                    ClearCLBuffer result = processor.process(clij2, input, pointlist, label_map, touch_matrix, distance_matrix);
                    ImagePlus imp = clij2.pull(result);
                    result.close();

                    for (int z = 0; z < imp.getNSlices(); z++) {
                        imp.setZ(z + 1);
                        stack.addSlice(imp.getProcessor());
                    }
                }
            }

            if (former_pointlist != null) {
                former_pointlist.close();
            }
            former_pointlist = pointlist;

            former_label_map = clij2.create(label_map);
            clij2.copy(label_map, former_label_map);
            input.close();
            if (label_map != input) {
                label_map.close();
            }
            //break;

            if (process_just_current_frame) {
                break;
            }
        }
        if (former_pointlist != null) {
            former_pointlist.close();
        }
        if (former_label_map != null) {
            former_label_map.close();
        }

        synced = new ArrayList<>();
        synced.add(input_imp);
        for (NeighborProcessor processor : processors) {
            if (result_stacks.keySet().contains(processor)) {
                ImageStack stack = result_stacks.get(processor);
                ImagePlus imp = new ImagePlus(processor.getName(), stack);
                if (imp.getNSlices() > 1) {
                    //int frames = imp.getNSlices() / input_imp.getNSlices();
                    //int slices = input_imp.getNSlices();
                    imp = HyperStackConverter.toHyperStack(imp, 1, input_imp.getNSlices(), input_imp.getNFrames());
                    imp.setTitle(processor.getName());
                }

                if (roi != null) {
                    imp.setRoi(roi);
                    IJ.run(imp, "Make Inverse", "");

                    for (int t = 0; t < imp.getNFrames(); t++) {
                        imp.setT(t + 1);
                        for (int z = 0; z < imp.getNSlices(); z++) {
                            imp.setZ(z + 1);
                            IJ.run(imp, "Multiply...", "value=0");
                        }
                    }
                    imp.setRoi(roi);
                    imp.changes = false;
                }
                synced.add(imp);
                imp.show();

                IJ.run(imp,"Enhance Contrast", "saturated=0.35");
                if (processor.getLUTName() != null) {
                    IJ.run(imp, processor.getLUTName(), "");
                }
            }
        }

        input_imp.setZ(original_z);
        input_imp.setT(original_t);
        input_imp.setRoi(roi);

        if (sync_results_and_input) {
            ImagePlus.addImageListener(new Syncronizer(synced));
        }


        System.out.println("Bye");
    }

    @Deprecated
    public static boolean voronoiLabeling(CLIJ2 clij2, ClearCLBuffer src, ClearCLImageInterface dst) {
        //CLIJx.getInstance().stopWatch("");

        ClearCLImage flip = clij2.create(dst.getDimensions(), ImageChannelDataType.Float);
        ClearCLImage flop = clij2.create(flip);
        //CLIJx.getInstance().stopWatch("alloc");

        ClearCLKernel flipKernel = null;
        ClearCLKernel flopKernel = null;

        clij2.copy(src, flip);
        //ConnectedComponentsLabelingBox.connectedComponentsLabelingBox(clij2, src, flip, false);
        //CLIJx.getInstance().stopWatch("cca");

        ClearCLBuffer flag = clij2.create(1,1,1);
        float[] flagBool = new float[1];
        flagBool[0] = 1;

        FloatBuffer buffer = FloatBuffer.wrap(flagBool);

        int i = 0;
        //CLIJx.getInstance().stopWatch("");
        while (flagBool[0] != 0) {
            //CLIJx.getInstance().stopWatch("h " + i);
            //System.out.println(i);

            flagBool[0] = 0;
            flag.readFrom(buffer, true);

            if (i % 2 == 0) {
                flipKernel = clij2.onlyzeroOverwriteMaximumBox(flip, flag, flop, flipKernel);
            } else {
                flopKernel = clij2.onlyzeroOverwriteMaximumDiamond(flop, flag, flip, flopKernel);
            }
            i++;

            flag.writeTo(buffer, true);
            //System.out.println(flagBool[0]);
        }
        //CLIJx.getInstance().stopWatch("h " + i);

        if (i % 2 == 0) {
            clij2.copy(flip, dst);
        } else {
            clij2.copy(flop, dst);
        }
        //CLIJx.getInstance().stopWatch("edges");

        if (flipKernel != null) {
            flipKernel.close();
        }
        if (flopKernel != null) {
            flopKernel.close();
        }
        clij2.release(flip);
        clij2.release(flop);
        clij2.release(flag);

        return true;
    }


    public static void main(String[] args) {
        new ImageJ();

        ImagePlus imp = IJ.openImage("C:/structure/data/027632.tif");
                //IJ.openImage("src/main/resources/thumbnails.tif");
        imp.killRoi();

        imp = new Duplicator().run(imp, 1,1,1,1,1,10);

        imp.show();

        NeighborAnalysis.spot_detectection_blur_sigma = 1;
        NeighborAnalysis.threshold = 30;


        //imp.setRoi(new EllipseRoi(153.0,45.0,101.0,492.0,0.53));

        NeighborAnalysis.exclude_labels_on_edges = true;
        new NeighborAnalysis().run(null);
    }
}
