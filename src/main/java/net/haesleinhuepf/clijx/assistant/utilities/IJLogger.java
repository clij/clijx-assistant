package net.haesleinhuepf.clijx.assistant.utilities;

import ij.IJ;

public class IJLogger implements Logger {
    @Override
    public void log(String text) {
        IJ.log(text);
    }
}
