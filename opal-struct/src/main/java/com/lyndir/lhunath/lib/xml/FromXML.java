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
package com.lyndir.lhunath.lib.xml;

import java.lang.annotation.*;


/**
 * <h2>{@link FromXML}<br> <sub>Mark a type whose instances can have XML data injected into them.</sub></h2>
 *
 * <p> Types with this annotation can be passed to {@link Structure#load(Class)}. This will cause the resource specified in this annotation
 * to be deserialized into the annotated object's fields. </p>
 *
 * <p> <i>Dec 15, 2008</i> </p>
 *
 * @author lhunath
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FromXML {

    /**
     * @return The name of the resource which contains the XML source data to inject.
     *
     *         <p> The resource will be loaded using the {@link ClassLoader#getSystemResource(String)} method. </p>
     */
    String value();
}
