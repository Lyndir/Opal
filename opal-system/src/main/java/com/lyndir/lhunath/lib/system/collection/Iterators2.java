package com.lyndir.lhunath.lib.system.collection;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * TODO: Get rid of this as soon as it's in Guava: http://code.google.com/p/google-collections/issues/detail?id=110
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class Iterators2 {

    public static <E> ListIterator<E> filter(final ListIterator<E> listIterator, final Predicate<? super E> filter) {

        return new FilteringListIterator<E>( listIterator, filter );
    }

    static class FilteringListIterator<E> implements ListIterator<E> {

        private final Predicate<? super E> filter;
        private final ListIterator<E>      listIterator;

        private int index;
        private int offset;
        private E   elementToReturn;
        private Accessed accessed = Accessed.NONE;


        private enum Accessed {

            NONE( false ) {
                @Override
                void calibrate(final ListIterator<?> listIterator) {

                    throw new IllegalStateException();
                }
            },
            NEXT( true ) {
                @Override
                void calibrate(final ListIterator<?> listIterator) {

                    listIterator.next();
                    listIterator.previous();
                }
            },
            PREVIOUS( true ) {
                @Override
                void calibrate(final ListIterator<?> listIterator) {

                    listIterator.previous();
                    listIterator.next();
                }
            };

            final boolean exists;

            Accessed(final boolean accessed) {

                exists = accessed;
            }

            abstract void calibrate(ListIterator<?> listIterator);
        }

        FilteringListIterator(final ListIterator<E> listIterator, final Predicate<? super E> filter) {

            this.listIterator = Preconditions.checkNotNull( listIterator );
            this.filter = Preconditions.checkNotNull( filter );
            index = 0;
        }

        @Override
        public void add(final E e) {

            moveBack();
            listIterator.add( e );
            index++;
            accessed = Accessed.NONE;
        }

        @Override
        public boolean hasNext() {

            if (elementToReturn != null && offset > 0) {
                return true;
            }
            elementToReturn = null;
            while (offset < 0) {
                listIterator.next();
                offset++;
            }
            do {
                if (!listIterator.hasNext()) {
                    return false;
                }
                E element = listIterator.next();
                offset++;
                if (filter.apply( element )) {
                    elementToReturn = element;
                    return true;
                }
            }
            while (true);
        }

        @Override
        public E next() {

            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            try {
                return elementToReturn;
            }
            finally {
                offset = 0;
                accessed = Accessed.PREVIOUS;
                elementToReturn = null;
                index++;
            }
        }

        @Override
        public boolean hasPrevious() {

            if (elementToReturn != null && offset < 0) {
                return true;
            }
            elementToReturn = null;
            while (offset > 0) {
                listIterator.previous();
                offset--;
            }
            do {
                if (!listIterator.hasPrevious()) {
                    return false;
                }
                E element = listIterator.previous();
                offset--;
                if (filter.apply( element )) {
                    elementToReturn = element;
                    return true;
                }
            }
            while (true);
        }

        @Override
        public E previous() {

            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            try {
                return elementToReturn;
            }
            finally {
                offset = 0;
                accessed = Accessed.NEXT;
                elementToReturn = null;
                index--;
            }
        }

        @Override
        public int nextIndex() {

            return index;
        }

        @Override
        public int previousIndex() {

            return index - 1;
        }

        @Override
        public void remove() {

            moveBack();
            accessed.calibrate( listIterator );
            if (accessed == Accessed.PREVIOUS) {
                index--;
            }
            listIterator.remove();
            accessed = Accessed.NONE;
            elementToReturn = null;
        }

        @Override
        public void set(final E e) {

            moveBack();
            accessed.calibrate( listIterator );
            listIterator.set( e );
        }

        private void moveBack() {

            while (offset > 0) {
                listIterator.previous();
                offset--;
            }
            while (offset < 0) {
                listIterator.next();
                offset++;
            }
        }
    }
}
