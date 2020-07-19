package net.haesleinhuepf.clincubator.interactive.processing;

import net.haesleinhuepf.clincubator.interactive.transform.MakeIsotropic;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;

public interface Denoiser extends SuggestedPlugin {

    default Class[] suggestedNextSteps() {
        return new Class[] {
                BackgroundSubtraction.class,
                MakeIsotropic.class
        };
    }

    default Class[] suggestedPreviousSteps() {
        return new Class[0];
    }
}
