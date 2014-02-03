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
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;


/**
 * A {@link Function} that can be applied only to not-{@code null} values and cannot yield {@code null} as result.
 *
 * @param <T> The type of the value this operation can be applied to.
 */
@SuppressWarnings({ "NullableProblems" })
public abstract class NNFunctionNN<F, T> implements Function<F, T> {

    @Nonnull
    @Override
    public abstract T apply(@Nonnull F input);

    public boolean equals(@Nonnull final Object object) {

        return super.equals( object );
    }

    public int hashCode() {

        return super.hashCode();
    }

    public static <F, T> NNFunctionNN<F, T> of(final Function<F, T> func) {

        return new NNFunctionNN<F, T>() {
            @Nonnull
            @Override
            public T apply(@Nonnull final F input) {

                return Preconditions.checkNotNull( func.apply( input ) );
            }

            @Override
            @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
            public boolean equals(@Nonnull final Object object) {

                return func.equals( object );
            }

            @Override
            public int hashCode() {

                return func.hashCode();
            }
        };
    }
}
