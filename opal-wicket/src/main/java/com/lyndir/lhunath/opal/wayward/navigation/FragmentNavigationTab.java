package com.lyndir.lhunath.opal.wayward.navigation;

import org.apache.wicket.IClusterable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jetbrains.annotations.NotNull;


/**
 * <h2>{@link FragmentNavigationTab}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 06, 2010</i> </p>
 *
 * @author lhunath
 */
public interface FragmentNavigationTab<P extends Panel, S extends FragmentState> extends IClusterable {

    /**
     * @return The string that identifies this tab when it's the zero'th argument in the fragment part of the URL.
     */
    @NotNull
    String getTabFragment();

    /**
     * Obtain the fragment state object that can restore the state of the given panel of this tab.
     *
     * @param panel The panel for this tab whose state must be serialized.
     *
     * @return The given panel's state serialized into a fragment state object.
     */
    @NotNull
    S buildFragmentState(@NotNull P panel);

    /**
     * Apply fragment state specific to this tab.
     *
     * @param panel The panel for this tab.
     * @param state The state to apply to the given panel.
     *
     * @throws IncompatibleStateException If the state is incompatible with the current state and can not be applied.
     */
    void applyFragmentState(@NotNull P panel, @NotNull S state)
            throws IncompatibleStateException;

    /**
     * @return The string that identifies this tab in its navigation menu.
     */
    @NotNull
    IModel<String> getTitle();

    /**
     * @return <code>true</code> if this tab should be visible in its navigation menu.
     */
    boolean isInNavigation();

    /**
     * @return The class of the panel that will serve this tab's content.
     */
    @NotNull
    Class<P> getContentPanelClass();

    /**
     * @param fragment The string that contains the fragment which needs to be parsed into state for this tab.
     *
     * @return A state object for this tab that contains state as specified by the given arguments.
     */
    @NotNull
    S getState(@NotNull String fragment);
}
