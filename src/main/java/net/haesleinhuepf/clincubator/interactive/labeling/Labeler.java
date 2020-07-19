package net.haesleinhuepf.clincubator.interactive.labeling;

import net.haesleinhuepf.clij2.plugins.AverageDistanceOfNFarOffPoints;
import net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesWithinRange;
import net.haesleinhuepf.clincubator.interactive.analysis.AverageNeighborDistance;
import net.haesleinhuepf.clincubator.interactive.analysis.CountNeighbors;
import net.haesleinhuepf.clincubator.interactive.detection.FindAndLabeledMaxima;
import net.haesleinhuepf.clincubator.utilities.MenuSeparator;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface Labeler extends SuggestedPlugin {

    default Class[] suggestedNextSteps() {
        return new Class[] {
                ExcludeLabelsOutsideSizeRange.class,
                ExcludeLabelsOnEdges.class,
                MenuSeparator.class,
                CountNeighbors.class,
                AverageNeighborDistance.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                FindAndLabeledMaxima.class
        };
    }
}
