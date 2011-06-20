package com.lyndir.lhunath.opal.wayward.navigation;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.opal.system.util.StringUtils;
import com.lyndir.lhunath.opal.wayward.js.AjaxHooks;
import com.lyndir.lhunath.opal.wayward.js.JSUtils;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link FragmentNavigationListener}<br> <sub>Listeners that implement support for fragment-based AJAX content navigation.</sub></h2>
 * <p/>
 * <p> To enable fragment-based navigation in your application, install the PageListener on the pages you want to enable fragment-based
 * navigation on, and add the AjaxRequestListener to each AjaxRequestTarget that can cause content state or navigation to change.</p>
 * <p/>
 * <p>The easiest way to do the latter is by overriding WebApplication#newAjaxRequestTarget(Page page) and adding the AjaxRequestListener
 * to
 * the target there.</p>
 * <p/>
 * <p> <i>07 06, 2010</i> </p>
 *
 * @author lhunath
 */
public interface FragmentNavigationListener {

    abstract class Controller<P extends Panel, S extends FragmentState> implements IClusterable {

        private static final Pattern FRAGMENT_ELEMENT = Pattern.compile( "([^/]*/)?" );

        static final Logger logger = Logger.get( Controller.class );

        private String                                          pageFragment;
        private FragmentNavigationTab<? extends P, ? extends S> activeTab;

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
        <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void activateTabWithState(@NotNull final TT tab,
                                                                                                         @NotNull final String fragment)
                throws IncompatibleStateException {

            Matcher matcher = FRAGMENT_ELEMENT.matcher( fragment );
            checkArgument(
                    !matcher.matches() || !ObjectUtils.isEqual( tab.getTabFragment(), matcher.group( 1 ) ),
                    "Can't load fragment (%s) for tab (%s), fragment's first element doesn't match tab fragment.", //
                    fragment, tab.getClass() );

            activateTabWithState( tab, tab.getState( matcher.replaceFirst( "" ) ) );
        }

        /**
         * Mark the given tab as active and restore state in its contents from the given tab-specific state arguments.
         *
         * @param tab   The tab that should be activated.
         * @param state The tab-specific state that should be applied to the tab's content.
         *
         * @throws IncompatibleStateException If the state is incompatible with the current state and can not be applied.
         */
        public <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void activateTabWithState(@NotNull final TT tab,
                                                                                                                @NotNull final SS state)
                throws IncompatibleStateException {

            PP tabPanel = getContent( tab );
            tab.applyFragmentState( tabPanel, state );
            activateTab( tab, tabPanel );
        }

        /**
         * Mark the given tab as active and create a new content panel for the tab.
         *
         * @param tab The tab that should be activated.
         */
        public <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void activateNewTab(@NotNull final TT tab) {

            activateTab( tab, null );
        }

        /**
         * Mark the given tab as active and use the given tabPanel for its content.
         *
         * @param tab      The tab that should be activated.
         * @param tabPanel The panel that provides the tab's content or <code>null</code> if a new content panel should be created for the
         *                 tab.
         */
        public <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void activateTab(@NotNull final TT tab,
                                                                                                       @Nullable PP tabPanel) {

            if (tabPanel == null)
                tabPanel = getContent( tab );
            tabPanel.setOutputMarkupPlaceholderTag( true );

            Page responsePage = RequestCycle.get().getResponsePage();
            if (responsePage == null || getTabPage() == null || responsePage.getClass() == getTabPage()) {
                // Request is a page request from this page, not a page request or pageClass is unset (we don't care about page redirects).
                // Activate the tab on the current page.
                logger.dbg( "Updating tab in response page: %s (tab exclusive page: %s)", responsePage, getTabPage() );

                // Cast TT to T because Java can't constrain TT to both T and FNT<PP,SS>
                onTabActivated( activeTab = tab, tabPanel );

                updateNavigationComponents();
            } else {
                // PageClass is set and the current request is a page request from another page; redirect to tab on pageClass.
                CharSequence pageUrl = RequestCycle.get().urlFor( getTabPage(), null );
                String tabFragment = tab.buildFragmentState( tabPanel ).toFragment();
                if (StringUtils.isEmpty( tabFragment ))
                    tabFragment = tab.getTabFragment();
                else
                    tabFragment = Joiner.on( '/' ).join( tab.getTabFragment(), tabFragment );

                logger.dbg( "Redirecting to tab-exclusive page: %s (url: %s), fragment: %s", getTabPage(), pageUrl, tabFragment );
                throw new RedirectToUrlException( String.format( "%s#%s", pageUrl, tabFragment ) );
            }
        }

        private void updateNavigationComponents() {

            AjaxRequestTarget target = AjaxRequestTarget.get();
            if (target != null)
                for (final Component component : getNavigationComponents())
                    target.addComponent( component );
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
        public FragmentNavigationTab<? extends P, ? extends S> getActiveTab() {

            return activeTab;
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
        protected abstract Iterable<? extends FragmentNavigationTab<? extends P, ? extends S>> getTabs();

        /**
         * @param tab The tab for which we need the content component.
         *
         * @return The component that represents the content of the given tab.
         */
        @NotNull
        protected abstract <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> PP getContent(@NotNull TT tab);

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
        protected abstract <TT extends FragmentNavigationTab<?, ?>> void onTabActivated(@NotNull TT tab, @NotNull Panel tabPanel);

        /**
         * Handle errors that occur when attempting to apply state that is incompatible with the current state.
         *
         * @param e The error that occurred.
         */
        protected abstract void onError(@NotNull IncompatibleStateException e);
    }


    /**
     * Install me on the page where tab navigation should happen.
     */
    class PageListener<P extends Panel, S extends FragmentState> implements AjaxHooks.IPageListener {

        static final Logger logger = Logger.get( PageListener.class );

        private final Controller<P, S> controller;

        /**
         * @param controller The object that controls fragment state for this page.
         */
        private PageListener(final Controller<P, S> controller) {

            this.controller = controller;
        }

        public static <P extends Panel, S extends FragmentState> PageListener<P, S> of(final Controller<P, S> controller) {

            return new PageListener<P, S>( controller );
        }

        @Override
        public void onReady(final AjaxRequestTarget target, final String pageUrl) {

            String fragment = URI.create( pageUrl ).getFragment();
            controller.setPageFragment( fragment );

            logger.dbg( "Found fragment: %s", fragment );
            if (fragment != null) {
                // There is a fragment, load state from it.
                String tabFragment = Splitter.on( '/' ).split( fragment ).iterator().next();

                for (final FragmentNavigationTab<? extends P, ? extends S> tab : controller.getTabs()) {
                    if (tab.getTabFragment().equalsIgnoreCase( tabFragment ))
                        try {
                            logger.dbg( "Is of tab: %s, activating state for it.", tab );
                            controller.activateTabWithState( tab, fragment );
                            return;
                        }
                        catch (IncompatibleStateException e) {
                            controller.onError( e );
                            return;
                        }
                }
            }

            // No fragment, fragment not recognised or fragment could not be applied, find and set a default tab.
            for (final FragmentNavigationTab<? extends P, ? extends S> tab : controller.getTabs())
                if (tab.isInNavigation()) {
                    controller.activateNewTab( tab );
                    return;
                }

            throw new IllegalStateException( "Could not activate a tab for page; no tabs are visible." );
        }
    }


    /**
     * Install me on the AjaxRequestTargets that can affect navigation (including those that can affect tab state).
     */
    class AjaxRequestListener<P extends Panel, S extends FragmentState> implements AjaxRequestTarget.IListener {

        static final Logger logger = Logger.get( AjaxRequestListener.class );

        private final Controller<P, S> controller;
        private       String           newFragment;

        /**
         * @param controller The object that controls fragment state for this page.
         */
        private AjaxRequestListener(final Controller<P, S> controller) {

            this.controller = controller;
        }

        public static <P extends Panel, S extends FragmentState> AjaxRequestListener<P, S> of(final Controller<P, S> controller) {

            return new AjaxRequestListener<P, S>( controller );
        }

        @Override
        public void onBeforeRespond(final Map<String, Component> map, final AjaxRequestTarget target) {

            FragmentNavigationTab<? extends P, ? extends S> tab = controller.getActiveTab();
            updateTabComponents( tab );
        }

        @Override
        public void onAfterRespond(final Map<String, Component> map, final AjaxRequestTarget.IJavascriptResponse response) {

            Collection<? extends Component> components = null;
            if (AjaxRequestTarget.get() != null)
                components = AjaxRequestTarget.get().getComponents();
            logger.dbg( "onAfterRespond for components: %s", components );

            updatePageFragment( response );
        }

        private <PP extends P> void updateTabComponents(@Nullable final FragmentNavigationTab<PP, ? extends S> activeTab) {

            if (activeTab == null)
                // No active tab, no page fragment to update.
                return;

            PP contentPanel = controller.getContent( activeTab );
            if (!AjaxRequestTarget.get().getPage().contains( contentPanel, true ))
                // Active panel is not on the AJAX request's page, tab does not apply to this page's fragment.
                return;

            newFragment = activeTab.buildFragmentState( contentPanel ).toFragment();
            if (!ObjectUtils.isEqual( newFragment, controller.getPageFragment() ))
                controller.updateNavigationComponents();
        }

        private void updatePageFragment(final AjaxRequestTarget.IJavascriptResponse response) {

            if (!ObjectUtils.isEqual( newFragment, controller.getPageFragment() )) {
                controller.setPageFragment( newFragment );
                response.addJavascript( "document.location.hash = " + JSUtils.toString( newFragment ) );
            }
        }
    }
}
