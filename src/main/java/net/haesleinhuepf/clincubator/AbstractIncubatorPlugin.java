package net.haesleinhuepf.clincubator;

import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import net.haesleinhuepf.IncubatorUtilities;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.gui.MemoryDisplay;
import net.haesleinhuepf.clincubator.utilities.IncubatorPlugin;
import net.haesleinhuepf.clincubator.utilities.MenuSeparator;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestionService;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.scijava.ui.swing.script.SyntaxHighlighter;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractIncubatorPlugin implements ImageListener, PlugIn, SuggestedPlugin, IncubatorPlugin {

    private final static String online_help = "https://github.com/haesleinhuepf/clincubator/";
    private final String doneText = "Done";
    private final String refreshText = "Refresh";

    private final Color refreshing_color = new Color(205, 205, 128);
    private final Color invalid_color = new Color(205, 128, 128);
    private final Color valid_color = new Color(128, 205, 128);


    protected ImagePlus my_source = null;
    /*
    int former_t = -1;
    int former_c = -1;
     */
    protected ImagePlus my_target = null;




    @Override
    public void run(String arg) {
        if (!configure()) {
            return;
        }
        IncubatorPluginRegistry.getInstance().register(this);
        ImagePlus.addImageListener(this);
        IJ.showStatus("Running " + IncubatorUtilities.niceName(this.getClass().getSimpleName()) + "...");
        refresh();
        IJ.showStatus("");

        GenericDialog dialog = buildNonModalDialog(my_target.getWindow());
        if (dialog != null) {
            registerDialogAsNoneModal(dialog);
            //dialog.showDialog();
        }
    }


    protected boolean configure() {
        setSource(IJ.getImage());
        return true;
    }

    protected GenericDialog buildNonModalDialog(Frame parent) {
        return new GenericDialog(IncubatorUtilities.niceName(this.getClass().getSimpleName()));
    }

    public abstract void refresh();
    public void refreshView() {
        if (my_target == null || my_source == null) {
            return;
        }
        if (my_source.getNSlices() == my_target.getNSlices()) {
            my_target.setZ(my_source.getZ());
        }
    }
    protected boolean parametersWereChanged() {
        return false;
    }


    public ImagePlus getSource() {
        return my_source;
    }

    protected void setSource(ImagePlus input) {
        my_source = input;
        my_target = null;
    }

    public ImagePlus getTarget() {
        return my_target;
    }

    private boolean paused = false;
    protected void setTarget(ImagePlus result) {
        paused = true;
        if (my_target == null) {
            my_target = result;
            my_target.setDisplayRange(my_source.getDisplayRangeMin(), my_source.getDisplayRangeMax());
            my_target.show();
            my_target.getWindow().getCanvas().disablePopupMenu(true);
            my_target.getWindow().getCanvas().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    int toolID = Toolbar.getToolId();
                    int flags = e.getModifiers();
                    if (toolID != Toolbar.MAGNIFIER && (e.isPopupTrigger() || (!IJ.isMacintosh() && (flags & Event.META_MASK) != 0))) {
                        handlePopupMenu(e);
                        return;
                    }

                }
            });
            System.out.println("added menu " + this);

        } else {
            ImagePlus output = result;
            double min = my_target.getDisplayRangeMin();
            double max = my_target.getDisplayRangeMax();
            my_target.setStack(output.getStack());
            my_target.setDisplayRange(min, max);
        }
        //System.out.println(my_target.getTitle() + " Pulling took " + (System.currentTimeMillis() - timeStamp) + " ms");
        paused = false;
        //invalidateTarget();
        //imageUpdated(my_target);
        IncubatorUtilities.transferCalibration(my_source, my_target);

        //validateTarget();
    }

    protected void handlePopupMenu(MouseEvent e) {
        PopupMenu popupmenu = buildPopup(e, my_source, my_target);
        my_target.getWindow().getCanvas().add(popupmenu);
        popupmenu.show(my_target.getWindow().getCanvas(), e.getX(), e.getY());
    }

    private void addMenuAction(Menu menu, String label, ActionListener listener) {
        label = IncubatorUtilities.niceName(label);
        MenuItem submenu = new MenuItem(label);
        if (listener != null) {
            submenu.addActionListener(listener);
        }
        menu.add(submenu);
    }


    protected PopupMenu buildPopup(MouseEvent e, ImagePlus my_source, ImagePlus my_target) {
        PopupMenu menu = new PopupMenu("CLIncubator");

        // -------------------------------------------------------------------------------------------------------------

        Menu suggestedFollowers = new Menu("Suggestions");
        for (Class klass : SuggestionService.getInstance().getSuggestedNextStepsFor(this)) {
            addMenuAction(suggestedFollowers, klass.getSimpleName(), (a) -> {
                my_target.show();
                try {
                    SuggestedPlugin plugin = (SuggestedPlugin) klass.newInstance();
                    plugin.run(null);
                } catch (InstantiationException ex) {


                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            });
        }
        menu.add(suggestedFollowers);
        menu.add("-");

        // -------------------------------------------------------------------------------------------------------------

        String former_category = "";
        Menu moreOptions = null;
        for (String entry : SuggestionService.getInstance().getHierarchy()) {
            String[] temp = entry.split("/");
            String category = temp[0];
            String name = temp[1];
            if (category.compareTo(former_category) != 0) {
                if (moreOptions != null) {
                    menu.add(moreOptions);
                }
                moreOptions = new Menu(IncubatorUtilities.niceName(category));
                former_category = category;
            }
            addMenuAction(moreOptions, name, (a) -> {
                SuggestionService.getInstance().getPluginByName(name).run("");
            });
        }
        if (moreOptions != null) {
            menu.add(moreOptions);
        }
        menu.add("-");

        // -------------------------------------------------------------------------------------------------------------

        Menu info = new Menu("Info");
        addMenuAction(info, "Source: " + my_source.getTitle(), (a) -> {
            System.out.println("huhu source");
            my_source.show();});
        addMenuAction(info, "Target: " + my_target.getTitle(), (a) -> {my_target.show();});
        menu.add(info);

        addMenuAction(menu, CLIJx.getInstance().getGPUName() + " " + MemoryDisplay.getStatus(), (a) -> {
            new MemoryDisplay().run("");
        });

        menu.add("-");

        addMenuAction(menu,"Operation: " + this.getClass().getSimpleName(), (a) -> {
            if (registered_dialog != null) {
                registered_dialog.show();
            }
        });

        menu.add("-");
        addMenuAction(menu,"CLIncubator online documentation", (a) -> {
            try {
                Desktop.getDesktop().browse(new URI(online_help));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e2) {
                e2.printStackTrace();
            }
        });
        return menu;
    }

    Timer heartbeat = null;
    GenericDialog registered_dialog = null;
    protected void registerDialogAsNoneModal(GenericDialog dialog) {


        dialog.setModal(false);
        dialog.setOKLabel(refreshText);

        dialog.setCancelLabel(doneText);
        dialog.showDialog();

        for (KeyListener listener : dialog.getKeyListeners()) {
            dialog.removeKeyListener(listener);
        }
        dialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.isActionKey()) {
                    //IncubatorPluginRegistry.getInstance().invalidate(getTarget());
                    return;
                }
                super.keyTyped(e);
            }
        });
        registered_dialog = dialog;

        setButtonColor(doneText, valid_color);
        setButtonColor(refreshText, valid_color);
        for (Button component : dialog.getButtons()) {
            if (component instanceof Button) {
                if (component.getLabel().compareTo(refreshText) == 0) {
                    for (ActionListener actionlistener : component.getActionListeners()) {
                        component.removeActionListener(actionlistener);
                    }
                    component.addActionListener((a) -> {
                        setTargetInvalid();
                    });
                }
            }
        }

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setTargetInvalid();
            }
        };
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                setTargetInvalid();
            }
        };

        ArrayList<Component> gui_components = new ArrayList<>();
        if (dialog.getCheckboxes() != null) {
            gui_components.addAll(dialog.getCheckboxes());
        }
        if (dialog.getSliders() != null) {
            gui_components.addAll(dialog.getSliders());
        }
        if (dialog.getNumericFields() != null) {
            gui_components.addAll(dialog.getNumericFields());
        }
        for (Component item : gui_components) {
            item.addKeyListener(keyAdapter);
            item.addMouseListener(mouseAdapter);
        }

        int delay = 500; //milliseconds
        heartbeat = new Timer();
        heartbeat.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (my_target != null && registered_dialog != null) {
                    registered_dialog.setLocation(my_target.getWindow().getX() + my_target.getWindow().getWidth(), my_target.getWindow().getY() );
                }
            }
        }, delay, delay);
    }

    private String calibrationToText(Calibration calibration) {
        return "" + calibration.pixelWidth + " " + calibration.getXUnit() +
                " " + calibration.pixelHeight + " " + calibration.getYUnit() +
                " " + calibration.pixelDepth + " " + calibration.getZUnit();
    }


    @Override
    public void imageOpened(ImagePlus imp) {

    }

    @Override
    public void imageClosed(ImagePlus imp) {
        if (imp != null && (imp == my_source || imp == my_target)) {
            ImagePlus.removeImageListener(this);
            IncubatorPluginRegistry.getInstance().unregister(this);
            if (heartbeat != null) {
                heartbeat.cancel();
                heartbeat = null;
            }
            if (registered_dialog != null) {
                registered_dialog.dispose();
                registered_dialog = null;
            }
        }
    }

    @Override
    public void imageUpdated(ImagePlus imp) {
        if (paused) {
            return;
        }
        if (imp == my_source) {
            //if (sourceWasChanged() || parametersWereChanged()) {
            //    //System.out.println("Updating " + imp.getTitle());
            //    refresh();
            //}

            refreshView();
        }
    }
/*
    String stamp = "";
    protected boolean sourceWasChanged() {
        if (my_source.getT() != former_t || my_source.getC() != former_c) {
            //System.out.println(my_source.getTitle() + " t or c were changed");
            return true;
        }
        if (my_source.getStack() instanceof  CLIJxVirtualStack) {
            if (IncubatorUtilities.checkStamp(((CLIJxVirtualStack) my_source.getStack()).getBuffer(), stamp)) {
                return false;
            } else {
                //System.out.println(my_source.getTitle() + " changed stamp " + stamp);
            }
        }
        return true;
    }
*/
    protected void validateSource() {
        /*former_c = my_source.getC();
        former_t = my_source.getT();
        if (my_source.getStack() instanceof  CLIJxVirtualStack) {
            stamp = ((CLIJxVirtualStack) my_source.getStack()).getBuffer().getName();
        }*/
    }

    public void setTargetInvalid() {
        IncubatorPluginRegistry.getInstance().invalidate(my_target);
        setButtonColor(refreshText, invalid_color);
    }

    public void setTargetIsProcessing() {
        if (my_target.getStack() instanceof CLIJxVirtualStack) {
            IncubatorUtilities.stamp(((CLIJxVirtualStack) my_target.getStack()).getBuffer());
        }
        setButtonColor(refreshText, refreshing_color);
    }

    @Override
    public void setTargetValid() {
        setButtonColor(refreshText, valid_color);
    }

    private void setButtonColor(String button, Color color) {
        if (registered_dialog != null) {
            for (Button component : registered_dialog.getButtons()) {
                if (component != null) {
                    if (component.getLabel().compareTo(button) == 0) {
                        component.setBackground(color);
                    }
                }
            }
        }
    }
}
