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

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentOnBeforeRenderListener;


/**
 * <h2>{@link ComponentStateListener}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 21, 2010</i> </p>
 *
 * @author lhunath
 */
public class ComponentStateListener implements IComponentOnBeforeRenderListener {

    protected final List<ComponentActivator> componentActivators;

    /**
     * @param componentActivators Component activators that should be checked during session attachment.
     */
    public ComponentStateListener(final ComponentActivator... componentActivators) {

        this.componentActivators = ImmutableList.copyOf( componentActivators );
    }

    @Override
    public void onBeforeRender(final Component component) {

        for (final ComponentActivator componentActivator : componentActivators)
            if (componentActivator.isActivatable() && componentActivator.isNecessary() && !componentActivator.isActive()) {
                componentActivator.activate();
                break;
            }
    }
}
