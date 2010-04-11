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

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;


/**
 * <h2>{@link DefaultTextField}<br>
 * <sub>Text field that uses a default value if its model object is <code>null</code>.</sub></h2>
 *
 * <p>
 * <i>Jan 21, 2010</i>
 * </p>
 *
 * @author lhunath
 * @param <T>
 * The model type.
 */
public class DefaultTextField<T> extends TextField<T> {

    private IModel<String> defaultValue;


    /**
     * Create a new {@link DefaultTextField} instance.
     *
     * @param id    The component's wicket ID.
     * @param model The component's model.
     */
    public DefaultTextField(String id, IModel<T> model) {

        super( id, model );

        setDefaultValue( new StringResourceModel( "none", this, null ) );
    }

    /**
     * Create a new {@link DefaultTextField} instance.
     *
     * @param id           The component's wicket ID.
     * @param model        The component's model.
     * @param defaultValue The model that provides the default value which will be used when the model doesn't provide an object.
     */
    public DefaultTextField(String id, IModel<T> model, IModel<String> defaultValue) {

        super( id, model );

        this.defaultValue = defaultValue;
    }

    /**
     * @return The defaultValue of this {@link DefaultTextField}.
     */
    public IModel<String> getDefaultValue() {

        return defaultValue;
    }

    /**
     * @param defaultValue The defaultValue of this {@link DefaultTextField}.
     */
    public void setDefaultValue(IModel<String> defaultValue) {

        this.defaultValue = defaultValue;
    }

    @Override
    protected String getModelValue() {

        if (getModelObject() == null)
            return defaultValue.getObject();

        return super.getModelValue();
    }
}
