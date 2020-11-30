package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.scijava.util.VersionUtils;

public class CluPathGroovyGenerator extends GroovyGenerator {

    @Override
    public String push(ImagePlus source) {
        String image1 = makeImageID(source);

        return "// Read image and convert it to ImageJ\n" +
                "def server = getCurrentServer()\n" +
                "def parent = getSelectedObject()\n" +
                "double downsample = 2.0\n" +
                "def request = parent == null ? RegionRequest.createInstance(server, downsample) : RegionRequest.createInstance(server.getPath(), downsample, parent.getROI())\n" +
                "def pathImage = IJTools.convertToImagePlus(server, request)\n" +
                "def imp = pathImage.getImage()\n\n" +
                "// push input image to GPU memory\n" +
                image1 + " = clijx.push(imp)\n\n";
    }

    @Override
    public String pull(AssistantGUIPlugin result) {
        if (!AssistantUtilities.resultIsBinaryImage(result)) {
            return "";
        }
        String image1 = makeImageID(result.getTarget());

        return "// pull back result and turn it into a QuPath ROI\n" +
                "imp = clijx.pull(" + image1 + ");\n" +
                "roi = clijx.pullAsROI(" + image1 + ");\n" +
                close(image1) + ";\n" +
                "imagePlane = IJTools.getImagePlane(roi, imp)\n" +
                "roi = IJTools.convertToROI(roi, -request.getX() / downsample, -request.getY() / downsample, downsample, imagePlane)\n" +
                "\n" +
                "// add the ROI as annotation\n" +
                "annotation = PathObjects.createAnnotationObject(roi)\n" +
                "addObject(annotation)";
    }

    @Override
    public String header() {
        return  "// To make this script run in QuPath, please install CluPath as described here: \n" +
                "// https://clij.github.io/clupath\n\n" +
                "// Note: QuPath support is experimental yet. \n" +
                "//       This script may only work if the result is a binary image.\n" +
                "// Generator version: " + VersionUtils.getVersion(this.getClass()) + "\n\n" +
                "import qupath.lib.regions.*\n" +
                "import qupath.imagej.tools.IJTools\n" +
                "import qupath.imagej.gui.IJExtension\n" +
                "import net.haesleinhuepf.clupath.CLUPATH\n\n" +
                "// Init GPU\n" +
                "clijx = CLUPATH.getInstance()\n";
    }
}
