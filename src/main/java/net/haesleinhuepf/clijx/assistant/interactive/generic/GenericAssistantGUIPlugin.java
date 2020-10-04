package net.haesleinhuepf.clijx.assistant.interactive.generic;

import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.plugins.Copy;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.services.CLIJMacroPluginService;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;
import org.scijava.plugin.Plugin;


@Plugin(type = AssistantGUIPlugin.class, priority = -1)
public class GenericAssistantGUIPlugin extends AbstractAssistantGUIPlugin {


    @Override
    public void run(String arg) {
        //IJ.log("ARG: " + arg);
        if (arg != null && arg.length() > 0) {
            CLIJMacroPlugin plugin = CLIJMacroPluginService.getInstance().getService().getCLIJMacroPlugin(arg);
            new GenericAssistantGUIPlugin(plugin).run("");
        } else {
            super.run(arg);
        }
    }

    public GenericAssistantGUIPlugin(CLIJMacroPlugin plugin) {
        super(plugin);
    }

    public GenericAssistantGUIPlugin() {
        super(new Copy());
    }

    public GenericAssistantGUIPlugin(AbstractCLIJPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean canManage(CLIJMacroPlugin plugin) {
        return AssistantUtilities.isIncubatablePlugin(plugin);
    }
}
