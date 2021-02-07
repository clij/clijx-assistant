package net.haesleinhuepf.clijx.assistant.services;

import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.interactive.generic.GenericAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.interactive.handcrafted.*;
import net.imagej.ImageJService;
import org.scijava.Context;
import org.scijava.InstantiableException;
import org.scijava.plugin.AbstractPTService;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@Plugin(type = Service.class)
public class AssistantGUIPluginService extends AbstractPTService<AssistantGUIPlugin> implements ImageJService {

    // private ArrayList<PluginInfo<AssistantGUIPlugin>> suggestedPlugins = new ArrayList<>();
    private HashMap<String, PluginInfo<AssistantGUIPlugin>> namedPlugins = new HashMap<>();
    private HashMap<String, Class> namedBackupPlugins = new HashMap<>();
    //private HashMap<Class, ArrayList<Class>> suggestedNextSteps = new HashMap<>();

    private ArrayList<AssistantGUIPlugin> plugins = new ArrayList<>();


    private static AssistantGUIPluginService fallbackService() {
        AssistantGUIPluginService service = new AssistantGUIPluginService();
        service.initialized = true;

        Class[] klasses = {
                AutomaticThreshold.class,
                Crop3D.class,
                CylinderTransform.class,
                ExtractChannel.class,
                LabelingWorkflowALX.class,
                MakeIsotropic.class,
                PullToROIManager.class,
                SphereTransform.class,
                GenericAssistantGUIPlugin.class
        };

        for(Class klass : klasses) {
            service.namedBackupPlugins.put(klass.getSimpleName(), klass);
            try {
                service.plugins.add((AssistantGUIPlugin) klass.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return service;
    }


    @Override
    public void initialize() {
        //initializeService();
    }

    private boolean initialized = false;
    private synchronized void initializeService() {
        if (initialized) {
            return;
        }

        for (final PluginInfo<AssistantGUIPlugin> info : getPlugins()) {
            /*String name = info.getName();
            if (name == null || name.isEmpty()) {
                name = info.getClassName();
            }*/

            //System.out.println(info);
            //System.out.println(info.getName());

            AssistantGUIPlugin current = pluginService().createInstance(info);
            plugins.add(current);

            if (current != null) {
                String name = current.getClass().getSimpleName();
                String[] temp = current.getClass().getPackage().getName().split("\\.");
                String packageName = temp[temp.length - 1];

                //suggestedPlugins.add(info);
                namedPlugins.put(name, info);

                //ArrayList<Class> services = new ArrayList<>();

                //System.out.println("Initial services for " + name);
                //for (Class suggestion : current.suggestedNextSteps()) {
                    //System.out.println("  " + suggestion.getSimpleName());
                //    services.add(suggestion);
                //}
                //suggestedNextSteps.put(current.getClass(), services);
            }
        }

        /*
        for (PluginInfo<AssistantGUIPlugin> info : suggestedPlugins) {
            AssistantGUIPlugin plugin = pluginService().createInstance(info);
            for (Class previousStep : plugin.suggestedPreviousSteps()) {
                if (suggestedNextSteps.get(previousStep) != null) {
                    if (!suggestedNextSteps.get(previousStep).contains(plugin.getClass())) {
                        suggestedNextSteps.get(previousStep).add(plugin.getClass());
                    }
                }
            }
        }
*/
//        IJ.log("number of plugins: " + suggestedPlugins.size());
        initialized = true;
    }


    @Override
    public Class<AssistantGUIPlugin> getPluginType() {
        return AssistantGUIPlugin.class;
    }
/*
    public Class[] getSuggestedNextStepsFor(AssistantGUIPlugin current) {
        initializeService();

        ArrayList<Class> list = suggestedNextSteps.get(current.getClass());
        if (list != null) {
            Class[] classes = new Class[list.size()];
            list.toArray(classes);
            return classes;
        } else {
            return new Class[0];
        }
    }*/

    static AssistantGUIPluginService instance = null;
    public static AssistantGUIPluginService getInstance() {
        if (instance == null) {
            try {
                instance = new Context(AssistantGUIPluginService.class).getService(AssistantGUIPluginService.class);
            } catch (Exception e) {
                instance = fallbackService();
            }
        }
        return instance;
    }

    public ArrayList<String> getNames() {
        initializeService();
        if (namedPlugins.size() > 0) {
            Set set = namedPlugins.keySet();
            ArrayList<String> list = new ArrayList<>(set);
            Collections.sort(list);
            return list;
        } else { // fallback
            Set set = namedBackupPlugins.keySet();
            ArrayList<String> list = new ArrayList<>(set);
            Collections.sort(list);
            return list;
        }
    }

    public AssistantGUIPlugin getPluginByName(String name) {
        initializeService();
        try {
            if (namedPlugins.size() > 0) {
                return namedPlugins.get(name).createInstance();
            } else {
                return (AssistantGUIPlugin) namedBackupPlugins.get(name).newInstance();
            }
        } catch (InstantiableException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class getIncubatorPluginClassFromCLIJ2Plugin(CLIJMacroPlugin plugin) {
        initializeService();
        for (AssistantGUIPlugin assistantGUIPlugin : plugins) {
            if (assistantGUIPlugin.canManage(plugin)) {
                return assistantGUIPlugin.getClass();
            }
        }
        return null;
    }
    public AssistantGUIPlugin getIncubatorPluginFromCLIJ2Plugin(CLIJMacroPlugin plugin) {
        initializeService();
        for (AssistantGUIPlugin assistantGUIPlugin : plugins) {
            if (assistantGUIPlugin.canManage(plugin)) {
                try {
                    return assistantGUIPlugin.getClass().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
