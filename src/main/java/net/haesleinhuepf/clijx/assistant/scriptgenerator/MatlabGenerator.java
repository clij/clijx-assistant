package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import org.scijava.util.VersionUtils;

public class MatlabGenerator extends JythonGenerator {

    int plot_count = 0;

    @Override
    public String comment(String text) {
        return "% " + text.replace("\n", "\n% ") + "\n";
    }

    @Override
    public String push(ImagePlus source){
        String output = "";

        String filename = getFilename(source);


        if (filename != null && filename.length() > 0) {
            output = output + "" +
                    "% Load image from disc \n" +
                    "image = imread(\"" + filename + "\");\n" +
                    "% Push " + source.getTitle() + " to GPU memory\n" +
                    makeImageID(source) + " = clijx.pushMat(image);\n\n";
        } else {
            output = output +
                    "% Push " + source.getTitle() + " to GPU memory\n" +
                    makeImageID(source) + " = ...;\n\n";
        }
        return output;

    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        String image1 = makeImageID(result.getTarget());

        plot_count++;

        return "\n" +
                "% pull result back from GPU and show it\n" +
                "result = clijx.pullMat(" + image1 + ");\n" +
                "subplot(#PLOT_COUNT_Y#, #PLOT_COUNT_X#, " + plot_count + "), imshow(result, [" + result.getTarget().getDisplayRangeMin() + ", " + result.getTarget().getDisplayRangeMax() + "]);\n" +
                close(image1) + "\n\n";
    }

    @Override
    public String execute(AssistantGUIPlugin plugin) {
        return pyToMatlab(super.execute(plugin));
    }

    @Override
    public String close(String image) {
        return pyToMatlab(super.close(image));
    }

    @Override
    public String fileEnding() {
        return ".m.ijm";
    }

    @Override
    public String header() {
        return  "% To make this script run in Matlab, please install \n" +
                "% clatlabx. Read more: https://clij.github.io/clatlabx/\n\n" +
                "% Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "\n\n" +
                "% Init GPU\n" +
                "\n" +
                "% initialize CLATLABx\n" +
                "clijx = init_clatlabx();\n";
    }


    @Override
    public String finish(String all) {
        String output = pyToMatlab(super.finish(all));

        int plot_count_x = plot_count % 8;
        int plot_count_y = plot_count / plot_count_x;

        output = output.replace("#PLOT_COUNT_X#", "" + plot_count_x);
        output = output.replace("#PLOT_COUNT_Y#", "" + plot_count_y);

        if (!clijx_used) {
            output = output.replace("init_clatlabx", "init_clatlab");
            output = output.replace("clijx", "clij2");
            output = output.replace("clatlabx", "clatlab");
        }
        return output;
    }

    protected String pyToMatlab(String text) {
        return text.replace("# ", "% ").replace(")\n", ");\n");
    }
}
