package com.lyndir.lhunath.lib.wayward.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link AjaxEditableLabel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 25, 2010</i> </p>
 *
 * @author lhunath
 */
public class AjaxEditableLabel<T> extends EditableLabel<T> {

    /**
     * @param id        The wicket ID of the component.
     * @param model     The model that holds the object that will be rendered in the label or field.
     * @param modelType The type of the model object, for field conversion purposes.
     */
    public AjaxEditableLabel(final String id, final IModel<T> model, final Class<T> modelType) {

        super( id, model, modelType );
        init();
    }

    /**
     * @param id                The wicket ID of the component.
     * @param model             The model that holds the object that will be rendered in the label or field.
     * @param modelType         The type of the model object, for field conversion purposes.
     * @param initiallyEditable <code>true</code> if the label should be editable from the start.
     */
    public AjaxEditableLabel(final String id, final IModel<T> model, final Class<T> modelType, final boolean initiallyEditable) {

        super( id, model, modelType, initiallyEditable );
        init();
    }

    private void init() {

        setOutputMarkupId( true );

        getField().add( new AjaxFormComponentUpdatingBehavior( "onBlur" ) {

            @Override
            protected void onUpdate(final AjaxRequestTarget target) {

                AjaxEditableLabel.this.onUpdate( target );
            }
        } );
    }

    /**
     * Override me to perform logic after the component has been updated by an AJAX request.
     *
     * @param target The AJAX request that updated the component.
     */
    protected void onUpdate(final AjaxRequestTarget target) {

    }
}
