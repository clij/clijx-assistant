package net.haesleinhuepf.clincubator.utilities;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;

public interface IncubatorPlugin {
    void refresh();
    ImagePlus getSource();
    ImagePlus getTarget();

    void setTargetInvalid();
    void setTargetIsProcessing();
    void setTargetValid();

    CLIJMacroPlugin getCLIJMacroPlugin();

    Object[] getArgs();

}
