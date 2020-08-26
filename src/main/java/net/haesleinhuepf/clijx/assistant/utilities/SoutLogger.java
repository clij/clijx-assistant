package net.haesleinhuepf.clijx.assistant.utilities;

public class SoutLogger implements Logger {
    @Override
    public void log(String text) {
        System.out.println(text);
    }
}
