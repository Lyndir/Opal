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

import org.apache.wicket.*;


/**
 * <h2>{@link AbstractComponentActivator}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 21, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AbstractComponentActivator implements ComponentActivator {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {

        Page responsePage = RequestCycle.get().getResponsePage();
        return responsePage.contains( getComponent(), true ) && getComponent().isVisibleInHierarchy();
    }

    protected abstract Component getComponent();
}
