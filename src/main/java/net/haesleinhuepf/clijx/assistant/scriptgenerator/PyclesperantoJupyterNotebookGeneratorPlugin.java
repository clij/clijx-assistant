package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.plugin.PlugIn;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;

public class PyclesperantoJupyterNotebookGeneratorPlugin implements PlugIn {

    @Override
    public void run(String arg) {
        AssistantGUIPlugin plugin = AssistantGUIStartingPoint.getCurrentPlugin();
        plugin.generateScriptFile(new PyclesperantoJupyterNotebookGenerator());
    }
}

