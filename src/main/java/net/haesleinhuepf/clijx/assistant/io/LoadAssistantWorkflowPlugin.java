package net.haesleinhuepf.clijx.assistant.io;

import ij.IJ;
import ij.WindowManager;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.ScriptGenerator;
import net.haesleinhuepf.clijx.assistant.scriptgenerator.AssistantGroovyGenerator;
import net.haesleinhuepf.clijx.assistant.services.AssistantGUIPlugin;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptService;
import weka.core.scripting.Groovy;
import weka.gui.scripting.GroovyScript;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoadAssistantWorkflowPlugin implements PlugIn {

    @Override
    public void run(String args) {

        String filename = new OpenDialog("Open CLIJx-Assistant workflow", IJ.getDirectory("current"), "*.groovy").getPath();

        new GroovyScript().run(new File(filename), new String[]{});
    }
}
