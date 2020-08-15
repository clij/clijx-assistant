package net.haesleinhuepf.clijx.te_oki;

import fiji.util.gui.GenericDialogPlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

public class TeOkiConfigurationDialog implements PlugIn {
    @Override
    public void run(String arg) {
        GenericDialogPlus gdp = new GenericDialogPlus("Te Oki Configuration");
        gdp.addDirectoryField("Conda directory", TeOkiEngine.conda_directory);
        gdp.addStringField("Conda environment", TeOkiEngine.conda_env);
        gdp.showDialog();

        if (gdp.wasCanceled()) {
            return;
        }

        TeOkiEngine.conda_directory = gdp.getNextString();
        TeOkiEngine.conda_env = gdp.getNextString();
    }
}
