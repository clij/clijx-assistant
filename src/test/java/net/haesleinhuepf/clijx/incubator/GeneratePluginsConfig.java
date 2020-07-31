package net.haesleinhuepf.clijx.incubator;

import net.haesleinhuepf.clijx.incubator.services.IncubatorPlugin;
import net.haesleinhuepf.clijx.incubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.clijx.incubator.services.IncubatorPluginService;
import net.haesleinhuepf.clijx.incubator.services.MenuService;

public class GeneratePluginsConfig {
    public static void main(String[] args) {
        IncubatorPluginService service = IncubatorPluginService.getInstance();

        int category_count = 0;
        for (String category : MenuService.getInstance().getCategories()) {

            category_count ++;
            for (IncubatorPlugin plugin : MenuService.getInstance().getPluginsInCategory(category)) {

                String niceName = IncubatorUtilities.niceName(plugin.getName());
                String clijName = plugin.getCLIJMacroPlugin().getName();

                System.out.println(
                        "Plugins>ImageJ on GPU (CLIJx-Incubator)>" + category_count + " " + category + ", " +
                                "\"" + niceName + " (experimental)_" + category_count + "\", " + plugin.getClass().getName() + "(\"" + clijName + "\")"
                );
            }
        }

    }
}