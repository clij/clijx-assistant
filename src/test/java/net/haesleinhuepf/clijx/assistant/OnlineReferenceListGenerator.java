package net.haesleinhuepf.clijx.assistant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OnlineReferenceListGenerator {
    public static void main(String[] args) {
        String path = "../clij2-docs/";

        StringBuilder output = new StringBuilder();
        for (File file : new File(path).listFiles()) {
            if (!file.isDirectory()) {
                if (file.getName().startsWith("reference_") && !file.getName().startsWith("reference__")) {
                    output.append(file.getName().substring(10).replace(".md", "") + "\n");
                }
            }
        }


        File outputTarget = new File("src/main/resources/online_reference.config");
        try {
            FileWriter writer = new FileWriter(outputTarget);
            writer.write(output.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
