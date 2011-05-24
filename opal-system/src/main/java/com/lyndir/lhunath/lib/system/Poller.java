/*
 *   Copyright 2005-2007 Maarten Billemont
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
package com.lyndir.lhunath.lib.system;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * A poller maintains a queue for responsible objects. Each responsible object has a queue of elements that it needs to process.
 *
 * @param <K> The responsible objects type.
 * @param <E> The responsible objects' element type.
 *
 * @author lhunath
 */
public class Poller<K, E> {

    private final Map<K, Queue<E>> queues;

    /**
     * Create a new {@link Poller} instance.
     */
    public Poller() {

        queues = Collections.synchronizedMap( new HashMap<K, Queue<E>>() );
    }

    /**
     * Offer a new element to the responsible object's queue.
     *
     * @param owner   The responsible object.
     * @param element The element that should be processed for the responsible object.
     */
    public void offer(final K owner, final E element) {

        Queue<E> queue = queues.get( owner );
        if (queue == null)
            queues.put( owner, queue = new ConcurrentLinkedQueue<E>() );

        queue.offer( element );
    }

    /**
     * Poll an element from the responsible object's queue.
     *
     * @param owner The responsible object whose queue to poll.
     *
     * @return The element that has been on the responsible object's queue the longest, or <code>null</code> if its queue is empty.
     */
    public E poll(final K owner) {

        if (owner != null && queues.containsKey( owner ))
            return queues.get( owner ).poll();

        return null;
    }

    /**
     * Check whether there is a responsible object that has available elements.
     *
     * @return A responsible object that has elements on its queue or <code>null</code> if no queues need polling. When there are multiple
     *         candidates, it is undefined which will be returned. You should continue to check this method and poll elements until it
     *         returns <code>null</code>.
     */
    public K pollKey() {

        for (final Map.Entry<K, Queue<E>> queueEntry : queues.entrySet())
            if (!queueEntry.getValue().isEmpty())
                return queueEntry.getKey();

        return null;
    }
}
