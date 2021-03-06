/*
 *   Copyright 2008, Maarten Billemont
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
package com.lyndir.lhunath.opal.system.collection;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nullable;


/**
 * <h2>{@link Pair}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * @param <K> The class of the keys.
 * @param <V> The class of the values.
 *
 *            <p> <i>Apr 17, 2008</i> </p>
 *
 * @author mbillemo
 */
@SuppressWarnings("NonSerializableFieldInSerializableClass")
public class Pair<K, V> extends MetaObject implements Map.Entry<K, V>, Serializable {

    private static final long serialVersionUID = 0;

    @Nullable
    private K key;
    @Nullable
    private V value;

    /**
     * Create a new {@link Pair} instance.
     *
     * @param key   The key of this pair.
     * @param value The value of this pair.
     */
    Pair(@Nullable final K key, @Nullable final V value) {

        this.key = key;
        this.value = value;
    }

    /**
     * @param key   The key of this pair.
     * @param value The value of this pair.
     *
     * @return A new {@link Pair} instance
     */
    public static <K, V> Pair<K, V> of(@Nullable final K key, @Nullable final V value) {

        return new Pair<>( key, value );
    }

    @Override
    @Nullable
    public K getKey() {

        return key;
    }

    @Nullable
    @SuppressWarnings({ "ParameterHidesMemberVariable" })
    public K setKey(@Nullable final K key) {

        K old = this.key;
        this.key = key;

        return old;
    }

    @Override
    @Nullable
    public V getValue() {

        return value;
    }

    @Override
    @Nullable
    @SuppressWarnings({ "ParameterHidesMemberVariable" })
    public V setValue(@Nullable final V value) {

        V old = this.value;
        this.value = value;

        return old;
    }
}
