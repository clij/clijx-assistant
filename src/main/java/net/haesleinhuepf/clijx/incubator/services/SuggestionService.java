package net.haesleinhuepf.clijx.incubator.services;

import ij.IJ;
import net.haesleinhuepf.clij.clearcl.util.StringUtils;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.incubator.interactive.handcrafted.MakeIsotropic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class SuggestionService {
    private static File[] getFiles() {
        String[] resourceFiles = {"suggestions.config", "manual_suggestions.config"};
        ArrayList<File> files = new ArrayList<>();
        for (String resourceFilename : resourceFiles) {

            InputStream resourceAsStream = SuggestionService.class.getClassLoader().getResourceAsStream(resourceFilename);

            String content = StringUtils.streamToString(resourceAsStream, "UTF-8");

            String filename = IJ.getDirectory("temp") + "/" + resourceFilename;
            try {
                Files.write(Paths.get(filename), content.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //System.out.println(filename);
            File file = new File(filename);
            files.add(file);
        }

        File folder = new File(IJ.getDirectory("imagej") + "/suggestions/");
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                if (!file.isDirectory() && file.getName().endsWith(".config")) {
                    files.add(file);
                }
            }
        }

        File[] fileArray = new File[files.size()];
        files.toArray(fileArray);
        return fileArray;

    }


    private HashMap<Class, ArrayList<CLIJMacroPlugin>> suggestions = new HashMap<>();

    private SuggestionService() {
        Class currentMainEntry = null;
        net.haesleinhuepf.clij.macro.CLIJMacroPluginService clijMacroPluginService = CLIJMacroPluginService.getInstance().getService();

        for (File file : getFiles()) {
            try (FileReader reader = new FileReader(file);
                 BufferedReader br = new BufferedReader(reader)) {

                String line;
                while ((line = br.readLine()) != null) {
                    //System.out.println(line);
                    line = line.replace("\t", " ");
                    while (line.contains("  ")) {
                        line = line.replace("  ", " ");
                    }

                    if (!line.startsWith(" ")) { // main entry
                        currentMainEntry = pluginNameToClass(line);
                        //System.out.println("main entry: " + currentMainEntry);
                    } else { // follower
                        if (!suggestions.containsKey(currentMainEntry)) {
                            suggestions.put(currentMainEntry, new ArrayList<>());
                        }
                        ArrayList<CLIJMacroPlugin> list = suggestions.get(currentMainEntry);

                        list.add(clijMacroPluginService.getCLIJMacroPlugin(line.split(" ")[1]));
                        //System.out.println("sub entry: " + clijMacroPluginService.getCLIJMacroPlugin(line.split(" ")[1]));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Class pluginNameToClass(String pluginName) {
        CLIJMacroPlugin clijMacroPlugin = CLIJMacroPluginService.getInstance().getService().getCLIJMacroPlugin(pluginName);
        if (clijMacroPlugin != null) {
            return clijMacroPlugin.getClass();
        } else {
            return null;
        }
    }

    private static SuggestionService instance = null;

    public synchronized static SuggestionService getInstance() {
        if (instance == null) {
            instance = new SuggestionService();
        }
        return instance;
    }

    public HashMap<String, Class> getIncubatorSuggestions(IncubatorPlugin incubatorPlugin) {
        IncubatorPluginService incubatorPluginService = IncubatorPluginService.getInstance();

        String name = incubatorPlugin.getCLIJMacroPlugin().getName();
        //System.out.println("NAME " + name);

        Class searchFor = pluginNameToClass(name);

        //System.out.println("Search for : " + searchFor);

        ArrayList<CLIJMacroPlugin> clijSuggestions = suggestions.get(searchFor);
        if (clijSuggestions == null) {
            return new HashMap<>();
        }
        HashMap<String, Class> incubatorSuggestions = new HashMap<>();

        for (CLIJMacroPlugin clijPlugin : clijSuggestions) {
            Class incubatorPluginClassFromCLIJ2Plugin = incubatorPluginService.getIncubatorPluginClassFromCLIJ2Plugin(clijPlugin);
            if (incubatorPluginClassFromCLIJ2Plugin != null) {
                incubatorSuggestions.put(clijPlugin.getName(), incubatorPluginClassFromCLIJ2Plugin);
            } else {
                //System.out.println("Was null ?! " + clijPlugin);
            }
        }


//        IncubatorPluginService incubatorPluginService = IncubatorPluginService.getInstance();
//        incubatorPluginService.getNames
//        for ()
//
//        Class incubatorPluginClassFromCLIJ2Plugin = incubatorPluginService.getIncubatorPluginClassFromCLIJ2Plugin(plugin.getCLIJMacroPlugin());
        return incubatorSuggestions;
    }


    public static void main(String[] args) {
        SuggestionService suggestionService = SuggestionService.getInstance();
        //for (Class whatever : suggestionService.getIncubatorSuggestions(new MakeIsotropic())) {
        //    System.out.println(whatever.getName());
        //}
    }

    public void invalidate() {
        instance = null;
    }
}
