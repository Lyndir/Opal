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

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.ifNotNullElse;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link StringUtils}<br> <sub>Some utility methods for working with {@link String}s.</sub></h2>
 * <p/>
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
     * Trim all {@code trim} strings off of the {@code source} string, operating only on the left side.
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
     * Trim all {@code trim} strings off of the {@code source} string, operating only on the left side.
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
     * Trim all {@code trim} strings off of the {@code source} string, operating on both sides.
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
     * @param home The {@link URL} that needs to be converted to a short string version.
     *
     * @return A concise representation of the URL showing only the root domain and final part of the path.
     */
    public static String shortUrl(final URL home) {

        String shortHome = TLD.matcher( home.getHost() ).replaceFirst( "$1" );
        String path = NON_FINAL_PATH.matcher( TRAILING_SLASHES.matcher( home.getPath() ).replaceFirst( "" ) ).replaceFirst( "" );

        return String.format( "%s:%s", shortHome, path );
    }

    /**
     * Perform an expansion operation on the given {@code source} string by expanding all curly-braced words with a certain
     * {@code keyPrefix} into an expansion value determined by the {@code keyToExpansion} function.
     * <p/>
     * <p>If the source string is {@code I am a $}{good} sentence.</code>, the keyPrefix is {@code $} and the keyToExpansion function
     * returns {@code bad} when its input is the string {@code good}, the result of this method will be: {@code I am a bad sentence.}</p>
     *
     * @param source         The string to search for expansion words and to expand into.
     * @param keyPrefix      The string that should come just before the opening curly-brace.
     * @param keyToExpansion The function that determines the value to expand an expansion word into.  The word within the curly braces
     *                       will be given to the function.  The expansion value will be expected as return value.
     *
     * @return An expanded version of the source string.
     */
    public static String expand(String source, String keyPrefix, Function<String, String> keyToExpansion) {

        Map<Integer, Integer> indexToEnds = Maps.newTreeMap();
        Map<Integer, String> indexToExpansions = Maps.newTreeMap();

        Pattern keyPattern = Pattern.compile( String.format( "%s\\{([^\\}]*)\\}", Pattern.quote( keyPrefix ) ) );
        Matcher matcher = keyPattern.matcher( source );
        while (matcher.find()) {
            int index = matcher.start();
            String key = matcher.group( 1 );

            indexToEnds.put( index, matcher.end() );
            indexToExpansions.put( index, ifNotNullElse( keyToExpansion.apply( key ), "" ) );
        }

        SortedSet<Integer> reverseIndexes = Sets.newTreeSet( Collections.reverseOrder() );
        reverseIndexes.addAll( indexToExpansions.keySet() );

        StringBuilder filtered = new StringBuilder( source );
        for (Integer index : reverseIndexes)
            filtered.replace( index, indexToEnds.get( index ), indexToExpansions.get( index ) );

        return filtered.toString();
    }

    public static String encodeHex(@Nullable final byte[] data) {

        return encodeHex( data, false );
    }

    public static String encodeHex(@Nullable final byte[] data, final boolean pretty) {

        StringBuffer bytes = new StringBuffer();
        Formatter formatter = new Formatter( bytes );
        String format = String.format( "%%02X%s", pretty? ":": "" );

        if (data != null)
            for (final byte b : data) {
                formatter.format( format, b );
            }
        if (pretty && bytes.length() > 0)
            bytes.deleteCharAt( bytes.length() - 1 );

        return bytes.toString();
    }

    public static byte[] decodeHex(@Nullable final String hexString) {

        if (hexString == null)
            return new byte[0];

        byte[] deviceToken = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2)
            deviceToken[i / 2] = Integer.valueOf( hexString.substring( i, i + 2 ), 16 ).byteValue();

        return deviceToken;
    }
}
