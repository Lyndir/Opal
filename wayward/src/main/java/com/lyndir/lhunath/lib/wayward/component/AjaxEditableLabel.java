package com.lyndir.lhunath.lib.wayward.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link AjaxEditableLabel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p><b>NOTE:</b> Do not put this component in an HTML tag that does not allow paragraph sub-elements or multiline labels will break.</p>
 *
 * <p> <i>05 25, 2010</i> </p>
 *
 * @author lhunath
 */
public class AjaxEditableLabel<T> extends EditableLabel<T> {

    private boolean clickToEdit;

    /**
     * Only use this convenience constructor if your generic type is String!
     *
     * @param id The wicket ID of the component.
     * @param model The model that holds the object that will be rendered in the label or field.
     */
    @SuppressWarnings( { "unchecked" })
    public AjaxEditableLabel(final String id, final IModel<T> model) {

        super( id, model );
    }

    /**
     * @param id The wicket ID of the component.
     * @param model The model that holds the object that will be rendered in the label or field.
     * @param modelType The type of the model object, for field conversion purposes.
     */
    public AjaxEditableLabel(final String id, final IModel<T> model, final Class<T> modelType) {

        super( id, model, modelType );
    }

    @Override
    protected void onInitialize() {

        super.onInitialize();

        setOutputMarkupId( true );

        getField( true ).add( new AjaxFormComponentUpdatingBehavior( "onBlur" ) {

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {

                AjaxEditableLabel.this.onUpdate( target );
            }
        } );
        getField( false ).add( new AjaxFormComponentUpdatingBehavior( "onBlur" ) {

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {

                AjaxEditableLabel.this.onUpdate( target );
            }
        } );

        getLabel().add( new AjaxEventBehavior( "onClick" ) {
            @Override
            protected void onEvent(final AjaxRequestTarget target) {

                setEditable( true );
                target.addComponent( AjaxEditableLabel.this );
            }

            @Override
            public boolean isEnabled(final Component component) {

                return isClickToEdit();
            }
        } );
    }

    /**
     * Override me to perform logic after the component has been updated by an AJAX request.
     *
     * <p>You must call <code>super.onUpdate(target)</code> at the end of your implementation.  Failing to do so will break certain features
     * such as clickToEdit.</p>
     *
     * @param target The AJAX request that updated the component.
     */
    @SuppressWarnings( { "UnusedParameters" })
    protected void onUpdate(final AjaxRequestTarget target) {

        if (isClickToEdit()) {
            setEditable( false );
            target.addComponent( this );
        }
    }

    /**
     * @param clickToEdit <code>true</code>: The non-editable label handles click events that turn the component editable.
     *
     * @return this.
     */
    public AjaxEditableLabel<T> setClickToEdit(final boolean clickToEdit) {

        this.clickToEdit = clickToEdit;
        return this;
    }

    /**
     * @return <code>true</code>: The non-editable label handles click events that turn the component editable.
     */
    public boolean isClickToEdit() {

        return clickToEdit;
    }

    @Override
    public AjaxEditableLabel<T> setEditable(boolean editable) {

        super.setEditable( editable );
        return this;
    }

    @Override
    public AjaxEditableLabel<T> setNullable(boolean nullable) {

        super.setNullable( nullable );
        return this;
    }

    @Override
    public AjaxEditableLabel<T> setMultiline(boolean multiline) {

        super.setMultiline( multiline );
        return this;
    }
}
