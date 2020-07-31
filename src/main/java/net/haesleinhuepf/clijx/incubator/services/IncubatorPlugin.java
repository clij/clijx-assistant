package net.haesleinhuepf.clijx.incubator.services;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import org.scijava.plugin.SciJavaPlugin;

public interface IncubatorPlugin extends SciJavaPlugin {
    void run(String command);

    void refresh();
    ImagePlus getSource();
    ImagePlus getTarget();

    void setTargetInvalid();
    void setTargetIsProcessing();
    void setTargetValid();

    CLIJMacroPlugin getCLIJMacroPlugin();

    Object[] getArgs();

    boolean canManage(CLIJMacroPlugin plugin);
    void setCLIJMacroPlugin(CLIJMacroPlugin plugin);
    String getName();
}
