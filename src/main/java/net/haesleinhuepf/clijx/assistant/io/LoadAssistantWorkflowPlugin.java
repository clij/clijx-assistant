package net.haesleinhuepf.clijx.assistant.io;

import ij.IJ;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import net.haesleinhuepf.clij2.assistant.services.AssistantGUIPlugin;
import weka.gui.scripting.GroovyScript;

import java.io.File;

public class LoadAssistantWorkflowPlugin implements PlugIn {

    @Override
    public void run(String args) {

        String filename = new OpenDialog("Open CLIJx-Assistant workflow", IJ.getDirectory("current"), "*.groovy").getPath();

        new GroovyScript().run(new File(filename), new String[]{});
    }
}
