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
package com.lyndir.lhunath.opal.wayward.provider;

import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;


/**
 * <h2>{@link AbstractCollectionProvider}<br> <sub>Provides data from a lazy loaded detachable list.</sub></h2>
 *
 * <p> <i>Mar 7, 2010</i> </p>
 *
 * @param <T> The type of data that will be provided.
 *
 * @author lhunath
 */
public abstract class AbstractCollectionProvider<T> extends AbstractIteratorProvider<T> {

    @Nullable
    private transient Collection<T> source;

    private Collection<T> getSource() {

        if (source == null)
            source = loadSource();

        return source;
    }

    @Override
    protected Iterator<T> load() {

        return getSource().iterator();
    }

    @Override
    public int size() {

        return getSource().size();
    }

    /**
     * @return The list that provides the data.
     */
    protected abstract Collection<T> loadSource();

    @Override
    public void detach() {

        super.detach();

        source = null;
    }
}
