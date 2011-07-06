package com.lyndir.lhunath.opal.wayward.i18n;

import com.lyndir.lhunath.opal.system.i18n.Localized;
import java.util.Locale;
import org.apache.wicket.Session;
import org.apache.wicket.util.convert.IConverter;


/**
* <i>07 05, 2011</i>
*
* @author lhunath
*/
public class LocalizedConverter implements IConverter {

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
