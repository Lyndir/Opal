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
package com.lyndir.lhunath.lib.wayward.provider;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link AbstractListProvider}<br> <sub>Provides data from a lazy loaded detachable list.</sub></h2>
 *
 * <p> <i>Mar 7, 2010</i> </p>
 *
 * @author lhunath
 * @param <T> The type of data that will be provided.
 */
public abstract class AbstractListProvider<T> implements IDataProvider<T> {

    private transient List<T> transientList = null;

    private List<T> getObject() {

        if (transientList == null)
            transientList = load();

        return transientList;
    }

    /**
     * @return The list that provides the data.
     */
    protected abstract List<T> load();

    /**
     * {@inheritDoc}
     *
     * <p><b>WARNING:</b> This implementation ASSUMES the object is serializable for convenience sake.  If this is not the case, you MUST
     * override this method or run-time madness will ensue.</p>
     */
    @Override
    @SuppressWarnings({ "unchecked" })
    public IModel<T> model(final T object) {

        return (IModel<T>) new Model<Serializable>( (Serializable) object );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detach() {

        transientList = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator(final int first, final int count) {

        List<T> list = getObject();
        int from = Math.min( first, list.size() );
        int to = Math.min( first + count, list.size() );

        return list.subList( from, to ).iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {

        return getObject().size();
    }
}
