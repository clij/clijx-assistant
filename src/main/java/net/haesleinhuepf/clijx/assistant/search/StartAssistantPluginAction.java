package net.haesleinhuepf.clijx.assistant.search;

import net.haesleinhuepf.clij2.assistant.services.AssistantGUIPlugin;
import org.scijava.search.SearchAction;

public class StartAssistantPluginAction implements SearchAction {
    private AssistantGUIPlugin plugin;

    public StartAssistantPluginAction(AssistantGUIPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.run("");
    }

    @Override
    public String toString() {
        return "Run " + plugin.getName();
    }
}
