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
 * <h2>{@link KeyMatch}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 31, 2010</i>
 * </p>
 *
 * @author lhunath
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface KeyMatch {

    /**
     * Value for {@link #ifNum()} that disables the check.
     */
    static final double NUM_UNSET = Double.NaN;

    /**
     * Value for {@link #ifString()} that disables the check.
     */

    static final String STRING_UNSET = "KeyMatch.unset";

    /**
     * Value for {@link #ifClass()} that disables the check.
     */
    static final Class<?> CLASS_UNSET = KeyMatch.class;


    /**
     * Trigger the {@link KeyAppender} if the parameter's value equals the given number.
     */
    double ifNum() default NUM_UNSET;

    /**
     * Trigger the {@link KeyAppender} if the parameter's value equals the given string.
     */
    String ifString() default STRING_UNSET;

    /**
     * Trigger the {@link KeyAppender} if the parameter's value is assignable to the given class.
     */
    Class<?> ifClass() default KeyMatch.class;

    /**
     * The key part to append when one of the if* conditions holds <code>true</code>.
     */
    String key();

    /**
     * The key part to append when all of the if* conditions holds <code>false</code>. Set to append nothing by default.
     */
    String elseKey() default STRING_UNSET;
}
