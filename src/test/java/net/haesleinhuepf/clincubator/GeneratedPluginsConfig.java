package net.haesleinhuepf.clincubator;

import net.haesleinhuepf.clincubator.utilities.IncubatorUtilities;
import net.haesleinhuepf.clincubator.utilities.SuggestedPlugin;
import net.haesleinhuepf.clincubator.utilities.SuggestionService;

public class GeneratedPluginsConfig {
    public static void main(String[] args) {
        SuggestionService service = SuggestionService.getInstance();

        for (String entry : service.getHierarchy()) {
            String[] temp = entry.split("/");
            String category = IncubatorUtilities.niceName(temp[0]);
            String name = temp[1];
            String niceName = IncubatorUtilities.niceName(temp[1]);

            SuggestedPlugin plugin = service.getPluginByName(name);

            System.out.println(
                    "Plugins>CLIncubator>" + category + ", " +
                    "\"" + niceName + " (CLIncubator, experimental)\", " + plugin.getClass().getName()
            );
        }

    }
}