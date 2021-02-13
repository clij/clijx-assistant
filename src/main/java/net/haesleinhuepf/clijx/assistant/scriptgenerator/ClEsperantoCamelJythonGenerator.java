package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.scijava.util.VersionUtils;

public class ClEsperantoCamelJythonGenerator extends JythonGenerator {

    @Override
    public String header() {
        return  "# To make this script run in Fiji, please activate the clij, " +
                "# clij2 and clijx-assistant update sites in your Fiji. \n" +
                "# Read more: \n" +
                "# https://clij.github.io/assistant\n" +
                "# \n" +
                "# To make this script run in cpython, install pyclesperanto_prototype:\n" +
                "# pip install pyclesperanto_prototype\n" +
                "# Read more: \n" +
                "# https://clesperanto.net\n" +
                "# \n" +
                "# Generator (J) version: " + VersionUtils.getVersion(this.getClass()) + "\n" +
                "# \n" +
                "import net.clesperanto.javaprototype.Camel as cle\n\n";
    }


    @Override
    public String push(ImagePlus source) {
        String output = "";

        String filename = getFilename(source);

        String image_id = makeImageID(source);

        output = output + "" +
                image_id + " = cle.imread(\"" + filename + "\")\n" +
                "cle.imshow(" + (image_id) + ")\n\n";

        return output;
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        String image1 = makeImageID(result.getTarget());

        return "# show result\n" +
                "cle.imshow(" + image1 + ", \"" + result.getTarget().getTitle() + "\", " +
                (AssistantUtilities.resultIsLabelImage(result)?"True":"False") + ", " +
                result.getTarget().getDisplayRangeMin() + ", " + result.getTarget().getDisplayRangeMax() + ")\n" +
                "\n";


    }

    @Override
    public String finish(String all) {
        return super.finish(all)
                .replace(".create(", ".create_like(")
                .replace("clijx.", "cle.")
                .replace("clij2.", "cle.")
                .replace(".getDimensions(), cle.Float", "")
                .replace(".getDimensions(), cle.UnsignedShort", "")
                .replace(".getDimensions(), cle.UnsignedByte", "")
                .replace(", cle.Float", "")
                .replace(", cle.UnsignedShort", "")
                .replace(", cle.UnsignedByte", "");
    }
}
