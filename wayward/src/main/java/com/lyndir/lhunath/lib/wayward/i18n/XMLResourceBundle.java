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
package com.lyndir.lhunath.lib.wayward.i18n;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import com.google.common.collect.ImmutableList;


/**
 * <h2>{@link XMLResourceBundle}<br>
 * <sub>A {@link PropertiesResourceBundle} that loads its values from a Java XML {@link Properties} file.</sub></h2>
 *
 * <p>
 * <i>Mar 26, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class XMLResourceBundle extends PropertiesResourceBundle {

    /**
     * Create a new {@link XMLResourceBundle} instance.
     *
     * @param props The properties to load into this XML based resource bundle.
     */
    XMLResourceBundle(Properties props) {

        super( props );
    }

    /**
     * @param baseName The base name for the XML properties file.
     *
     * @return A resource bundle for the given base name and the default locale.
     *
     * @see ResourceBundle#getBundle(String)
     */
    public static ResourceBundle getXMLBundle(String baseName) {

        return getBundle( baseName, new Control() );
    }

    /**
     * @param baseName The base name for the XML properties file.
     * @param locale   The locale for which a resource bundle is desired.
     *
     * @return A resource bundle for the given base name and the given locale.
     *
     * @see ResourceBundle#getBundle(String, Locale)
     */
    public static ResourceBundle getXMLBundle(String baseName, Locale locale) {

        return getBundle( baseName, locale, new Control() );
    }

    /**
     * @param baseName The base name for the XML properties file.
     * @param locale   The locale for which a resource bundle is desired.
     * @param loader   The class loader from which to load the resource bundle.
     *
     * @return A resource bundle for the given base name and the given locale.
     *
     * @see ResourceBundle#getBundle(String, Locale, ClassLoader)
     */
    public static ResourceBundle getXMLBundle(String baseName, Locale locale, ClassLoader loader) {

        return getBundle( baseName, locale, loader, new Control() );
    }


    static class Control extends ResourceBundle.Control {

        Control() {

        }

        @Override
        public List<String> getFormats(String baseName) {

            return ImmutableList.of( "xml" );
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
                                        boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {

            checkNotNull( baseName );
            checkNotNull( locale );
            checkNotNull( format );
            checkNotNull( loader );

            if (!format.equals( "xml" ))
                return null;

            String bundleName = toBundleName( baseName, locale );
            String resourceName = toResourceName( bundleName, format );

            URL url = loader.getResource( resourceName );
            if (url == null)
                return null;
            URLConnection connection = url.openConnection();
            if (connection == null)
                return null;
            if (reload)
                connection.setUseCaches( false );
            InputStream stream = connection.getInputStream();
            if (stream == null)
                return null;

            Properties properties = new Properties();
            BufferedInputStream input = new BufferedInputStream( stream );
            try {
                properties.loadFromXML( input );
            }

            finally {
                input.close();
            }

            return new XMLResourceBundle( properties );
        }
    }
}
