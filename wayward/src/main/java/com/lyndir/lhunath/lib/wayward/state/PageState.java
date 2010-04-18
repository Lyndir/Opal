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
package com.lyndir.lhunath.lib.wayward.state;

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.component.RedirectToPageException;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;


/**
 * <h2>{@link PageState}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 21, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public abstract class PageState implements ComponentState {

    static final Logger logger = Logger.get( PageState.class );

    private final Class<? extends Page> pageClass;


    /**
     * @param pageClass The page that we're activating when the state is right.
     */
    protected PageState(final Class<? extends Page> pageClass) {

        this.pageClass = pageClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {

        return pageClass.isAssignableFrom( RequestCycle.get().getResponsePageClass() );
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
