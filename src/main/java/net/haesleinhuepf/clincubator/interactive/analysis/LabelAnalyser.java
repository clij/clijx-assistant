package net.haesleinhuepf.clincubator.interactive.analysis;

import net.haesleinhuepf.clij2.plugins.ExcludeLabelsWithValuesWithinRange;
import net.haesleinhuepf.clincubator.interactive.detection.FindAndLabeledMaxima;
import net.haesleinhuepf.clincubator.interactive.labeling.ConnectedComponentsLabeling;
import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsUntilTheyTouch;
import net.haesleinhuepf.clincubator.interactive.labeling.ExtendLabelsWithMaximumRadius;
import net.haesleinhuepf.clincubator.interactive.processing.BackgroundSubtraction;
import net.haesleinhuepf.clincubator.interactive.transform.CylinderProjection;
import net.haesleinhuepf.clincubator.interactive.transform.RigidTransform3D;
import net.haesleinhuepf.clincubator.interactive.transform.SphereProjection;
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
