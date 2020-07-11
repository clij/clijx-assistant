package net.haesleinhuepf.clincubator.analysis;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.plugins.StatisticsOfLabelledPixels;
import net.haesleinhuepf.clij2.plugins.VoronoiLabeling;
import net.haesleinhuepf.clijx.framor.AbstractFrameProcessor;
import net.haesleinhuepf.clijx.framor.FrameProcessor;
import net.haesleinhuepf.clijx.framor.Framor;
import net.haesleinhuepf.clincubator.projection.Utilities;
import net.haesleinhuepf.spimcat.measurement.neighbors.*;
import net.haesleinhuepf.spimcat.measurement.neighbors.implementations.*;

public class NeighborAnalysisFrameProcessor extends AbstractFrameProcessor implements PlugInFilter {
    public enum InputImageType{
        RAW_IMAGE,
        SPOT_IMAGE,
        BINARY_IMAGE,
        LABEL_MAP;

        public static final InputImageType[] all = {
                RAW_IMAGE,
                SPOT_IMAGE,
                BINARY_IMAGE,
                LABEL_MAP
        };

        public static final String[] allAsString = {
                RAW_IMAGE.toString(),
                SPOT_IMAGE.toString(),
                BINARY_IMAGE.toString(),
                LABEL_MAP.toString()
        };
    }

    int background_subtraction_radius = 0;
    float spot_detectection_blur_sigma = 3;

    float min_intensity_threshold = 0;
    float max_intensity_threshold = Float.MAX_VALUE;

    float min_size_threshold = 0;
    float max_size_threshold = Float.MAX_VALUE;

    boolean exclude_labels_on_edges = true;
    InputImageType input_type = InputImageType.RAW_IMAGE;

    boolean fill_gaps_between_labels = true;

    public NeighborAnalysisFrameProcessor(){}
    public NeighborAnalysisFrameProcessor(InputImageType input_type, int background_subtraction_radius, Float spot_detectection_blur_sigma, Float min_intensity_threshold, Float max_intensity_threshold, Float min_size_threshold, Float max_size_threshold, boolean exclude_labels_on_edges, boolean fill_gaps_between_labels, boolean[] selection){
        this.input_type = input_type;
        this.background_subtraction_radius = background_subtraction_radius;
        this.spot_detectection_blur_sigma = spot_detectection_blur_sigma;
        this.min_intensity_threshold = min_intensity_threshold;
        this.max_intensity_threshold = max_intensity_threshold;
        this.min_size_threshold = min_size_threshold;
        this.max_size_threshold = max_size_threshold;
        this.exclude_labels_on_edges = exclude_labels_on_edges;
        this.fill_gaps_between_labels = fill_gaps_between_labels;
        this.selection = selection;
    }

    static NeighborProcessor[] processors = new NeighborProcessor[] {
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
            new ParametricImageProcessor(StatisticsOfLabelledPixels.STATISTICS_ENTRY.MAX_MEAN_DISTANCE_TO_MASS_CENTER_RATIO.toString())
    };

    boolean[] selection = null;

    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        ImagePlus input_imp = IJ.getImage();
        input_imp.killRoi();

        GenericDialog gd = new GenericDialog("Neighbor analyser");
        gd.addChoice("Input_type", new String[]{"Raw image", "Spot image", "Binary image", "Label Map"}, input_type.toString());
        gd.addNumericField("Background_subtraction_radius (top-hat)", background_subtraction_radius, 0);
        gd.addNumericField("Spot_detection_blur_sigma", spot_detectection_blur_sigma, 2);
        gd.addNumericField("Min intensity threshold", min_intensity_threshold, 2);
        gd.addNumericField("Max intensity threshold", max_intensity_threshold, 2);
        gd.addNumericField("Min size threshold (squared or cubic micron)", min_size_threshold, 2);
        gd.addNumericField("Max size threshold (squared or cubic micron)", max_size_threshold, 2);
        gd.addCheckbox("Fill gaps between labels", fill_gaps_between_labels);
        gd.addCheckbox("Exclude_labels_on_edges", exclude_labels_on_edges);


        int check_box_count = 0;
        for (NeighborProcessor processor : processors) {
            boolean selected = processor.getDefaultActivated();
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

        input_type = InputImageType.all[gd.getNextChoiceIndex()];
        background_subtraction_radius = (int) gd.getNextNumber();
        spot_detectection_blur_sigma = (float)gd.getNextNumber();
        min_intensity_threshold = (float)gd.getNextNumber();
        max_intensity_threshold = (float)gd.getNextNumber();
        min_size_threshold = (float)gd.getNextNumber();
        max_size_threshold = (float)gd.getNextNumber();


        fill_gaps_between_labels = gd.getNextBoolean();
        exclude_labels_on_edges = gd.getNextBoolean();

        selection = new boolean[processors.length];

        int i = 0;
        for (NeighborProcessor processor : processors) {
            if (gd.getNextBoolean()) {
                selection[i] = true;
            }
            i++;
        }

        new Framor(input_imp, duplicate()).getResult().show();
    }

    static boolean[] makeSelection() {
        boolean[] selection = new boolean[processors.length];

        int i = 0;
        for (NeighborProcessor processor : processors) {
            selection[i] = processor.getDefaultActivated();
            i++;
        }
        return selection;
    }

    public ImagePlus process(ImagePlus input_imp) {
        CLIJ2 clij2 = getCLIJ2();

        ClearCLBuffer input = clij2.pushCurrentZStack(input_imp);
        ClearCLBuffer label_map = null;
        ClearCLBuffer pointlist = null;

        float pixel_size = (float)(input_imp.getCalibration().pixelWidth * input_imp.getCalibration().pixelHeight);
        if (input.getDimension() == 3 && input.getDepth() > 1) {
            pixel_size = (float)(pixel_size * input_imp.getCalibration().pixelDepth);
        }

        int number_of_objects = 0;
        ResultsTable table = null;

        if (input_type == InputImageType.RAW_IMAGE) {
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

            if (min_intensity_threshold != 0) {
                clij2.greaterOrEqualConstant(temp2, temp1, min_intensity_threshold);
                clij2.mask(temp3, temp1, temp2);
            } else {
                clij2.copy(temp3, temp2);
            }

            if (max_intensity_threshold != Float.MAX_VALUE) {
                clij2.smallerOrEqualConstant(temp2, temp1, max_intensity_threshold);
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

        } else if (input_type == InputImageType.SPOT_IMAGE) {
            number_of_objects = (int) clij2.sumOfAllPixels(input);
            pointlist = clij2.create(number_of_objects, input.getDimension());
            clij2.spotsToPointList(input, pointlist);

            label_map = clij2.create(input.getDimensions(), clij2.Float);
            VoronoiLabeling.voronoiLabeling(clij2, input, label_map);
        } else if (input_type == InputImageType.BINARY_IMAGE) {
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
            clij2.extendLabelingViaVoronoi(label_map, temp);
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

        if (
            min_intensity_threshold != 0 ||
            max_intensity_threshold != Float.MAX_VALUE ||
            min_size_threshold != 0 ||
            max_size_threshold != Float.MAX_VALUE
        ) {
            System.out.println("Checking intensity and size of labels");
            ResultsTable stats_table = new ResultsTable();
            clij2.statisticsOfBackgroundAndLabelledPixels(input, label_map, stats_table);

            ClearCLBuffer exclude_labels_vector = clij2.create(new long[]{stats_table.size(), 1, 1}, NativeTypeEnum.UnsignedByte);
            clij2.set(exclude_labels_vector, 0);

            if (input_type == InputImageType.RAW_IMAGE) {
                if (min_intensity_threshold != 0) {
                    ClearCLBuffer values = Utilities.pushTableColumn(clij2, stats_table, StatisticsOfLabelledPixels.STATISTICS_ENTRY.MEAN_INTENSITY.toString());
                    ClearCLBuffer bits = Utilities.smallerConstant(clij2, values, min_intensity_threshold);
                    ClearCLBuffer or = Utilities.binaryOr(clij2, exclude_labels_vector, bits);
                    clij2.copy(or, exclude_labels_vector);
                    values.close();
                    bits.close();
                    or.close();
                }
                if (max_intensity_threshold != Float.MAX_VALUE) {
                    ClearCLBuffer values = Utilities.pushTableColumn(clij2, stats_table, StatisticsOfLabelledPixels.STATISTICS_ENTRY.MEAN_INTENSITY.toString());
                    ClearCLBuffer bits = Utilities.greaterConstant(clij2, values, max_intensity_threshold);
                    ClearCLBuffer or = Utilities.binaryOr(clij2, exclude_labels_vector, bits);
                    clij2.copy(or, exclude_labels_vector);
                    values.close();
                    bits.close();
                    or.close();
                }
            } else {
                IJ.log("Neighbor analysis warning: Intensity threshold set, but no intensity given. Parameter ignored.");
            }
            if (min_size_threshold != 0) {
                ClearCLBuffer values = Utilities.pushTableColumn(clij2, stats_table, StatisticsOfLabelledPixels.STATISTICS_ENTRY.MEAN_INTENSITY.toString());
                ClearCLBuffer bits = Utilities.smallerConstant(clij2, values, min_size_threshold * pixel_size);
                ClearCLBuffer or = Utilities.binaryOr(clij2, exclude_labels_vector, bits);
                clij2.copy(or, exclude_labels_vector);
                values.close();
                bits.close();
                or.close();
            }
            if (max_size_threshold != Float.MAX_VALUE) {
                ClearCLBuffer values = Utilities.pushTableColumn(clij2, stats_table, StatisticsOfLabelledPixels.STATISTICS_ENTRY.MEAN_INTENSITY.toString());
                ClearCLBuffer bits = Utilities.greaterConstant(clij2, values, max_size_threshold * pixel_size);
                ClearCLBuffer or = Utilities.binaryOr(clij2, exclude_labels_vector, bits);
                clij2.copy(or, exclude_labels_vector);
                values.close();
                bits.close();
                or.close();
            }

            clij2.setColumn(exclude_labels_vector, 0, 0);
            ClearCLBuffer new_label_map = clij2.create(label_map);
            clij2.excludeLabels(exclude_labels_vector, label_map, new_label_map);
            label_map.close();
            label_map = new_label_map;
            exclude_labels_vector.close();
            number_of_objects = (int) clij2.maximumOfAllPixels(label_map);
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

        ImageStack stack = new ImageStack();
        int i = 0;
        for (NeighborProcessor processor : processors) {
            if (selection[i]) {
                if (processor instanceof TakesPropertyTable) {
                    if (table == null) {
                        table = new ResultsTable();
                        clij2.statisticsOfBackgroundAndLabelledPixels(input, label_map, table);
                    }
                    ((TakesPropertyTable) processor).setTable(table);
                }

                ClearCLBuffer result = processor.process(clij2, input, pointlist, label_map, touch_matrix, distance_matrix);
                ImagePlus imp = clij2.pull(result);
                result.close();

                for (int z = 0; z < imp.getNSlices(); z++) {
                    imp.setZ(z + 1);
                    stack.addSlice(imp.getProcessor());
                }
            }
            i++;
        }

        input.close();
        if (label_map != input) {
            label_map.close();
        }

        System.out.println("Bye");
        return new ImagePlus("", stack);
    }

    @Override
    public FrameProcessor duplicate() {
        return new NeighborAnalysisFrameProcessor(
                input_type,
                background_subtraction_radius,
                spot_detectection_blur_sigma,
                min_intensity_threshold,
                max_intensity_threshold,
                min_size_threshold,
                max_size_threshold,
                exclude_labels_on_edges,
                fill_gaps_between_labels,
                selection
        );
    }

    @Override
    public long getMemoryNeedInBytes(ImagePlus imp) {

        int count = 10;
        for (boolean selected : selection) {
            if (selected) {
                count++;
            }
        }

        return count * imp.getWidth() * imp.getHeight() * imp.getNSlices() * 4;
    }

    public static void main(String[] args) {
        new ImageJ();

        ImagePlus imp = IJ.openImage("C:/structure/data/clincubator_data/Lund_HSCMaxP_000500.tif");


//        imp.show();
//        new NeighborAnalysisFrameProcessor().run(null);
//        if (true) return;


        //imp.killRoi();

        //imp = new Duplicator().run(imp, 1,1,1,1,1,10);
        //imp.show();

        int background_subtraction_radius = 0;
        float spot_detectection_blur_sigma = 1;
        float min_intensity_threshold = 70;
        float max_intensity_threshold = Float.MAX_VALUE;
        float min_size_threshold = 0;
        float max_size_threshold = Float.MAX_VALUE;

        InputImageType input_type = InputImageType.RAW_IMAGE;

        boolean exclude_labels_on_edges = true;
        boolean fill_gaps_between_labels = false;
        boolean[] selection = makeSelection();

        new Framor(imp, new NeighborAnalysisFrameProcessor(
                input_type,
                background_subtraction_radius,
                spot_detectection_blur_sigma,
                min_intensity_threshold,
                max_intensity_threshold,
                min_size_threshold,
                max_size_threshold,
                exclude_labels_on_edges,
                fill_gaps_between_labels,
                selection
        )).getResult().show();
    }

}
