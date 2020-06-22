package net.haesleinhuepf.spimcat.measurement;

import autopilot.image.DoubleArrayImage;
import autopilot.measures.FocusMeasures;
import de.mpicbg.rhaase.utils.DoubleArrayImageImgConverter;
import ij.measure.ResultsTable;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clijx.CLIJx;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.Views;

public class SliceAnalyser implements Runnable {
    private ClearCLBuffer image;
    private FocusMeasures.FocusMeasure[] features;
    private ResultsTable table;

    public SliceAnalyser(ClearCLBuffer image, FocusMeasures.FocusMeasure[] features, ResultsTable table) {
        this.image = image;
        this.features = features;
        this.table = table;
    }

    @Override
    public void run() {
        ClearCLBuffer slice = image;

        CLIJx clijx = CLIJx.getInstance();
        if (slice.getNativeType() != NativeTypeEnum.Float) {
            slice = clijx.create(image.getDimensions(), NativeTypeEnum.Float);
            clijx.copy(image, slice);
        }
        RandomAccessibleInterval rai = CLIJ.getInstance().pullRAI(slice);
        if (slice != image) {
            clijx.release(slice);
        }

        DoubleArrayImage image = new DoubleArrayImageImgConverter(Views.iterable(rai)).getDoubleArrayImage();

        for (FocusMeasures.FocusMeasure focusMeasure : features) {
            //System.out.println("Determining " + focusMeasure.getLongName());
            double focusMeasureValue = FocusMeasures.computeFocusMeasure(focusMeasure, image);
            table.addValue(focusMeasure.getLongName(), focusMeasureValue);
        }
    }
}
