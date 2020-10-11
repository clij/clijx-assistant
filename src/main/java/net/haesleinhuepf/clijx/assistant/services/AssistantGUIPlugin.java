package net.haesleinhuepf.clijx.assistant.services;

import ij.ImagePlus;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.optimize.Workflow;
import org.scijava.plugin.SciJavaPlugin;

public interface AssistantGUIPlugin extends SciJavaPlugin {
    void run(String command);

    void refresh();
    void setSources(ImagePlus[] imps);
    ImagePlus getSource(int source);
    int getNumberOfSources();
    ImagePlus getTarget();

    void setTargetInvalid();
    void setTargetIsProcessing();
    void setTargetValid();

    CLIJMacroPlugin getCLIJMacroPlugin();

    Object[] getArgs();

    boolean canManage(CLIJMacroPlugin plugin);
    void setCLIJMacroPlugin(CLIJMacroPlugin plugin);
    String getName();

    void refreshDialogFromArguments();

    Workflow getWorkflow();
    String generateScript(ScriptGenerator generator);
    void generateScriptFile(ScriptGenerator generator);
}
