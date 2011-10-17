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
import com.lyndir.lhunath.opal.wayward.navigation.*;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link TabActivator}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 21, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class TabActivator<P extends Panel, S extends TabState<P>, T extends TabDescriptor<P, S>> implements ComponentActivator {

    static final Logger logger = Logger.get( TabActivator.class );
    private final T tab;

    protected TabActivator(final T tab) {

        this.tab = tab;
    }

    @Override
    public boolean isActivatable() {

        return findController() != null;
    }

    @Override
    public boolean isActive() {

        NavigationController controller = findController();
        return controller != null && tab.equals( controller.getActiveTab() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() {

        logger.dbg( "Activating %s", tab );
        findController().activateNewTab( tab );
    }

    protected abstract NavigationController findController();
}
