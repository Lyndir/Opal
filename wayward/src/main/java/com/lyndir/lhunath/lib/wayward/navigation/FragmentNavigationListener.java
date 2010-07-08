package com.lyndir.lhunath.lib.wayward.navigation;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.lyndir.lhunath.lib.wayward.js.AjaxHooks;
import com.lyndir.lhunath.lib.wayward.js.JSUtils;
import java.net.URI;
import java.util.Map;
import org.apache.wicket.Component;
import org.apache.wicket.IClusterable;
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

    abstract class Controller implements IClusterable {

        /**
         * Mark the given tab as active and restore state in its contents from the given tab-specific state arguments.
         *
         * @param tab   The tab that should be activated.
         * @param state The tab-specific state that should be applied to the tab's content.
         * @param <P>   The type of the tab's content.
         */
        public <P extends Panel> void activateTabWithState(final FragmentNavigationTab<P> tab, final String... state) {

            P tabPanel = tab.getPanel( getTabContentId() );
            tab.applyFragmentState( tabPanel, state );

            activateTab( tab, tabPanel );
        }

        /**
         * Mark the given tab as active and create a new content panel for the tab.
         *
         * @param tab The tab that should be activated.
         */
        public void activateTab(final FragmentNavigationTab<?> tab) {

            activateTab( tab, null );
        }

        /**
         * Mark the given tab as active and use the given tabPanel for its content.
         *
         * @param tab      The tab that should be activated.
         * @param tabPanel The panel that provides the tab's content or <code>null</code> if a new content panel should be created for the
         *                 tab.
         */
        public void activateTab(final FragmentNavigationTab<?> tab, Panel tabPanel) {

            if (tabPanel == null)
                tabPanel = tab.getPanel( getTabContentId() );
            tabPanel.setOutputMarkupPlaceholderTag( true );

            AjaxRequestTarget target = AjaxRequestTarget.get();
            if (target != null)
                target.addComponent( tabPanel );

            setActiveTab( tab, tabPanel );
        }

        /**
         * Invoked when a page is loaded to indicate the page's active tab as determined by fragment state.
         *
         * @param tab      The tab that needs to be activated.
         * @param tabPanel The panel that contains the tab's content as determined by fragment state.
         */
        protected abstract void setActiveTab(final FragmentNavigationTab<?> tab, final Panel tabPanel);

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
        protected abstract Iterable<FragmentNavigationTab<?>> getTabs();
    }


    class PageListener implements AjaxHooks.IPageListener {

        private final Controller controller;

        /**
         * @param controller The object that controls fragment state for this page.
         */
        public PageListener(final Controller controller) {

            this.controller = controller;
        }

        @Override
        public void onReady(final AjaxRequestTarget target, final String pageUrl) {

            URI uri = URI.create( pageUrl );
            if (uri.getFragment() == null) {
                // No fragment, find and set a default tab.
                for (final FragmentNavigationTab<?> tab : controller.getTabs()) {
                    if (tab.isVisible()) {
                        controller.activateTab( tab );
                        break;
                    }
                }
            } else {
                // There is a fragment, load state from it.
                Iterable<String> arguments = Splitter.on( '/' ).split( uri.getFragment() );
                String tabFragment = arguments.iterator().next();

                for (final FragmentNavigationTab<?> tab : controller.getTabs()) {
                    if (tab.getFragment().equalsIgnoreCase( tabFragment ) && tab.isVisible()) {
                        controller.activateTabWithState( tab, Iterables.toArray( arguments, String.class ) );
                        break;
                    }
                }
            }
        }
    }


    abstract class AjaxRequestListener implements AjaxRequestTarget.IListener {

        @Override
        public void onBeforeRespond(final Map<String, Component> map, final AjaxRequestTarget target) {

        }

        @Override
        public void onAfterRespond(final Map<String, Component> map, final AjaxRequestTarget.IJavascriptResponse response) {

            updatePageFragment( getActiveTab(), response );
        }

        private <P extends Panel> void updatePageFragment(final FragmentNavigationTab<P> activeTab, final AjaxRequestTarget.IJavascriptResponse response) {

            Class<P> panelClass = activeTab.getPanelClass();
            Component contentPanel = getActiveContent();

            if (panelClass.isInstance( contentPanel )) {
                StringBuilder fragmentBuilder = new StringBuilder( activeTab.getFragment() ).append( '/' );
                fragmentBuilder.append( Joiner.on( '/' ).join( activeTab.getFragmentState( panelClass.cast( contentPanel ) ) ) );

                response.addJavascript( "window.location.hash = " + JSUtils.toString( fragmentBuilder.toString() ) );
            }
        }

        protected abstract Component getActiveContent();

        protected abstract FragmentNavigationTab<?> getActiveTab();
    }
}
