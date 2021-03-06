package com.lyndir.lhunath.opal.wayward.js;

import java.util.Map;
import org.apache.wicket.*;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;


/**
 * <h2>{@link AjaxHooks}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> AjaxHooks is a convenience implementation which will give you client-side access to AJAX update events and server-side access to
 * ready events (eg. after an anchor update). </p>
 *
 * <p> On the server side (in your Java code), you install the AjaxHooks by doing these two things: <ul> <li>Installing it on any visible
 * component that's part of the page ({@code AjaxHooks.installAjaxEvents( mycomponent )}).  This activates AjaxHooks for that
 * component's page.</li> <li>Installing it on your AjaxRequestTargets (for access to the list of updated elements).</li> </ul>
 *
 * The easiest and most consistent way to do the latter is by overriding Application#newAjaxRequestTarget:
 *
 * <pre>{@code
 * public AjaxRequestTarget newAjaxRequestTarget(final Page page) {
 *
 *     AjaxRequestTarget target = super.newAjaxRequestTarget( page );
 *     AjaxHooks.installAjaxEvents(target);
 * }
 * }</pre>
 *
 * <p> <i>05 31, 2010</i> </p>
 */
public abstract class AjaxHooks {

    /**
     * Adds a header contribution to the component which will install client-side AjaxHooks support on the component's page.
     *
     * You can then register a client-side JavaScript function using AjaxHooks.registerPostAjax() which will be fired after each AJAX call
     * with the updated elements as array argument.
     *
     * @param component The component whose page to add support for AjaxHooks to.
     */
    public static void installAjaxEvents(final Component component) {

        component.add( JavascriptPackageResource.getHeaderContribution( AjaxHooks.class, "AjaxHooks.js" ) );
    }

    /**
     * Adds an IListener to the AjaxRequestTarget which will install server-side AjaxHooks support for the given request target.
     *
     * This is necessary to fire the functions registered with AjaxHooks.registerPostAjax after the target has completed.
     *
     * @param target The request target to add AjaxHooks support to.
     */
    public static void installAjaxEvents(final AjaxRequestTarget target) {

        target.addListener( new AjaxRequestTarget.IListener() {

            @Override
            public void onBeforeRespond(final Map<String, Component> map, final AjaxRequestTarget target_) {

            }

            @Override
            public void onAfterRespond(final Map<String, Component> map, final AjaxRequestTarget.IJavascriptResponse response) {

                response.addJavascript( JSUtils.callFunction( "AjaxHooks.setUpdatedDomIds", map.keySet() ) );
            }
        } );
    }

    /**
     * Adds a behaviour to the component which will install client-side support for notifying the server-side about page-ready events.
     *
     * The listener will be triggered each time the page that the component is on becomes ready (reloads or has its anchor updated).
     *
     * @param component The component whose page to add support for page events to.
     * @param listener  The listener to fire when the page is updated.
     */
    public static void installPageEvents(final Component component, final IPageListener listener) {

        component.add( new AbstractDefaultAjaxBehavior() {

            @Override
            protected CharSequence getCallbackScript(final boolean onlyTargetActivePage) {

                return generateCallbackScript(
                        String.format( "wicketAjaxGet('%s&pageUrl='+wicketEncode(document.location.href)", getCallbackUrl() ) );
            }

            @Override
            public void renderHead(final IHeaderResponse response) {

                super.renderHead( response );
                response.renderOnDomReadyJavascript( getCallbackScript().toString() );
            }

            @Override
            protected void respond(final AjaxRequestTarget target) {

                RequestCycle requestCycle = RequestCycle.get();
                String pageUrl = requestCycle.getRequest().getParameter( "pageUrl" );

                listener.onReady( target, pageUrl );
            }
        } );
    }

    public interface IPageListener extends IClusterable {

        void onReady(AjaxRequestTarget target, String pageUrl);
    }
}
