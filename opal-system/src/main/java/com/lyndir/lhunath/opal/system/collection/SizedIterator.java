package com.lyndir.lhunath.opal.system.collection;

import com.google.common.collect.ForwardingIterator;
import java.util.Collection;
import java.util.Iterator;


/**
 * <h2>{@link SizedIterator}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 06, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class SizedIterator<T> extends ForwardingIterator<T> {

    /**
     * @return The size of this iterator.  It is the amount of elements this iterator claims to be able to provide.
     */
    public abstract int size();

    /**
     * Create an iterator that claims to have a certain constant size.
     *
     * @param iterator The iterator that provides the data for this iterator.
     * @param size     The constant size this iterator should claim to have.
     * @param <T>      The type of objects this iterator will provide.
     *
     * @return A sized iterator backed by the given iterator which claims to have the given constant size.
     */
    public static <T> SizedIterator<T> of(final Iterator<T> iterator, final int size) {

        return new SizedIterator<T>() {

            @Override
            public int size() {

                return size;
            }

            @Override
            protected Iterator<T> delegate() {

                return iterator;
            }
        };
    }

    public static <T> SizedIterator<T> of(final Collection<T> source) {

        return of( source.iterator(), source.size() );
    }
}
