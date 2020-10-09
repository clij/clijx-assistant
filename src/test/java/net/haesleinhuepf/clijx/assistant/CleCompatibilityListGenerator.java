package net.haesleinhuepf.clijx.assistant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CleCompatibilityListGenerator {
    public static void main(String[] args) {
        String path = "../pyclesperanto_prototype/pyclesperanto_prototype/";

        StringBuilder output = new StringBuilder();
        for (File subfolder : new File(path).listFiles()) {
            if (subfolder.isDirectory()) {
                for (File file : subfolder.listFiles()) {
                    if (!file.isDirectory()) {
                        if (file.getName().startsWith("_") && file.getName().endsWith(".py") && !(file.getName().compareTo("__init__.py") == 0)) {
                            output.append(file.getName().substring(1).replace(".py", "") + "\n");
                        }
                    }
                }
            }
        }


        File outputTarget = new File("src/main/resources/cle_compatibility.config");
        try {
            FileWriter writer = new FileWriter(outputTarget);
            writer.write(output.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
