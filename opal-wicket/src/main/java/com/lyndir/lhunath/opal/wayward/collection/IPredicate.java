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
package com.lyndir.lhunath.opal.wayward.collection;

import java.io.Serializable;
import java.util.function.Predicate;


/**
 * <h2>{@link IPredicate}<br> <sub>A {@link Serializable} {@link Predicate}.</sub></h2>
 *
 * <p> <i>Mar 23, 2010</i> </p>
 *
 * @param <T> The type that the predicate can be applied to.
 *
 * @author lhunath
 */
public interface IPredicate<T> extends Predicate<T>, Serializable {

}
