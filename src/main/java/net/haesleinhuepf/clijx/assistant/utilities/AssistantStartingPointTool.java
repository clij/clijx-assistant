package net.haesleinhuepf.clijx.assistant.utilities;

import ij.IJ;
import ij.IJEventListener;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Toolbar;
import ij.gui.YesNoCancelDialog;
import ij.plugin.tool.PlugInTool;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;
import net.haesleinhuepf.clijx.gui.InteractiveWindowPosition;


import javax.swing.*;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.ignoreEvent;


public class AssistantStartingPointTool extends PlugInTool {

    public AssistantStartingPointTool() {
        IJ.addEventListener(new IJEventListener() {
            @Override
            public void eventOccurred(int eventID) {
                if (ignoreEvent) {
                    return;
                }
                if (eventID == IJEventListener.TOOL_CHANGED) {
                    if (IJ.getToolName().compareTo(getToolName()) == 0 ) {
                        if (IJ.getImage() == null) {
                            IJ.error("No image open. Please open an image and try again.");
                            Toolbar.addPlugInTool(new InteractiveWindowPosition());
                            return;
                        }
                        if (IJ.getImage().getType() != ImagePlus.GRAY8 && IJ.getImage().getType() != ImagePlus.GRAY16 && IJ.getImage().getType() != ImagePlus.GRAY32) {
                            YesNoCancelDialog yncd = new YesNoCancelDialog(null, "Incompatible image type","The current image has an unsupported type. Do you want to convert it first?");

                            if (!yncd.yesPressed()) {
                                Toolbar.addPlugInTool(new InteractiveWindowPosition());
                                return;
                            }
                            IJ.run("RGB Stack");
                            IJ.run("Make Composite", "display=Composite");
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        new AssistantGUIStartingPoint().run("");
                    }
                }
            }
        });
    }

    @Override
    public String getToolIcon() {
        return "C001" +
                "R1010"+
                "R0320" +
                "R3110"+
                "R3400" +

                "Ref11"+
                "R8f41"+
                "R7e01" +

                "C800" +
                "Rb401" +
                "Ra301" +
                "R8211" +
                "R7301" +
                "R6401" +
                "R7601" +
                "R8701" +

                "C080" +
                "R9911" +
                "Rb811" +
                "Rd711" +

                "C008" +
                "R9c21" +
                "Rcb01" +
                "Reb11" +
                "";
    }

    @Override
    public String getToolName() {
        return "CLIJx Assistant";
    }

    public static void main(String[] args) {
        new ImageJ();

        ignoreEvent = true;
        Toolbar.addPlugInTool(new AssistantStartingPointTool());

    }
}
