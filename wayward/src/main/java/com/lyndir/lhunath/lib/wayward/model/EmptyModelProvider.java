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
package com.lyndir.lhunath.lib.wayward.model;

import org.apache.wicket.model.IModel;


/**
 * <h2>{@link EmptyModelProvider}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 11, 2010</i> </p>
 *
 * @param <P> This type.
 *
 * @author lhunath
 */
public abstract class EmptyModelProvider<P extends EmptyModelProvider<P>> extends ModelProvider<P, Object> {

    /**
     * Create a new {@link EmptyModelProvider} instance.
     */
    protected EmptyModelProvider() {

        super( null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public Object getObject() {

        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public void setObject(final Object object) {

        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public void setWrappedModel(final IModel<Object> wrappedModel) {

        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public IModel<Object> getWrappedModel() {

        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public Object getModelObject() {

        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public void setModelObject(final Object object) {

        throw new UnsupportedOperationException();
    }
}
