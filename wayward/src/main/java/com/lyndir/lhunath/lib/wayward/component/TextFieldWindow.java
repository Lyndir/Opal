package com.lyndir.lhunath.lib.wayward.component;

import com.lyndir.lhunath.lib.wayward.behavior.FocusOnReady;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.AppendingStringBuffer;


/**
 * <h2>{@link TextFieldWindow}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 18, 2010</i> </p>
 *
 * @author lhunath
 */
public class TextFieldWindow<T> extends ModalWindow {

    IModel<T> model;

    /**
     * @param id       The wicket ID of the form in the markup.
     * @param model    The model that manages the text field's data.
     * @param type     The type of the text field's data.
     * @param callback The callback to invoke when the window is closed.
     */
    public TextFieldWindow(final String id, final IModel<T> model, final Class<T> type, final WindowClosedCallback callback) {

        super( id, model );

        setContent( new TextFieldPanel<T>( getContentId(), model, type ) );
        setInitialHeight( 200 );
        setWindowClosedCallback( callback );
    }

    private static class TextFieldPanel<T> extends Panel {

        private final Form<Object> form;

        TextFieldPanel(final String id, final IModel<T> model, final Class<T> type) {

            super( id, model );

            add( (form = new Form<Object>( "form" ) {
                {
                    add( new TextField<T>( "field", model, type ).add( new FocusOnReady() ) );
                }}).add( new AjaxFormSubmitBehavior( form, "onsubmit" ) {

                @Override
                protected void onSubmit(final AjaxRequestTarget target) {

                    closeCurrent( target );
                }

                @Override
                protected void onError(final AjaxRequestTarget target) {

                    // TODO: Feedback.
                }

                @Override
                protected CharSequence getEventHandler() {

                    // Prevents the form from generating an http request.
                    // If we do not provide this, the AJAX event is processed AND the form still gets submitted.
                    // FIXME: Ugly. Should probably be moved into AjaxFormSubmitBehaviour.
                    return new AppendingStringBuffer( super.getEventHandler() ).append( "; return false;" );
                }
            } ) );
        }
    }
}
