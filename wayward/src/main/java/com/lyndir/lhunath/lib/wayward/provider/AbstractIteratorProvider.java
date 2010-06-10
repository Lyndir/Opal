package com.lyndir.lhunath.lib.wayward.provider;

import com.google.common.collect.AbstractIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link AbstractIteratorProvider}<br> <sub>Iterator-based data provider</sub></h2>
 *
 * <p> This implementation makes it easy to create data providers from any iterator.  Objects already iterated over are cached allowing you
 * to use this provider from differently-sized DataViews. </p>
 *
 * <p> <i>05 07, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AbstractIteratorProvider<T> implements IDataProvider<T> {

    private transient Iterator<T> transientIt = null;
    private transient LinkedList<T> transientList = null;

    protected Iterator<T> getIterator() {

        if (transientIt == null) {
            transientIt = load();
        }

        return transientIt;
    }

    private LinkedList<T> getCache() {

        if (transientList == null) {
            transientList = new LinkedList<T>();
        }

        return transientList;
    }

    /**
     * @return The iterator that provides the data.
     */
    protected abstract Iterator<T> load();

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

        transientIt = null;
        transientList = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator(final int first, final int count) {

        return new AbstractIterator<T>() {

            private ListIterator<T> listIterator = getCache().listIterator( first );

            @Override
            protected T computeNext() {

                if (listIterator.nextIndex() - first > count)
                    // Requesting object past the count of the data provider's current view.
                    return endOfData();

                if (listIterator.hasNext())
                    // Requested object is cached.
                    return listIterator.next();

                if (!getIterator().hasNext())
                    // Requesting object past the size of the data provider's source.
                    return endOfData();

                // Add source's next value to the cache and return it.
                T next = getIterator().next();
                listIterator.add( next );
                return next;
            }
        };
    }
}
