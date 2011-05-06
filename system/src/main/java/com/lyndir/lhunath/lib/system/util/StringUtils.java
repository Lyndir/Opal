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
package com.lyndir.lhunath.lib.system.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Charsets;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link StringUtils}<br> <sub>Some utility methods for working with {@link String}s.</sub></h2>
 *
 * <p> <i>Jan 12, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class StringUtils {

    private static final Pattern TLD              = Pattern.compile( "^.*?([^\\.]+\\.[^\\.]+)$" );
    private static final Pattern TRAILING_SLASHES = Pattern.compile( "/+$" );
    private static final Pattern NON_FINAL_PATH   = Pattern.compile( "^.*/" );

    /**
     * Concatenate several strings into one.
     *
     * @param delimitor The delimitor to use to separate the strings.
     * @param elements  The strings that should be concatenated, in order form left to right.
     *
     * @return A long string containing all the given strings delimited by the delimitor.
     */
    public static String concat(final String delimitor, final String... elements) {

        StringBuilder concatenation = new StringBuilder();
        for (final String element : elements)
            concatenation.append( element ).append( delimitor );

        return concatenation.substring( 0, concatenation.length() - delimitor.length() );
    }

    public static boolean isEmpty(final String data) {

        return data == null || data.length() == 0;
    }

    /**
     * Trim all <code>trim</code> strings off of the <code>source</code> string, operating only on the left side.
     *
     * @param source The source object that needs to be converted to a string and trimmed.
     * @param trim   The object that needs to be converted to a string and is what will be trimmed off.
     *
     * @return The result of the trimming.
     */
    @Nullable
    public static String ltrim(final String source, final String trim) {

        if (source == null || trim == null)
            return source == null? null: source;

        StringBuilder trimmed = new StringBuilder( source );
        while (trimmed.indexOf( trim ) == 0)
            trimmed.delete( 0, trim.length() );

        return trimmed.toString();
    }

    /**
     * Trim all <code>trim</code> strings off of the <code>source</code> string, operating only on the left side.
     *
     * @param source The source object that needs to be converted to a string and trimmed.
     * @param trim   The object that needs to be converted to a string and is what will be trimmed off.
     *
     * @return The result of the trimming.
     */
    @Nullable
    public static String rtrim(final String source, final String trim) {

        if (source == null || trim == null)
            return source == null? null: source;

        StringBuilder trimmed = new StringBuilder( source );
        while (trimmed.indexOf( trim, trimmed.length() - trim.length() ) == trimmed.length() - trim.length())
            trimmed.delete( trimmed.length() - trim.length(), trimmed.length() );

        return trimmed.toString();
    }

    /**
     * Trim all <code>trim</code> strings off of the <code>source</code> string, operating on both sides.
     *
     * @param source The source object that needs to be converted to a string and trimmed.
     * @param trim   The object that needs to be converted to a string and is what will be trimmed off.
     *
     * @return The result of the trimming.
     */
    @Nullable
    public static String trim(final String source, final String trim) {

        return rtrim( ltrim( source, trim ), trim );
    }

    /**
     * Calculate a digest hash for the given string.
     *
     * @param data The data to calculate the sum for.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    @NotNull
    public static String getMD5(final String data) {

        return checkNotNull( IOUtils.getDigest( new ByteArrayInputStream( data.getBytes( Charsets.UTF_8 ) ), IOUtils.Digest.MD5 ) );
    }

    /**
     * Calculate a digest hash for the given bytes.
     *
     * @param data The data to calculate the sum for.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    @NotNull
    public static String getMD5(final byte[] data) {

        return checkNotNull( IOUtils.getDigest( new ByteArrayInputStream( data ), IOUtils.Digest.MD5 ) );
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param data       The data to calculate the sum for.
     * @param digestType The digest to calculate.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    @NotNull
    public static String getDigest(final String data, final IOUtils.Digest digestType) {

        return checkNotNull( IOUtils.getDigest( new ByteArrayInputStream( data.getBytes() ), digestType ) );
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param data       The data to calculate the sum for.
     * @param digestType The digest to calculate.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    @NotNull
    public static String getDigest(final byte[] data, final IOUtils.Digest digestType) {

        return checkNotNull( IOUtils.getDigest( new ByteArrayInputStream( data ), digestType ) );
    }

    /**
     * @param home The {@link URL} that needs to be converted to a short string version.
     *
     * @return A concise representation of the URL showing only the root domain and final part of the path.
     */
    public static String shortUrl(final URL home) {

        String shortHome = TLD.matcher( home.getHost() ).replaceFirst( "$1" );
        String path = NON_FINAL_PATH.matcher( TRAILING_SLASHES.matcher( home.getPath() ).replaceFirst( "" ) ).replaceFirst( "" );

        return String.format( "%s:%s", shortHome, path );
    }
}
