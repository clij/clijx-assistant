package net.haesleinhuepf.clijx.assistant.services;

import ij.IJ;
import net.haesleinhuepf.clij.macro.CLIJHandler;
import net.haesleinhuepf.clij2.legacy.FallBackCLIJMacroPluginService;
import org.scijava.Context;

public class CLIJMacroPluginService {

    private net.haesleinhuepf.clij.macro.CLIJMacroPluginService clijMacroPluginService = null;

    private CLIJMacroPluginService() {
        try {
            clijMacroPluginService = new Context(net.haesleinhuepf.clij.macro.CLIJMacroPluginService.class).getService(net.haesleinhuepf.clij.macro.CLIJMacroPluginService.class);
        } catch (IllegalArgumentException e) {
            IJ.log("replace service");
            clijMacroPluginService = CLIJHandler.getInstance().getPluginService();
            IJ.log("service replaced");
        }
        if (clijMacroPluginService == null) {
            IJ.log("loading fallback service");
            clijMacroPluginService = new FallBackCLIJMacroPluginService();
            IJ.log("fallback service loaded");
        }
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
