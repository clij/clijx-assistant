package net.haesleinhuepf.clijx.assistant.scriptgenerator;

import ij.plugin.PlugIn;
import net.haesleinhuepf.clijx.assistant.AssistantGUIStartingPoint;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.te_oki.TeOkiEngine;

import javax.script.ScriptException;

public class PyclesperantoGeneratorPlugin implements PlugIn {

    @Override
    public void run(String arg) {
        AssistantGUIPlugin plugin = AssistantGUIStartingPoint.getCurrentPlugin();
        try {
            new TeOkiEngine(null).eval(plugin.generateScript(new PyclesperantoGenerator(true)));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}
