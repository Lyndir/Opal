/*
 *   Copyright 2005-2007 Maarten Billemont
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
package com.lyndir.lhunath.lib.system;

import java.util.*;


/**
 * <i>Locale - This class allows access to locale specific externalized strings for use in the application.</i><br> <br> When a locale is
 * defined by the {@link #setLang(java.util.Locale)} method, the {@link #explain(String, Object...)} method will return the strings
 * requested by unique keys in the set language. If the string is not available in that language, it will fall back to the default
 * locale.<br> <br>
 *
 * @author lhunath
 */
public class Locale {

    private static final Locale instance = new Locale();
    private ResourceBundle resources;

    private Locale() {

        setLang( null );
    }

    /**
     * Retrieve a reference to this singleton.
     *
     * @return The instance of this class.
     */
    public static Locale getLocale() {

        return instance;
    }

    /**
     * Switch to a new locale.
     *
     * @param lang The new locale.
     *
     * @return Reference to this locale instance.
     */
    public Locale setLang(final java.util.Locale lang) {

        try {
            if (lang == null)
                resources = ResourceBundle.getBundle( "messages" );
            else
                resources = ResourceBundle.getBundle( "messages", lang );
        }
        catch (MissingResourceException ignored) {
            resources = new ListResourceBundle() {

                @Override
                protected Object[][] getContents() {

                    return new Object[0][0];
                }
            };
        }

        return this;
    }

    /**
     * Retrieves an externalized string for the given key, or the key if none was found.<br> The result is parsed by {@link
     * String#format(java.util.Locale, String, Object...)} using the optional additional arguments as input values.
     *
     * @param messageKey The key of the message or the message to format.
     * @param arguments  Arguments to use in formatting of the result.
     *
     * @return The formatted externalized string, or key if none was found.
     */
    public static String explain(final String messageKey, final Object... arguments) {

        return getLocale()._explain( messageKey, arguments );
    }

    /**
     * Retrieves an externalized string for the given key, or the key if none was found.<br> The result is parsed by {@link
     * String#format(java.util.Locale, String, Object...)} using the optional additional arguments as input values.
     *
     * @param messageKey The key of the message or the message to format.
     * @param arguments  Arguments to use in formatting of the result.
     *
     * @return The formatted externalized string, or key if none was found.
     */
    private String _explain(final String messageKey, final Object... arguments) {

        if (messageKey == null)
            return null;

        try {
            if (arguments != null && arguments.length > 0)
                return String.format( resources.getLocale(), resources.getString( messageKey ), arguments );
            return resources.getString( messageKey );
        }
        catch (MissingResourceException ignored) {
            StringBuilder messageBuilder = new StringBuilder( messageKey );

            if (arguments == null || arguments.length == (messageKey + ' ').split( "%[-#+ 0,\\(\\.]*\\w" ).length - 1)
                return String.format( resources.getLocale(), messageKey, arguments );

            for (final Object arg : arguments)
                messageBuilder.append( arg );

            return messageBuilder.toString();
        }
    }
}
