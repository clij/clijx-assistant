package net.haesleinhuepf.clijx.assistant.utilities;

import ij.IJ;
import ij.IJEventListener;
import ij.ImageJ;
import ij.gui.Toolbar;
import ij.plugin.tool.PlugInTool;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;

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
                        new AssistantGUIStartingPoint().run("");
                    }
                }
            }
        });
    }

    @Override
    public String getToolIcon() {
        return "C000" +
                "R0020"+
                "R0320" +
                "R3110"+
                "R3410" +

                "Rff00"+
                "R9f40"+
                "R8e00" +

                "C800" +
                "Rc400" +
                "Rb300" +
                "R9210" +
                "R8300" +
                "R7401" +
                "R7610" +
                "R8700" +

                "C080" +
                "Ra910" +
                "Rc810" +
                "Re710" +

                "C008" +
                "Rac20" +
                "Rdb00" +
                "Rfb00" +
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
