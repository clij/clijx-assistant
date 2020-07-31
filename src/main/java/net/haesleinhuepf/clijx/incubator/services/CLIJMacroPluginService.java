package net.haesleinhuepf.clijx.incubator.services;

import org.scijava.Context;

public class CLIJMacroPluginService {

    private net.haesleinhuepf.clij.macro.CLIJMacroPluginService clijMacroPluginService = null;

    private CLIJMacroPluginService() {
        clijMacroPluginService = new Context(net.haesleinhuepf.clij.macro.CLIJMacroPluginService.class).getService(net.haesleinhuepf.clij.macro.CLIJMacroPluginService.class);
    }

    private static CLIJMacroPluginService instance = null;

    public synchronized static CLIJMacroPluginService getInstance() {
        if (instance == null) {
            instance = new CLIJMacroPluginService();
        }
        return instance;
    }

    public net.haesleinhuepf.clij.macro.CLIJMacroPluginService getService() {
        return clijMacroPluginService;
    }
}
