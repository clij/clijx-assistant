package net.haesleinhuepf.clijx.incubator;

import ij.ImagePlus;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

class IncubatorPluginRegistry {
    Timer heartbeat = null;

    private IncubatorPluginRegistry() {
    }

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
        if (registeredPlugins.size() == 0) {
            int delay = 100;
            heartbeat = new Timer();
            heartbeat.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    regenerate();
                }
            }, delay, delay);
        }

        if (!registeredPlugins.contains(plugin)) {
            registeredPlugins.add(plugin);
        }
    }

    public void unregister(IncubatorPlugin plugin) {
        registeredPlugins.remove(plugin);
        if (registeredPlugins.size() == 0) {
            heartbeat.cancel();
            heartbeat = null;
        }
    }

    // execute actions
    public void invalidate(ImagePlus imp) {
        if (imp == null) {
            return;
        }
        //IJ.log("Invalidate " + imp.getTitle());
        if (imp.getStack() instanceof CLIJxVirtualStack) {
            ((CLIJxVirtualStack) imp.getStack()).getBuffer(0).setName("");
        }

        // search for plugins which have it as source and invalidate their targets
        for (IncubatorPlugin plugin : registeredPlugins) {
            if (plugin.getSource() == imp) {
                plugin.setTargetInvalid();
            }
        }
    }

    boolean regenerating = false;

    private void regenerate() {
        if (regenerating) {
            return;
        }

        regenerating = true;

        //System.out.println("Regen");

        boolean found_something_to_regenerate = true;
        while (found_something_to_regenerate) {
            found_something_to_regenerate = false;

            for (IncubatorPlugin plugin : registeredPlugins) {
                ImagePlus source = plugin.getSource();
                ImagePlus target = plugin.getTarget();
                if (source != null && target != null && isValid(source) && !isValid(target)) {
                    //IJ.log("Regenerating " + target.getTitle());

                    plugin.setTargetIsProcessing();
                    plugin.refresh();
                    found_something_to_regenerate = true;

                    if (isValid(target)) {
                        plugin.setTargetValid();
                    } else {
                        plugin.setTargetInvalid();
                    }

                    break;
                }
            }
        }

        regenerating = false;
    }

    private boolean isValid(ImagePlus imp) {
        if (imp.getStack() instanceof CLIJxVirtualStack) {
            return ((CLIJxVirtualStack) imp.getStack()).getBuffer(0).getName().length() != 0;
        }
        return true;
    }


    public String generateScript(ScriptGenerator generator) {
        String result = "";

        // find start(s)
        for (IncubatorPlugin plugin : registeredPlugins) {
            ImagePlus source = plugin.getSource();
            ImagePlus target = plugin.getTarget();
            if (source != null && target != null && isNeverTarget(source)) {
                result = result + generator.overview(plugin);
                result = result + generator.push(source);
                result = result + script(generator, plugin) + "\n\n";
            }
        }

        return result;
    }

    private String script(ScriptGenerator generator, IncubatorPlugin plugin) {
        String result = "\n";
        result = result + generator.execute(plugin);
        for (IncubatorPlugin followers : findFollowers(plugin)) {
            result = result + script(generator, followers);
        }
        return result;
    }

    private ArrayList<IncubatorPlugin> findFollowers(IncubatorPlugin plugin) {
        ArrayList<IncubatorPlugin> list = new ArrayList<>();
        ImagePlus target = plugin.getTarget();
        if (target == null) {
            return list;
        }

        for (IncubatorPlugin otherplugin : registeredPlugins) {
            ImagePlus source = otherplugin.getSource();
            if (source != null && target == source) {
                list.add(otherplugin);
            }
        }
        return list;
    }


    private boolean isNeverTarget(ImagePlus source) {
        for (IncubatorPlugin plugin : registeredPlugins) {
            ImagePlus target = plugin.getTarget();
            if (target != null && target == source) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<ImagePlus> getFollowers(ImagePlus source) {
        ArrayList<ImagePlus> followers = new ArrayList();

        for (IncubatorPlugin plugin : registeredPlugins) {
            if (plugin.getSource() == source) {
                followers.add(plugin.getTarget());
            }
        }

        return followers;
    }

    public ArrayList<IncubatorPlugin> getFollowers(IncubatorPlugin node) {
        ArrayList<IncubatorPlugin> followers = new ArrayList();

        for (IncubatorPlugin plugin : registeredPlugins) {
            if (plugin.getSource() == node.getTarget()) {
                followers.add(plugin);
            }
        }

        return followers;
    }

    public IncubatorPlugin getPlugin(ImagePlus target) {
        for (IncubatorPlugin plugin : registeredPlugins) {
            if (plugin.getTarget() == target) {
                return plugin;
            }
        }

        return null;
    }
}