package net.haesleinhuepf.clincubator;

import ij.IJ;
import ij.ImagePlus;
import net.haesleinhuepf.clincubator.utilities.IncubatorPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.awt.*;
import java.util.ArrayList;

class IncubatorPluginRegistry {
    private IncubatorPluginRegistry() { }

    // singleton
    private static IncubatorPluginRegistry instance = null;
    public static IncubatorPluginRegistry getInstance() {
        if (instance == null) {
            instance = new IncubatorPluginRegistry();
        }
        return instance;
    }

    // register and unregister
    ArrayList<IncubatorPlugin> registeredPlugins = new ArrayList<>();
    public void register(IncubatorPlugin plugin) {
        if (!registeredPlugins.contains(plugin)) {
            registeredPlugins.add(plugin);
        }
    }
    public void unregister(IncubatorPlugin plugin) {
        registeredPlugins.remove(plugin);
    }

    // execute actions
    int level = 0;
    boolean regenerating = false;
    public void invalidate(ImagePlus imp) {
        if (regenerating) {
            return;
        }

        IJ.log("Invalidate " + imp.getTitle());
        if (imp.getStack() instanceof CLIJxVirtualStack) {
            ((CLIJxVirtualStack) imp.getStack()).getBuffer().setName("");
        }

        level ++;
        // search for plugins which have it as source and invalidate their targets
        for (IncubatorPlugin plugin : registeredPlugins) {
            if (plugin.getSource() == imp) {
                plugin.invalidateTarget();
            }
        }
        level--;
        if (level == 0) { // the whole tree has been marked
            IJ.log("--- Starting to regenerate tree");
            Panel panel = new Panel();
            panel.setSize(20, 20);
            panel.setBackground(new Color(255, 255, 128));
            IJ.getInstance().add(panel);
            panel.setLocation(0, 0);
            regenerate();
            IJ.getInstance().remove(panel);
        }
    }

    private void regenerate() {
        regenerating = true;

        boolean found_something_to_regenerate = true;
        while(found_something_to_regenerate) {
            found_something_to_regenerate = false;

            for (IncubatorPlugin plugin : registeredPlugins) {
                if (isValid(plugin.getSource()) && !isValid(plugin.getTarget())) {
                    IJ.log("Regenerating " + plugin.getTarget().getTitle());
                    plugin.refresh();
                    found_something_to_regenerate = true;
                    break;
                }
            }
        }

        regenerating = false;
    }

    private boolean isValid(ImagePlus imp) {
        if (imp.getStack() instanceof CLIJxVirtualStack) {
            return ((CLIJxVirtualStack) imp.getStack()).getBuffer().getName().length() != 0;
        }
        return true;
    }


}
