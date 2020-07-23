package net.haesleinhuepf.clincubator;

import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Toolbar;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import net.haesleinhuepf.clij.utilities.CLInfo;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.haesleinhuepf.clincubator.scriptgenerator.PyclesperantoGenerator;
import net.haesleinhuepf.clincubator.utilities.*;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.gui.MemoryDisplay;
import net.haesleinhuepf.clijx.utilities.AbstractCLIJxPlugin;
import net.haesleinhuepf.clincubator.scriptgenerator.MacroGenerator;
import net.haesleinhuepf.clincubator.scriptgenerator.ScriptGenerator;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractIncubatorPlugin implements ImageListener, PlugIn, SuggestedPlugin, IncubatorPlugin {

    private final static String online_documentation_link = "https://clij.github.io/clij2-docs/reference";
    private final String doneText = "Done";
    private final String refreshText = "Refresh";

    private final Color refreshing_color = new Color(205, 205, 128);
    private final Color invalid_color = new Color(205, 128, 128);
    private final Color valid_color = new Color(128, 205, 128);


    protected ImagePlus my_source = null;

    protected ImagePlus my_target = null;

    private AbstractCLIJPlugin plugin = null;
    protected Object[] args = null;

    public AbstractIncubatorPlugin(){}

    public AbstractIncubatorPlugin(AbstractCLIJPlugin plugin) {
        this.plugin = plugin;
        if (plugin instanceof AbstractCLIJPlugin) {
            ((AbstractCLIJPlugin) plugin).setClij(CLIJx.getInstance().getCLIJ());
        }

        if (plugin instanceof AbstractCLIJ2Plugin) {
            ((AbstractCLIJ2Plugin) plugin).setCLIJ2(CLIJx.getInstance());
        } else if (plugin instanceof AbstractCLIJxPlugin) {
            //((AbstractCLIJxPlugin) plugin).setCLIJx(CLIJx.getInstance());
        }
    }

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
        }
    }

    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = new GenericDialog(IncubatorUtilities.niceName(this.getClass().getSimpleName()));
        if (plugin == null) {
            return gd;
        }
        Object[] default_values = plugin.getDefaultValues();

        String[] parameters = plugin.getParameterHelpText().split(",");
        if (parameters.length > 0 && parameters[0].length() > 0) {
            for (int i = 0; i < parameters.length; i++) {
                String[] parameterParts = parameters[i].trim().split(" ");
                String parameterType = parameterParts[0];
                String parameterName = parameterParts[1];
                boolean byRef = false;
                if (parameterType.compareTo("ByRef") == 0) {
                    parameterType = parameterParts[1];
                    parameterName = parameterParts[2];
                    byRef = true;
                }
                if (parameterType.compareTo("Image") == 0) {
                    // no choice
                } else if (parameterType.compareTo("String") == 0) {
                    if (default_values != null) {
                        gd.addStringField(parameterName, (String) default_values[i], 2);
                    } else {
                        gd.addStringField(parameterName, "");
                    }
                } else if (parameterType.compareTo("Boolean") == 0) {
                    if (default_values != null) {
                        gd.addCheckbox(parameterName, Boolean.valueOf("" + default_values[i]));
                    } else {
                        gd.addCheckbox(parameterName, true);
                    }
                } else { // Number
                    if (default_values != null) {
                        gd.addNumericField(parameterName, Double.valueOf("" + default_values[i]), 2);
                    } else {
                        gd.addNumericField(parameterName, 2, 2);
                    }
                }
            }
        }
        return gd;
    }

    ClearCLBuffer result = null;
    public synchronized void refresh()
    {
        if (plugin == null) {
            return;
        }

        ClearCLBuffer pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);

        String[] parameters = plugin.getParameterHelpText().split(",");


        Object[] default_values = plugin.getDefaultValues();
        args = new Object[parameters.length];

        int boolean_count = 0;
        int number_count = 0;
        int string_count = 0;

        if (parameters.length > 0 && parameters[0].length() > 0) {
            // skip first two parameters because they are images
            for (int i = 2; i < parameters.length; i++) {
                String[] parameterParts = parameters[i].trim().split(" ");
                String parameterType = parameterParts[0];
                String parameterName = parameterParts[1];
                boolean byRef = false;
                if (parameterType.compareTo("ByRef") == 0) {
                    parameterType = parameterParts[1];
                    parameterName = parameterParts[2];
                    byRef = true;
                }

                if (parameterType.compareTo("Image") == 0) {
                    // no choice
                } else if (parameterType.compareTo("String") == 0) {
                    if (registered_dialog == null) {
                        if (default_values != null) {
                            args[i] = default_values[i];
                        } else {
                            args[i] = "";
                        }
                    } else {
                        args[i] = ((TextField)registered_dialog.getStringFields().get(string_count)).getText();
                        string_count++;
                    }
                } else if (parameterType.compareTo("Boolean") == 0) {
                    if (registered_dialog == null) {
                        if (default_values != null) {
                            args[i] = (boolean) default_values[i] ? 1 : 0;
                        } else {
                            args[i] = 0;
                        }
                    } else {
                        boolean value = ((Checkbox)registered_dialog.getCheckboxes().get(boolean_count)).getState();
                        boolean_count ++;
                        args[i] = value ? 1.0 : 0.0;
                    }
                } else { // Number
                    if (registered_dialog == null) {
                        if (default_values != null) {
                            args[i] = default_values[i];
                        } else {
                            args[i] = 2;
                        }
                    } else {
                        try {
                            args[i] = Double.parseDouble(((TextField)registered_dialog.getNumericFields().get(number_count)).getText());
                        } catch (NumberFormatException e) {
                            return;
                        }
                        number_count++;
                    }
                }
            }
        }

        args[0] = pushed;
        plugin.setArgs(args);
        if (result == null) {
            result = plugin.createOutputBufferFromSource(pushed);

        }
        args[1] = result;
        plugin.setArgs(args); // might not be necessary

        if (plugin instanceof CLIJOpenCLProcessor) {
            ((CLIJOpenCLProcessor) plugin).executeCL();
        }
        pushed.close();

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle(IncubatorUtilities.niceName(this.getClass().getSimpleName()) + " of " + my_source.getTitle());
        if (this.getClass().getSimpleName().toLowerCase().contains("label")) {
            IncubatorUtilities.glasbey(my_target);
            my_target.setDisplayRange(0, CLIJx.getInstance().maximumOfAllPixels(result));
        } else if (this.getClass().getSimpleName().toLowerCase().contains("binary") ||
                this.getClass().getSimpleName().toLowerCase().contains("threshold") ||
                (plugin instanceof IsCategorized && (((IsCategorized)plugin).getCategories().toLowerCase().contains("segmentation") || ((IsCategorized)plugin).getCategories().toLowerCase().contains("binary")))
        ) {
            my_target.setDisplayRange(0, 1);
        }
    }

    protected boolean configure() {
        setSource(IJ.getImage());
        return true;
    }

    public void refreshView() {
        if (my_target == null || my_source == null) {
            return;
        }
        if (my_source.getNSlices() == my_target.getNSlices()) {
            my_target.setZ(my_source.getZ());
        }
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
        paused = false;
        IncubatorUtilities.transferCalibration(my_source, my_target);
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

        addMenuAction(menu, this.getClass().getSimpleName(), (a) -> {
            if (registered_dialog != null) {
                registered_dialog.show();
            }
        });
        menu.add("-");

        // -------------------------------------------------------------------------------------------------------------

        Menu suggestedFollowers = new Menu("Suggested next steps");
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

        int category_count = 0;
        for (String category : MenuOrganiser.getCategories()) {
            category_count ++;

            int menu_count = 0;
            Menu moreOptions = new Menu(category_count + " " + IncubatorUtilities.niceName(category));
            for (IncubatorPlugin plugin : MenuOrganiser.getPluginsInCategory(category)) {
                addMenuAction(moreOptions, IncubatorUtilities.niceName(plugin.getClass().getSimpleName()), (a) -> {
                    plugin.run("");
                });
                menu_count ++;
            }
            if (menu_count > 0) {
                menu.add(moreOptions);
            }
        }
        menu.add("-");

        // -------------------------------------------------------------------------------------------------------------

        Menu script = new Menu("Generate script");

        addMenuAction(script, "ImageJ Macro", (a) -> {generateScript(new MacroGenerator());});
        addMenuAction(script, "clEsperanto Python", (a) -> {generateScript(new PyclesperantoGenerator());});

        menu.add(script);


        // -------------------------------------------------------------------------------------------------------------

        Menu info = new Menu("Info");
        addMenuAction(info, "Source: " + my_source.getTitle(), (a) -> {
            System.out.println("huhu source");
            my_source.show();});
        addMenuAction(info, "Target: " + my_target.getTitle(), (a) -> {my_target.show();});
        menu.add(info);

        addMenuAction(info,"GPU: " + CLIJx.getInstance().getGPUName(), (a) -> {
            IJ.log(CLIJx.clinfo());
        });

        addMenuAction(info,"Memory usage " + MemoryDisplay.getStatus(), (a) -> {
            new MemoryDisplay().run("");
            IJ.log(CLIJx.getInstance().reportMemory());
        });

        menu.add("-");




        String documentation_link = online_documentation_link +
                ((plugin != null) ?"_" + plugin.getName().replace("CLIJ2_", "").replace("CLIJx_", ""):"");

        addMenuAction(menu,"Online documentation", (a) -> {
            try {
                Desktop.getDesktop().browse(new URI(documentation_link));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e2) {
                e2.printStackTrace();
            }
        });
        return menu;
    }

    protected void generateScript(ScriptGenerator generator) {
        String script = generator.header() +
                IncubatorPluginRegistry.getInstance().generateScript(generator);

        File outputTarget = new File(System.getProperty("java.io.tmpdir") + "new" + generator.fileEnding());

        try {
            FileWriter writer = new FileWriter(outputTarget);
            writer.write(script);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        IJ.open(outputTarget.getAbsolutePath());
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
                    // this is to prevent the dialog from closing
                    // todo: check if this is necessary
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
            refreshView();
        }
    }

    public void setTargetInvalid() {
        IncubatorPluginRegistry.getInstance().invalidate(my_target);
        setButtonColor(refreshText, invalid_color);
    }

    public void setTargetIsProcessing() {
        if (my_target.getStack() instanceof CLIJxVirtualStack) {
            ((CLIJxVirtualStack) my_target.getStack()).getBuffer().setName(this.getClass().getName());
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

    public AbstractCLIJPlugin getCLIJMacroPlugin() {
        return plugin;
    }

    public Object[] getArgs() {
        return args;
    }
}
