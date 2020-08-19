package net.haesleinhuepf.clijx.incubator.services;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MenuService {
    ArrayList<String> names = new ArrayList<>();

    private MenuService() {
        IncubatorPluginService incubatorPluginService = IncubatorPluginService.getInstance();
        net.haesleinhuepf.clij.macro.CLIJMacroPluginService clijMacroPluginService = CLIJMacroPluginService.getInstance().getService();

        for (String name : clijMacroPluginService.getCLIJMethodNames()) {
            Class incubatorPluginClass = incubatorPluginService.getIncubatorPluginClassFromCLIJ2Plugin(clijMacroPluginService.getCLIJMacroPlugin(name));
            if (incubatorPluginClass != null) {
                names.add(name);
            }
        }

        Collections.sort(names);
    }

    private static MenuService instance = null;

    public synchronized static MenuService getInstance() {
        if (instance == null) {
            instance = new MenuService();
        }
        return instance;
    }

    public IncubatorPlugin getPluginByName(String name) {
        net.haesleinhuepf.clij.macro.CLIJMacroPluginService service = CLIJMacroPluginService.getInstance().getService();

        CLIJMacroPlugin plugin = service.getCLIJMacroPlugin(name);
        return getPluginByCLIJPlugin(plugin);
    }

    public IncubatorPlugin getPluginByCLIJPlugin(CLIJMacroPlugin plugin) {
        IncubatorPluginService incubatorPluginService = IncubatorPluginService.getInstance();
        try {
            IncubatorPlugin newPlugin = (IncubatorPlugin) incubatorPluginService.getIncubatorPluginClassFromCLIJ2Plugin(plugin).newInstance();
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

    final static String ALL_STRING = "All";

    public ArrayList<IncubatorPlugin> getPluginsInCategory(String search_string) {
        net.haesleinhuepf.clij.macro.CLIJMacroPluginService service = CLIJMacroPluginService.getInstance().getService();

        ArrayList<IncubatorPlugin> result = new ArrayList<>();

        if (search_string.compareTo(ALL_STRING) == 0) {
            search_string = "";
        }

        for (String entry : getNames()) {
            String name = entry;
            String description = "";
            String categories = "";
            String available_for_dimensions = "";
            String parameter_help_text = "";

            CLIJMacroPlugin macroPlugin = service.getCLIJMacroPlugin(entry);
            if ( IncubatorUtilities.isIncubatablePlugin(macroPlugin)) {
                if (macroPlugin instanceof OffersDocumentation) {
                    description = ((OffersDocumentation) macroPlugin).getDescription();
                    available_for_dimensions = available_for_dimensions;
                }
                if (macroPlugin instanceof IsCategorized) {
                    categories = ((IsCategorized) macroPlugin).getCategories();
                }
                parameter_help_text = macroPlugin.getParameterHelpText();

                if (isInCategory(name, macroPlugin.getClass().getName(), description, parameter_help_text, available_for_dimensions, categories, search_string)) {
                    result.add(getPluginByCLIJPlugin(macroPlugin));
                }
            }
        }

        return result;
    }

    public static boolean isInCategory(String name, String class_name, String description, String parameter_help_text, String available_for_dimensions, String categories, String search_string) {
        if (search_string.length() == 0) {
            return true;
        }


        search_string = search_string.toLowerCase();

        String major = search_string.split(">")[0];
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
    }

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

    public String[] getCategories() {
        return new String[] {
                "Filter>Noise removal",
                "Filter>Background removal",
                "Filter>Edges",
                "Filter>All",
                "Transform",
                "Projection",
                "Binary",
                "Label>Segmentation",
                "Label>Processing",
                "Label>Measurement",
                "Label>All",
                ALL_STRING
        };
    }

    public static void main(String[] args) {
        System.out.println(MenuService.getInstance().getPluginsInCategory("Binary"));
    }
}


