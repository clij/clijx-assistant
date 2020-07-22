package net.haesleinhuepf.clincubator;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPluginService;
import org.scijava.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class CombinedUsageStats {

    HashMap<String, Integer> followers = new HashMap<>();
    HashMap<String, Integer> following = new HashMap<>();

    public CombinedUsageStats(String... macroPaths) {
        for (String macroPath : macroPaths) {
            File folder = new File(macroPath);
            if (folder.exists()) {

                for (File file : folder.listFiles()) {
                    if (file.getName().endsWith(".ijm")) {
                        try {
                            //System.out.println("Parsing " + file);
                            parseFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //break;
                    }
                }
            }
        }
    }

    private void parseFile(File file) throws IOException {
        String content = new String(Files.readAllBytes(file.toPath()));
        //System.out.println(content);
        String[] lines = content.split("\n");

        CLIJMacroPluginService service = new Context(CLIJMacroPluginService.class).getService(CLIJMacroPluginService.class);

        HashMap<String, String> producers = new HashMap<>();
        HashMap<String, String> consumers = new HashMap<>();
        for (String line : lines) {
            line = line.replace("CLIJx_", "CLIJ2_");
            if (line.contains("Ext.CLIJ2_")) {
                String[] temp = line.split("Ext.CLIJ2_")[1].split("\\(");
                String method = temp[0];
                String[] parameters = temp[1].replace(");","").split(",");


                System.out.println("Method: " + method);
                CLIJMacroPlugin plugin = service.getCLIJMacroPlugin("CLIJ2_" + method);
                if (plugin == null) {
                    plugin = service.getCLIJMacroPlugin("CLIJx_" + method);
                }
                if (plugin == null) {
                    continue;
                }
                //System.out.println(plugin);
                String[] parameterDefinitions = plugin.getParameterHelpText().replace(",", ", ").replace("  ", " ").replace("  ", " ").split(",");

                //System.out.println("Parameters: " + parameterDefinitions.length);
                //System.out.println("Parameters values: " + parameters.length);

                if (parameterDefinitions.length == parameters.length) {
                    for (int p = 0; p < parameters.length; p++) {
                        parameters[p] = parameters[p].trim();
                        if (!isNumeric(parameters[p])) {
                            if (parameterDefinitions[p].toLowerCase().contains("byref")) {
                                //System.out.println("Producer: " + method + " -> " + parameters[p]);
                                producers.put(parameters[p], method);
                            } else {
                                //System.out.println("Consumer: " + method + " <- " + parameters[p]);
                                consumers.put(parameters[p], method);
                            }
                        }
                    }
                }
            }
        }

        for (String data : consumers.keySet()) {
            //System.out.println(data);
            String producer = producers.get(data);
            String consumer = consumers.get(data);

            if (producer != null && consumer != null && producer.compareTo("push") != 0 && producer.compareTo("copy") != 0 && !consumer.startsWith("pull")) {
                //System.out.println("Followers: " + producer + " > " + consumer);
                addToMap(followers, consumer, producer);
                addToMap(following, producer, consumer);
            }
        }
    }

    private void addToMap(HashMap<String, Integer> followers, String consumer, String producer) {
        String key = producer + " > " + consumer;
        int count = 1;
        if (followers.containsKey(key)) {
            count = followers.get(key);
            followers.remove(key);
        }
        followers.put(key, count + 1);
    }

    public HashMap<String, Integer> getFollowersOf(String command) {
        HashMap<String, Integer> myFollowers = new HashMap<>();
        for (String key : followers.keySet()) {
            if (key.startsWith( command + " > ")) {
                String follower = key.split(" > ")[1];
                myFollowers.put(follower, followers.get(key));
            }
        }
        return myFollowers;
    }

    public static boolean isNumeric(String str) {
        try{
            Double.parseDouble(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        new CombinedUsageStats("../clij2-docs/src/main/macro/");
    }

    public HashMap<String, Integer> getFollowing(String command) {
        HashMap<String, Integer> myFollowing = new HashMap<>();
        for (String key : following.keySet()) {
            if (key.startsWith( command + " > ")) {
                String follower = key.split(" > ")[1];
                myFollowing.put(follower, following.get(key));
            }
        }
        return myFollowing;

    }
}
