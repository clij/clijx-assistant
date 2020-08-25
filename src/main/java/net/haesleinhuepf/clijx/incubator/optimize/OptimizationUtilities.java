package net.haesleinhuepf.clijx.incubator.optimize;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.plugin.Selection;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;

import java.util.Arrays;

public class OptimizationUtilities {
    public static ClearCLBuffer makeGroundTruth(CLIJ2 clij2, int width, int height, int depth, RoiManager rm) {
        ImagePlus groundTruthImp = NewImage.createByteImage("ground_truth", width, height, depth, NewImage.FILL_BLACK);
        for (int i = 0; i < rm.getCount(); i++) {
            Roi roi = rm.getRoi(i);
            if (roi instanceof PolygonRoi) {
                String name = roi.getName();
                try {
                    groundTruthImp.setSliceWithoutUpdate(roi.getZPosition());
                    System.out.println("Roi z " + roi.getZPosition());
                    System.out.println("Roi p " + roi.getPosition());

                    roi = Selection.lineToArea(roi);
                    if (roi.getStatistics().area > 0) {
                        groundTruthImp.setRoi(roi);
                        //groundTruthImp.setPosition(roi.getPosition());
                        IJ.run(groundTruthImp, "Multiply...", "value=0");
                        //IJ.run(groundTruthImp, "Enlarge...", "enlarge=1");
                        IJ.run(groundTruthImp, "Add...", "value=" + (name.startsWith("p") ? 2 : 1));
                    } else {
                        System.out.println("Roi area 0");
                    }
                } catch (Exception e){}
            }
        }
        ClearCLBuffer ground_truth = clij2.push(groundTruthImp);
        //clij2.show(ground_truth, "gr");
        return ground_truth;
    }

    public static CLIJMacroPlugin[] getCLIJMacroPluginsFromIncubatorPlugins(IncubatorPlugin[] input) {
        CLIJMacroPlugin[] output = new CLIJMacroPlugin[input.length - 1];
        for (int i = 1; i < input.length; i++) {
            output[i - 1] = input[i].getCLIJMacroPlugin();
        }
        return output;
    }
    public static Object[][] getParameterArraysFromIncubatorPlugins(IncubatorPlugin[] input) {
        Object[][] output = new Object[input.length - 1][];
        for (int i = 1; i < input.length; i++) {
            output[i - 1] = input[i].getArgs();
        }
        return output;
    }

    public static int[] getParameterIndexMap(Workflow workflow, boolean show_gui) {

        final String CONSTANT = "Constant";

        String[] parameter_names = workflow.getNumericParameterNames();
        int[] parameter_index_map = new int[parameter_names.length];

        GenericDialog dialog = new GenericDialog("Optimize workflow...");
        String[] group = new String[parameter_names.length + 1];
        for (int i = 0; i < group.length; i++ ) {
            if (i < group.length - 1) {
                group[i] = "" + (i + 1);
            } else {
                group[i] = CONSTANT;
            }
        }
        int counter = -1;
        String formername = "";
        for (int i = 0; i < parameter_names.length; i++) {
            String name = parameter_names[i];
            if (hammingStringDistance(name, formername) > 1) {
                counter ++;
            }

            String default_value = group[counter];
            if (name.endsWith("_z") || name.endsWith("Z")) {
                ClearCLBuffer output = workflow.getOutput();
                if (output.getDimension() != 3 || output.getDepth() == 1) {
                    default_value = CONSTANT;
                }
            }

            dialog.addRadioButtonGroup(name, group, 1, group.length, default_value);

            parameter_index_map[i] = counter;
            formername = name;
        }
        if (show_gui) {
            dialog.showDialog();
            if (dialog.wasCanceled()) {
                return null;
            }
        }


        int num_dimensions = 0;
        for (int i = 0; i < parameter_names.length; i++) {
            String choice = dialog.getNextRadioButton();
            if (choice.compareTo(CONSTANT) != 0) {
                int dimension = Integer.parseInt(choice);
                parameter_index_map[i] = dimension - 1;
                if (num_dimensions < dimension) {
                    num_dimensions = dimension;
                }
            } else {
                parameter_index_map[i] = -1;
            }
            //System.out.println(choice);
        }

        return parameter_index_map;
    }


    public static int hammingStringDistance(String name1, String name2) {
        int distance = Math.abs(name1.length() - name2.length());
        for (int i = 0; i < name1.length() && i < name2.length(); i++) {
            if (name1.substring(i, i + 1).compareTo(name2.substring(i, i + 1)) != 0) {
                distance++;
            }
        }
        return distance;
    }


    static double[] range(int numDimensions, String[] numericParameterNames, int[] parameter_index_map, double factor) {
        double[] steps = new double[numDimensions];
        for (int i = 0; i < steps.length; i++) {
            steps[i] = 1;
        }

        for (int j = 0; j < parameter_index_map.length; j++) {
            int i = parameter_index_map[j];
            System.out.println("param: " + j + " -> " + i);
            if (i >= 0) {
                System.out.println("name: " + numericParameterNames[j]);
                steps[i] = IncubatorUtilities.parmeterNameToStepSizeSuggestion(numericParameterNames[j], true) * factor;
                System.out.println("step: " + steps[i]);
            }
        }
        return steps;
    }

}
