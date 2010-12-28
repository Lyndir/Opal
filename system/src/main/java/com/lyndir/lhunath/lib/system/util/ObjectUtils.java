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
}
