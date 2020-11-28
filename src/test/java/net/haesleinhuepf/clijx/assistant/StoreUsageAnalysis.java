package net.haesleinhuepf.clijx.assistant;

import net.haesleinhuepf.clijx.assistant.services.UsageAnalyser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StoreUsageAnalysis {
    public static void main(String[] args) throws IOException {
        UsageAnalyser combinedUsageStats = new UsageAnalyser(
                "src/main/macro/",
                "../clij2-docs/src/main/macro/",
                "../clijx/src/main/macro/",
                "../scripts_hidden/",
                "../scripts/");

        String output = combinedUsageStats.all();

        File outputTarget = new File("src/main/resources/Robert_Haase_suggestions.config");
        try {
            FileWriter writer = new FileWriter(outputTarget);
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
