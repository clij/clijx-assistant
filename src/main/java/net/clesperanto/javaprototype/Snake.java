package net.clesperanto.javaprototype;

import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.CLIJx;

public class Snake implements CommonAPI, SnakeInterface {

    static void select_device(String device_name) {
        CLIJx.getInstance(device_name);
    }

    public static String device_name() {
       return CLIJx.getInstance().getGPUName();
   }
}
