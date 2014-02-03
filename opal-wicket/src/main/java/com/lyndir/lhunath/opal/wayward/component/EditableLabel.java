package com.lyndir.lhunath.opal.wayward.component;

import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link EditableLabel}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 25, 2010</i> </p>
 *
 * @author lhunath
 */
public class EditableLabel<T> extends GenericPanel<T> {

    private final TextField<T> singleLineField;
    private final TextArea<T>  multiLineField;
    private       WebComponent label;
    private boolean nullable = true;
    private boolean multiline;
    private boolean editable;

    /**
     * Only use this convenience constructor if your generic type is String!
     *
     * @param id    The wicket ID of the component.
     * @param model The model that holds the object that will be rendered in the label or field.
     */
    @SuppressWarnings({ "unchecked" })
    public EditableLabel(final String id, final IModel<T> model) {

        this( id, model, (Class<T>) String.class );
    }

    /**
     * @param id        The wicket ID of the component.
     * @param model     The model that holds the object that will be rendered in the label or field.
     * @param modelType The type of the model object, for field conversion purposes.
     */
    public EditableLabel(final String id, final IModel<T> model, final Class<T> modelType) {

        super( id, model );

        IModel<T> myModel = new IModel<T>() {
            @Override
            public T getObject() {

                return getModel().getObject();
            }

            @Override
            public void setObject(final T object) {

                getModel().setObject( object );
            }

            @Override
            public void detach() {

                // Let me detach myself.
            }
        };

        add( label = newLabel( myModel ) );
        add( singleLineField = new TextField<T>( "singleLineField", myModel, modelType ) {

            @Override
            public boolean isVisible() {

                return isEditable() && !isMultiline();
            }

            @Override
            public boolean isInputNullable() {

                return nullable;
            }
        } );
        add( multiLineField = new TextArea<T>( "multiLineField", myModel ) {

            @Override
            public boolean isVisible() {

                return isEditable() && isMultiline();
            }

            @Override
            public boolean isInputNullable() {

                return nullable;
            }
        } );
    }

    private WebComponent newLabel(final IModel<?> model) {

        if (isMultiline())
            return new MultiLineLabel( "label", model ) {

                @Override
                public boolean isVisible() {

                    return !isEditable();
                }
            };

        return new Label( "label", model ) {

            @Override
            public boolean isVisible() {

                return !isEditable();
            }
        };
    }

    /**
     * @param editable {@code true} to make the label editable by rendering its object in a field and updating the model when that
     *                 field is submitted.
     */
    public EditableLabel<T> setEditable(final boolean editable) {

        this.editable = editable;

        return this;
    }

    /**
     * @return {@code true} makes the label editable by rendering its object in a field and updating the model when that field is
     *         submitted.
     */
    public boolean isEditable() {

        return editable;
    }

    /**
     * @return Whether value of the editable field can be emptied ({@code true}). (Default: {@code true} )
     */
    public boolean isNullable() {

        return nullable;
    }

    /**
     * @param nullable Whether value of the editable field can be emptied ({@code true}).
     */
    public EditableLabel<T> setNullable(final boolean nullable) {

        this.nullable = nullable;

        return this;
    }

    /**
     * @return {@code true}: Generate markup that handles multiple lines in model values properly.
     */
    public boolean isMultiline() {

        return multiline;
    }

    /**
     * @param multiline {@code true}: Generate markup that handles multiple lines in model values properly.
     */
    public EditableLabel<T> setMultiline(final boolean multiline) {

        this.multiline = multiline;

        label.replaceWith( label = newLabel( label.getDefaultModel() ) );

        return this;
    }

    /**
     * @return The component that renders the model object as a non-editable label.
     */
    public WebComponent getLabel() {

        return label;
    }

    /**
     * @return The component that renders the model object when the label is editable.  The component returned depends on whether the label
     *         is currently multi-line or not.
     */
    public AbstractTextComponent<T> getField() {

        return getField( isMultiline() );
    }

    /**
     * @param forMultiline {@code true} Return the component used when the label is in multi-line mode.  {@code false} Return the
     *                     component used when the label is in single line mode.
     *
     * @return The component that renders the model object when the label is editable.
     */
    public AbstractTextComponent<T> getField(final boolean forMultiline) {

        return forMultiline? multiLineField: singleLineField;
    }
}
