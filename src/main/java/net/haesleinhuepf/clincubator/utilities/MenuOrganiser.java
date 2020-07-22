package net.haesleinhuepf.clincubator.utilities;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.utilities.IsCategorized;

import java.util.ArrayList;

public class MenuOrganiser {
    final static String ALL_STRING = "All";

    public static ArrayList<IncubatorPlugin> getPluginsInCategory(String search_string) {
        ArrayList<IncubatorPlugin> result = new ArrayList<>();

        if (search_string.compareTo(ALL_STRING) == 0) {
            search_string = "";
        }

        SuggestionService service = SuggestionService.getInstance();
        for (String entry : service.getNames()) {
            SuggestedPlugin plugin = service.getPluginByName(entry);
            String name = entry;
            String description = "";
            String categories = "";
            if (plugin instanceof IncubatorPlugin) {
                CLIJMacroPlugin macroPlugin = ((IncubatorPlugin) plugin).getCLIJMacroPlugin();
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
                    result.add((IncubatorPlugin) plugin);
                }
            }
        }
        return result;
    }

    public static String[] getCategories() {
        return new String[] {
                "Filter",
                "Transform",
                "Projection",
                "Segmentation",
                "Binary",
                "Label",
                "Measurement",
                ALL_STRING
        };
    }
}
