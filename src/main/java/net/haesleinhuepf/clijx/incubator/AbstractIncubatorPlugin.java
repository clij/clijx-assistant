package net.haesleinhuepf.clijx.incubator;

import ij.CompositeImage;
import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.*;
import ij.measure.Calibration;
import ij.plugin.Duplicator;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clij2.utilities.HasLicense;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.Crop;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.ExtractChannel;
import net.haesleinhuepf.clijx.incubator.optimize.AnnotationTool;
import net.haesleinhuepf.clijx.incubator.optimize.BinaryImageFitnessFunction;
import net.haesleinhuepf.clijx.incubator.optimize.OptimizationUtilities;
import net.haesleinhuepf.clijx.incubator.optimize.Workflow;
import net.haesleinhuepf.clijx.incubator.scriptgenerator.*;
import net.haesleinhuepf.clijx.incubator.services.CLIJMacroPluginService;
import net.haesleinhuepf.clijx.incubator.utilities.*;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.gui.MemoryDisplay;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.services.MenuService;
import net.haesleinhuepf.clijx.incubator.services.SuggestionService;
import net.haesleinhuepf.clijx.utilities.AbstractCLIJxPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities.parmeterNameToStepSizeSuggestion;

public abstract class AbstractIncubatorPlugin implements ImageListener, PlugIn, IncubatorPlugin {

    public final static String online_documentation_link = "https://clij.github.io/clij2-docs/reference";
    private final static String online_website_link = "https://clij.github.io/incubator";
    private final String doneText = "Done";
    private final String refreshText = "Refresh";

    private final Color refreshing_color = new Color(205, 205, 128);
    private final Color invalid_color = new Color(205, 128, 128);
    private final Color valid_color = new Color(128, 205, 128);


    protected ImagePlus my_source = null;

    protected ImagePlus my_target = null;

    private CLIJMacroPlugin plugin = null;
    protected Object[] args = null;

    boolean auto_contrast = true;
    static boolean auto_position = true;

    public AbstractIncubatorPlugin(CLIJMacroPlugin plugin) {
        setCLIJMacroPlugin(plugin);
    }

    public void setCLIJMacroPlugin(CLIJMacroPlugin plugin) {
        this.plugin = plugin;

        if (plugin != null) {
            plugin.setClij(CLIJx.getInstance().getCLIJ());

            if (plugin instanceof AbstractCLIJ2Plugin) {
                ((AbstractCLIJ2Plugin) plugin).setCLIJ2(CLIJx.getInstance());
            } else if (plugin instanceof AbstractCLIJxPlugin) {
                //((AbstractCLIJxPlugin) plugin).setCLIJx(CLIJx.getInstance());
            }
        }
    }

    @Override
    public void run(String arg) {
        if (!configure()) {
            return;
        }
        IncubatorPluginRegistry.getInstance().register(this);
        ImagePlus.addImageListener(this);
        IJ.showStatus("Running " + IncubatorUtilities.niceName(this.getName()) + "...");
        refresh();
        IJ.showStatus("");

        GenericDialog dialog = buildNonModalDialog(my_target.getWindow());
        if (dialog != null) {
            registerDialogAsNoneModal(dialog);
        }
    }

    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = new GenericDialog(IncubatorUtilities.niceName(this.getName()));
        if (plugin == null) {
            return gd;
        }
        Object[] default_values = null;
        if (plugin instanceof AbstractCLIJPlugin) {
            default_values = ((AbstractCLIJPlugin) plugin).getDefaultValues();
        }
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
                    addPlusMinusPanel(gd, parameterName);

                    

                }
            }
        }
        return gd;
    }

    protected void addPlusMinusPanel(GenericDialog gd, String parameterName) {
        int element = gd.getNumericFields().size() - 1;
        double small_delta = parmeterNameToStepSizeSuggestion(parameterName, true);
        double large_delta = parmeterNameToStepSizeSuggestion(parameterName, false);

        Panel panel = new Panel();
        addPlusMinusButton(panel, gd, element, -large_delta, "<<");
        addPlusMinusButton(panel, gd, element, -small_delta, "<");
        addPlusMinusButton(panel, gd, element, small_delta, ">");
        addPlusMinusButton(panel, gd, element, large_delta, ">>");

        gd.addToSameRow();
        gd.addPanel(panel);
    }

    private void addPlusMinusButton(Panel panel,GenericDialog gd, int element, double delta, String label) {

        Button button = new Button(label);
        button.addActionListener((a) -> {
            TextField t = ((TextField) gd.getNumericFields().get(element));
            try {
                double new_value = Double.parseDouble(t.getText()) + delta;
                t.setText("" + new_value);
                setTargetInvalid();
            } catch (Exception e) {
            }
        });
        panel.add(button);

    }

    ClearCLBuffer[] result = null;
    public synchronized void refresh()
    {
        if (plugin == null) {
            return;
        }
        System.out.println("Updating " + my_source);

        ClearCLBuffer[] pushed = CLIJxVirtualStack.imagePlusToBuffer(my_source);

        String[] parameters = plugin.getParameterHelpText().split(",");


        Object[] default_values = null;
        if (plugin instanceof AbstractCLIJPlugin) {
            default_values = ((AbstractCLIJPlugin) plugin).getDefaultValues();
        }
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
                            args[i] = new Double ((boolean) default_values[i] ? 1.0 : 0.0);
                        } else {
                            args[i] = new Double (0);
                        }
                    } else {
                        boolean value = ((Checkbox)registered_dialog.getCheckboxes().get(boolean_count)).getState();
                        boolean_count ++;
                        args[i] = new Double (value ? 1.0 : 0.0);
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

        args[0] = pushed[0]; // todo: potentially store the whole array here
        plugin.setArgs(args);
        if (result == null) {
            result = createOutputBufferFromSource(pushed);

        }
        args[1] = result[0]; // todo: potentially store the whole array here

        executeCL(pushed, result);
        cleanup(my_source, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle(IncubatorUtilities.niceName(this.getName()) + " of " + my_source.getTitle());
        if (this.getName().toLowerCase().contains("label")) {
            my_target.setDisplayRange(0, CLIJx.getInstance().maximumOfAllPixels(result[0]));
        } else if (this.getName().toLowerCase().contains("binary") ||
                this.getName().toLowerCase().contains("threshold") ||
                (plugin instanceof IsCategorized && (((IsCategorized)plugin).getCategories().toLowerCase().contains("segmentation") || ((IsCategorized)plugin).getCategories().toLowerCase().contains("binary")))
        ) {
            my_target.setDisplayRange(0, 1);
        } else {

            enhanceContrast();
        }
    }

    protected void cleanup(ImagePlus my_source, ClearCLBuffer[] pushed) {
        if (!(my_source.getStack() instanceof CLIJxVirtualStack)) {
            for (int i = 0; i < pushed.length; i++) {
                pushed[i].close();
            }
        }
    }

    protected void executeCL(ClearCLBuffer[] whole_input, ClearCLBuffer[] whole_output) {
        if (plugin instanceof CLIJOpenCLProcessor) {
            if (my_source.getNChannels() > 1) {
                int number_of_channels = my_source.getNChannels();
                for (int c = 0; c < number_of_channels; c++) {
                    ClearCLBuffer input = whole_input[c];
                    ClearCLBuffer output = whole_output[c];

                    args[0] = input;
                    args[1] = output;

                    if (plugin instanceof CLIJOpenCLProcessor) {
                        ((CLIJOpenCLProcessor) plugin).executeCL();
                    }
                }

                args[0] = whole_input[0];
                args[1] = whole_output[0];

            } else {
                ((CLIJOpenCLProcessor) plugin).executeCL();
            }
        }
    }

    protected ClearCLBuffer[] createOutputBufferFromSource(ClearCLBuffer[] pushed) {
        CLIJx clijx = CLIJx.getInstance();
        ClearCLBuffer result = plugin.createOutputBufferFromSource(pushed[0]);
        if (pushed.length > 1) {
            ClearCLBuffer[] output = new ClearCLBuffer[pushed.length];
            output[0] = result;
            for (int i = 1; i < pushed.length; i ++) {
                output[i] = clijx.create(output[0]);
            }
            return output;
        } else {
            return new ClearCLBuffer[] {result};
        }
    }

    protected boolean configure() {
        setSource(IJ.getImage());
        return true;
    }

    public void refreshView() {
        if (paused)
        {
            System.out.println("Paused");
            return;
        }

        if (my_target == null || my_source == null) {
            return;
        }
        if (my_source.getNSlices() == my_target.getNSlices()) {
            if (my_source.getZ() != my_target.getZ()) {
                System.out.println("Setting Z");
                my_target.setZ(my_source.getZ());
            }
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

    protected boolean paused = false;
    protected void setTarget(ImagePlus result) {
        paused = true;
        if (my_target == null) {
            if (my_source != null && my_source.isComposite() && result.getNChannels() > 1) {
                System.out.println("Channels: " + result.getNChannels());
                my_target = new CompositeImage(result, my_source.getCompositeMode());
                ((CompositeImage)my_target).copyLuts(my_source);
            } else {
                my_target = result;
            }

            my_target.show();
            attachMenu(my_target);
            enhanceContrast();
        } else {
            ImagePlus output = result;
            my_target.setStack(output.getStack());
        }
        IncubatorUtilities.transferCalibration(my_source, my_target);
        String name_to_consider = (my_source.getTitle() + " " + my_target.getTitle()).toLowerCase() + this.getName();

        if (name_to_consider.contains("map") || name_to_consider.contains("mesh") ) {
            IncubatorUtilities.fire(my_target);
        } else if (name_to_consider.contains("label") && !name_to_consider.contains("ROI")) {
            IncubatorUtilities.glasbey(my_target);
        } else {
            //my_target.setLut(my_source.getProcessor().getLut());
        }
        paused = false;

        refreshView();
    }

    private void attachMenu(ImagePlus imp) {
        System.out.println("Attach menu");
        ImageCanvas canvas = imp.getWindow().getCanvas();
        canvas.disablePopupMenu(true);
        for (MouseListener listener : canvas.getMouseListeners()) {
            if (listener instanceof MyMouseAdapter) {
                canvas.removeMouseListener(listener);
            }
        }
        canvas.addMouseListener(new MyMouseAdapter(imp));
        System.out.println("Menu attached");
    }

    class MyMouseAdapter extends MouseAdapter {

        private ImagePlus imp;

        MyMouseAdapter(ImagePlus imp) {
            this.imp = imp;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int toolID = Toolbar.getToolId();
            int flags = e.getModifiers();
            if (toolID != Toolbar.MAGNIFIER && (e.isPopupTrigger() || ( (flags & Event.META_MASK) != 0))) {
                AbstractIncubatorPlugin incplugin = ((AbstractIncubatorPlugin) IncubatorPluginRegistry.getInstance().getPlugin(imp));
                if (incplugin != null) {
                    incplugin.handlePopupMenu(e);
                }
                return;
            }

        }
    }

    protected void handlePopupMenu(MouseEvent e) {
        PopupMenu popupmenu = buildPopup(e, my_source, my_target);
        my_target.getWindow().getCanvas().add(popupmenu);
        popupmenu.show(my_target.getWindow().getCanvas(), e.getX(), e.getY());
    }

    private void addMenuAction(Menu menu, String label, ActionListener listener) {
        MenuItem submenu = new MenuItem(label);
        if (listener != null) {
            submenu.addActionListener(listener);
        }
        menu.add(submenu);
    }

    //Checkbox sync_view = null;
    protected PopupMenu buildPopup(MouseEvent e, ImagePlus my_source, ImagePlus my_target) {
        PopupMenu menu = new PopupMenu("CLIncubator");

        addMenuAction(menu, "CLIJx " + IncubatorUtilities.niceName(this.getName()) + " (experimental)", (a) -> {
            if (registered_dialog != null) {
                registered_dialog.show();
            }
        });
        addMenuAction(menu, "Hide", (a) -> {
            my_target.getWindow().setVisible(false);
        });
        menu.add("-");

        // -------------------------------------------------------------------------------------------------------------

        Menu suggestedFollowers = new Menu("Suggested next steps");
        if (my_target.getNChannels() > 1) {
            addMenuAction(suggestedFollowers, "Extract channel", (a) -> {
                my_target.show();
                new ExtractChannel().run(null);
            });
            addMenuAction(suggestedFollowers, "-", null);
        }
                    // was:  IncubatorPluginService.getInstance().getSuggestedNextStepsFor(this)

        HashMap<String, Class> suggestions = SuggestionService.getInstance().getIncubatorSuggestions(this);
        ArrayList<String> suggestedNames = new ArrayList<>();
        suggestedNames.addAll(suggestions.keySet());

        Collections.sort(suggestedNames);

        for (String name : suggestedNames ) {
            Class klass = suggestions.get(name);
            addMenuAction(suggestedFollowers, IncubatorUtilities.niceName(name.replace("CLIJ2_", "").replace("CLIJx_", "")), (a) -> {
                my_target.show();
                try {
                    IncubatorPlugin plugin = (IncubatorPlugin) klass.newInstance();
                    plugin.setCLIJMacroPlugin(CLIJMacroPluginService.getInstance().getService().getCLIJMacroPlugin(name));
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
        for (String category : MenuService.getInstance().getCategories()) {
            category_count ++;

            int menu_count = 0;
            Menu moreOptions = new Menu(category_count + " " + IncubatorUtilities.niceName(category));
            for (IncubatorPlugin plugin : MenuService.getInstance().getPluginsInCategory(category)) {
                addMenuAction(moreOptions, IncubatorUtilities.niceName(plugin.getName()), (a) -> {
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
        script.add("-");
        addMenuAction(script, "Icy Javascript", (a) -> {generateScript(new IcyJavaScriptGenerator());});
        addMenuAction(script, "Matlab", (a) -> {generateScript(new MatlabGenerator());});
        addMenuAction(script, "ImageJ Groovy", (a) -> {generateScript(new GroovyGenerator());});
        addMenuAction(script, "ImageJ JavaScript", (a) -> {generateScript(new JavaScriptGenerator());});
        addMenuAction(script, "ImageJ Jython", (a) -> {generateScript(new JythonGenerator());});
        script.add("-");
        addMenuAction(script, "Human readable protocol", (a) -> {generateScript(new HumanReadibleProtocolGenerator());});
        addMenuAction(script, "clEsperanto Python", (a) -> {generateScript(new PyclesperantoGenerator(false));});
        addMenuAction(script, "clEsperanto Python + Napari", (a) -> {generateScript(new PyclesperantoGenerator(true));});
        menu.add(script);

        // -------------------------------------------------------------------------------------------------------------
        Menu more_actions = new Menu("More actions");
        if (IncubatorUtilities.resultIsBinaryImage(this)) {
            addMenuAction(more_actions, "Optimize parameters (auto)", (a) -> {
                optimize(false);
            });
            addMenuAction(more_actions, "Optimize parameters (config)", (a) -> {
                optimize(true);
            });
            more_actions.add("-");
        }

        menu.add(more_actions);

        // -------------------------------------------------------------------------------------------------------------
        Menu info = new Menu("Info");
        // -------------------------------------------------------------------------------------------------------------

        Menu predecessor = new Menu("Predecessor");
        addMenuAction(predecessor, IncubatorUtilities.shortName(my_source.getTitle()), (a) -> {
            my_source.show();
            my_source.getWindow().toFront();
            attachMenu(my_source);
        });
        info.add(predecessor);

        // -------------------------------------------------------------------------------------------------------------
        Menu followers = new Menu("Followers");
        for (ImagePlus follower : IncubatorPluginRegistry.getInstance().getFollowers(my_target)) {
            addMenuAction(followers, IncubatorUtilities.shortName(follower.getTitle()), (a) -> {
                follower.show();
                follower.getWindow().toFront();
                attachMenu(follower);
            });
        }
        info.add(followers);
        // -------------------------------------------------------------------------------------------------------------

        Menu graph = new Menu("Compute graph");
        ArrayList<Object[]> graphImages = IncubatorPluginRegistry.getInstance().getGraph(my_target);

        String presign = "\\";
        for (Object[] graphImage : graphImages) {
            String name = (String) graphImage[0];
            ImagePlus node = (ImagePlus) graphImage[1];
            if (node == my_target) {
                name = " " + name;
                presign = "/";
            } else {
                name = presign + name;
            }
            addMenuAction(graph, IncubatorUtilities.shortName(name), (a) -> {
                node.show();
                node.getWindow().toFront();
                attachMenu(node);
            });
        }
        info.add(graph);
        info.add("-");

        // -------------------------------------------------------------------------------------------------------------

        addMenuAction(info,"GPU: " + CLIJx.getInstance().getGPUName(), (a) -> {
            IJ.log(CLIJx.clinfo());
        });

        addMenuAction(info,"Memory usage " + MemoryDisplay.getStatus(), (a) -> {
            new MemoryDisplay().run("");
            IJ.log(CLIJx.getInstance().reportMemory());
        });

        // -------------------------------------------------------------------------------------------------------------
        menu.add("-");

        MenuItem auto_contrast_item = new MenuItem("Auto Brightness & Contrast: " + (auto_contrast?"ON":"OFF"));
        auto_contrast_item.addActionListener((a) -> {
            auto_contrast = !auto_contrast;
            enhanceContrast();
        });
        menu.add(auto_contrast_item);

        MenuItem auto_position_item = new MenuItem("Auto Window Position: " + (auto_position?"ON":"OFF"));
        auto_position_item.addActionListener((a) -> {
            auto_position = !auto_position;
        });
        menu.add(auto_position_item);

        addMenuAction(menu, "Duplicate and go ahead with ImageJ", (a) -> {
            new Duplicator().run(my_target, 1, my_target.getNSlices()).show();
        });

        menu.add("-");

        //String documentation_link =
        //        ((plugin != null) ?online_documentation_link + "_" + plugin.getName().replace("CLIJ2_", "").replace("CLIJx_", ""):online_website_link);

        addMenuAction(menu,"Documentation for " + IncubatorUtilities.niceName(getName()), (a) -> {
            String documentation = "";
            if (plugin instanceof HasAuthor) {
                documentation = documentation + "By" + ((HasAuthor) plugin).getAuthorName() + "\n";
            }

            if (plugin instanceof OffersDocumentation) {
                documentation = documentation + ((OffersDocumentation) plugin).getDescription() + "\n";
            }

            documentation = documentation + "Parameters: " + plugin.getParameterHelpText() + "\n";
            if (plugin instanceof OffersDocumentation) {
                documentation = documentation + "Supported dimensions: " + ((OffersDocumentation) plugin).getAvailableForDimensions() + "\n";
            }

            if (plugin instanceof HasLicense) {
                documentation = documentation + "License: " + ((HasLicense) plugin).getLicense() + "\n";
            }

            IJ.log("Documentation for " + getName() + "\n\n" + documentation);

            /*try {
                Desktop.getDesktop().browse(new URI(documentation_link));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e2) {
                e2.printStackTrace();
            }*/
        });
        return menu;
    }


    protected synchronized void enhanceContrast() {
        if (!auto_contrast) {
            return;
        }

        paused = true;
        int c_before = my_target.getC();
        for (int c = 0; c < my_target.getNChannels(); c++) {
            my_target.setC(c);
            IJ.resetMinAndMax(my_target);
            IJ.run(my_target, "Enhance Contrast", "saturated=0.35");
        }
        my_target.setC(c_before);
        paused = false;
    }

    protected void generateScript(ScriptGenerator generator) {
        String script = generator.header() +
                IncubatorPluginRegistry.getInstance().generateScript(generator);

        File outputTarget = new File(System.getProperty("java.io.tmpdir") + "/new" + generator.fileEnding());

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
        if (dialog.getNumericFields() == null && dialog.getCheckboxes() == null && dialog.getChoices() == null) {
            dialog.setVisible(false);
        }

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
        if (dialog.getChoices() != null) {
            gui_components.addAll(dialog.getChoices());
        }
        for (Component item : gui_components) {
            item.addKeyListener(keyAdapter);
            item.addMouseListener(mouseAdapter);
        }

        int delay = 100; //milliseconds
        heartbeat = new Timer();
        heartbeat.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                reposition();
            }
        }, delay, delay);
    }

    private Integer relativePositionToSourceX = 0;
    private Integer relativePositionToSourceY = 0;
    private void reposition() {
        if (!auto_position) {
            return;
        }

        if (my_target != null && registered_dialog != null) {
            registered_dialog.setLocation(my_target.getWindow().getX() + my_target.getWindow().getWidth() - 15, my_target.getWindow().getY() );
        }
        if (my_source == null) {
            return;
        }
        ImageWindow sourceWindow = my_source.getWindow();
        if (sourceWindow == null) {
            return;
        }
        if (my_target == null) {
            return;
        }
        ImageWindow targetWindow = my_target.getWindow();
        if (targetWindow == null) {
            return;
        }

        if (my_target == IJ.getImage()) {
            relativePositionToSourceX = targetWindow.getX() - sourceWindow.getX();
            relativePositionToSourceY = targetWindow.getY() - sourceWindow.getY();
        } else if (relativePositionToSourceX != null && relativePositionToSourceY != null){
            int newPositionX = sourceWindow.getX() + relativePositionToSourceX;
            int newPositionY = sourceWindow.getY() + relativePositionToSourceY;

            if (Math.abs(newPositionX - targetWindow.getX()) > 1 && Math.abs(newPositionY - targetWindow.getY()) > 1) {
                targetWindow.setLocation(newPositionX, newPositionY);
            }
        }
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
            //IJ.log("Updating " + my_source);
            //enhanceContrast();
            refreshView();
        }
    }

    public void setTargetInvalid() {
        IncubatorPluginRegistry.getInstance().invalidate(my_target);
        setButtonColor(refreshText, invalid_color);
    }

    public void setTargetIsProcessing() {
        if (my_target.getStack() instanceof CLIJxVirtualStack) {
            ((CLIJxVirtualStack) my_target.getStack()).getBuffer(0).setName(this.getClass().getName());
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

    public CLIJMacroPlugin getCLIJMacroPlugin() {
        return plugin;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public boolean canManage(CLIJMacroPlugin plugin) {
        if (this.plugin == null) {
            return false;
        } else {
            return this.plugin.getClass() == plugin.getClass();
        }
    }

    public String getName() {
        return plugin.getName().replace("CLIJ2_", "").replace("CLIJx_", "");
    }

    public void optimize(boolean show_gui) {
        CLIJ2 clij2 = CLIJx.getInstance();

        // -------------------------------------------------------------------------------------------------------------
        // determine ground truth
        RoiManager rm = RoiManager.getRoiManager();
        if (rm.getCount() == 0) {
            IJ.log("Please define reference ROIs in the ROI Manager.\nThese ROIs should have names starting with 'p' for positive and 'n' for negative.");
            Toolbar.addPlugInTool(new AnnotationTool());
            return;
        }
        ClearCLBuffer ground_truth = OptimizationUtilities.makeGroundTruth(clij2, my_target.getWidth(), my_target.getHeight(), my_target.getNSlices(), rm);
        //clij2.show(ground_truth, "ground");
        //new WaitForUserDialog("dd tr").show();
        ClearCLBuffer mask = clij2.create(ground_truth);
        clij2.greaterConstant(ground_truth, mask, 0);

        // -------------------------------------------------------------------------------------------------------------
        // determine workflow to optimize

        IncubatorPlugin[] path = IncubatorPluginRegistry.getInstance().getPathToRoot(this);
        System.out.println("Path: " + Arrays.toString(path));

        CLIJMacroPlugin[] plugins = OptimizationUtilities.getCLIJMacroPluginsFromIncubatorPlugins(path);
        Object[][] parameters = OptimizationUtilities.getParameterArraysFromIncubatorPlugins(path);

        Workflow workflow = new Workflow(plugins, parameters);

        System.out.println(Arrays.toString(workflow.getNumericParameterNames()));
        System.out.println(Arrays.toString(workflow.getPluginIndices()));
        System.out.println(Arrays.toString(workflow.getParameterIndices()));




        int[] parameter_index_map = OptimizationUtilities.getParameterIndexMap(workflow, show_gui);
        if (parameter_index_map == null) {
            System.out.println("Optimization cancelled");
            return;
        }
        System.out.println("Index map: " + Arrays.toString(parameter_index_map));


        BinaryImageFitnessFunction f = new BinaryImageFitnessFunction(clij2, workflow,
                parameter_index_map,
                ground_truth,
                mask
        );

        SimplexOptimizer optimizer = new SimplexOptimizer(-1, 1e-5);

        double[] current = f.getCurrent();
        System.out.println("Initial: " + Arrays.toString(current));

        int iterations = 6;
        for (int i = 0; i < iterations; i++) {

            NelderMeadSimplex simplex = OptimizationUtilities.makeOptimizer(f.getNumDimensions(), workflow.getNumericParameterNames(), parameter_index_map, Math.pow(2, iterations / 2 - i - 1));
            //double[] lowerBounds = new double[simplex.getDimension()];
            //double[] upperBounds = new double[simplex.getDimension()];
            //for (int b = 0; b < upperBounds.length; b++) {
            //    upperBounds[b] = Double.MAX_VALUE;
            //}
            //, new SimpleBounds(lowerBounds, upperBounds)
            PointValuePair solution = optimizer.optimize(new MaxEval(1000), new InitialGuess(current), simplex, new ObjectiveFunction(f), GoalType.MINIMIZE);

            current = solution.getKey();
            System.out.println("Intermediate optimum: " + Arrays.toString(current));
        }

        System.out.println("Optimum: ");
        f.value(current);
        for (IncubatorPlugin plugin : path ) {
            plugin.refreshDialogFromArguments();
        }
        path[0].setTargetInvalid();

        //UnivariatePointValuePair next =  new UnivariatePointValuePair(solution.getPointRef()[0], solution.getValue());

        ground_truth.close();
        mask.close();
    }

    public void refreshDialogFromArguments() {
        if (registered_dialog == null) {
            return;
        }

        Vector numericFields = registered_dialog.getNumericFields();
        String[] parameterHelpTexts = plugin.getParameterHelpText().split(",");

        int count = 0;
        int parameter_count = 0;
        for (String parameterHelpText : parameterHelpTexts) {
            while (parameterHelpText.contains("  ")) {
                parameterHelpText = parameterHelpText.replace("  ", "");
            }
            String[] temp = parameterHelpText.split(" ");
            if (temp[temp.length - 2].compareTo("Number") == 0) {
                ((TextField)(numericFields.get(count))).setText("" + args[parameter_count]);
                count++;
                //result[count] = parameter_count;
            }
            parameter_count++;
        }
        registered_dialog.invalidate();
    }
}
