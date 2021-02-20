package net.haesleinhuepf.clijx.assistant.services;

import ij.IJ;
import net.haesleinhuepf.clij.clearcl.util.StringUtils;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.utilities.AssistantUtilities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class SuggestionService {
    private static File[] getFiles() {
        String[] resourceFiles = {"Robert_Haase_suggestions.config", "manual_suggestions.config", "BramvandenBroek_suggestions.config"};
        ArrayList<File> files = new ArrayList<>();
        for (String resourceFilename : resourceFiles) {

            InputStream resourceAsStream = SuggestionService.class.getClassLoader().getResourceAsStream(resourceFilename);

            String content;
            try {
                content = StringUtils.streamToString(resourceAsStream, "UTF-8");
            } catch (Exception e) {
                return new File[0];
            }

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

    public HashMap<String, Class> getIncubatorSuggestions(AssistantGUIPlugin assistantGUIPlugin) {
        AssistantGUIPluginService assistantGUIPluginService = AssistantGUIPluginService.getInstance();

        String name = assistantGUIPlugin.getCLIJMacroPlugin().getName();
        //System.out.println("NAME " + name);

        Class searchFor = pluginNameToClass(name);

        int dimensionality = assistantGUIPlugin.getTarget().getNSlices() > 1?3:2;

        //System.out.println("Search for : " + searchFor);

        ArrayList<CLIJMacroPlugin> clijSuggestions = suggestions.get(searchFor);
        if (clijSuggestions == null) {
            return new HashMap<>();
        }
        HashMap<String, Class> incubatorSuggestions = new HashMap<>();

        for (CLIJMacroPlugin clijPlugin : clijSuggestions) {
            if (clijPlugin != null) {
                Class incubatorPluginClassFromCLIJ2Plugin = assistantGUIPluginService.getIncubatorPluginClassFromCLIJ2Plugin(clijPlugin);
                if (incubatorPluginClassFromCLIJ2Plugin != null) {
                    boolean keep = true;
                    if (clijPlugin instanceof OffersDocumentation) {
                        String dimensionality_constraint = ((OffersDocumentation) clijPlugin).getAvailableForDimensions().replace(" ", "").toUpperCase();
                        if (dimensionality_constraint.compareTo("3D->2D") == 0) {
                            // projections
                            keep = dimensionality == 3;
                        } else {
                            //keep = (dimensionality_constraint.contains(dimensionality + "D"));
                        }
                    }
                    if (keep && !(AbstractAssistantGUIPlugin.show_advanced || !AssistantUtilities.isAdvancedPlugin(clijPlugin))) {
                        keep = false;
                    }
                    if (keep) {
                        incubatorSuggestions.put(clijPlugin.getName(), incubatorPluginClassFromCLIJ2Plugin);
                    }
                } else {
                    //System.out.println("Was null ?! " + clijPlugin);
                }
            }
        }


//        AssistantGUIPluginService assistantGUIPluginService = AssistantGUIPluginService.getInstance();
//        assistantGUIPluginService.getNames
//        for ()
//
//        Class incubatorPluginClassFromCLIJ2Plugin = assistantGUIPluginService.getIncubatorPluginClassFromCLIJ2Plugin(plugin.getCLIJMacroPlugin());
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
