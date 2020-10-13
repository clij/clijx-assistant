package net.haesleinhuepf.clijx.assistant.services;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPluginService;
import org.scijava.Context;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class UsageAnalyser {

    HashMap<String, Integer> followers = new HashMap<>();
    HashMap<String, Integer> following = new HashMap<>();

    public UsageAnalyser(String... macroPaths) {
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

        HashMap<String, ArrayList<String>> producers = new HashMap<>();
        HashMap<String, ArrayList<String>> consumers = new HashMap<>();

        int count = 0;

        for (String line : lines) {
            if (line.contains("Ext.")) {
                String[] temp = line.split("Ext.")[1].split("\\(");
                String method = temp[0];
                if (temp.length > 1) {
                    String[] parameters = temp[1].replace(");", "").split(",");


                    //System.out.println("Method: " + method);
                    CLIJMacroPlugin plugin = service.getCLIJMacroPlugin(method);
                    if (plugin == null) {
                        System.out.println("No plugin found for " + method);
                        continue;
                    }
                    //System.out.println(plugin);
                    String[] parameterDefinitions = plugin.getParameterHelpText().replace(",", ", ").replace("  ", " ").replace("  ", " ").split(",");

                    System.out.println("Parameters: " + parameterDefinitions.length);
                    //System.out.println("Parameters values: " + parameters.length);

                    if (parameterDefinitions.length == parameters.length) {
                        for (int p = 0; p < parameters.length; p++) {
                            parameters[p] = parameters[p].trim();
                            if (!isNumeric(parameters[p])) {
                                if (parameterDefinitions[p].toLowerCase().contains("byref") || parameterDefinitions[p].toLowerCase().contains("destination") || method.contains("_push")) {
                                    System.out.println("Producer: " + method + " -> " + parameters[p]);

                                    ArrayList<String> methods = producers.containsKey(parameters[p]) ? producers.get(parameters[p]) : new ArrayList<>();
                                    methods.add(method);

                                    producers.put(parameters[p], methods);
                                } else {

                                    ArrayList<String> methods = consumers.containsKey(parameters[p]) ? consumers.get(parameters[p]) : new ArrayList<>();
                                    methods.add(method);
                                    System.out.println("Consumer: " + method + " <- " + parameters[p]);
                                    consumers.put(parameters[p], methods);
                                }
                                count++;
                            }
                        }
                    }
                }
            }
        }

        for (String data : consumers.keySet()) {
            //System.out.println(data);

            if (producers.containsKey(data) && consumers.containsKey(data)) {
                for (String producer : producers.get(data)) {
                    //System.out.println("Producer: "+ producer);
                    for (String consumer : consumers.get(data)) {
                        //System.out.println("Consumer: "+ consumer);

                        //if (producer.compareTo("push") != 0 && producer.compareTo("copy") != 0 && !consumer.startsWith("pull")) {
                        //System.out.println("Followers: " + producer + " > " + consumer);
                        addToMap(followers, consumer, producer);
                        addToMap(following, producer, consumer);
                        //}
                    }
                }
            }
        }
    }

    private void addToMap(HashMap<String, Integer> followers, String consumer, String producer) {
        String key = producer + " > " + consumer;
        int count = 0;
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
        System.out.println(new UsageAnalyser("C:/structure/temp/scriptfolder/").all());
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

    public String suggestionsAsString(String command) {
        StringBuilder output = new StringBuilder();
        HashMap<String, Integer> suggestions = getFollowersOf(command);
        ArrayList<String> names = new ArrayList<String>();
        names.addAll(suggestions.keySet());
        Collections.sort(names);

        output.append(command + "\n");
        for (String key : names) {
            output.append("\t" + key + "\t" + suggestions.get(key) + "\n");
        }

        return output.toString();
    }

    public String all() {
        StringBuilder output = new StringBuilder();

        ArrayList<String> names = new ArrayList<String>();
        names.addAll(followers.keySet());
        Collections.sort(names);

        String former_key = "";
        for (String key : names) {
            //System.out.println(">>> " + key);
            String current_key = key.split(" ")[0];
            if (current_key.compareTo(former_key) != 0) {
                output.append(suggestionsAsString(current_key));
            }
            former_key = current_key;
        }
        return output.toString();
    }
}
