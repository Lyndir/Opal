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

/**
 * <h2>{@link StringUtils}<br>
 * <sub>Some utility methods for working with {@link String}s.</sub></h2>
 * 
 * <p>
 * <i>Jan 12, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class StringUtils {

    /**
     * Concatenate several strings into one.
     * 
     * @param delimitor
     *            The delimitor to use to separate the strings.
     * @param elements
     *            The strings that should be concatenated, in order form left to right.
     * @return A long string containing all the given strings delimited by the delimitor.
     */
    public static String concat(String delimitor, String... elements) {

        StringBuilder concatenation = new StringBuilder();
        for (String element : elements)
            concatenation.append( element ).append( delimitor );

        return concatenation.substring( 0, concatenation.length() - delimitor.length() );
    }
}
