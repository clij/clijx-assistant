package net.haesleinhuepf.clijx.assistant.utilities;

import net.haesleinhuepf.clijx.assistant.AbstractAssistantGUIPlugin;
import net.haesleinhuepf.clijx.assistant.optimize.Workflow;

public class ParameterContainer {
    Object[][] parameters;
    public ParameterContainer(Object[][] parameters) {
        this.parameters = new Object[parameters.length][];
        for (int i = 0; i < this.parameters.length; i++) {
            if (parameters[i] != null) {
                this.parameters[i] = new Object[parameters[i].length];
                for (int j = 0; j < this.parameters[i].length; j++) {
                    this.parameters[i][j] = parameters[i][j];
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParameterContainer)) {
            return false;
        }
        Object[][] parameters = ((ParameterContainer) obj).parameters;
        if (this.parameters.length != parameters.length) {
            return false;
        }
        for (int i = 0; i < this.parameters.length; i++) {
            if (this.parameters[i] != null && parameters[i] != null) {
                if (this.parameters[i].length != parameters[i].length) {
                    return false;
                }
                for (int j = 0; j < this.parameters[i].length; j++) {
                    if (this.parameters[i][j] != parameters[i][j]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void copyTo(Object[][] parameters) {
        for (int i = 0; i < this.parameters.length; i++) {
            if (this.parameters[i] != null && parameters[i] != null) {
                for (int j = 0; j < this.parameters[i].length; j++) {
                    parameters[i][j] = this.parameters[i][j];
                }
            }
        }

    }
}
