package net.haesleinhuepf.clincubator.interactive.labeling;

import net.haesleinhuepf.clincubator.interactive.analysis.CountNeighbors;
import net.haesleinhuepf.clincubator.interactive.detection.FindAndLabeledMaxima;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface Labeler extends SuggestedPlugin {

    default Class[] suggestedNextSteps() {
        return new Class[] {
                CountNeighbors.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                FindAndLabeledMaxima.class
        };
    }
}
