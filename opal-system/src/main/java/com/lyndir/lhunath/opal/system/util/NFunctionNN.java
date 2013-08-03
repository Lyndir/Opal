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

import com.google.common.base.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * A {@link Function} that can be applied only to not-{@code null} values but yield {@code null} as result.
 *
 * @param <T> The type of the value this operation can be applied to.
 */
@SuppressWarnings({ "NullableProblems" })
public interface NFunctionNN<F, T> extends Function<F, T> {

    @Nullable
    @Override
    T apply(@Nonnull F input);

    boolean equals(@Nonnull Object object);

    int hashCode();
}
