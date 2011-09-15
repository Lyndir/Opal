package com.lyndir.lhunath.opal.wayward.navigation;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Joiner;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.opal.system.util.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <i>06 26, 2011</i>
 *
 * @author lhunath
 */
public abstract class NavigationController implements IClusterable {

    private static final Pattern FRAGMENT_ELEMENT = Pattern.compile( "(([^/]+)(?:/|$))" );

    static final Logger logger = Logger.get( NavigationController.class );

    private String              pageFragment;
    private TabDescriptor<?, ?> activeTab;

    /**
     * Mark the given tab as active and restore state in its contents from the given tab-specific state arguments.
     *
     * @param tab      The tab that should be activated.
     * @param fragment The string that contains the fragment which needs to be parsed into tab-specific state.
     *
     * @throws IncompatibleStateException If the state represented by the fragment is incompatible with the current state and can not
     *                                    be
     *                                    loaded.
     */
    <T extends TabDescriptor<P, S>, S extends TabState<P>, P extends Panel> void activateTabWithState(@NotNull final T tab,
                                                                                                      @NotNull final String fragment)
            throws IncompatibleStateException {

        Matcher matcher = FRAGMENT_ELEMENT.matcher( fragment );
        checkArgument( matcher.find(), "Can't parse elements from fragment: %s, using: %s", matcher.pattern() );
        String tabFragment = tab.getFragment();
        String firstFragment = matcher.group( 2 );
        checkArgument( ObjectUtils.isEqual( tabFragment, firstFragment ),
                       "Can't load fragment: %s, for tab: %s, fragment's first element: %s, doesn't match tab fragment: %s.", //
                       fragment, tab, firstFragment, tabFragment );

        matcher.reset();
        activateTabWithState( tab, tab.newState( matcher.replaceFirst( "" ) ) );
    }

    /**
     * Mark the given tab as active and restore state in its contents from the given tab-specific state arguments.
     *
     * @param tab   The tab that should be activated.
     * @param state The tab-specific state that should be applied to the tab's content.
     *
     * @throws IncompatibleStateException If the state is incompatible with the current state and can not be applied.
     */
    public <T extends TabDescriptor<P, S>, S extends TabState<P>, P extends Panel> void activateTabWithState(@NotNull final T tab,
                                                                                                             @NotNull final S state)
            throws IncompatibleStateException {

        P tabPanel = getContent( tab );
        state.apply( tabPanel );

        activateTab( tab, tabPanel );
    }

    /**
     * Mark the given tab as active and create a new content panel for the tab.
     *
     * @param tab The tab that should be activated.
     */
    public void activateNewTab(@NotNull final TabDescriptor<?, ?> tab) {

        activateTab( tab, null );
    }

    /**
     * Mark the given tab as active and use the given tabPanel for its content.
     *
     * @param tab   The tab that should be activated.
     * @param panel The panel that provides the tab's content or <code>null</code> if a new content panel should be created for the
     *              tab.
     */
    public <T extends TabDescriptor<P, ?>, P extends Panel, PP extends P> void activateTab(@NotNull final T tab, @Nullable final PP panel) {

        P tabPanel = panel == null? getContent( tab ): panel;
        tabPanel.setOutputMarkupPlaceholderTag( true );

        Page responsePage = RequestCycle.get().getResponsePage();
        if (responsePage == null || getTabPage() == null || responsePage.getClass() == getTabPage()) {
            // Request is a page request from this page, not a page request or pageClass is unset (we don't care about page redirects).
            // Activate the tab on the current page.
            logger.dbg( "Updating tab in response page: %s (tab exclusive page: %s)", responsePage, getTabPage() );

            activeTab = tab;
            onTabActivated( tab, tabPanel );

            updateNavigationComponents( tabPanel );
        } else {
            // PageClass is set and the current request is a page request from another page; redirect to tab on pageClass.
            CharSequence pageUrl = RequestCycle.get().urlFor( getTabPage(), null );
            String tabFragment = toFragment( tab, tabPanel );

            logger.dbg( "Redirecting to tab-exclusive page: %s (url: %s), fragment: %s", getTabPage(), pageUrl, tabFragment );
            throw new RedirectToUrlException( String.format( "%s#%s", pageUrl, tabFragment ) );
        }
    }

    public <T extends TabDescriptor<P, ?>, P extends Panel> String toFragment(final T tab, final P content) {

        String tabFragment = tab.newState( content ).toFragment();
        if (StringUtils.isEmpty( tabFragment ))
            return tab.getFragment();

        return Joiner.on( '/' ).join( tab.getFragment(), tabFragment );
    }

    void updateNavigationComponents(final Panel tabPanel) {

        AjaxRequestTarget target = AjaxRequestTarget.get();
        if (target != null) {
            target.addComponent( tabPanel );
            for (final Component component : getNavigationComponents())
                target.addComponent( component );
        }
    }

    /**
     * @return The current fragment of the page.
     */
    @Nullable
    public String getPageFragment() {

        return pageFragment;
    }

    /**
     * @param pageFragment The new fragment of the page.
     */
    public void setPageFragment(@Nullable final String pageFragment) {

        this.pageFragment = pageFragment;
    }

    /**
     * @return The currently active tab.
     */
    @Nullable
    @SuppressWarnings({ "unchecked" })
    public <T extends TabDescriptor<?, ?>> T getActiveTab() {

        return (T) activeTab;
    }

    /**
     * @return The page on which the tabs should be showing.  If not <code>null</code>; any time a tab is activated on another page;
     *         the user will be redirected to a url on this page with the activated tab's state in the url fragment.
     */
    @Nullable
    protected Class<? extends Page> getTabPage() {

        return null;
    }

    /**
     * Note:   The order should reflect the defaulting preference.  When no tab is selected by the fragment (or there is no fragment),
     * the first tab will be used instead, if visible.  If not visible, the next one will be tried, and so on.
     *
     * @return The application's tabs.
     */
    @NotNull
    protected abstract Iterable<? extends TabDescriptor<?, ?>> getTabs();

    /**
     * @param tab The tab for which we need the content component.
     *
     * @return The component that represents the content of the given tab.
     */
    @NotNull
    protected abstract <T extends TabDescriptor<P, ?>, P extends Panel> P getContent(@NotNull T tab);

    /**
     * @return All components that should be updated whenever page navigation changes.
     */
    @NotNull
    protected abstract Iterable<? extends Component> getNavigationComponents();

    /**
     * Invoked when a page is loaded to indicate the page's active tab as determined by fragment state.
     *
     * @param tab      The tab that needs to be activated.
     * @param tabPanel The panel that contains the tab's content as determined by fragment state.
     */
    protected abstract <T extends TabDescriptor<P, ?>, P extends Panel> void onTabActivated(@NotNull T tab, @NotNull P tabPanel);

    /**
     * Handle errors that occur when attempting to apply state that is incompatible with the current state.
     *
     * @param e The error that occurred.
     */
    protected abstract void onError(@NotNull IncompatibleStateException e);
}
