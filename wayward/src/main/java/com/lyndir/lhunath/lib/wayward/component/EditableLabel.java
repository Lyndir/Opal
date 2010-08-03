package com.lyndir.lhunath.lib.wayward.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link EditableLabel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 25, 2010</i> </p>
 *
 * @author lhunath
 */
public class EditableLabel<T> extends GenericPanel<T> {

    private final Label        label;
    private final TextField<T> field;
    private       boolean      editable;

    /**
     * @param id        The wicket ID of the component.
     * @param model     The model that holds the object that will be rendered in the label or field.
     * @param modelType The type of the model object, for field conversion purposes.
     */
    public EditableLabel(final String id, final IModel<T> model, final Class<T> modelType) {

        super( id, model );

        add( label = new Label( "label", model ) {

            @Override
            public boolean isVisible() {

                return !isEditable();
            }
        } );
        add( field = new TextField<T>( "field", model, modelType ) {

            @Override
            public boolean isVisible() {

                return isEditable();
            }
        } );
    }

    /**
     * @param id                The wicket ID of the component.
     * @param model             The model that holds the object that will be rendered in the label or field.
     * @param modelType         The type of the model object, for field conversion purposes.
     * @param initiallyEditable <code>true</code> if the label should be editable from the start.
     */
    public EditableLabel(final String id, final IModel<T> model, final Class<T> modelType, final boolean initiallyEditable) {

        this( id, model, modelType );
        editable = initiallyEditable;
    }

    /**
     * @param editable <code>true</code> to make the label editable by rendering its object in a field and updating the model when that
     *                 field is submitted.
     */
    public void setEditable(final boolean editable) {

        this.editable = editable;
    }

    /**
     * @return <code>true</code> makes the label editable by rendering its object in a field and updating the model when that field is
     *         submitted.
     */
    public boolean isEditable() {

        return editable;
    }

    /**
     * @return The component that renders the model object as a non-editable label.
     */
    protected Label getLabel() {
        return label;
    }

    /**
     * @return The component that renders the model object as an editable text field.
     */
    public TextField<T> getField() {
        return field;
    }
}
