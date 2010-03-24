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
 * <h2>{@link SafeObjects}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 22, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class SafeObjects {

    /**
     * Check whether two objects are equal according to {@link #equals(Object)}.
     * 
     * <p>
     * <b>NOTE:</b> This method is <code>null</code>-safe and two <code>null</code> objects are also considered equal.
     * </p>
     * 
     * <p>
     * <b>NOTE:</b> This method attempts to aid in type safety of the objects that are being compared.
     * </p>
     * 
     * @param <A>
     *            The type of the first object. The higher type in the hierarchy.
     * @param <B>
     *            The type of the second object must be of the same type or an assignment-compatible type (see
     *            {@link Class#isAssignableFrom(Class)}) of A.
     * 
     * @param a
     *            The first object, or <code>null</code>.
     * @param b
     *            The second object, or <code>null</code>.
     * @return <code>true</code> if both objects are <code>null</code> or if neither are and {@link #equals(Object)}
     *         considers them equal.
     */
    public static <A, B extends A> boolean equal(A a, B b) {

        return a == b || a != null && a.equals( b );
    }
}
