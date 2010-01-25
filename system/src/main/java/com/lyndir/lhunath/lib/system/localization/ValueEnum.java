/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.lhunath.lib.system.localization;

/**
 * <h2>{@link ValueEnum}<br>
 * <sub>An interface for {@link Enum}s whose keys provide a simple value.</sub></h2>
 *
 * <p>
 * <i>Mar 29, 2009</i>
 * </p>
 *
 * @param <T>
 *            The type of values provided by this {@link Enum} class.
 *
 * @author lhunath
 */
public interface ValueEnum<T> {

    String name();

    /**
     * @return The value provided by the {@link Enum} instance.
     */
    T value();
}
