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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <h2>{@link ObjectUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 22, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class ObjectUtils {

    /**
     * Check whether two objects are equal according to {@link #equals(Object)}.
     *
     * <p> <b>NOTE:</b> This method is <code>null</code>-safe and two <code>null</code> objects are also considered equal. </p>
     *
     * <p> <b>NOTE:</b> This method attempts to aid in type safety of the objects that are being compared. </p>
     *
     * @param <A>    The type of the first parameter.  It should be of the same type or a subtype (more concrete type) of the second
     *               parameter.
     * @param <B>    The type of the second parameter. The type of this parameter must be the same type or more generic
     *               assignment-compatible to that of the first parameter.
     * @param first  The first object, or <code>null</code>.
     * @param second The second object, or <code>null</code>.
     *
     * @return <code>true</code> if both objects are <code>null</code> or if neither are and {@link #equals(Object)} considers them equal.
     */
    public static <B, A extends B> boolean equal(final A first, final B second) {

        return first == second || first != null && first.equals( second );
    }

    /**
     * Concatenate two arrays or append elements to an array.
     *
     * @param array    The array that provides the first set of elements.
     * @param elements The second set of elements.
     * @param <T>      The type of the array elements.
     *
     * @return A new array that contains all elements from the first parameter with the other elements appended to it.  If no additional
     *         elements are given, returns the first array as it is (<b>not a copy!</b>).
     */
    public static <T> T[] concat(final T[] array, final T... elements) {

        if (elements.length == 0)
            return array;

        // TODO: Optimize using array copying.
        List<T> elementList = new ArrayList<T>( array.length + elements.length );
        elementList.addAll( Arrays.asList( array ) );
        elementList.addAll( Arrays.asList( elements ) );
        return elementList.toArray( array );
    }
}
