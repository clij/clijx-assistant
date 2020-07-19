package net.haesleinhuepf.clincubator.interactive.detection;

import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsUntilTheyTouch;
import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsWithMaximumRadius;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.processing.DifferenceOfGaussian;
import net.haesleinhuepf.clincubator.interactive.processing.GaussianBlur;
import net.haesleinhuepf.clincubator.interactive.projections.MaximumZProjection;
import net.haesleinhuepf.clincubator.interactive.projections.MeanZProjection;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface Detector extends SuggestedPlugin {

    default Class[] suggestedNextSteps() {
        return new Class[] {
                ExtendLabelsUntilTheyTouch.class,
                ExtendLabelsWithMaximumRadius.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                GaussianBlur.class,
                DifferenceOfGaussian.class,
                BackgroundSubtraction.class,
                MaximumZProjection.class,
                MeanZProjection.class
        };
    }
}
