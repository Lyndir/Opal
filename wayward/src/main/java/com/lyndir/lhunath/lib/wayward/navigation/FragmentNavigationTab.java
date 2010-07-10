package com.lyndir.lhunath.lib.wayward.navigation;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link FragmentNavigationTab}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 06, 2010</i> </p>
 *
 * @author lhunath
 */
public interface FragmentNavigationTab<P extends Panel, S extends FragmentState<P, S>> extends ITab {

    /**
     * @return The string that identifies this tab when it's the zero'th argument in the fragment part of the URL.
     */
    String getTabFragment();

    /**
     * Obtain the tab fragment and state arguments that would restore the state of the given panel of this tab.
     *
     * @param panel The panel for this tab; guaranteed of the type that was returned from #getPanel(String).
     *
     * @return All fragment state arguments.  That is each part of the fragment <b>except for the tab identifier (#getTabFragment)</b>.  In
     *         the order that #applyFragmentState(Panel, String...) would take them to restore the given panel's state in another session.
     */
    Iterable<String> getFragmentState(P panel);

    /**
     * Apply fragment state specific to this tab.
     *
     * @param panel The panel for this tab.
     * @param state The state to apply to the given panel.
     */
    void applyFragmentState(P panel, S state);

    /**
     * @param panelId The wicket ID to bind the panel to.
     *
     * @return The panel that serves this tab's contents.
     */
    @Override
    P getPanel(String panelId);

    /**
     * @return The class of the panel that will serve this tab's content.
     *
     * @see #getPanel(String)
     */
    Class<P> getPanelClass();

    /**
     * @param fragment The string that contains the fragment which needs to be parsed into state for this tab.
     *
     * @return A state object for this tab that contains state as specified by the given arguments.
     */
    S getState(String fragment);
}
