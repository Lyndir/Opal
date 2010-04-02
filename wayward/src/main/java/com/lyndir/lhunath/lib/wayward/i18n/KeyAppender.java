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
package com.lyndir.lhunath.lib.wayward.i18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <h2>{@link KeyAppender}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Mar 31, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface KeyAppender {

    /**
     * Value for {@link #nullKey()} and {@link #notNullKey()} that prevent them from appending.
     */
    static final String STRING_UNSET = "KeyAppender.unset";


    /**
     * Matchers are used to append a specific key part when the parameter's value passes (or fails) a certain test.
     */
    KeyMatch[] value() default {};

    /**
     * Key to append when the value for this parameter is <code>null</code>.
     */
    String nullKey() default STRING_UNSET;

    /**
     * Key to append when the value for this parameter is not <code>null</code>.
     */
    String notNullKey() default STRING_UNSET;

    /**
     * Determines whether to pass the value for this parameter to the evaluation of the localization value's format
     * string.
     */
    boolean useValue() default false;
}
