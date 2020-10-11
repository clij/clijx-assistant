package net.haesleinhuepf.clijx.assistant.options;

import fiji.util.gui.GenericDialogPlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import net.haesleinhuepf.clijx.te_oki.TeOkiEngine;

public class AssistantOptionsDialog implements PlugIn {
    @Override
    public void run(String arg) {
        AssistantOptions ao = AssistantOptions.getInstance();

        GenericDialogPlus gdp = new GenericDialogPlus("Build and run options");

        gdp.addFileField("Icy executable", ao.getIcyExecutable());
        gdp.addDirectoryField("Conda directory", ao.getCondaPath());
        gdp.addStringField("Conda environment", ao.getCondaEnv());
        gdp.addFileField("git executable", ao.getGitExecutable());
        gdp.addFileField("maven executable", ao.getMavenExecutable());
        gdp.addDirectoryField("JDK home", ao.getJdkHome());

        gdp.showDialog();

        if (gdp.wasCanceled()) {
            return;
        }

        ao.setIcyExecutable(gdp.getNextString());
        ao.setCondaPath(gdp.getNextString());
        ao.setCondaEnv(gdp.getNextString());

        ao.setGitExecutable(gdp.getNextString());
        ao.setMavenExecutable(gdp.getNextString());
        ao.setJdkHome(gdp.getNextString());
    }
}
