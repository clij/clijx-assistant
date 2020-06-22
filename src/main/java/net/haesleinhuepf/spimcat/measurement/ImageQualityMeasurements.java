package net.haesleinhuepf.spimcat.measurement;

import autopilot.measures.FocusMeasures;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clijx.CLIJx;

public class ImageQualityMeasurements implements PlugInFilter {

    @Override
    public void run(ImageProcessor ip)
    {
        ImagePlus imp = IJ.getImage();
        int firstFrame = 0;
        int lastFrame = imp.getNFrames();
        int frameStep = 1;
        ResultsTable maximumProjectionAnalysisResults = new ResultsTable();
        ResultsTable meanProjectionAnalysisResults = new ResultsTable();

        CLIJx clijx = CLIJx.getInstance();

        ClearCLBuffer projection = null;

        for (int f = firstFrame; f <= lastFrame; f+=frameStep) {
            System.out.println("f " + f);
            maximumProjectionAnalysisResults.incrementCounter();
            meanProjectionAnalysisResults.incrementCounter();

            maximumProjectionAnalysisResults.addValue("Frame", f);
            meanProjectionAnalysisResults.addValue("Frame", f);

            ImagePlus timePointStack = imp;

            timePointStack.setZ(f + 1);
            ClearCLBuffer input = clijx.pushCurrentZStack(timePointStack);
            projection = clijx.create(new long[]{input.getWidth(), input.getHeight()}, NativeTypeEnum.Float);

            clijx.maximumZProjection(input, projection);
            new SliceAnalyser(projection, FocusMeasures.getFocusMeasuresArray(), maximumProjectionAnalysisResults).run();

            clijx.meanZProjection(input, projection);
            new SliceAnalyser(projection, FocusMeasures.getFocusMeasuresArray(), meanProjectionAnalysisResults).run();

            input.close();

            clijx.clear();
        }
        projection.close();
        maximumProjectionAnalysisResults.show("Max projection analysis results");
        meanProjectionAnalysisResults.show("Mean projection analysis Results");


    }

    public static void main(String ... arg) {
        new ImageJ();

        String sourceFolder = "C:/structure/data/2019-12-17-16-54-37-81-Lund_Tribolium_nGFP_TMR/";
        String datasetFolder = "C0opticsprefused";

        //new ImageQualityMeasurements(dataSet).run();
    }



    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }
}
