package net.haesleinhuepf.clincubator;

import net.haesleinhuepf.clincubator.utilities.*;
import scala.collection.$colon$plus;

import java.awt.*;

public class GeneratedPluginsConfig {
    public static void main(String[] args) {
        SuggestionService service = SuggestionService.getInstance();

        int category_count = 0;
        for (String category : MenuOrganiser.getCategories()) {

            category_count ++;
            for (IncubatorPlugin plugin : MenuOrganiser.getPluginsInCategory(category)) {

                String niceName = IncubatorUtilities.niceName(plugin.getClass().getSimpleName());

                System.out.println(
                        "Plugins>CLIncubator>" + category_count + " " + category + ", " +
                                "\"" + niceName + " (CLIncubator, experimental)_" + category_count + "\", " + plugin.getClass().getName()
                );
            }
        }

    }
}