package com.lyndir.lhunath.lib.wayward.component;

import com.lyndir.lhunath.lib.wayward.resources.Resources;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.PackageResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link AjaxEditableImage}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 25, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AjaxEditableImage extends Panel implements ModalWindow.WindowClosedCallback {

    private final IModel<FileUpload> file = new LoadableDetachableModel<FileUpload>() {

        @Override
        protected FileUpload load() {

            return null;
        }
    };
    private final ModalWindow window;
    private       boolean     editable;

    /**
     * @param id The wicket ID of the component.
     */
    protected AjaxEditableImage(final String id) {

        super( id );
        setOutputMarkupId( true );

        add( new Image( "image", new DynamicImageResource() {

            @Override
            protected byte[] getImageData() {

                return AjaxEditableImage.this.getImageData();
            }
        }.setCacheable( false ) ) {

            @Override
            public boolean isVisible() {

                return hasImageData();
            }
        }.add( new AttributeAppender( "class", new LoadableDetachableModel<String>() {

            @Override
            protected String load() {

                return isEditable()? "link": "";
            }
        }, " " ) ).add( new AjaxEventBehavior( "onClick" ) {

            @Override
            protected void onEvent(final AjaxRequestTarget target) {

                if (isEditable())
                    window.show( target );
            }
        } ) );
        add( new Image( "noimage", PackageResource.get( Resources.class, "boxed-x2.png" ) ) {

            @Override
            public boolean isVisible() {

                return !hasImageData() && isEditable();
            }
        }.add( new AjaxEventBehavior( "onClick" ) {

            @Override
            protected void onEvent(final AjaxRequestTarget target) {

                window.show( target );
            }
        } ) );
        add( window = new ModalWindow( "upload" ) {

            {
                setInitialWidth( 300 );
                setInitialHeight( 100 );
                setContent( new UploadPanel( this, getContentId() ) );
                setWindowClosedCallback( AjaxEditableImage.this );
            }
        } );
    }

    /**
     * @param id                The wicket ID of the component.
     * @param initiallyEditable <code>true</code> if the image can be edited by clicking on it and uploading a new one.
     */
    protected AjaxEditableImage(final String id, final boolean initiallyEditable) {

        this( id );
        editable = initiallyEditable;
    }

    /**
     * Override me to perform a more lazy check of whether image data is available. None will be loaded from {@link #getImageData()} if this
     * returns <code>false</code>.
     *
     * @return <code>true</code>  if image data is available for this component.
     */
    protected boolean hasImageData() {

        return getImageData() != null;
    }

    protected abstract byte[] getImageData();

    protected abstract void setImageData(byte[] imageData);

    /**
     * @param editable <code>true</code> if the image can be edited by clicking on it and uploading a new one.
     */
    public void setEditable(final boolean editable) {

        this.editable = editable;
    }

    /**
     * @return <code>true</code> if the image can be edited by clicking on it and uploading a new one.
     */
    public boolean isEditable() {

        return editable;
    }

    @Override
    public void onClose(final AjaxRequestTarget target) {

        if (isEditable() && file.getObject() != null)
            setImageData( file.getObject().getBytes() );

        target.addComponent( this );
    }

    private class UploadPanel extends Panel {

        UploadPanel(final ModalWindow modalWindow, final String id) {

            super( id );

            add( new Form<Void>( "form" ) {

                {
                    add( new FileUploadField( "file", file ) );
                    add( new AjaxButton( "submit" ) {

                        @Override
                        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {

                            setImageData( file.getObject().getBytes() );
                            modalWindow.close( target );
                        }
                    } );
                }
            } );
        }
    }
}
