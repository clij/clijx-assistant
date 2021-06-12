package net.haesleinhuepf.clijx.assistant.interactive.handcrafted;

import net.haesleinhuepf.clij2.assistant.annotation.AnnotationTool;
import net.haesleinhuepf.clij2.assistant.services.AssistantGUIPlugin;
import org.scijava.plugin.Plugin;

@Plugin(type = AssistantGUIPlugin.class)
public class WekaRegionalLabelClassifier extends WekaLabelClassifier {


    public WekaRegionalLabelClassifier() {
        super(new net.haesleinhuepf.clijx.weka.WekaRegionalLabelClassifier());
    }

}