package net.clesperanto.javaprototype;

import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clijx.CLIJx;

@Deprecated
public class Camel extends CamelInterface {

    public static NativeTypeEnum Float = NativeTypeEnum.Float;
    public static NativeTypeEnum UnsignedShort = NativeTypeEnum.UnsignedShort;
    public static NativeTypeEnum UnsignedByte = NativeTypeEnum.UnsignedByte;

    static void selectDevice(String deviceName) {
        CLIJx.getInstance(deviceName);
    }

    public static String deviceName() {
        return CLIJx.getInstance().getGPUName();
    }
}
