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

import java.util.*;


/**
 * <h2>{@link EnumResourceBundle}<br>
 * <sub>An implementation of {@link ResourceBundle} that uses {@link Enum}s with a value as the resource.</sub></h2>
 *
 * <p>
 * The {@link Enum} must implement the {@link ValueEnum} interface.
 * </p>
 *
 * <p>
 * <i>Mar 29, 2009</i>
 * </p>
 *
 * @author lhunath
 * @param <T>
 * The type of values provided as resources.
 */
public class EnumResourceBundle<T> extends ResourceBundle {

    private final LinkedList<String> keyList;
    private final Class<? extends ValueEnum<T>> enumType;


    /**
     * @param enumType The enum that will provide values for this resource bundle.
     */
    public EnumResourceBundle(Class<? extends ValueEnum<T>> enumType) {

        if (!enumType.isEnum())
            throw new IllegalArgumentException( "Expected an enum, got: " + enumType );

        this.enumType = enumType;

        keyList = new LinkedList<String>();
        for (ValueEnum<T> element : enumType.getEnumConstants())
            keyList.add( element.name() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<String> getKeys() {

        return Collections.enumeration( keyList );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected T handleGetObject(String key) {

        for (ValueEnum<T> element : enumType.getEnumConstants())
            if (element.name().equals( key ))
                return element.value();

        throw new MissingResourceException( "Resource key '" + key + "' not implemented.", enumType.getName(), key );
    }
}
