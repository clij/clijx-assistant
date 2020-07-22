package net.haesleinhuepf.clincubator.utilities;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;

public interface IncubatorPlugin extends PlugIn {
    void refresh();
    ImagePlus getSource();
    ImagePlus getTarget();

    void setTargetInvalid();
    void setTargetIsProcessing();
    void setTargetValid();

    CLIJMacroPlugin getCLIJMacroPlugin();

    Object[] getArgs();

}
