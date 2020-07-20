package net.haesleinhuepf.clincubator.utilities;

import ij.ImagePlus;

public interface IncubatorPlugin {
    void refresh();
    ImagePlus getSource();
    ImagePlus getTarget();

    void setTargetInvalid();
    void setTargetIsProcessing();
    void setTargetValid();
}
