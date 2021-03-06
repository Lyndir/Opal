package com.lyndir.lhunath.opal.wayward.component;

import com.lyndir.lhunath.opal.wayward.behavior.FocusOnReady;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link TextWindow}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 18, 2010</i> </p>
 *
 * @author lhunath
 */
public class TextWindow<T> extends ModalWindow {

    boolean oneLine = true;

    /**
     * @param id       The wicket ID of the form in the markup.
     * @param model    The model that manages the text field's data.
     * @param type     The type of the text field's data.
     * @param callback The callback to invoke when the window is closed.
     */
    public TextWindow(final String id, final IModel<T> model, final Class<T> type, final WindowClosedCallback callback) {

        super( id, model );

        setContent( new TextPanel<>( getContentId(), model, type ) );
        setInitialHeight( 200 );
        setWindowClosedCallback( callback );
    }

    private class TextPanel<T> extends Panel {

        TextPanel(final String id, final IModel<T> model, final Class<T> type) {

            super( id, model );

            add( new Form<Object>( "form" ) {

                private final Component field;
                private final Component area;

                {
                    add( field = new TextField<>( "field", model, type ).add( new FocusOnReady() ) );
                    add( area = new TextArea<>( "area", model ).add( new FocusOnReady() ) );
                    add( new AjaxButton( "submit" ) {
                        @Override
                        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {

                            closeCurrent( target );
                        }
                    } );
                }

                @Override
                protected void onConfigure() {

                    super.onConfigure();

                    field.setVisible( oneLine );
                    area.setVisible( !oneLine );
                }
            } );
        }
    }

    public boolean isOneLine() {

        return oneLine;
    }

    public TextWindow<T> setOneLine(final boolean oneLine) {

        this.oneLine = oneLine;

        return this;
    }
}
