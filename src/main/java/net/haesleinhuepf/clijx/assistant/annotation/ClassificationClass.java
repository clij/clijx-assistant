package net.haesleinhuepf.clijx.assistant.annotation;

import net.haesleinhuepf.clijx.weka.gui.CLIJxWekaObjectClassification;

import java.awt.*;

public class ClassificationClass {
    private int identifier;
    private String name;
    private Color color;

    public ClassificationClass(int identifier) {
        name = "" + identifier;
        this.identifier = identifier;
        this.color = CLIJxWekaObjectClassification.getColor(identifier);
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public int getIdentifier() {
        return identifier;
    }
}
