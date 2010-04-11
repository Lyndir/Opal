/*
 *   Copyright 2005-2007 Maarten Billemont
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
package com.lyndir.lhunath.lib.system;

import java.lang.reflect.Field;


/**
 * An interface that allows public access to an instance's private and protected fields.
 *
 * @author lhunath
 */
public interface Reflective {

    /**
     * Retrieve the object in the given field for this instance.<br>
     * Put this in here:
     *
     * <pre>
     * return field.get( this );
     * </pre>
     *
     * @param field The field that contains the object that's being requested.
     *
     * @return The field's value.
     *
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    Object getFieldValue(Field field)
            throws IllegalArgumentException, IllegalAccessException;
}
