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
package com.lyndir.lhunath.opal.wayward.state;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.wayward.RedirectToPageException;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;


/**
 * <h2>{@link PageActivator}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 21, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class PageActivator implements ComponentActivator {

    static final Logger logger = Logger.get( PageActivator.class );

    private final Class<? extends Page> pageClass;

    /**
     * @param pageClass The page that we're activating when the state is right.
     */
    protected PageActivator(final Class<? extends Page> pageClass) {

        this.pageClass = pageClass;
    }

    @Override
    public boolean isActivatable() {

        Class<? extends Page> responsePageClass = RequestCycle.get().getResponsePageClass();
        return responsePageClass != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {

        Class<? extends Page> responsePageClass = RequestCycle.get().getResponsePageClass();
        return pageClass.isAssignableFrom( responsePageClass );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {

        logger.dbg( "Activating %s", pageClass );

        throw new RedirectToPageException( pageClass );
    }
}
