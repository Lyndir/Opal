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
package com.lyndir.lhunath.opal.system.util;

import com.google.common.base.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * A {@link Predicate} that will only receive non-{@code null} input values.
 *
 * @param <T> The type of the supplied value.
 */
@SuppressWarnings("NullableProblems")
public interface PredicateNN<T> extends Predicate<T> {

    @Override
    boolean apply(@Nonnull T input);

    boolean equals(@Nullable Object object);

    int hashCode();
}