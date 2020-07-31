package net.haesleinhuepf.clijx.incubator.services;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;

import java.util.ArrayList;

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

            CLIJMacroPlugin macroPlugin = service.getCLIJMacroPlugin(entry);
            if ( IncubatorUtilities.isIncubatablePlugin(macroPlugin)) {
                if (macroPlugin instanceof OffersDocumentation) {
                    description = ((OffersDocumentation) macroPlugin).getDescription();
                }
                if (macroPlugin instanceof IsCategorized) {
                    categories = ((IsCategorized) macroPlugin).getCategories();
                }

                if (search_string.length() == 0 ||
                        (description != null && (description.toLowerCase().contains(search_string.toLowerCase()))) ||
                        (categories != null && categories.toLowerCase().contains(search_string.toLowerCase())) ||
                        name.toLowerCase().contains(search_string.toLowerCase())) {
                    result.add(getPluginByCLIJPlugin(macroPlugin));
                }
            }
        }

        return result;
    }

    public String[] getCategories() {
        return new String[] {
                "Filter",
                "Transform",
                "Projection",
                "Binary",
                "Label",
                "Measurement",
                ALL_STRING
        };
    }

    public static void main(String[] args) {
        System.out.println(MenuService.getInstance().getPluginsInCategory("Binary"));
    }
}


