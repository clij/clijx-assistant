package net.haesleinhuepf.clijx.incubator.utilities;

import net.haesleinhuepf.clijx.incubator.AbstractIncubatorPlugin;

public class MenuSeparator extends AbstractIncubatorPlugin {
    @Override
    public void refresh() {
        // will never get executed
    }

    @Override
    public <T extends SuggestedPlugin> Class<T>[] suggestedNextSteps() {
        return new Class[0];
    }

    @Override
    public <T extends SuggestedPlugin> Class<T>[] suggestedPreviousSteps() {
        return new Class[0];
    }
}
