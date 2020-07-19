package net.haesleinhuepf.clincubator.interactive.segmentation;

import net.haesleinhuepf.clincubator.interactive.labeling.ConnectedComponentsLabeling;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface Segmenter extends SuggestedPlugin {

    default Class[] suggestedNextSteps() {
        return new Class[]{
                ConnectedComponentsLabeling.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[]{

        };
    }
}
