package com.lyndir.lhunath.opal.wayward.i18n;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.lyndir.lhunath.opal.system.i18n.Localized;
import com.lyndir.lhunath.opal.system.i18n.internal.MessagesInvocationHandler;
import com.lyndir.lhunath.opal.wayward.model.Models;
import java.util.Locale;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;


/**
 * <i>07 05, 2011</i>
 *
 * @author lhunath
 */
public class LocalizedConverter implements IConverter {

    static {
        MessagesInvocationHandler.registerWrapperType( IModel.class, new Function<Supplier<String>, IModel<String>>() {
            @Override
            public IModel<String> apply(final Supplier<String> input) {

                return Models.supplied( input );
            }
        } );
    }

    @Override
    public Object convertToObject(final String value, final Locale locale) {

        throw new UnsupportedOperationException( "This object can only be serialized, not deserialized." );
    }

    @Override
    public String convertToString(final Object value, final Locale locale) {

        Session session = Session.get();
        Locale oldLocale = session.getLocale();
        try {
            session.setLocale( locale );
            return ((Localized) value).getLocalizedInstance();
        }
        finally {
            session.setLocale( oldLocale );
        }
    }
}
