/*
 * SafeOnline project.
 *
 * Copyright 2006-2009 Lin.k N.V. All rights reserved.
 * Lin.k N.V. proprietary/confidential. Use is subject to license terms.
 */
package com.lyndir.lhunath.opal.wayward.behavior;

import com.lyndir.lhunath.opal.wayward.js.JSUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractHeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;


/**
 * <h2>{@link FocusOnReady}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Jan 21, 2009</i> </p>
 *
 * @author lhunath
 */
public class FocusOnReady extends AbstractHeaderContributor {

    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private IHeaderContributor headerContributor;

    @Override
    public void bind(final Component component) {

        component.setOutputMarkupId( true );
        headerContributor = new IHeaderContributor() {

            @Override
            public void renderHead(final IHeaderResponse response) {

                if (component.isVisibleInHierarchy()) {
                    String id = component.getMarkupId( true );
                    response.renderOnDomReadyJavascript( String.format( "document.getElementById(%s).focus()", JSUtils.toString( id ) ) );
                }
            }
        };
    }

    @Override
    public IHeaderContributor[] getHeaderContributors() {

        return new IHeaderContributor[]{ headerContributor };
    }
}
