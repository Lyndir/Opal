package com.lyndir.lhunath.opal.wayward.navigation;

import org.apache.wicket.IClusterable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jetbrains.annotations.NotNull;


/**
 * <h2>{@link TabDescriptor}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 06, 2010</i> </p>
 *
 * @author lhunath
 */
public interface TabDescriptor<P extends Panel, S extends TabState<? extends P>> extends IClusterable {

    /**
     * @return The string that identifies this tab when it's the zero'th argument in the fragment part of the URL.
     */
    @NotNull
    String getFragment();

    /**
     * Obtain the fragment state object that can restore the state of the given panel of this tab.
     *
     * @param panel The panel for this tab whose state must be serialized.
     *
     * @return The given panel's state serialized into a fragment state object.
     */
    @NotNull
    S newState(@NotNull P panel);

    /**
     * @param fragment The string that contains the fragment which needs to be parsed into state for this tab.
     *
     * @return A state object for this tab that contains state as specified by the given arguments.
     */
    @NotNull
    S newState(@NotNull String fragment);

    /**
     * @return The string that identifies this tab in its navigation menu.
     */
    @NotNull
    IModel<String> getTitle();

    /**
     * @return <code>true</code> if this tab should be visible in its navigation menu.
     */
    boolean shownInNavigation();

    /**
     * @return The class of the panel that will serve this tab's content.
     */
    @NotNull
    Class<? extends P> getContentPanelClass();
}
