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

import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link GenericWebComponent}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Jan 18, 2010</i>
 * </p>
 *
 * @author lhunath
 * @param <T>
 * The model type.
 */
public class GenericWebComponent<T> extends WebComponent {

    /**
     * Create a new {@link GenericWebComponent} instance.
     *
     * @param id    The component's wicket ID.
     * @param model The component's model.
     */
    public GenericWebComponent(String id, IModel<T> model) {

        super( id, model );
    }

    /**
     * Gets model
     *
     * @return model
     */
    @SuppressWarnings("unchecked")
    public final IModel<T> getModel() {

        return (IModel<T>) getDefaultModel();
    }

    /**
     * Sets model
     *
     * @param model
     */
    public final void setModel(IModel<T> model) {

        setDefaultModel( model );
    }

    /**
     * Gets model object
     *
     * @return model object
     */
    @SuppressWarnings("unchecked")
    public final T getModelObject() {

        return (T) getDefaultModelObject();
    }

    /**
     * Sets model object
     *
     * @param object
     */
    public final void setModelObject(T object) {

        setDefaultModelObject( object );
    }
}
