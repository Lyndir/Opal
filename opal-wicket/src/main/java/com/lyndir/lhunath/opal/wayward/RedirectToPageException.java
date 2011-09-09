/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.opal.wayward;

import org.apache.wicket.*;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;


/**
 * <h2>{@link RedirectToPageException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Feb 5, 2010</i> </p>
 *
 * @author lhunath
 */
public class RedirectToPageException extends AbstractRestartResponseException {

    /**
     * Create a new {@link RedirectToPageException} instance.
     *
     * @param pageClass The class of the page to redirect to.
     */
    public RedirectToPageException(final Class<? extends Page> pageClass) {

        this( pageClass, null );
    }

    /**
     * Create a new {@link RedirectToPageException} instance.
     *
     * @param pageClass  The class of the page to redirect to.
     * @param parameters The parameters to pass to the page.
     */
    public RedirectToPageException(final Class<? extends Page> pageClass, final PageParameters parameters) {

        RequestCycle rc = RequestCycle.get();
        if (rc == null)
            throw new IllegalStateException( "This exception can only be thrown from within request processing cycle" );
        Response r = rc.getResponse();
        if (!(r instanceof WebResponse))
            throw new IllegalStateException( "This exception can only be thrown when wicket is processing an http request" );

        // abort any further response processing
        rc.setRequestTarget( new RedirectRequestTarget( rc.urlFor( pageClass, parameters ).toString() ) );
    }
}
