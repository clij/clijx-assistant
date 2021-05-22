package net.haesleinhuepf.clijx.assistant.services;

import ij.IJ;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.plugins.BinaryNot;
import net.haesleinhuepf.clij2.plugins.GaussianBlur2D;
import net.haesleinhuepf.clij2.plugins.GenerateTouchMatrix;
import net.haesleinhuepf.clij2.utilities.HasClassifiedInputOutput;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities.*;

public class MenuService {
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> names_and_tags = new ArrayList<>();

    HashMap<String, Comparator<HasClassifiedInputOutput>> categories = new HashMap<>();
    ArrayList<String> category_names = new ArrayList();
    String[] category_names_array;

    private void addCategory(String name, Comparator<HasClassifiedInputOutput>  comparator) {
        categories.put(name, comparator);
        category_names.add(name);
    }

    private MenuService() {
        addCategory("Filter (noise removal)", (a,b) -> {
            return b instanceof IsCategorized && ((IsCategorized) b).isInCategory("filter") && ((IsCategorized) b).isInCategory("noise") &&
                    a.getOutputType().equals("Image") &&
                    a.getInputType().equals("Image") &&
                    b.getOutputType().equals("Image")?1:0;
        });
        addCategory("Filter (background removal)", (a,b) -> {
            return b instanceof IsCategorized && ((IsCategorized) b).isInCategory("filter") && ((IsCategorized) b).isInCategory("background") &&
                    a.getOutputType().equals("Image") &&
                    a.getInputType().equals("Image") &&
                    b.getOutputType().equals("Image")?1:0;
        });
        addCategory("Filter (other)", (a,b) -> {
            return b instanceof IsCategorized && ((IsCategorized) b).isInCategory("Filter") &&
                    (!((IsCategorized) b).isInCategory("noise")) &&
                    (!((IsCategorized) b).isInCategory("background")) &&
                    (b instanceof CLIJMacroPlugin && !((CLIJMacroPlugin) b).getName().endsWith("Map")) &&
                    a.getOutputType().equals("Image") &&
                    a.getInputType().equals("Image") &&
                    b.getOutputType().equals("Image")?1:0;
        });
        addCategory("Math", (a,b) -> {
            return b instanceof IsCategorized && ((IsCategorized) b).isInCategory("Math") &&
                    a.getOutputType().equals("Image")&&
                    b.getOutputType().equals("Image")?1:0;
        });
        addCategory("Transform", (a,b) -> {
            return b instanceof IsCategorized && ((IsCategorized) b).isInCategory("Transform")?1:0;
        });
        addCategory("Projection", (a,b) -> {
            return b instanceof IsCategorized && ((IsCategorized) b).isInCategory("Project")?1:0;
        });
        addCategory("Binarize (auto threshold)", (a,b) -> {
            return  b.getClass().getSimpleName().contains("Threshold") &&
                    (!a.getOutputType().contains("Binary Image")) &&
                    b.getInputType().contains(a.getOutputType()) &&
                    (!b.getInputType().contains("Binary Image")) &&
                    b.getOutputType().equals("Binary Image")?1:0;
        });
        addCategory("Binarize", (a,b) -> {
            return  (!b.getClass().getSimpleName().contains("Threshold")) &&
                    (!a.getOutputType().contains("Binary Image")) &&
                    b.getInputType().contains(a.getOutputType()) &&
                    (!b.getInputType().contains("Binary Image")) &&
                    b.getOutputType().equals("Binary Image")?1:0;
        });
        addCategory("Binary processing", (a,b) -> {
            return a.getOutputType().contains("Binary Image") &&
                    b.getInputType().contains("Binary Image") &&
                    b.getOutputType().equals("Binary Image")?1:0;
        });
        addCategory("Label", (a,b) -> {
            return b.getInputType().contains(a.getOutputType()) &&
                    (!a.getOutputType().contains("Label Image")) &&
                    (!b.getInputType().contains("Label Image")) &&
                    b.getOutputType().equals("Label Image")?1:0;
        });
        addCategory("Label processing", (a,b) -> {
            return a.getOutputType().contains("Label Image") &&
                    b.getInputType().contains("Label Image")&&
                    b.getOutputType().equals("Label Image")?1:0;
        });
        addCategory("Label measurements", (a,b) -> {
            return a.getOutputType().contains("Label Image") &&
                    b.getInputType().contains("Label Image") &&
                    b.getOutputType().equals("Image") &&
                    b instanceof IsCategorized &&
                    (!((IsCategorized) b).isInCategory("Graph")) &&
                    ((IsCategorized) b).isInCategory("Measurement")
                    ?1:0;
        });
        addCategory("Label neighbor filters", (a,b) -> {
            return a.getOutputType().contains("Label Image") &&
                    b.getInputType().contains("Label Image") &&
                    b.getOutputType().equals("Image") &&
                    b instanceof IsCategorized &&
                    ((IsCategorized) b).isInCategory("Graph") &&
                    ((IsCategorized) b).isInCategory("Measurement") &&
                    ((IsCategorized) b).isInCategory("Neighbor-Filter")
                    ?1:0;
        });
        addCategory("Label neighbor graph based measurements", (a,b) -> {
            return a.getOutputType().contains("Label Image") &&
                    b.getInputType().contains("Label Image") &&
                    b.getOutputType().equals("Image") &&
                    b instanceof IsCategorized &&
                    ((IsCategorized) b).isInCategory("Graph") &&
                    ((IsCategorized) b).isInCategory("Measurement") &&
                    (!((IsCategorized) b).isInCategory("Neighbor-Filter"))
                    ?1:0;
        });
        addCategory("Vector and matrix processing", (a,b) -> {
            return AbstractAssistantGUIPlugin.show_advanced && b.getInputType().contains(a.getOutputType()) &&
                    ((a.getOutputType().contains("Vector") || a.getOutputType().contains("Matrix") || a.getOutputType().contains("Pointlist")) ||
                    (b.getOutputType().contains("Vector") || b.getOutputType().contains("Matrix") || b.getOutputType().contains("Pointlist")))
                    ?1:0;
        });

        addCategory("All", (a,b) -> 1);

        category_names_array = new String[category_names.size()];
        category_names.toArray(category_names_array);


        AssistantGUIPluginService assistantGUIPluginService = AssistantGUIPluginService.getInstance();
        net.haesleinhuepf.clij.macro.CLIJMacroPluginService clijMacroPluginService = CLIJMacroPluginService.getInstance().getService();

        AbstractAssistantGUIPlugin.show_advanced = true;

        for (String name : clijMacroPluginService.getCLIJMethodNames()) {
            Class incubatorPluginClass = assistantGUIPluginService.getIncubatorPluginClassFromCLIJ2Plugin(clijMacroPluginService.getCLIJMacroPlugin(name));
            if (incubatorPluginClass != null) {
                names.add(name);
                CLIJMacroPlugin plugin = clijMacroPluginService.getCLIJMacroPlugin(name);
                String tags = "";
                if (plugin instanceof IsCategorized) {
                    tags = ((IsCategorized) plugin).getCategories();
                }
                if (plugin.getClass().getPackage().toString().contains(".clij2.") || AssistantUtilities.CLIJxAssistantInstalled()) {
                    names_and_tags.add(name + "," + tags + "," + getCompatibilityString(name));
                }
            }
        }

        AbstractAssistantGUIPlugin.show_advanced = false;

        Collections.sort(names, AssistantUtilities.niceNameComparator);
        //for (String name : names) {
        //    System.out.println(niceName(name));
        //}
    }

    private static MenuService instance = null;

    public synchronized static MenuService getInstance() {
        if (instance == null) {
            instance = new MenuService();
        }
        return instance;
    }

    public AssistantGUIPlugin getPluginByName(String name) {
        net.haesleinhuepf.clij.macro.CLIJMacroPluginService service = CLIJMacroPluginService.getInstance().getService();

        CLIJMacroPlugin plugin = service.getCLIJMacroPlugin(name);
        return getPluginByCLIJPlugin(plugin);
    }

    public AssistantGUIPlugin getPluginByCLIJPlugin(CLIJMacroPlugin plugin) {
        AssistantGUIPluginService assistantGUIPluginService = AssistantGUIPluginService.getInstance();
        try {
            Class incubatorPlugin = assistantGUIPluginService.getIncubatorPluginClassFromCLIJ2Plugin(plugin);
            if (incubatorPlugin == null) {
                return null;
            }
            AssistantGUIPlugin newPlugin = (AssistantGUIPlugin)(incubatorPlugin.newInstance());
            newPlugin.setCLIJMacroPlugin(plugin);
            return newPlugin;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getNames() {
        String[] output = new String[names.size()];
        names.toArray(output);
        return output;
    }
    public String[] getNamesAndTags() {
        String[] output = new String[names_and_tags.size()];
        names_and_tags.toArray(output);
        return output;
    }

    //final static String ALL_STRING = "All";


    public ArrayList<AssistantGUIPlugin> getPluginsInCategory(String search_string, CLIJMacroPlugin plugin) {
        net.haesleinhuepf.clij.macro.CLIJMacroPluginService service = CLIJMacroPluginService.getInstance().getService();

        ArrayList<AssistantGUIPlugin> result = new ArrayList<>();

        Comparator<HasClassifiedInputOutput> comparator = categories.get(search_string);

        //if (search_string.compareTo(ALL_STRING) == 0) {
        ///    search_string = "";
        //}
        boolean all = (search_string.compareTo("All") == 0);

        for (String entry : getNames()) {
            //String name = entry;
            //String description = "";
            //String categories = "";
            //String available_for_dimensions = "";
            //String parameter_help_text = "";

            CLIJMacroPlugin macroPlugin = service.getCLIJMacroPlugin(entry);
            if (macroPlugin == null) {
                System.out.println("Warning: CLIJ Plugin " + entry + " might not be installed properly. Try to reinstall it.");
                continue;
            }
            //System.out.println(entry);

            if (all || (
                    plugin instanceof HasClassifiedInputOutput &&
                            macroPlugin instanceof HasClassifiedInputOutput &&
                comparator.compare((HasClassifiedInputOutput)plugin, (HasClassifiedInputOutput)macroPlugin) == 1)) {
                AssistantGUIPlugin pluginByCLIJPlugin = getPluginByCLIJPlugin(macroPlugin);
                if (pluginByCLIJPlugin != null) {
                    result.add(pluginByCLIJPlugin);
                }
            }
//            if ( AssistantUtilities.isIncubatablePlugin(macroPlugin)) {
//                if (macroPlugin instanceof OffersDocumentation) {
//                    description = ((OffersDocumentation) macroPlugin).getDescription();
//                    available_for_dimensions = ((OffersDocumentation) macroPlugin).getAvailableForDimensions();
//                }
//                if (macroPlugin instanceof IsCategorized) {
//                    categories = ((IsCategorized) macroPlugin).getCategories();
//                }
//                parameter_help_text = macroPlugin.getParameterHelpText();
//
//                if (isInCategory(name, macroPlugin.getClass().getName(), description, parameter_help_text, available_for_dimensions, categories, search_string)) {
//                    result.add(getPluginByCLIJPlugin(macroPlugin));
//                }
//            }
        }

        return result;
    }

    /*
    public static boolean isInCategory(String name, String class_name, String description, String parameter_help_text, String available_for_dimensions, String categories, String search_string) {
        if (search_string.length() == 0) {
            return true;
        }


        search_string = search_string.toLowerCase();

        //String major = search_string.split(">")[0];
        String search_in = categories.toLowerCase();
        for (String search_for : search_string.split(">")) {
            search_for = search_for.split(" ")[0]; // ignore "removal"

            if (!search_in.contains(search_for)) {
                return false;
            }
        }
        return true;
      */  /*


        String minor = "";
        if (search_string.contains(">")) {
            minor = search_string.split(">")[1];
        }
        String any = "";
        String any_other = "";
        String none = "";

        String all_other = major;
        if (major.contains("filter")) {
            all_other = "";
            any = "filter,math";
        } else if (major.contains("transform")) {
            none = "label spots";
        }

        // todo: put the respective information into the plugins
        if (minor.contains("noise")) {
            any_other = any_other + ",mean,median,gaussian blur,bilateral,nonlocal mean,difference of gaussian";
            none = " mean average ,equalize,correction,watershed";
        } else if (minor.contains("background")) {
            any = "";
            any_other = any_other + ",laplacianofgaussian,differenceofgaussian,tophat,subtractgaussianbackground";
        } else if (minor.contains("edges")) {
            any_other = any_other + ",laplace,sobel,edge,laplacian of gaussian,difference of gaussian";
        } else if (major.contains("filter")) {
            none = "threshold,labels";
        } else if (major.contains("binary")) {
            none = "drift,find,label,connected,pull,invert";
        } else if (major.contains("transform")) {
            none = "labelspots";
        } else if (major.contains("label") && minor.contains("segmentation")){
            any_other = any_other + ",find,detect,connected";
            none = "extend,exclude,edges,merge";
        } else if (major.contains("label") && minor.contains("proces")){
            any_other = any_other + ",extend,exclude,edges,merge";
        } else if (major.contains("label") && minor.contains("measurement")){
            none = "connected,extend,exclude,merge,edges,find,detect,centroidsofbackground,mask,labeltomask,voronoi";
        } else if (minor.contains("mesh")){
            all_other = all_other + ",draw,mesh";
        } else if (minor.contains("map")){
            all_other = all_other + ",draw,map";
        } else if (minor.contains("measurements")) {
            none = "exclude,extend";
        }

        if (description == null) {
            description = "";
        }
        if (parameter_help_text == null) {
            parameter_help_text = "";
        }
        if (available_for_dimensions == null) {
            available_for_dimensions = "";
        }
        if (categories == null) {
            categories = "";
        }

        String search_in = (
                name + "\n" +
                description + "\n" +
                parameter_help_text + "\n" +
                available_for_dimensions.toLowerCase() + "\n" +
                categories + "\n" +
                class_name).toLowerCase();

        if (none.length() > 0) {
            if (containsAny(search_in, none.split(","))) {
                return false;
            }
        }
        if (any.length() > 0) {
            if (!containsAny(search_in, any.split(","))) {
                return false;
            }
        }
        if (!containsAll(search_in, all_other.split(","))) {
            return false;
        }
        if (any_other.length() > 0) {
            if (!containsAny(search_in, any_other.split(","))) {
                return false;
            }
        }

        return true;
        */
    /*}*/

    /*
    private static boolean containsAll(String search_in, String[] search_for) {
        for (String search : search_for) {
            if (search.length() > 0) {
                if (!search_in.contains(search)) {
                    return false;
                }
            }
        }
        return true;
    }
    private static boolean containsAny(String search_in, String[] search_for) {
        for (String search : search_for) {
            if (search.length() > 0) {
                if (search_in.contains(search)) {
                    //System.out.println("contained " + search);
                    return true;
                }
            }
        }
        return false;
    }
*/

    public String[] getCategories() {
        return category_names_array;
    }

    public static void main(String[] args) {
        //System.out.println(MenuService.getInstance().getPluginsInCategory("Binary"));
        HasClassifiedInputOutput a = new GaussianBlur2D();
        HasClassifiedInputOutput b = new BinaryNot();

        System.out.println((!a.getOutputType().contains("Binary Image")) &&
                    b.getInputType().contains(a.getOutputType()) &&
                    b.getOutputType().equals("Binary Image")?1:0);

    }
}


