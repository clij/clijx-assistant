package net.clesperanto.macro.api;

public class ClEsperantoMacroAPI {
    static String generated = null;
    public static String generate() {
        if (generated == null) {
            generated = new ClEsperantoMacroAPIGenerator().toString();
        }
        return generated;
    }
}
