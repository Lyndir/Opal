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

import com.google.common.base.Supplier;
import javax.annotation.Nullable;


/**
 * A {@link Supplier} that can supply {@code null}.
 *
 * @param <T> The type of the supplied value.
 */
public interface NSupplier<T> extends Supplier<T> {

    @Nullable
    @Override
    T get();
}
