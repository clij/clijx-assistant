package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

public class ClEsperantoSnakeJythonGenerator extends ClEsperantoCamelJythonGenerator {
    @Override
    public String pythonize(String methodName) {
        return AssistantUtilities.niceName(methodName).trim().replace(" ", "_").toLowerCase();
    }

    @Override
    public String finish(String all) {
        return super.finish(all).replace("net.clesperanto.javaprototype.Camel", "pyclesperanto_prototype");
    }

    @Override
    public String close(String image) {
        return "";
    }
}
