package net.haesleinhuepf.clincubator.utilities;

import net.haesleinhuepf.clincubator.AbstractIncubatorPlugin;

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
