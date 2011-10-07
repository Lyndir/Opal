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
package com.lyndir.lhunath.opal.wayward.model;

import static com.google.common.base.Preconditions.*;

import com.lyndir.lhunath.opal.system.logging.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.model.*;


/**
 * <h2>{@link ModelProvider}<br> <sub>A base class for {@link IModel} providers.</sub></h2>
 *
 * <p> Model providers are classes that serve to extract the maintenance of a component's models from the component's logic. </p>
 *
 * <p> Generally, you would create a {@link ModelProvider} for each component that you have a top-level class for. The {@link
 * ModelProvider}
 * for your component should be the component's model.<br /> Then, create accessor methods in the {@link ModelProvider} for all the models
 * you'll need in your component. It is often helpful to replicate your component's component hierarchy in your {@link ModelProvider} by
 * using inner {@link ModelProvider} classes, providing accessor methods for them in their parent and setting them as the model for your
 * component's child components. </p>
 *
 * <p> <i>Mar 11, 2010</i> </p>
 *
 * @param <P> This type.
 * @param <M> The type of the base model for this provider.
 *
 * @author lhunath
 */
public abstract class ModelProvider<P extends ModelProvider<P, M>, M> implements IWrapModel<M> {

    static final Logger logger = Logger.get( ModelProvider.class );

    private transient IModel<P> model        = null;
    private           IModel<M> wrappedModel = null;

    private Component component = null;

    /**
     * @param model The base model.
     */
    protected ModelProvider(final IModel<M> model) {

        if (model != null)
            setWrappedModel( model );
    }

    /**
     * @return This model wrapped in another.
     */
    public IModel<P> getModel() {

        if (model == null)
            model = new AbstractReadOnlyModel<P>() {

                @Override
                @SuppressWarnings("unchecked")
                public P getObject() {

                    return (P) ModelProvider.this;
                }
            };

        return model;
    }

    /**
     * @param wrappedModel The base model.
     */
    public void setWrappedModel(final IModel<M> wrappedModel) {

        this.wrappedModel = wrappedModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IModel<M> getWrappedModel() {

        return wrappedModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public M getObject() {

        if (getWrappedModel() == null) {
            logger.wrn( "Attempt to getObject() while model is unset." );
            return null;
        }

        return getWrappedModel().getObject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObject(final M object) {

        if (getWrappedModel() == null) {
            logger.wrn( "Attempt to setObject(%s) while model is unset.", object );
            return;
        }

        getWrappedModel().setObject( object );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detach() {

        if (getWrappedModel() != null)
            getWrappedModel().detach();
    }

    /**
     * @return model The object of the base model.
     */
    public M getModelObject() {

        if (getWrappedModel() == null) {
            logger.wrn( "Attempt to getModelObject() while model is unset." );
            return null;
        }

        return getWrappedModel().getObject();
    }

    /**
     * @param object The new object of the base model.
     */
    public void setModelObject(final M object) {

        if (getWrappedModel() == null) {
            logger.wrn( "Attempt to setModelObject(%s) while model is unset.", object );
            return;
        }

        getWrappedModel().setObject( object );
    }

    private void setComponent(final Component component) {

        this.component = component;
    }

    /**
     * @param component The component we have been or will be attached to.
     */
    @SuppressWarnings({ "hiding", "ParameterHidesMemberVariable" })
    public void attach(final Component component) {

        setComponent( checkNotNull( component, "Can't attach model provider (%s) to null.", this ) );
    }

    /**
     * @return The component we've been attached to.
     */
    public Component getComponent() {

        return checkNotNull( component, "Model provider (%s) hasn't been attached yet.", this );
    }
}
