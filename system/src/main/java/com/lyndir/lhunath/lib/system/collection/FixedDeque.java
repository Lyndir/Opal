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
package com.lyndir.lhunath.lib.system.collection;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * <h2>{@link FixedDeque}<br>
 * <sub>A {@link Deque} implementation with a maximum size.</sub></h2>
 *
 * <p>
 * This implementation will begin to pop off old elements when it's reached its maximum size and adding new elements to
 * the head of the queue (or pop off head elements when adding new elements to the tail of the queue).
 * </p>
 *
 * <p>
 * <i>Mar 2, 2010</i>
 * </p>
 *
 * @author lhunath
 * @param <E>
 * The type of elements held in this collection.
 */
public class FixedDeque<E> implements Deque<E> {

    private int maxSize;
    private Deque<E> deque;


    /**
     * Create a new {@link FixedDeque} instance.
     *
     * @param maxSize The fixed size of this {@link Deque}.
     */
    public FixedDeque(int maxSize) {

        this.maxSize = maxSize;
        deque = new LinkedList<E>();
    }

    /**
     * Create a new {@link FixedDeque} instance initialized by copying elements form another collection.
     *
     * @param maxSize The fixed size of this {@link Deque}.
     * @param c       The collection whose elements are to be placed into this list.
     */
    public FixedDeque(int maxSize, Collection<? extends E> c) {

        this.maxSize = maxSize;
        deque = new LinkedList<E>( c );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {

        return deque.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {

        return deque.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] a) {

        return deque.toArray( a );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<?> c) {

        return deque.containsAll( c );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {

        if (!deque.addAll( c ))
            return false;

        while (deque.size() > maxSize)
            deque.pollLast();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> c) {

        return deque.removeAll( c );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection<?> c) {

        return deque.retainAll( c );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {

        deque.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFirst(E e) {

        if (deque.size() >= maxSize)
            deque.removeLast();
        deque.addFirst( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLast(E e) {

        if (deque.size() >= maxSize)
            deque.removeFirst();
        deque.addLast( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean offerFirst(E e) {

        if (deque.size() >= maxSize)
            return false;

        return deque.offerFirst( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean offerLast(E e) {

        if (deque.size() >= maxSize)
            return false;

        return deque.offerLast( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeFirst() {

        return deque.removeFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E removeLast() {

        return deque.removeLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E pollFirst() {

        return deque.pollFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E pollLast() {

        return deque.pollLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getFirst() {

        return deque.getFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E getLast() {

        return deque.getLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E peekFirst() {

        return deque.peekFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E peekLast() {

        return deque.peekLast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeFirstOccurrence(Object o) {

        return deque.removeFirstOccurrence( o );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeLastOccurrence(Object o) {

        return deque.removeLastOccurrence( o );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E e) {

        addLast( e );

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean offer(E e) {

        return offerLast( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E remove() {

        return removeFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E poll() {

        return pollFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E element() {

        return getFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E peek() {

        return peekFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void push(E e) {

        addFirst( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E pop() {

        return removeFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {

        return removeFirstOccurrence( o );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {

        return deque.contains( o );
    }

    /**
     * @return The maximum size of this {@link FixedDeque}.
     */
    public int getMaxSize() {

        return maxSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {

        return deque.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {

        return deque.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> descendingIterator() {

        return deque.descendingIterator();
    }
}
