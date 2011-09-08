package com.lyndir.lhunath.opal.wayward.navigation;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import com.lyndir.lhunath.opal.wayward.js.JSUtils;
import java.util.Collection;
import java.util.Map;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.jetbrains.annotations.Nullable;


/**
 * Install me on the AjaxRequestTargets that can affect navigation (including those that can affect tab state).
 */
public class NavigationAjaxRequestListener implements AjaxRequestTarget.IListener {

    static final Logger logger = Logger.get( NavigationAjaxRequestListener.class );

    private final NavigationController controller;
    private       String               newFragment;

    /**
     * @param controller The object that controls fragment state for this page.
     */
    public NavigationAjaxRequestListener(final NavigationController controller) {

        this.controller = controller;
    }

    public static NavigationAjaxRequestListener of(final NavigationController controller) {

        return new NavigationAjaxRequestListener( controller );
    }

    @Override
    public void onBeforeRespond(final Map<String, Component> map, final AjaxRequestTarget target) {

        updateTabComponents( (TabDescriptor) controller.getActiveTab() );
    }

    @Override
    public void onAfterRespond(final Map<String, Component> map, final AjaxRequestTarget.IJavascriptResponse response) {

        Collection<? extends Component> components = null;
        if (AjaxRequestTarget.get() != null)
            components = AjaxRequestTarget.get().getComponents();
        logger.dbg( "onAfterRespond for components: %s", components );

        updatePageFragment( response );
    }

    private <T extends TabDescriptor<P, S>, S extends TabState<P>, P extends Panel> void updateTabComponents(@Nullable final T activeTab) {

        if (activeTab == null)
            // No active tab, no page fragment to update.
            return;

        P contentPanel = controller.getContent( activeTab );
        if (!AjaxRequestTarget.get().getPage().contains( contentPanel, true ))
            // Active panel is not on the AJAX request's page, tab does not apply to this page's fragment.
            return;

        newFragment = controller.toFragment( activeTab, contentPanel );
        if (!ObjectUtils.isEqual( newFragment, controller.getPageFragment() ))
            controller.updateNavigationComponents( contentPanel );
    }

    private void updatePageFragment(final AjaxRequestTarget.IJavascriptResponse response) {

        if (!ObjectUtils.isEqual( newFragment, controller.getPageFragment() )) {
            controller.setPageFragment( newFragment );
            response.addJavascript( "document.location.hash = " + JSUtils.toString( newFragment ) );
        }
    }
}
