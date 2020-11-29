package net.clesperanto.javaprototype;

import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clijx.CLIJx;

public class Snake extends SnakeInterface {

    public static NativeTypeEnum Float = NativeTypeEnum.Float;
    public static NativeTypeEnum UnsignedShort = NativeTypeEnum.UnsignedShort;
    public static NativeTypeEnum UnsignedByte = NativeTypeEnum.UnsignedByte;

    static void select_device(String device_name) {
        CLIJx.getInstance(device_name);
    }

    public static String device_name() {
       return CLIJx.getInstance().getGPUName();
   }
}
