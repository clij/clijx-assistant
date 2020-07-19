package net.haesleinhuepf.clincubator.interactive.analysis;

import net.haesleinhuepf.clincubator.interactive.labeling.ConnectedComponentsLabeling;
import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsUntilTheyTouch;
import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsWithMaximumRadius;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface LabelAnalyser extends SuggestedPlugin {
        default Class[] suggestedNextSteps() {
            return new Class[] {
                    // trhesholding
                    // spot detection
            };
        }

        default Class[] suggestedPreviousSteps() {
            return new Class[]{
                    ConnectedComponentsLabeling.class,
                    ExtendLabelsUntilTheyTouch.class,
                    ExtendLabelsWithMaximumRadius.class
            };
        }
}
