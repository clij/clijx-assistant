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
        return "C888" +
                "R008f"+
                "Ra076"+
                "Ceee" +
                "F117d" +
                "Cf88" +
                "Fb135" +
                "C8f8" +
                "Fe135";
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
