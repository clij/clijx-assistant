package net.haesleinhuepf.clincubator.utilities;

import ij.IJ;
import ij.IJEventListener;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Toolbar;
import ij.plugin.tool.PlugInTool;

import static net.haesleinhuepf.clincubator.utilities.IncubatorUtilities.ignoreEvent;


public class IncubatorStartingPointTool extends PlugInTool {

    public IncubatorStartingPointTool () {
        IJ.addEventListener(new IJEventListener() {
            @Override
            public void eventOccurred(int eventID) {
                if (ignoreEvent) {
                    return;
                }
                if (eventID == IJEventListener.TOOL_CHANGED) {
                    if (IJ.getToolName().compareTo(getToolName()) == 0 ) {
                        System.out.println("Start acquisition");
                        new IncubatorStartingPoint().run("");
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

    public static void main(String[] args) {
        new ImageJ();

        ignoreEvent = true;
        Toolbar.addPlugInTool(new IncubatorStartingPointTool());
    }
}
