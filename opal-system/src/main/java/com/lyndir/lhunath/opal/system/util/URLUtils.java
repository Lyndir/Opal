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
package com.lyndir.lhunath.opal.system.util;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.CodeUtils;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.MessageFormat;


/**
 * <h2>{@link URLUtils}<br> <sub>Utilities for working with URLs.</sub></h2>
 * <p/>
 * <p> <i>Sep 17, 2009</i> </p>
 *
 * @author lhunath
 */
public abstract class URLUtils {

    /**
     * Add a GET parameter to the query component of the given URL.
     *
     * @param url   The base URL on which to append the parameter.
     * @param key   The key of the parameter to add.
     * @param value The value of the parameter to add.
     *
     * @return A new URL which is the base URL with the given query parameter added to it.
     */
    public static URL addParameter(URL url, String key, Object value) {

        try {
            return new URL( addParameter( url.toExternalForm(), key, value ) );
        }
        catch (MalformedURLException e) {
            throw new IllegalStateException( "Bug.", e );
        }
    }

    /**
     * Add a GET parameter to the query component of the given URL.
     *
     * @param url   The base URL on which to append the parameter.
     * @param key   The key of the parameter to add.
     * @param value The value of the parameter to add.
     *
     * @return A new URL which is the base URL with the given query parameter added to it.
     */
    public static String addParameter(String url, String key, Object value) {

        if (key == null)
            throw new IllegalArgumentException( "key to add to url can't be null" );

        StringBuilder urlString = new StringBuilder( url );
        if (url.contains( "?" ))
            urlString.append( '&' );
        else
            urlString.append( '?' );

        try {
            urlString.append( URLEncoder.encode( key, "UTF-8" ) );
            if (value != null) {
                urlString.append( '=' );
                urlString.append( URLEncoder.encode( value.toString(), "UTF-8" ) );
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException( "UTF-8 unsupported by VM", e );
        }

        return urlString.toString();
    }

    /**
     * Concatenate the given paths by gluing them together and making sure there is only one slash separating them.
     *
     * @param paths The path to glue together.
     *
     * @return The glued together path.
     */
    public static String concat(String... paths) {

        if (paths.length == 0)
            return "";

        String base = paths[0] == null? "": paths[0];
        if (paths.length == 1)
            return base;

        // Glue the other paths.
        String[] otherPaths = new String[paths.length - 1];
        System.arraycopy( paths, 1, otherPaths, 0, paths.length - 1 );
        String otherPathsGlued = concat( otherPaths );

        // Glue the base onto the other paths.
        StringBuilder glued = new StringBuilder( base );
        glued.append( base.charAt( base.length() - 1 ) == '/' || otherPathsGlued.isEmpty()? "": '/' );
        if (!otherPathsGlued.isEmpty())
            glued.append( otherPathsGlued.charAt( 0 ) == '/'? otherPathsGlued.substring( 1 ): otherPathsGlued );

        return glued.toString();
    }

    /**
     * Generate a URL by passing the {@code urlFormat} to {@code MessageFormat} and injecting the {@code urlFormatArgs} after first URL
     * encoding their {@code toString()}.
     *
     * @param urlFormat     The URL format as specified by MessageFormat.
     * @param urlFormatArgs The arguments to inject into the URL format after URL encoding them.
     *
     * @return The URL produced by expanding the URL format with the encoded arguments.
     */
    public static URL newURL(String urlFormat, Object... urlFormatArgs) {

        try {
            return new URL( MessageFormat.format( urlFormat,
                    Collections2.transform( ImmutableList.copyOf( urlFormatArgs ), new Function<Object, String>() {
                        @Override
                        public String apply(final Object input) {

                            return CodeUtils.encodeURL( input == null? "": input.toString() );
                        }
                    } ).toArray() ) );
        }
        catch (MalformedURLException e) {
            throw Throwables.propagate( e );
        }
    }

    public static URL newURL(URL baseURL, CharSequence relativeURLString) {

        try {
            return relativeURLString == null? baseURL: new URL( baseURL, relativeURLString.toString() );
        }
        catch (MalformedURLException e) {
            throw Throwables.propagate( e );
        }
    }
}
