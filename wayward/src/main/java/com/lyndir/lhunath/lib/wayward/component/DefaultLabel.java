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
package com.lyndir.lhunath.lib.wayward.component;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.*;


/**
 * <h2>{@link DefaultLabel}<br> <sub>Text field that uses a default value if its model object is <code>null</code>.</sub></h2>
 *
 * <p> <i>Jan 21, 2010</i> </p>
 *
 * @author lhunath
 */
public class DefaultLabel extends Label {

    private IModel<String> defaultValue = null;

    /**
     * Create a new {@link DefaultLabel} instance.
     *
     * @param id          The component's wicket ID.
     * @param modelObject The component's model value.
     */
    public DefaultLabel(final String id, final String modelObject) {

        this( id, new Model<String>( modelObject ) );
    }

    /**
     * Create a new {@link DefaultLabel} instance.
     *
     * @param id    The component's wicket ID.
     * @param model The component's model.
     */
    public DefaultLabel(final String id, final IModel<String> model) {

        super( id, model );

        setDefaultValue( new StringResourceModel( "none", this, null ) );
    }

    /**
     * Create a new {@link DefaultLabel} instance.
     *
     * @param id           The component's wicket ID.
     * @param model        The component's model.
     * @param defaultValue The model that provides the default value which will be used when the model doesn't provide an object.
     */
    public DefaultLabel(final String id, final IModel<String> model, final IModel<String> defaultValue) {

        super( id, model );

        this.defaultValue = defaultValue;
    }

    /**
     * @return The defaultValue of this {@link DefaultLabel}.
     */
    public IModel<String> getDefaultValue() {

        return defaultValue;
    }

    /**
     * @param defaultValue The defaultValue of this {@link DefaultLabel}.
     */
    public void setDefaultValue(final IModel<String> defaultValue) {

        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

        if (getDefaultModelObject() == null)
            replaceComponentTagBody( markupStream, openTag, getDefaultModelObjectAsString( defaultValue.getObject() ) );
        else
            super.onComponentTagBody( markupStream, openTag );
    }
}
