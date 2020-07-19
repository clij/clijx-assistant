package net.haesleinhuepf.clincubator.utilities;

import net.haesleinhuepf.clij.macro.CLIJHandler;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.imagej.ImageJService;
import org.scijava.Context;
import org.scijava.InstantiableException;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@Plugin(type = Service.class)
public class SuggestionService  extends AbstractPTService<SuggestedPlugin> implements ImageJService {

    private ArrayList<PluginInfo<SuggestedPlugin>> suggestedPlugins = new ArrayList<>();
    private HashMap<String, PluginInfo<SuggestedPlugin>> namedPlugins = new HashMap<>();
    private HashMap<Class, ArrayList<Class>> suggestedNextSteps = new HashMap<>();

    private HashMap<String, PluginInfo<SuggestedPlugin>> categorizedNamedPlugins = new HashMap<>();

    @Override
    public void initialize() {
        //initializeService();
    }

    private boolean initialized = false;
    private synchronized void initializeService() {
        if (initialized) {
            return;
        }

        for (final PluginInfo<SuggestedPlugin> info : getPlugins()) {
            /*String name = info.getName();
            if (name == null || name.isEmpty()) {
                name = info.getClassName();
            }*/

            SuggestedPlugin current = pluginService().createInstance(info);
            String name = current.getClass().getSimpleName();
            String[] temp = current.getClass().getPackage().getName().split("\\.");
            String packageName = temp[temp.length - 1];

            suggestedPlugins.add(info);
            namedPlugins.put(name, info);
            categorizedNamedPlugins.put(packageName + "/" + name, info);

            ArrayList<Class> suggestions = new ArrayList<>();

            //System.out.println("Initial suggestions for " + name);
            for (Class suggestion : current.suggestedNextSteps()) {
                //System.out.println("  " + suggestion.getSimpleName());
                suggestions.add(suggestion);
            }
            suggestedNextSteps.put(current.getClass(), suggestions);
        }

        for (PluginInfo<SuggestedPlugin> info : suggestedPlugins) {
            SuggestedPlugin plugin = pluginService().createInstance(info);
            for (Class previousStep : plugin.suggestedPreviousSteps()) {
                if (suggestedNextSteps.get(previousStep) != null) {
                    if (!suggestedNextSteps.get(previousStep).contains(plugin.getClass())) {
                        suggestedNextSteps.get(previousStep).add(plugin.getClass());
                    }
                }
            }
        }


        initialized = true;
    }


    @Override
    public Class<SuggestedPlugin> getPluginType() {
        return SuggestedPlugin.class;
    }

    public Class[] getSuggestedNextStepsFor(SuggestedPlugin current) {
        initializeService();

        ArrayList<Class> list = suggestedNextSteps.get(current.getClass());
        if (list != null) {
            Class[] classes = new Class[list.size()];
            list.toArray(classes);
            return classes;
        } else {
            return new Class[0];
        }
    }

    static SuggestionService instance = null;
    public static SuggestionService getInstance() {
        if (instance == null) {
            instance = new Context(SuggestionService.class).getService(SuggestionService.class);
        }
        return instance;
    }

    public ArrayList<String> getHierarchy() {
        initializeService();
        Set set = categorizedNamedPlugins.keySet();
        ArrayList<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

    public SuggestedPlugin getPluginByName(String name) {
        initializeService();
        try {
            return namedPlugins.get(name).createInstance();
        } catch (InstantiableException e) {
            e.printStackTrace();
        }
        return null;
    }

}
