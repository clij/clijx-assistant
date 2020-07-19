package net.haesleinhuepf.clincubator.interactive.labeling;

import net.haesleinhuepf.clincubator.interactive.detection.FindAndLabelMaxima;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface LabelingModifier extends SuggestedPlugin {

    default Class[] suggestedNextSteps() {
        return new Class[] {
                ExtendLabelsUntilTheyTouch.class,
                ExtendLabelsUntilTheyTouch.class,
                ExtendLabelsWithMaximumRadius.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{
                ConnectedComponentsLabeling.class,
                FindAndLabelMaxima.class
        };
    }
}
