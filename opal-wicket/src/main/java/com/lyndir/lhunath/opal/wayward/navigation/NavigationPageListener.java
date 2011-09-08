package com.lyndir.lhunath.opal.wayward.navigation;

import com.google.common.base.Splitter;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.js.AjaxHooks;
import java.net.URI;
import org.apache.wicket.ajax.AjaxRequestTarget;


/**
 * Install me on the page where tab navigation should happen.
 */
public class NavigationPageListener implements AjaxHooks.IPageListener {

    static final Logger logger = Logger.get( NavigationPageListener.class );

    private final NavigationController controller;

    /**
     * @param controller The object that controls fragment state for this page.
     */
    public NavigationPageListener(final NavigationController controller) {

        this.controller = controller;
    }

    public static NavigationPageListener of(final NavigationController controller) {

        return new NavigationPageListener( controller );
    }

    @Override
    public void onReady(final AjaxRequestTarget target, final String pageUrl) {

        String fragment = URI.create( pageUrl ).getFragment();
        controller.setPageFragment( fragment );

        logger.dbg( "Found fragment: %s", fragment );
        if (fragment != null) {
            // There is a fragment, load state from it.
            String tabFragment = Splitter.on( '/' ).split( fragment ).iterator().next();

            //noinspection RawUseOfParameterizedType
            for (final TabDescriptor tab : controller.getTabs()) {
                if (tab.getFragment().equalsIgnoreCase( tabFragment ))
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
        for (final TabDescriptor tab : controller.getTabs())
            if (tab.shownInNavigation()) {
                controller.activateNewTab( tab );
                return;
            }

        throw new IllegalStateException( "Could not activate a tab for page; no tabs are visible." );
    }
}
