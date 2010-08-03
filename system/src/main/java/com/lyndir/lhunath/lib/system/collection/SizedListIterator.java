package com.lyndir.lhunath.lib.system.collection;

import java.util.List;
import java.util.ListIterator;


/**
 * <h2>{@link SizedListIterator}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 06, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class SizedListIterator<T> extends SizedIterator<T> implements ListIterator<T> {

    @Override
    protected abstract ListIterator<T> delegate();

    public void add(T element) {
        delegate().add( element );
    }

    public boolean hasPrevious() {
        return delegate().hasPrevious();
    }

    public int nextIndex() {
        return delegate().nextIndex();
    }

    public T previous() {
        return delegate().previous();
    }

    public int previousIndex() {
        return delegate().previousIndex();
    }

    public void set(T element) {
        delegate().set( element );
    }

    /**
     * Create an iterator that claims to have a certain constant size.
     *
     * @param source The iterator that provides the data for this iterator.
     * @param size   The constant size this iterator should claim to have.
     * @param <T>    The type of objects this iterator will provide.
     *
     * @return A sized iterator backed by the given iterator which claims to have the given constant size.
     */
    public static <T> SizedListIterator<T> of(final ListIterator<T> source, final int size) {

        return new SizedListIterator<T>() {

            @Override
            public int size() {

                return size;
            }

            @Override
            protected ListIterator<T> delegate() {

                return source;
            }
        };
    }

    public static <T> SizedListIterator<T> of(final List<T> source) {

        return of( source.listIterator(), source.size() );
    }
}
