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
package com.lyndir.lhunath.opal.wayward.component;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link GenericFormComponent}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Jan 18, 2010</i> </p>
 *
 * @param <T> The model type.
 *
 * @author lhunath
 */
public class GenericFormComponent<T> extends GenericWebMarkupContainer<T> {

    /**
     * Create a new {@link GenericFormComponent} instance.
     *
     * @param id    The component's wicket ID.
     * @param model The component's model.
     */
    public GenericFormComponent(final String id, final IModel<T> model) {

        super( id, model );
    }

    /**
     * @see Component#onComponentTag(ComponentTag)
     */
    @Override
    protected void onComponentTag(final ComponentTag tag) {

        tag.put( "value", getDefaultModelObjectAsString() );

        super.onComponentTag( tag );
    }
}
