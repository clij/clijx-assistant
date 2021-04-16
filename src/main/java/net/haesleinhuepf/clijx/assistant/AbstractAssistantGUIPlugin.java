package net.haesleinhuepf.clijx.assistant;

import fiji.util.gui.GenericDialogPlus;
import ij.*;
import ij.gui.*;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.Duplicator;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clij2.utilities.HasLicense;
import net.haesleinhuepf.clijx.assistant.annotation.AnnotationTool;
import net.haesleinhuepf.clijx.assistant.interactive.handcrafted.Crop2D;
import net.haesleinhuepf.clijx.assistant.interactive.handcrafted.Crop3D;
import net.haesleinhuepf.clijx.assistant.interactive.handcrafted.ExtractChannel;
import net.haesleinhuepf.clijx.assistant.optimize.*;
import net.haesleinhuepf.clijx.assistant.scriptgenerator.*;
import net.haesleinhuepf.clijx.assistant.services.*;
import net.haesleinhuepf.clijx.assistant.utilities.*;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.gui.MemoryDisplay;
import net.haesleinhuepf.clijx.plugins.VisualizeOutlinesOnOriginal;
import net.haesleinhuepf.clijx.utilities.AbstractCLIJxPlugin;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStack;
import net.haesleinhuepf.spimcat.io.CLIJxVirtualStackRegistry;
import org.scijava.util.VersionUtils;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.*;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.distributionName;
import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.*;

public abstract class AbstractAssistantGUIPlugin implements ImageListener, PlugIn, AssistantGUIPlugin {

    public final static String online_documentation_link = "https://clij.github.io/clij2-docs/reference";
    private final static String online_website_link = "https://clij.github.io/incubator";
    private final String doneText = "Done";
    private final String refreshText = "Refresh";
    private String helpText = "Action";

    final static Color REFRESHING_COLOR = new Color(205, 205, 128);
    final static Color INVALID_COLOR = new Color(205, 128, 128);
    final static Color VALID_COLOR = new Color(128, 205, 128);
    final static Color VALID_3D_COLOR = new Color(128, 205, 228);

    private Boolean input_output_sizes_equal = null;

    protected ImagePlus[] my_sources = null;

    protected ImagePlus my_target = null;

    private CLIJMacroPlugin plugin = null;
    protected Object[] args = null;

    boolean auto_contrast = true;
    boolean auto_lut = true;
    static boolean auto_position = true;
    public static boolean show_connections = false;
    public static boolean show_compatibility = false;
    public static boolean show_advanced = false;

    public AbstractAssistantGUIPlugin(CLIJMacroPlugin plugin) {
        CLIJxVirtualStackRegistry.getInstance();
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
        AssistantGUIPluginRegistry.getInstance().register(this);
        ImagePlus.addImageListener(this);
        IJ.showStatus("Running " + AssistantUtilities.niceNameWithoutDimShape(this.getName())  + " (" + distributionName(plugin.getClass()) + ")" + "...");
        refresh();
        IJ.showStatus("");

        GenericDialog dialog = buildNonModalDialog(my_target.getWindow());
        if (dialog != null) {
            registerDialogAsNoneModal(dialog);
        }
    }

    protected GenericDialog buildNonModalDialog(Frame parent) {
        GenericDialog gd = new GenericDialog(AssistantUtilities.niceNameWithoutDimShape(this.getName()) + " (" + distributionName(plugin.getClass()) + ")");
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
                    if (default_values != null && default_values.length > i) {
                        gd.addStringField(parameterName, (String) default_values[i], 30);
                    } else {
                        gd.addStringField(parameterName, "", 30);
                    }
                } else if (parameterType.compareTo("Boolean") == 0) {
                    if (default_values != null && default_values.length > i) {
                        gd.addCheckbox(parameterName, Boolean.valueOf("" + default_values[i]));
                    } else {
                        gd.addCheckbox(parameterName, true);
                    }
                } else { // Number
                    if (default_values != null && default_values.length > i) {
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
        button.setFocusable(false);
        panel.add(button);

    }

    protected ClearCLBuffer[] result = null;
    public synchronized void refresh()
    {
        if (plugin == null) {
            return;
        }

        System.out.println("Updating from " + Arrays.toString(my_sources));

        ClearCLBuffer[][] pushed = CLIJxVirtualStack.imagePlusesToBuffers(my_sources);

        String[] parameters = plugin.getParameterHelpText().split(",");


        Object[] default_values = null;
        if (plugin instanceof AbstractCLIJPlugin) {
            default_values = ((AbstractCLIJPlugin) plugin).getDefaultValues();
        }
        args = new Object[parameters.length];

        int boolean_count = 0;
        int number_count = 0;
        int string_count = 0;
        int result_index = -1;

        if (parameters.length > 0 && parameters[0].length() > 0) {
            // skip first two parameters because they are images
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
                    if (byRef || parameterName.contains("destination")) {
                        result_index = i;
                    } else {
                        args[i] = pushed[i][0]; // todo: potentially store the whole array here
                    }
                } else if (parameterType.compareTo("String") == 0) {
                    if (registered_dialog == null) {
                        if (default_values != null && default_values.length > i) {
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
                        if (default_values != null && default_values.length > i) {
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
                        if (default_values != null && default_values.length > i) {
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


        plugin.setArgs(args);
        checkResult();
        if (result == null) {
            result = createOutputBufferFromSource(pushed[0]);
        }
        args[result_index] = result[0]; // todo: potentially store the whole array here

        executeCL(pushed, new ClearCLBuffer[][]{result});
        cleanup(my_sources, pushed);

        setTarget(CLIJxVirtualStack.bufferToImagePlus(result));
        my_target.setTitle(AssistantUtilities.niceNameWithoutDimShape(this.getName()) + " of " + my_sources[0].getTitle());
        enhanceContrast();
    }

    protected void showTable() {
        if (result == null) {
            System.out.println("Show table: Error no result");
        }

        ResultsTable my_table = new ResultsTable();

        CLIJx clijx = CLIJx.getInstance();
        clijx.pullToResultsTable(result[0], my_table);
        my_table.show(my_target.getTitle() + " table");
    }

    protected void checkResult() {
        if (input_output_sizes_equal != null && input_output_sizes_equal && result != null) {
            long[] new_dimensions = null;
            ImagePlus source = my_sources[0];
            if (source.getNSlices() > 1) {
                new_dimensions = new long[]{source.getWidth(), source.getHeight(), source.getNSlices()};
            } else {
                new_dimensions = new long[]{source.getWidth(), source.getHeight()};
            }
            System.out.println("Size: " + Arrays.toString(new_dimensions));

            invalidateResultsIfDimensionsChanged(new_dimensions);

        }
    }

    protected void invalidateResultsIfDimensionsChanged(long[] new_dimensions) {
        if (result != null) {
            ClearCLBuffer first = result[0];
            long[] result_dimensions = first.getDimensions();


            boolean equal_dimensions = true; //result_dimensions.length == new_dimensions.length;
            if (equal_dimensions) {
                for (int d = 0; d < new_dimensions.length && d < result_dimensions.length; d++) {
                    System.out.println("" + new_dimensions[d] + " != " + result_dimensions[d]);
                    if (new_dimensions[d] != result_dimensions[d]) {
                        System.out.println("!");
                        equal_dimensions = false;
                        break;
                    }
                }
            }

            if (!equal_dimensions) {
                for (ClearCLBuffer buffer : result) {
                    buffer.close();
                }
                System.out.println("Make a new result");
                result = null;
                input_output_sizes_equal = null;
            }
        }
    }


    protected void cleanup(ImagePlus[] my_source, ClearCLBuffer[][] pushed) {
        for (int i = 0; i < my_source.length; i++) {
            cleanup(my_source[i], pushed[i]);
        }
    }

    protected void cleanup(ImagePlus my_source, ClearCLBuffer[] pushed) {
        if (!(my_source.getStack() instanceof CLIJxVirtualStack)) {
            for (int i = 0; i < pushed.length; i++) {
                pushed[i].close();
            }
        }
    }

    /*
    @Deprecated
    protected void executeCL(ClearCLBuffer[] whole_input, ClearCLBuffer[] whole_output) {
        if (plugin instanceof CLIJOpenCLProcessor) {
            if (my_sources[0].getNChannels() > 1) {
                int number_of_channels = my_sources[0].getNChannels();
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
    }*/


    protected void executeCL(ClearCLBuffer[][] whole_input, ClearCLBuffer[][] whole_output) {
        ClearCLBuffer[][] whole = new ClearCLBuffer[whole_output.length + whole_input.length][];
        for (int i = 0; i < whole_input.length; i ++) {
            whole[i] = whole_input[i];
        }
        for (int i = 0; i < whole_output.length; i ++) {
            whole[i + whole_input.length] = whole_output[i];
        }
        executeCL(whole);
    }

    protected void executeCL(ClearCLBuffer[][] whole) {
        if (plugin instanceof CLIJOpenCLProcessor) {
            if (my_sources[0].getNChannels() > 1) {
                int number_of_channels = my_sources[0].getNChannels();
                for (int c = 0; c < number_of_channels; c++) {
                    for (int i = 0; i < whole.length; i++) {
                        if (whole[i].length > c) {
                            args[i] = whole[i][c];
                        } else {
                            args[i] = whole[i][0];
                        }
                    }
                    if (plugin instanceof CLIJOpenCLProcessor) {
                        ((CLIJOpenCLProcessor) plugin).executeCL();
                    }
                }
                for (int i = 0; i < whole.length; i++) {
                    args[i] = whole[i][0];
                }
            } else {
                for (int i = 0; i < whole.length; i++) {
                    args[i] = whole[i][0];
                }
                ((CLIJOpenCLProcessor) plugin).executeCL();
            }
        }
    }


    protected ClearCLBuffer[] createOutputBufferFromSource(ClearCLBuffer[] pushed) {
        CLIJx clijx = CLIJx.getInstance();
        System.out.println("PUSHED[0]: " + pushed[0]);
        System.out.println("PUSHED[0] class: " + pushed[0].getClass());
        ClearCLBuffer result = plugin.createOutputBufferFromSource(pushed[0]);
        if (input_output_sizes_equal == null) {
            input_output_sizes_equal =
                    pushed[0].getWidth() == result.getWidth() &&
                    pushed[0].getHeight() == result.getHeight();

            if (input_output_sizes_equal && pushed[0].getDimensions() == result.getDimensions()) {
                input_output_sizes_equal =
                        pushed[0].getDepth() == result.getDepth();
            }
        }
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

    boolean myWasOKed;
    boolean myWasCancelled;
    private class MyGenericDialogPlus extends GenericDialogPlus {
        public MyGenericDialogPlus(String title) {
            super(title);
        }

        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            IJ.setKeyDown(keyCode);

            if (keyCode == 10 && this.textArea1 == null) {
                myWasOKed = true;
                myWasCancelled = false;
                this.dispose();
            } else if (keyCode == 27) {
                myWasOKed = false;
                myWasCancelled = true;
                this.dispose();
                IJ.resetEscape();
            } else if (keyCode == 87 && (e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0) {
                myWasOKed = false;
                myWasCancelled = true;
                this.dispose();
            }

        }

    }

    protected boolean configure() {
        if (my_sources != null) {
            return true;
        }
        String[] names_sources = getNamesOfSources();
        if (names_sources.length == 1) {
            setSources(new ImagePlus[]{IJ.getImage()});
        } else {
            MyGenericDialogPlus gd = new MyGenericDialogPlus(niceNameWithoutDimShape(this.getName()) + " (" + distributionName(plugin.getClass()) + ")");
            for (int s = 0; s < names_sources.length; s++) {
                gd.addImageChoice(names_sources[s], IJ.getImage().getTitle());
            }
            if (hasOnlineReference(plugin.getName())) {
                String function_name = pluginNameToFunctionName(plugin.getName());
                gd.addHelp("https://clij.github.io/clij2-docs/reference_" + function_name);
                gd.setHelpLabel("Help");
            }

            myWasCancelled = false;
            myWasOKed = false;
            gd.showDialog();
            if (gd.wasCanceled() || myWasCancelled || (!myWasOKed && !gd.wasOKed())) {
                return false;
            }

            ImagePlus[] images = new ImagePlus[names_sources.length];
            for (int s = 0; s < names_sources.length; s++) {
                images[s] = gd.getNextImage();
            }
            setSources(images);
        }
        return true;
    }

    public void refreshView() {
        if (paused)
        {
            System.out.println("Paused");
            return;
        }

        if (my_target == null || my_sources == null) {
            return;
        }
        if (my_sources[0].getNSlices() == my_target.getNSlices()) {
            if (my_sources[0].getZ() != my_target.getZ()) {
                System.out.println("Setting Z");
                my_target.setZ(my_sources[0].getZ());
            }
        }
        my_target.updateAndRepaintWindow();
    }


    public ImagePlus getSource(int source) {
        return my_sources[source];
    }
    public int getNumberOfSources() {
        if (my_sources != null) {
            return my_sources.length;
        } else {
            return getNamesOfSources().length;
        }
    }

    public String[] getNamesOfSources() {
        String[] parameters = plugin.getParameterHelpText().split(",");
        ArrayList<String> names = new ArrayList<String>();

        for (String parameter : parameters) {
            String[] parameterParts = parameter.trim().split(" ");
            String parameterType = parameterParts[0];
            String parameterName = parameterParts[1];
            boolean byRef = false;
            if (parameterType.compareTo("ByRef") == 0) {
                parameterType = parameterParts[1];
                parameterName = parameterParts[2];
                byRef = true;
            }
            if (parameterType.compareTo("Image") == 0) {
                if (!(parameterName.contains("destination") || byRef)) {
                    names.add(parameter);
                }
            }
        }

        String[] name_array = new String[names.size()];
        names.toArray(name_array);
        return name_array;
    }

    public void setSources(ImagePlus[] input) {
        my_sources = input;
        my_target = null;
    }

    public ImagePlus getTarget() {
        return my_target;
    }

    protected boolean paused = false;
    protected void setTarget(ImagePlus result) {
        paused = true;
        if (my_target == null) {
            if (my_sources[0] != null && my_sources[0].isComposite() && result.getNChannels() > 1) {
                System.out.println("Channels: " + result.getNChannels());
                my_target = new CompositeImage(result, my_sources[0].getCompositeMode());
                ((CompositeImage)my_target).copyLuts(my_sources[0]);
            } else {
                my_target = result;
            }
            if (my_sources[0].getStack() instanceof CLIJxVirtualStack && my_target.getStack() instanceof CLIJxVirtualStack) {
                ((CLIJxVirtualStack) my_target.getStack()).setProjectionStyle(((CLIJxVirtualStack) my_sources[0].getStack()).getProjectionStyle());
            }

            my_target.show();
            attachMenu(my_target);
            enhanceContrast();
            if (my_sources != null && my_sources[0].getWindow() != null) {
                my_target.getWindow().setLocation(
                        my_sources[0].getWindow().getX() + my_sources[0].getWindow().getWidth() / 4 * (4 - AssistantGUIPluginRegistry.getInstance().getFollowers(my_sources[0]).size()),
                        my_sources[0].getWindow().getY() + my_sources[0].getWindow().getHeight() / 4 * AssistantGUIPluginRegistry.getInstance().getFollowers(my_sources[0]).size()
                        );
            }

        } else {
            ImagePlus output;
            if (my_sources[0] != null && my_sources[0].isComposite() && result.getNChannels() > 1) {
                System.out.println("ChChannels: " + result.getNChannels());
                output = new CompositeImage(result, my_sources[0].getCompositeMode());
                //((CompositeImage)my_target).copyLuts(my_sources[0]);
            } else {
                output = result;
            }

            ImageWindow win = my_target.getWindow();

            ignore_closing = true;
            CLIJxVirtualStackRegistry.getInstance().unregister(my_target);
            if (my_target.getStack() instanceof CLIJxVirtualStack && output.getStack() instanceof CLIJxVirtualStack) {
                ((CLIJxVirtualStack) output.getStack()).setProjectionStyle(((CLIJxVirtualStack) my_target.getStack()).getProjectionStyle());
            }
            my_target.setStack(output.getStack(), output.getNChannels(), output.getNSlices(), output.getNFrames());
            CLIJxVirtualStackRegistry.getInstance().register(my_target);
            ignore_closing = false;

            if (win != my_target.getWindow()) {
                attachMenu(my_target);
            }
        }
        AssistantUtilities.attachCloseListener(my_target);
        AssistantUtilities.transferCalibration(my_sources[0], my_target);

        refreshLUT();
        paused = false;

        refreshView();
    }

    public void refreshLUT() {
        if (auto_lut) {
            if (plugin instanceof VisualizeOutlinesOnOriginal) {
                AssistantUtilities.hi(my_target);
            }
            String name_to_consider = (my_sources[0].getTitle() + " " + my_target.getTitle()).toLowerCase() + this.getName();
            if (name_to_consider.contains("map") || name_to_consider.contains("mesh") ) {
                AssistantUtilities.fire(my_target);
            } else if (name_to_consider.contains("label") && !name_to_consider.contains("ROI")) {
                AssistantUtilities.glasbey(my_target);
            } else {
                //my_target.setLut(my_source.getProcessor().getLut());
            }
        }
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
        canvas.addMouseListener(new MyMouseAdapter(imp, canvas));
        System.out.println("Menu attached");

        ImageWindow window = imp.getWindow();
        for (MouseListener listener : window.getMouseListeners()) {
            if (listener instanceof MyMouseAdapter) {
                window.removeMouseListener(listener);
            }
        }
        window.addMouseListener(new MyMouseAdapter(imp, window));
        System.out.println("Menu attached");
    }

    class MyMouseAdapter extends MouseAdapter {

        private ImagePlus imp;
        private Component component;

        MyMouseAdapter(ImagePlus imp, Component component) {
            this.imp = imp;
            this.component = component;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int toolID = Toolbar.getToolId();
            int flags = e.getModifiers();
            if (toolID != Toolbar.MAGNIFIER && (e.isPopupTrigger() || ( (flags & Event.META_MASK) != 0))) {
                AbstractAssistantGUIPlugin incplugin = ((AbstractAssistantGUIPlugin) AssistantGUIPluginRegistry.getInstance().getPlugin(imp));
                if (incplugin != null) {
                    incplugin.handlePopupMenu(e, component);
                }
                return;
            }

        }
    }

    protected void handlePopupMenu(MouseEvent e, Component component) {
        PopupMenu popupmenu = buildPopup(e);
        component.add(popupmenu);
        popupmenu.show(component, e.getX(), e.getY());
    }


    //Checkbox sync_view = null;
    protected PopupMenu buildPopup(MouseEvent e) {
        PopupMenu menu = new PopupMenu("CLIJx-Assistant");

        addMenuAction(menu, AssistantUtilities.niceNameWithoutDimShape(this.getName()) + " (" + distributionName(plugin.getClass()) + (show_compatibility?(", " + (getCompatibilityString(plugin.getName()))):"") + ", experimental)", (a) -> {
            if (registered_dialog != null) {
                registered_dialog.show();
            }
        });
        /*
        addMenuAction(menu, "Hide", (a) -> {
            my_target.getWindow().setVisible(false);
        });
        */
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
                    // was:  AssistantGUIPluginService.getInstance().getSuggestedNextStepsFor(this)

        HashMap<String, Class> suggestions = SuggestionService.getInstance().getIncubatorSuggestions(this);
        ArrayList<String> suggestedNames = new ArrayList<>();
        suggestedNames.addAll(suggestions.keySet());

        Collections.sort(suggestedNames, AssistantUtilities.niceNameComparator);

        for (String name : suggestedNames ) {
            //System.out.println("Suggested: " + name);
            Class klass = suggestions.get(name);

            CLIJMacroPlugin clijPlugin = CLIJMacroPluginService.getInstance().getService().getCLIJMacroPlugin(name);
            if (isSuitable(clijPlugin, this)) {
                Class pluginClass = clijPlugin.getClass();
                addMenuAction(suggestedFollowers, AssistantUtilities.niceName(name.replace("CLIJ2_", "").replace("CLIJx_", "")) + " (" + distributionName(pluginClass) + (show_compatibility?(", " + (getCompatibilityString(clijPlugin.getName()))):"") + ")", (a) -> {
                    my_target.show();
                    try {
                        AssistantGUIPlugin plugin = (AssistantGUIPlugin) klass.newInstance();
                        plugin.setCLIJMacroPlugin(CLIJMacroPluginService.getInstance().getService().getCLIJMacroPlugin(name));
                        plugin.run(null);
                    } catch (InstantiationException ex) {


                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }
        menu.add(suggestedFollowers);

        // -------------------------------------------------------------------------------------------------------------
        // Favorites
        loadFavorites();
        if (my_favorites.length() > 1) {
            Menu favoriteFollowers = new Menu("My favorites");

            ArrayList<String> favNames = new ArrayList<>();
            for (String fav : my_favorites.split(";")) {
                if (fav.length() > 0) {
                    favNames.add(fav);
                }
            }

            Collections.sort(favNames, AssistantUtilities.niceNameComparator);

            int invalid_count = 0;
            for (int i = 0; i < 2; i++) {
                for (String name : favNames) {
                    CLIJMacroPlugin clijPlugin = CLIJMacroPluginService.getInstance().getService().getCLIJMacroPlugin(name);
                    if (
                            (isSuitable(clijPlugin, this) && isReasonable(clijPlugin, this) && i == 0) ||
                            (((!isSuitable(clijPlugin, this)) || (!isReasonable(clijPlugin, this))) && i == 1)) {
                        Class pluginClass = clijPlugin.getClass();
                        addMenuAction(favoriteFollowers, AssistantUtilities.niceName(name.replace("CLIJ2_", "").replace("CLIJx_", "")) + " (" + distributionName(pluginClass) + (show_compatibility ? (", " + (getCompatibilityString(clijPlugin.getName()))) : "") + ")", (a) -> {
                            my_target.show();
                            System.out.println("Get plugin by name: " + name);
                            CLIJMacroPlugin macroPlugin = CLIJMacroPluginService.getInstance().getService().getCLIJMacroPlugin(name);
                            AssistantGUIPlugin plugin = AssistantGUIPluginService.getInstance().getIncubatorPluginFromCLIJ2Plugin(macroPlugin);
                            plugin.setCLIJMacroPlugin(macroPlugin);
                            plugin.run(null);
                        });
                    } else {
                        invalid_count++;
                    }
                }
                if (i == 0 && invalid_count > 0) {
                    favoriteFollowers.add("-");
                }
            }
            menu.add(favoriteFollowers);
        }

        menu.add("-");

        // -------------------------------------------------------------------------------------------------------------

        int category_count = 0;

        String[] categories = MenuService.getInstance().getCategories();
        for (String category : categories) {
            category_count ++;

            int menu_count = 0;
            Menu menuCategory = new Menu(AssistantUtilities.niceName(category).replace(">", " > " ));
            for (AssistantGUIPlugin plugin : MenuService.getInstance().getPluginsInCategory(category, this.getCLIJMacroPlugin())) {
                CLIJMacroPlugin clijPlugin = plugin.getCLIJMacroPlugin();
                if (category_count == categories.length || isSuitable(clijPlugin, this)) {
                    addMenuAction(menuCategory, AssistantUtilities.niceName(plugin.getName()) + " (" + distributionName(plugin.getCLIJMacroPlugin().getClass())  + (show_compatibility?(", " + (getCompatibilityString(clijPlugin.getName()))):"") + ")", (a) -> {
                        plugin.run("");
                    });
                    menu_count ++;
                }
            }
            if (menu_count > 0) {
                menu.add(menuCategory);
            }
        }
        menu.add("-");


        // -------------------------------------------------------------------------------------------------------------

        Menu script = new Menu("Generate script");
        addMenuAction(script, "ImageJ Macro", (a) -> {generateScriptFile(new MacroGenerator());});
        addMenuAction(script, "Human readable protocol", (a) -> {generateScriptFile(new HumanReadibleProtocolGenerator());});
        script.add("-");
        addMenuAction(script, "Icy JavaScript", (a) -> {generateScriptFile(new IcyJavaScriptGenerator());});
        addMenuAction(script, "ImageJ JavaScript", (a) -> {generateScriptFile(new JavaScriptGenerator());});
        addMenuAction(script, "Matlab", (a) -> {generateScriptFile(new MatlabGenerator());});
        addMenuAction(script, "Fiji Groovy", (a) -> {generateScriptFile(new GroovyGenerator());});
        addMenuAction(script, "Fiji Jython", (a) -> {generateScriptFile(new JythonGenerator());});
        script.add("-");

        addMenuAction(script, "Icy Protocol", (a) -> {generateScriptFile(new IcyProtocolGenerator());});
        addMenuAction(script, "ImageJ Macro Markdown", (a) -> {generateScriptFile(new MacroMarkdownGenerator());});
        addMenuAction(script, "Export workflow as groovy (for re-loading)", (a) -> {generateScriptFile(new AssistantGroovyGenerator());});
        addMenuAction(script, "CLIJx / Fiji plugin (Java)", (a) -> {new Thread(() -> {new MavenJavaProjectGeneratorPlugin().generate(this);}).start();});
        addMenuAction(script, "CLIJPy Python", (a) -> {generateScriptFile(new CLIJPyGenerator());});
        addMenuAction(script, "CluPath Groovy", (a) -> {generateScriptFile(new CluPathGroovyGenerator());});
        addMenuAction(script, "clEsperanto CLIc (C++)", (a) -> {generateScriptFile(new ClicGenerator());});
        addMenuAction(script, "clEsperanto ImageJ Macro", (a) -> {generateScriptFile(new ClEsperantoMacroGenerator());});
        addMenuAction(script, "clEsperanto Jython/Python", (a) -> {generateScriptFile(new ClEsperantoSnakeJythonGenerator());});
        addMenuAction(script, "clEsperanto Python", (a) -> {generateScriptFile(new PyclesperantoGenerator(false));});
        addMenuAction(script, "clEsperanto Python Jupyter Notebook", (a) -> {generateScriptFile(new PyclesperantoJupyterNotebookGenerator());});
        addMenuAction(script, "clEsperanto Python + Napari", (a) -> {generateScriptFile(new PyclesperantoGenerator(true));});
        menu.add(script);

        // -------------------------------------------------------------------------------------------------------------
        Menu more_actions = new Menu("More actions");
        addMoreActions(more_actions);
        if (more_actions.getItemCount() > 0) {
            menu.add(more_actions);
        }

        // -------------------------------------------------------------------------------------------------------------
        Menu info = new Menu("Info and options");
        // -------------------------------------------------------------------------------------------------------------

        Menu predecessor = new Menu("Predecessor" + (getNumberOfSources() > 1?"s":"") );
        for (int s = 0; s < getNumberOfSources(); s++) {
            final int s_ = s;
            addMenuAction(predecessor, AssistantUtilities.shortName(my_sources[s].getTitle()), (a) -> {
                my_sources[s_].show();
                my_sources[s_].getWindow().toFront();
                attachMenu(my_sources[s_]);
            });
        }
        info.add(predecessor);

        // -------------------------------------------------------------------------------------------------------------
        Menu followers = new Menu("Followers");
        try {
            for (ImagePlus follower : AssistantGUIPluginRegistry.getInstance().getFollowers(my_target)) {
                addMenuAction(followers, AssistantUtilities.shortName(follower.getTitle()), (a) -> {
                    follower.show();
                    follower.getWindow().toFront();
                    attachMenu(follower);
                });
            }
            info.add(followers);
        } catch (NullPointerException npe) {
            System.out.println(npe.toString());
        }
        // -------------------------------------------------------------------------------------------------------------
/*
        Menu graph = new Menu("Compute graph");
        ArrayList<Object[]> graphImages = AssistantGUIPluginRegistry.getInstance().getGraph(my_target);

        String presign = "\\";
        for (Object[] graphImage : graphImages) {
            String name = (String) graphImage[0];
            ImagePlus node = \(ImagePlus) graphImage[1];
            if (node == my_target) {
                name = " " + name;
                presign = "/";
            } else {
                name = presign + name;
            }
            addMenuAction(graph, AssistantUtilities.shortName(name), (a) -> {
                node.show();
                node.getWindow().toFront();
                attachMenu(node);
            });
        }
        info.add(graph);
*/
        // -------------------------------------------------------------------------------------------------------------

        Menu history = new Menu("Parameter history");

        for (String timestamp : storedParameterKeys) {
            addMenuAction(history, timestamp, (a) -> {
                restoreParameters(storedParameters.get(timestamp));
            });
        }

        info.add(history);
        info.add("-");

        if (my_favorites.contains(";" + getCLIJMacroPlugin().getName() + ";")) {// not in favorites yet
            MenuItem fav_item = new MenuItem("Remove from favorites");
            fav_item.addActionListener((a) -> {
                removeFromFavorites();
            });
            info.add(fav_item);
        } else {
            MenuItem fav_item = new MenuItem("Add to favorites");
            fav_item.addActionListener((a) -> {
                addToFavorites();
            });
            info.add(fav_item);
        }

        info.add("-");

        // -------------------------------------------------------------------------------------------------------------


        MenuItem auto_contrast_item = new MenuItem("Auto Brightness & Contrast: " + (auto_contrast?"ON":"OFF"));
        auto_contrast_item.addActionListener((a) -> {
            auto_contrast = !auto_contrast;
            enhanceContrast();
        });
        info.add(auto_contrast_item);

        MenuItem auto_lut_item = new MenuItem("Auto Look-up table: " + (auto_lut?"ON":"OFF"));
        auto_lut_item.addActionListener((a) -> {
            auto_lut = !auto_lut;
            enhanceContrast();
        });
        info.add(auto_lut_item);



        MenuItem auto_position_item = new MenuItem("Auto Window Position: " + (auto_position?"ON":"OFF"));
        auto_position_item.addActionListener((a) -> {
            auto_position = !auto_position;
        });
        info.add(auto_position_item);

        MenuItem show_connections_item = new MenuItem("Visualize connections: " + (show_connections?"ON":"OFF"));
        show_connections_item.addActionListener((a) -> {
            show_connections = !show_connections;
        });
        info.add(show_connections_item);

        MenuItem show_compatibility_item = new MenuItem("Show compatibility in menus: " + (show_compatibility?"ON":"OFF"));
        show_compatibility_item.addActionListener((a) -> {
            show_compatibility = !show_compatibility;
        });
        info.add(show_compatibility_item);

        MenuItem show_advanced_item = new MenuItem("Show advanced operations: " + (show_advanced?"ON":"OFF"));
        show_advanced_item.addActionListener((a) -> {
            show_advanced = !show_advanced;
        });
        info.add(show_advanced_item);


        info.add("-");


        // -------------------------------------------------------------------------------------------------------------

        addMenuAction(info,"GPU: " + CLIJx.getInstance().getGPUName(), (a) -> {
            IJ.log(CLIJx.clinfo());
        });

        addMenuAction(info,"Memory usage " + MemoryDisplay.getStatus(), (a) -> {
            new MemoryDisplay().run("");
            IJ.log(CLIJx.getInstance().reportMemory());
            IJ.log(CLIJxVirtualStackRegistry.getInstance().report());
        });
        if (my_target != null && my_target.getNSlices() > 1 && my_target.getStack() instanceof CLIJxVirtualStack) {
            Menu visualizationMenu = new Menu("Visualization");
            for (CLIJxVirtualStack.ProjectionStyle p : CLIJxVirtualStack.ProjectionStyle.all()) {
                String selected = "   ";
                if (((CLIJxVirtualStack) my_target.getStack()).getProjectionStyle() == p) {
                    selected = " - ";
                }
                addMenuAction(visualizationMenu, selected + p.toString(), (a) -> {
                    ((CLIJxVirtualStack) my_target.getStack()).setProjectionStyle(p);
                    setTargetInvalid();
                });
            }
            info.add("-");
            info.add(visualizationMenu);
        }

        addMenuAction(info,"Computation state", (a) -> {
            IJ.log(AssistantGUIPluginRegistry.getInstance().log());
        });

        menu.add(info);

        // -------------------------------------------------------------------------------------------------------------
        menu.add("-");

        addMenuAction(menu, "Duplicate and go ahead with ImageJ", (a) -> {
            new Duplicator().run(my_target, 1, my_target.getNChannels(), 1, my_target.getNSlices(),  1, my_target.getNFrames()).show();
        });

        if (show_advanced) {
            MenuItem show_result_as_table_item = new MenuItem("Show current slice as table");
            show_result_as_table_item.addActionListener((a) -> {
                showTable();
            });
            menu.add(show_result_as_table_item);
        }
        menu.add("-");

        //String documentation_link =
        //        ((plugin != null) ?online_documentation_link + "_" + plugin.getName().replace("CLIJ2_", "").replace("CLIJx_", ""):online_website_link);

        addMenuAction(menu,"Documentation for " + AssistantUtilities.niceNameWithoutDimShape(getName())  + " (" + distributionName(plugin.getClass()) + ")", (a) -> {
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

        if (AssistantUtilities.hasOnlineReference(plugin.getName())) {
            addMenuAction(menu,"Online help for " + AssistantUtilities.niceName(getName())  + " (" + distributionName(plugin.getClass()) + ")", (a) -> {
                AssistantUtilities.callOnlineReference(plugin.getName());
            });
        }

        return menu;
    }

    static String my_favorites = null;
    static final String FAVORITES_KEY = "assistant.favorites";
    protected static void loadFavorites() {
        if (my_favorites == null) {
            my_favorites = Prefs.get(FAVORITES_KEY, ";");
        }
    }
    protected static void saveFavorites() {
        if (my_favorites != null) {
            Prefs.set(FAVORITES_KEY, my_favorites);
            Prefs.savePreferences();
        }
    }

    protected void removeFromFavorites() {
        loadFavorites();
        my_favorites = my_favorites.replace(";" + getCLIJMacroPlugin().getName() + ";", ";");
        saveFavorites();
    }

    protected void addToFavorites() {
        loadFavorites();
        removeFromFavorites(); // to prevent duplicates
        my_favorites = my_favorites + getCLIJMacroPlugin().getName() + ";";
        saveFavorites();
    }

    protected void addMoreActions(Menu more_actions) {
        if (AssistantUtilities.resultIsBinaryImage(this)) {
            addMenuAction(more_actions, "Optimize parameters", (a) -> {
                optimize(new SimplexOptimizer(), new IJLogger(), false);
            });
            addMenuAction(more_actions, "Optimize parameters (gradient descent)", (a) -> {
                optimize(new GradientDescentOptimizer(), new IJLogger(), false);
            });
            more_actions.add("-");
            addMenuAction(more_actions, "Optimize parameters (simplex, configurable)", (a) -> {
                optimize(new SimplexOptimizer((int)IJ.getNumber( "Range",6 )), new IJLogger(), true);
            });
            addMenuAction(more_actions, "Optimize parameters (gradient descent, configurable)", (a) -> {
                optimize(new GradientDescentOptimizer((int)IJ.getNumber( "Range",6 )), new IJLogger(), true);
            });
        }

    }

    double[] minimum_display_intensities = null;
    double[] maximum_display_intensities = null;
    protected synchronized void enhanceContrast() {
        int c_before = my_target.getC();

        if (!auto_contrast) {
            if (minimum_display_intensities != null && maximum_display_intensities != null) {
                for (int c = 0; c < my_target.getNChannels(); c++) {
                    my_target.setC(c);
                    my_target.setDisplayRange(minimum_display_intensities[c], maximum_display_intensities[c]);
                }
                my_target.setC(c_before);
            }
            return;
        }


        paused = true;
        boolean binary = resultIsBinaryImage(this);
        boolean labelimage = resultIsLabelImage(this);

        minimum_display_intensities = new double[my_target.getNChannels()];
        maximum_display_intensities = new double[my_target.getNChannels()];
        for (int c = 0; c < my_target.getNChannels(); c++) {
            my_target.setC(c);
            if (plugin instanceof VisualizeOutlinesOnOriginal) {
                System.out.println("Set min-1 / max");
                IJ.setMinAndMax(my_target,CLIJx.getInstance().minimumOfAllPixels(result[0]) - 1, CLIJx.getInstance().maximumOfAllPixels(result[0]));
            } else if (labelimage) {
                System.out.println("Set 0 max");
                IJ.setMinAndMax(my_target,0, CLIJx.getInstance().maximumOfAllPixels(result[0]));
            } else if (binary) {
                System.out.println("Set 0 1");
                IJ.setMinAndMax(my_target, 0, 1);
            } else {
                System.out.println("Set Auto");
                IJ.resetMinAndMax(my_target);
                IJ.run(my_target, "Enhance Contrast", "saturated=0.35");
            }
            minimum_display_intensities[c] = my_target.getDisplayRangeMin();
            maximum_display_intensities[c] = my_target.getDisplayRangeMax();
        }

        my_target.setC(c_before);
        paused = false;
    }


    int script_count = 0;
    public void generateScriptFile(ScriptGenerator generator) {
        String script = generateScript(generator);

        script_count++;
        File outputTarget = new File(AssistantUtilities.getNewSelfDeletingTempDir() + "/new" + script_count + generator.fileEnding());

        try {
            FileWriter writer = new FileWriter(outputTarget);
            writer.write(script);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String file = outputTarget.getAbsolutePath();
        if (file.endsWith("ipynb")) {
            AssistantUtilities.openJupyterNotebook(file);
        } else if (file.endsWith(".protocol")) {
            AssistantUtilities.openIcyProtocol(file);
        } else {
            IJ.open(file);
        }
    }

    public String generateScript(ScriptGenerator generator) {
        return AssistantGUIPluginRegistry.getInstance().generateScript(generator);
    }


    Timer heartbeat = null;
    GenericDialog registered_dialog = null;
    protected void registerDialogAsNoneModal(GenericDialog dialog) {
        dialog.setModal(false);
        dialog.setOKLabel(refreshText);
        dialog.setCancelLabel(doneText);

        Menu menu = new Menu("temp");
        addMoreActions(menu);
        if (menu.getItemCount() > 0) {
            helpText = menu.getItem(0).getLabel();
            dialog.enableYesNoCancel(refreshText, helpText);
            //dialog.setHelpLabel(helpText);
            //dialog.addHelp("http://clij.github.io");
        }

        if (AssistantUtilities.hasOnlineReference(plugin.getName())) {
            Panel panel = new Panel();
            Button button = new Button("?");
            button.addActionListener((a)->{
                AssistantUtilities.callOnlineReference(plugin.getName());
            });
            panel.add(button);
            dialog.addPanel(panel);
            dialog.addToSameRow();
        }

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

        //setButtonColor(doneText, VALID_COLOR);
        setButtonColor(refreshText, VALID_COLOR);
        for (Button component : dialog.getButtons()) {
            if (component instanceof Button) {
                if (component.getLabel().compareTo(refreshText) == 0) {
                    for (ActionListener actionlistener : component.getActionListeners()) {
                        component.removeActionListener(actionlistener);
                    }
                    component.addActionListener((a) -> {
                        setTargetInvalid();
                    });
                } else if (component.getLabel().compareTo(helpText) == 0) {
                    for (ActionListener actionlistener : component.getActionListeners()) {
                        component.removeActionListener(actionlistener);
                    }
                    component.addActionListener(menu.getItem(0).getActionListeners()[0]);
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
        if (heartbeat == null) {
            return;
        }
        try {
            if (my_target != null && my_target.getWindow() != null && registered_dialog != null) {
                if (auto_position) {
                    registered_dialog.setLocation(my_target.getWindow().getX() + my_target.getWindow().getWidth() - 15, my_target.getWindow().getY());
                }
            }
            if (my_sources == null) {
                return;
            }
            if (my_target == null) {
                return;
            }

            ImageWindow targetWindow = my_target.getWindow();
            if (targetWindow == null) {
                return;
            }

            int source_x = 0;
            int source_y = 0;
            int num_sources_found = 0;

            for (ImagePlus source : my_sources) {
                ImageWindow sourceWindow = source.getWindow();
                if (sourceWindow != null) {
                    source_x += sourceWindow.getX();
                    source_y += sourceWindow.getY();
                    num_sources_found++;
                }
            }
            if (num_sources_found == 0) {
                return;
            }
            source_x = source_x / num_sources_found;
            source_y = source_y / num_sources_found;

            if (my_target == IJ.getImage() || !auto_position) {
                relativePositionToSourceX = targetWindow.getX() - source_x;
                relativePositionToSourceY = targetWindow.getY() - source_y;
            } else if (relativePositionToSourceX != null && relativePositionToSourceY != null) {
                int newPositionX = source_x + relativePositionToSourceX;
                int newPositionY = source_y + relativePositionToSourceY;

                if (Math.abs(newPositionX - targetWindow.getX()) > 1 && Math.abs(newPositionY - targetWindow.getY()) > 1) {
                    if (auto_position) {
                        targetWindow.setLocation(newPositionX, newPositionY);
                    }
                }
            }
            if (image_life_time < 1) {
                my_target.updateAndDraw();
            }
            image_life_time++;
        } catch (Exception e) {
            System.out.println("Exception in AbstractAsssistantGUIPlugin " + e);
            e.printStackTrace();
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

    @Deprecated // do not use this; it's a workaround because ImageJ closes windows when Composite stacks are replaced
    boolean ignore_closing = false;

    @Override
    public void imageClosed(ImagePlus imp) {
        if (imp != null && (imp == my_target)) {
            if (!ignore_closing) {

                ImagePlus.removeImageListener(this);
                AssistantGUIPluginRegistry.getInstance().unregister(this);
                if (heartbeat != null) {
                    Timer copy = heartbeat;
                    heartbeat = null;
                    copy.cancel();
                }

                if (registered_dialog != null) {
                    System.out.println("Dispose dialog 5");
                    registered_dialog.dispose();
                    registered_dialog = null;
                }
            }
        }
    }

    @Override
    public void imageUpdated(ImagePlus imp) {
        if (paused) {
            return;
        }
        if (my_sources != null) {
            for (ImagePlus source : my_sources) {
                if (imp == source) {
                    //IJ.log("Updating " + my_source);
                    //enhanceContrast();
                    refreshView();
                    return;
                }
            }
        }
    }

    public void setTargetInvalid() {
        AssistantGUIPluginRegistry.getInstance().invalidate(my_target);
        setButtonColor(refreshText, INVALID_COLOR);
    }

    public void setTargetIsProcessing() {
        if (my_target.getStack() instanceof CLIJxVirtualStack) {
            ((CLIJxVirtualStack) my_target.getStack()).getBuffer(0).setName(this.getClass().getName());
        }
        storeParameters();
        setButtonColor(refreshText, REFRESHING_COLOR);
    }

    int image_life_time = 0;
    @Override
    public void setTargetValid() {
        setButtonColor(refreshText, VALID_COLOR);
        image_life_time = 0;
    }

    private void setButtonColor(String button, Color color) {

        if (color == VALID_COLOR && my_target != null && my_target.getStack() != null && my_target.getStack() instanceof CLIJxVirtualStack && ((CLIJxVirtualStack) my_target.getStack()).getProjectionStyle() != CLIJxVirtualStack.ProjectionStyle.SINGLE_SLICE) {
            color = VALID_3D_COLOR;
        }


        if (registered_dialog != null) {
            for (Button component : registered_dialog.getButtons()) {
                if (component != null) {
                    if (component.getLabel().compareTo(button) == 0) {
                        component.setBackground(color);
                    }
                }
            }
        }
        if (button.compareTo(refreshText) == 0) {
            if (my_target != null) {
                ImageWindow win = my_target.getWindow();
                if (win != null) {
                    win.setBackground(color);
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

    public void optimize(Optimizer optimizer, Logger logger, boolean show_gui) {

        // -------------------------------------------------------------------------------------------------------------
        // determine ground truth
        RoiManager rm = RoiManager.getRoiManager();
        if (rm.getCount() == 0) {
            IJ.log("Please define reference ROIs in the ROI Manager.\n\n" +
                    "These ROIs should have names starting with 'p' for positive and 'n' for negative.\n\n" +
                            "The just activated annotation tool can help you with that.");
            Toolbar.addPlugInTool(new AnnotationTool());
            return;
        }

        logger.log("Optimize");
        logger.log("--------");
        logger.log("Optimizer: " + optimizer.getClass().getSimpleName());

        CLIJ2 clij2 = CLIJx.getInstance();
        logger.log("GPU: " + clij2.getGPUName() + " (OCLv: " + clij2.getOpenCLVersion() + ", AssistantV: " + VersionUtils.getVersion(optimizer.getClass()) + ")");

        ClearCLBuffer ground_truth = OptimizationUtilities.makeGroundTruth(clij2, my_target.getWidth(), my_target.getHeight(), my_target.getNSlices(), rm);
        //clij2.show(ground_truth, "ground");
        //new WaitForUserDialog("dd tr").show();
        ClearCLBuffer mask = clij2.create(ground_truth);
        clij2.greaterConstant(ground_truth, mask, 0);

        // -------------------------------------------------------------------------------------------------------------
        // determine workflow to optimize

        AssistantGUIPlugin[] path = AssistantGUIPluginRegistry.getInstance().getPathToRoot(this);
        System.out.println("Path: " + Arrays.toString(path));

        CLIJMacroPlugin[] plugins = OptimizationUtilities.getCLIJMacroPluginsFromIncubatorPlugins(path);
        Object[][] parameters = OptimizationUtilities.getParameterArraysFromIncubatorPlugins(path);

        Workflow workflow = new Workflow(plugins, parameters);
        logger.log(workflow.toString());

        System.out.println(Arrays.toString(workflow.getNumericParameterNames()));
        System.out.println(Arrays.toString(workflow.getPluginIndices()));
        System.out.println(Arrays.toString(workflow.getParameterIndices()));




        int[] parameter_index_map = OptimizationUtilities.getParameterIndexMap(workflow, show_gui);
        if (parameter_index_map == null) {
            System.out.println("Optimization cancelled");
            return;
        }
        System.out.println("Index map: " + Arrays.toString(parameter_index_map));
        logger.log("Index map: " + Arrays.toString(parameter_index_map));


        BinaryImageFitnessFunction f = new BinaryImageFitnessFunction(clij2, workflow,
                parameter_index_map,
                ground_truth,
                mask
        );

        double[] current = f.getCurrent();
        System.out.println("Initial: " + Arrays.toString(current));

        //current = Optimizers.optimizeSimplex(current, workflow, parameter_index_map, f);
        current = optimizer.optimize(current, workflow, parameter_index_map, f, logger);

        logger.log("Optimization done.");
        System.out.println("Optimum: ");
        f.value(current);
        for (AssistantGUIPlugin plugin : path ) {
            if (!(plugin instanceof Crop3D || plugin instanceof Crop2D)) {
                plugin.refreshDialogFromArguments();
            }
        }
        path[0].setTargetInvalid();
        logger.log("Bye.");

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

    private HashMap<String, ParameterContainer> storedParameters = new HashMap<String, ParameterContainer>();
    private ArrayList<String> storedParameterKeys = new ArrayList<>();
    private void storeParameters() {
        String key = AssistantUtilities.now();

        AssistantGUIPlugin[] path = AssistantGUIPluginRegistry.getInstance().getPathToRoot(this);
        Object[][] parameters = OptimizationUtilities.getParameterArraysFromIncubatorPlugins(path);

        ParameterContainer container = new ParameterContainer(parameters);

        if (storedParameterKeys.size() > 0) {
            // check if last stored entries are identical with current
            ParameterContainer formerContainer = storedParameters.get(storedParameterKeys.get(0));
            if (formerContainer.equals(container)) {
                return;
            }
        }

        storedParameters.put(key, container);
        storedParameterKeys.add(0, key);
    }

    private void restoreParameters(ParameterContainer container) {
        AssistantGUIPlugin[] path = AssistantGUIPluginRegistry.getInstance().getPathToRoot(this);
        Object[][] parameters = OptimizationUtilities.getParameterArraysFromIncubatorPlugins(path);

        container.copyTo(parameters);

        for (AssistantGUIPlugin plugin : path ) {
            plugin.refreshDialogFromArguments();
        }
        path[0].setTargetInvalid();
    }

    public static AssistantGUIPlugin getPluginFromTargetImage(ImagePlus imp) {
        return AssistantGUIPluginRegistry.getInstance().getPlugin(imp);
    }

    public Workflow getWorkflow() {
        AssistantGUIPlugin[] path = AssistantGUIPluginRegistry.getInstance().getPathToRoot(this);
        System.out.println("Path: " + Arrays.toString(path));

        CLIJMacroPlugin[] plugins = OptimizationUtilities.getCLIJMacroPluginsFromIncubatorPlugins(path);
        Object[][] parameters = OptimizationUtilities.getParameterArraysFromIncubatorPlugins(path);

        return new Workflow(plugins, parameters);
    }

    public static boolean isAutoPosition() {
        return auto_position;
    }

    public static void setAutoPosition(boolean auto_position) {
        AbstractAssistantGUIPlugin.auto_position = auto_position;
    }
}
