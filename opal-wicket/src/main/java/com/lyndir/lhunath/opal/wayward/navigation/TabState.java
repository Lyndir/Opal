package com.lyndir.lhunath.opal.wayward.navigation;

import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link TabState}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 10, 2010</i> </p>
 *
 * @author lhunath
 */
public interface TabState<P extends Panel> {

    /**
     * @return The representation of this state as it should be used in the URL's fragment for navigation.
     */
    @Nullable
    String toFragment();

    /**
     * Apply this state to the given panel.
     *
     * @param panel The panel to apply the state to.
     *
     * @throws IncompatibleStateException If the state is incompatible with the current state and can not be applied.
     */
    void apply(@NotNull P panel)
            throws IncompatibleStateException;
}
