package net.haesleinhuepf.clijx.assistant;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import net.imglib2.converter.AbstractConvertedRandomAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.Timer;

class AssistantGUIPluginRegistry {
    Timer heartbeat = null;

    private AssistantGUIPluginRegistry() {
    }

    // singleton
    private static AssistantGUIPluginRegistry instance = null;

    public static AssistantGUIPluginRegistry getInstance() {
        if (instance == null) {
            instance = new AssistantGUIPluginRegistry();
        }
        return instance;
    }

    // register and unregister
    ArrayList<AssistantGUIPlugin> registeredPlugins = new ArrayList<>();

    public void register(AssistantGUIPlugin plugin) {
        if (registeredPlugins.size() == 0) {
            int delay = 100;
            heartbeat = new Timer();
            heartbeat.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    highlightConnections();
                    regenerate();
                }
            }, delay, delay);
        }

        if (!registeredPlugins.contains(plugin)) {
            registeredPlugins.add(plugin);
        }
    }

    ArrayList<Frame> connection_tiles = new ArrayList<Frame>();
    long former_time = 0;
    long former_duration = 0;

    private synchronized void removeConnections() {
        try {
            for (int i = 0; i < this.connection_tiles.size(); i++) {
                Frame tile = this.connection_tiles.get(i);
                if (tile.isVisible()) {
                    tile.setVisible(false);
                }
                tile.dispose();
            }
        } catch (ConcurrentModificationException e) {}
        this.connection_tiles.clear();
    }

    private synchronized void highlightConnections() {

        try{
            if (!AbstractAssistantGUIPlugin.show_connections) {
                removeConnections();
                return;
            }

            ArrayList<Frame> connection_tiles = new ArrayList<Frame>();
            long time = System.currentTimeMillis();
            if (time - former_time < former_duration || time - former_time < 200 ) {
                //System.out.println("Leave because time");
                return;
            }
            former_time = time;

            long start_time = System.currentTimeMillis();
            for (AssistantGUIPlugin plugin : registeredPlugins) {
                ImagePlus target = plugin.getTarget();
                if (target == null || target.getWindow() == null) {
                    continue;
                }
                for (int s = 0; s < plugin.getNumberOfSources(); s++) {
                    ImagePlus source = plugin.getSource(s);
                    if (source == null || source.getWindow() == null) {
                        continue;
                    }

                    Window source_window = source.getWindow();
                    ImageWindow target_window = target.getWindow();

                    int startX = source_window.getX() + source_window.getWidth() / 2;
                    int startY = source_window.getY() + source_window.getHeight() / 2;
                    int endX = target_window.getX() + target_window.getWidth() / 2;
                    int endY = target_window.getY() + target_window.getHeight() / 2;

                    int distance = (int) Math.sqrt(
                            Math.pow(startX - endX, 2) +
                            Math.pow(startY - endY, 2)
                    );
                    int tile_size = 10;
                    int step_size = 12;
                    int num_steps = distance / step_size;
                    if (num_steps == 0) {
                        continue;
                    }
                    double step_x = (endX - startX) / num_steps;
                    double step_y = (endY - startY) / num_steps;

                    double x = startX;
                    double y = startY;


                    for (int i = 0; i < num_steps; i++) {
                        int color = 128 + 100 - (Math.abs(Math.abs((int)(time / 100 - i) % 10 * 20)));
                        Color status = target_window.getBackground();


                        if (! within(x, y, source_window) && ! within(x, y, target_window)) {
                            Frame tile;
                            if (this.connection_tiles.size() > connection_tiles.size()) {
                                tile = this.connection_tiles.get(connection_tiles.size());
                            } else {
                                tile = new Frame();
                                tile.setFocusable(false);
                                tile.setType(Window.Type.UTILITY);
                            }
                            tile.setLocation((int)(x - tile_size / 2), (int)(y - tile_size / 2));
                            if (!tile.isUndecorated()) {
                                tile.setUndecorated(true);
                            }
                            if (tile.getWidth() != tile_size || tile.getHeight() != tile_size) {
                                tile.setSize(tile_size, tile_size);
                            }
                            //tile.setShape(new Ellipse2D.Float((int)(x - tile_size / 2), (int)(y - tile_size / 2), tile_size, tile_size));
                            // System.out.println(color);
                            tile.setBackground(new Color(
                                    status.getRed() == 128?color:status.getRed(),
                                    status.getGreen() == 128?color:status.getGreen(),
                                    status.getBlue() == 128?color:status.getBlue()));
                            if (!tile.isVisible()) {
                                tile.setVisible(true);
                            }
                            connection_tiles.add(tile);
                        }
                        x += step_x;
                        y += step_y;
                    }
                }
            }


            for (int i = connection_tiles.size(); i < this.connection_tiles.size(); i++) {
                Frame tile = this.connection_tiles.get(i);
                if (tile.isVisible()) {
                    tile.setVisible(false);
                }
                connection_tiles.add(tile);
                //this.connection_tiles.get(i).dispose();
            }
            this.connection_tiles = connection_tiles;

            former_duration = System.currentTimeMillis() - start_time;
            //System.out.println("duration: " + former_duration);
        } catch (ConcurrentModificationException e) {}
        /*
        try {
            for (AssistantGUIPlugin plugin : registeredPlugins) {
                setColor(plugin.getTarget(), Color.white);
            }

            ImagePlus current = IJ.getImage();
            if (current == null) {
                return;
            }
            AssistantGUIPlugin plugin = getPlugin(current);
            if (plugin == null) {
                return;
            }
            ImagePlus predecessor = plugin.getSource();
            if (!isNeverTarget(predecessor)) {
                setColor(predecessor, new Color(235, 235, 235));
            }

            setColor(current, new Color(215, 215, 215));

            for (ImagePlus imp : getFollowers(current)) {
                setColor(current, new Color(235, 235, 235));
            }
        } catch (ConcurrentModificationException e) {}
        */


    }

    private boolean within(double x, double y, Window source_window) {
        return
                x > source_window.getX() && x < source_window.getX() + source_window.getWidth() &&
                y > source_window.getY() && y < source_window.getY() + source_window.getHeight();
    }


    private void setColor(ImagePlus current, Color color) {
        if (current != null) {
            ImageWindow win = current.getWindow();
            if (win != null) {
                win.setBackground(color);
            }
        }
    }


    public void unregister(AssistantGUIPlugin plugin) {
        registeredPlugins.remove(plugin);
        if (registeredPlugins.size() == 0) {

            heartbeat.cancel();
            heartbeat = null;
            removeConnections();
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
        for (AssistantGUIPlugin plugin : registeredPlugins) {
            for (int s = 0; s < plugin.getNumberOfSources(); s++) {
                if (plugin.getSource(s) == imp) {
                    plugin.setTargetInvalid();
                }
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
        try{
            boolean found_something_to_regenerate = true;
            while (found_something_to_regenerate) {
                found_something_to_regenerate = false;

                for (AssistantGUIPlugin plugin : registeredPlugins) {
                    //ImagePlus source = plugin.getSource();
                    ImagePlus target = plugin.getTarget();
                    if (target != null && allSourcesValid(plugin) && !isValid(target)) {
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
        } catch (ConcurrentModificationException e) {}
        regenerating = false;
    }

    public String log() {
        StringBuilder builder = new StringBuilder();
        for (AssistantGUIPlugin plugin : registeredPlugins) {
            builder.append(plugin.getName() + "Source: " + allSourcesValid(plugin) + " Target: " + isValid(plugin.getTarget()) + "\n");
        }
        return builder.toString();
    }

    private boolean allSourcesValid(AssistantGUIPlugin plugin) {
        for (int s = 0; s < plugin.getNumberOfSources(); s++) {
            if (!isValid(plugin.getSource(s))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValid(ImagePlus imp) {
        if (imp.getStack() instanceof CLIJxVirtualStack) {
            return ((CLIJxVirtualStack) imp.getStack()).getBuffer(0).getName().length() != 0;
        }
        return true;
    }


    public String generateScript(ScriptGenerator generator) {
        String result = generator.header();

        for (AssistantGUIPlugin plugin : registeredPlugins) {
            //result = result + generator.overview(plugin);
            for (int s = 0; s < plugin.getNumberOfSources(); s++) {
                ImagePlus source = plugin.getSource(s);

                if (!result.contains(generator.makeImageID(source) + " =") &&
                    !result.contains(generator.makeImageID(source) + ",") &&
                    !result.contains(generator.makeImageID(source) + ")")  &&
                    !result.contains(generator.makeImageID(source) + ":") &&
                    !result.contains(generator.makeImageID(source) + ".")
                ) {
                    result = result + generator.push(source);
                }
            }
            result = result + generator.execute(plugin);
            result = result + generator.pull(plugin);
        }
        result = generator.finish(result);

        return result;
    }

/*
    private ArrayList<AssistantGUIPlugin> findFollowers(AssistantGUIPlugin plugin) {
        ArrayList<AssistantGUIPlugin> list = new ArrayList<>();
        ImagePlus target = plugin.getTarget();
        if (target == null) {
            return list;
        }

        for (AssistantGUIPlugin otherplugin : registeredPlugins) {
            ImagePlus source = otherplugin.getSource();
            if (source != null && target == source) {
                list.add(otherplugin);
            }
        }
        return list;
    }
*/

    private boolean isNeverTarget(ImagePlus source) {
        for (AssistantGUIPlugin plugin : registeredPlugins) {
            ImagePlus target = plugin.getTarget();
            if (target != null && target == source) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<ImagePlus> getFollowers(ImagePlus source) {
        ArrayList<ImagePlus> followers = new ArrayList();

        for (AssistantGUIPlugin plugin : registeredPlugins) {
            for (int s = 0; s < plugin.getNumberOfSources(); s++) {
                if (plugin.getSource(s) == source) {
                    followers.add(plugin.getTarget());
                }
            }
        }

        return followers;
    }

    public ArrayList<AssistantGUIPlugin> getFollowers(AssistantGUIPlugin node) {
        ArrayList<AssistantGUIPlugin> followers = new ArrayList();

        for (AssistantGUIPlugin plugin : registeredPlugins) {
            for (int s = 0; s < plugin.getNumberOfSources(); s++) {
                if (plugin.getSource(s) == node.getTarget()) {
                    followers.add(plugin);
                }
            }
        }

        return followers;
    }

    public AssistantGUIPlugin getPlugin(ImagePlus target) {
        for (AssistantGUIPlugin plugin : registeredPlugins) {
            if (plugin.getTarget() == target) {
                return plugin;
            }
        }

        return null;
    }
/*
    ArrayList<Object[]> getGraph(ImagePlus imp) {
        //ImagePlus root = findRoot(imp);

        //System.out.println("Root: " + root);
        ArrayList<Object[]> list = new ArrayList<Object[]>();

        for (AssistantGUIPlugin plugin : registeredPlugins) {
            ImagePlus imp = plugin.getTarget();
            list.add(new Object[]{name + imp.getTitle(), imp});
        }
        //getGraph(root, list, 1);

        return list;
    }
    private void getGraph(ImagePlus imp, ArrayList<Object[]> list, int depth) {
        String name = "";
        for (int i = 0; i < depth; i++) {
            name = name + " ";
        }
        //System.out.println("Node: " + imp);

        list.add(new Object[]{name + imp.getTitle(), imp});

        for (AssistantGUIPlugin plugin : registeredPlugins) {
            if (plugin.getSource() == imp) {
                getGraph(plugin.getTarget(), list, depth + 1);
            }
        }
    }

    private ImagePlus findRoot(ImagePlus imp) {
        for (AssistantGUIPlugin plugin : registeredPlugins) {
            ImagePlus source = plugin.getSource();
            ImagePlus target = plugin.getTarget();
            if (target == imp) {
                if (isNeverTarget(source)) {
                    return target;
                } else {
                    return findRoot(source);
                }
            }
        }
        return null;
    }
*/
    public AssistantGUIPlugin[] getPathToRoot(AssistantGUIPlugin leaf) {
        ArrayList<AssistantGUIPlugin> list = new ArrayList<>();
        for (AssistantGUIPlugin plugin : registeredPlugins) {
            list.add(plugin);
            if (leaf == plugin) {
                break;
            }
        }

        AssistantGUIPlugin[] array = new AssistantGUIPlugin[list.size()];
        list.toArray(array);
        return array;
    }
/*
    private void getPathToRoot(AssistantGUIPlugin leaf, ArrayList<AssistantGUIPlugin> list) {
        list.add(0, leaf);
        if (isNeverTarget(leaf.getSource())) {
            return;
        }
        for (AssistantGUIPlugin plugin : registeredPlugins) {
            if (leaf.getSource() == plugin.getTarget()) {
                getPathToRoot(plugin, list);
            }
        }
    }

 */
}