package com.lyndir.lhunath.lib.wayward.navigation;

import com.google.common.base.Splitter;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import com.lyndir.lhunath.lib.wayward.js.AjaxHooks;
import com.lyndir.lhunath.lib.wayward.js.JSUtils;
import java.net.URI;
import java.util.Map;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link FragmentNavigationListener}<br> <sub>Listeners that implement support for fragment-based AJAX content navigation.</sub></h2>
 *
 * <p> To enable fragment-based navigation in your application, install the PageListener on the pages you want to enable fragment-based
 * navigation on, and add the AjaxRequestListener to each AjaxRequestTarget that can cause content state or navigation to change.</p>
 *
 * <p>The easiest way to do the latter is by overriding WebApplication#newAjaxRequestTarget(Page page) and adding the AjaxRequestListener to
 * the target there.</p>
 *
 * <p> <i>07 06, 2010</i> </p>
 *
 * @author lhunath
 */
public interface FragmentNavigationListener {

    abstract class Controller<P extends Panel, S extends FragmentState, T extends FragmentNavigationTab<? extends P, ? extends S>>
            implements IClusterable {

        static final Logger logger = Logger.get( Controller.class );

        private String pageFragment;
        private T      activeTab;

        /**
         * Mark the given tab as active and restore state in its contents from the given tab-specific state arguments.
         *
         * @param tab      The tab that should be activated.
         * @param fragment The string that contains the fragment which needs to be parsed into tab-specific state.
         *
         * @throws IncompatibleStateException If the state represented by the fragment is incompatible with the current state and can not be
         *                                    loaded.
         */
        <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void activateTabWithState(final TT tab,
                                                                                                         final String fragment)
                throws IncompatibleStateException {

            activateTabWithState( tab, tab.getState( fragment ) );
        }

        /**
         * Mark the given tab as active and restore state in its contents from the given tab-specific state arguments.
         *
         * @param tab   The tab that should be activated.
         * @param state The tab-specific state that should be applied to the tab's content.
         *
         * @throws IncompatibleStateException If the state is incompatible with the current state and can not be applied.
         */
        public <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void activateTabWithState(final TT tab,
                                                                                                                final SS state)
                throws IncompatibleStateException {

            PP tabPanel = tab.getPanel( getTabContentId() );
            tab.applyFragmentState( tabPanel, state );
            activateTab( tab, tabPanel );
        }

        /**
         * Mark the given tab as active and create a new content panel for the tab.
         *
         * @param tab The tab that should be activated.
         */
        public <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void activateNewTab(final TT tab) {

            activateTab( tab, null );
        }

        /**
         * Mark the given tab as active and use the given tabPanel for its content.
         *
         * @param tab      The tab that should be activated.
         * @param tabPanel The panel that provides the tab's content or <code>null</code> if a new content panel should be created for the
         *                 tab.
         */
        public <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void activateTab(final TT tab, PP tabPanel) {

            if (tabPanel == null)
                tabPanel = tab.getPanel( getTabContentId() );
            tabPanel.setOutputMarkupPlaceholderTag( true );

            Page responsePage = RequestCycle.get().getResponsePage();
            if (responsePage == null || getTabExclusivePage() == null || responsePage.getClass().equals( getTabExclusivePage() )) {
                // Request is a page request from this page, not a page request or pageClass is unset (we don't care about page redirects).
                // Activate the tab on the current page.
                logger.dbg( "Updating tab in response page: %s (tab exclusive page: %s)", responsePage, getTabExclusivePage() );

                // Cast TT to T because Java can't constrain TT to both T and FNT<PP,SS>
                @SuppressWarnings( { "unchecked" })
                T _tab = (T) tab;
                activeTab = _tab;
                setActiveTab( tab, tabPanel );

                updateNavigationComponents();
            } else {
                // PageClass is set and the current request is a page request from another page; redirect to tab on pageClass.
                CharSequence pageUrl = RequestCycle.get().urlFor( getTabExclusivePage(), null );
                String tabFragment = tab.buildFragmentState( tabPanel ).toFragment();

                logger.dbg( "Redirecting to tab-exclusive page: %s (url: %s), fragment: %s", getTabExclusivePage(), pageUrl, tabFragment );
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
        public String getPageFragment() {

            return pageFragment;
        }

        /**
         * @param pageFragment The new fragment of the page.
         */
        public void setPageFragment(final String pageFragment) {

            this.pageFragment = pageFragment;
        }

        /**
         * @return The currently active tab.
         */
        public T getActiveTab() {

            return activeTab;
        }

        /**
         * @return The page on which the tabs should be showing.  If not <code>null</code>; any time a tab is activated on another page; the
         *         user will be redirected to a url on this page with the activated tab's state in the url fragment.
         */
        protected Class<? extends Page> getTabExclusivePage() {

            return null;
        }

        /**
         * @return The component that represents the content of the currently active tab.
         */
        protected abstract Component getActiveContent();

        /**
         * @return All components that should be updated whenever page navigation changes.
         */
        protected abstract Iterable<? extends Component> getNavigationComponents();

        /**
         * Invoked when a page is loaded to indicate the page's active tab as determined by fragment state.
         *
         * @param tab      The tab that needs to be activated.
         * @param tabPanel The panel that contains the tab's content as determined by fragment state.
         */
        protected abstract <TT extends FragmentNavigationTab<PP, SS>, PP extends P, SS extends S> void setActiveTab(final TT tab,
                                                                                                                    final Panel tabPanel);

        /**
         * @return The wicket ID that the tab's content panel should bind to when generated to apply fragment state on it.
         */
        protected abstract String getTabContentId();

        /**
         * Note:   The order should reflect the defaulting preference.  When no tab is selected by the fragment (or there is no fragment),
         * the first tab will be used instead, if visible.  If not visible, the next one will be tried, and so on.
         *
         * @return The application's tabs.
         */
        protected abstract Iterable<T> getTabs();

        /**
         * Handle errors that occur when attempting to apply state that is incompatible with the current state.
         *
         * @param e The error that occurred.
         */
        protected abstract void onError(final IncompatibleStateException e);
    }


    class PageListener<P extends Panel, S extends FragmentState, T extends FragmentNavigationTab<? extends P, ? extends S>>
            implements AjaxHooks.IPageListener {

        static final Logger logger = Logger.get( PageListener.class );

        private final Controller<P, S, T> controller;

        /**
         * @param controller The object that controls fragment state for this page.
         */
        private PageListener(final Controller<P, S, T> controller) {

            this.controller = controller;
        }

        public static <P extends Panel, S extends FragmentState, T extends FragmentNavigationTab<? extends P, ? extends S>> PageListener<P, S, T> of(
                final Controller<P, S, T> controller) {

            return new PageListener<P, S, T>( controller );
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
                    if (tab.getTabFragment().equalsIgnoreCase( tabFragment )) {
                        logger.dbg( "Is of tab: %s, activating state for it.", tab );
                        try {
                            controller.activateTabWithState( tab, fragment );
                            return;
                        }
                        catch (IncompatibleStateException e) {
                            controller.onError( e );
                        }

                        break;
                    }
                }
            }

            // No fragment, fragment not recognised or fragment could not be applied, find and set a default tab.
            for (final FragmentNavigationTab<? extends P, ? extends S> tab : controller.getTabs()) {
                if (tab.isVisible()) {
                    controller.activateNewTab( tab );
                    return;
                }
            }

            throw logger.err( "Could not activate a tab for page; no tabs are visible." ).toError( IllegalStateException.class );
        }
    }


    class AjaxRequestListener<P extends Panel, S extends FragmentState, T extends FragmentNavigationTab<? extends P, ? extends S>>
            implements AjaxRequestTarget.IListener {

        static final Logger logger = Logger.get( AjaxRequestListener.class );

        private final Controller<P, S, T> controller;
        private       String              newFragment;

        /**
         * @param controller The object that controls fragment state for this page.
         */
        private AjaxRequestListener(final Controller<P, S, T> controller) {

            this.controller = controller;
        }

        public static <P extends Panel, S extends FragmentState, T extends FragmentNavigationTab<? extends P, ? extends S>> AjaxRequestListener<P, S, T> of(
                final Controller<P, S, T> controller) {

            return new AjaxRequestListener<P, S, T>( controller );
        }

        @Override
        public void onBeforeRespond(final Map<String, Component> map, final AjaxRequestTarget target) {

            FragmentNavigationTab<? extends P, ? extends S> tab = controller.getActiveTab();
            updateTabComponents( tab );
        }

        @Override
        public void onAfterRespond(final Map<String, Component> map, final AjaxRequestTarget.IJavascriptResponse response) {

            updatePageFragment( response );
        }

        private <PP extends P> void updateTabComponents(final FragmentNavigationTab<PP, ? extends S> activeTab) {

            Class<PP> panelClass = activeTab.getPanelClass();
            Component contentPanel = controller.getActiveContent();

            if (panelClass.isInstance( contentPanel )) {
                newFragment = activeTab.buildFragmentState( panelClass.cast( contentPanel ) ).toFragment();

                if (!ObjectUtils.equal( newFragment, controller.getPageFragment() ))
                    controller.updateNavigationComponents();
            }
        }

        private void updatePageFragment(final AjaxRequestTarget.IJavascriptResponse response) {

            response.addJavascript( "window.location.hash = " + JSUtils.toString( newFragment ) );
        }
    }
}
