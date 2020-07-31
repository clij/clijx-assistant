package net.haesleinhuepf.clijx.incubator.interactive.generic;

import ij.IJ;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij2.plugins.Copy;
import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.services.CLIJMacroPluginService;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import org.scijava.plugin.Plugin;

import java.util.ArrayList;


@Plugin(type = IncubatorPlugin.class, priority = -1)
public class GenericIncubatorPlugin extends AbstractIncubatorPlugin {


    @Override
    public void run(String arg) {
        //IJ.log("ARG: " + arg);
        if (arg != null && arg.length() > 0) {
            CLIJMacroPlugin plugin = CLIJMacroPluginService.getInstance().getService().getCLIJMacroPlugin(arg);
            new GenericIncubatorPlugin(plugin).run("");
        } else {
            super.run(arg);
        }
    }

    public GenericIncubatorPlugin(CLIJMacroPlugin plugin) {
        super(plugin);
    }

    public GenericIncubatorPlugin() {
        super(new Copy()); //todo: not sure if this is ok
    }

    public GenericIncubatorPlugin(AbstractCLIJPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean canManage(CLIJMacroPlugin plugin) {
        return IncubatorUtilities.isIncubatablePlugin(plugin);
    }
}
