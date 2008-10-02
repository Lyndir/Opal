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

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * The poller acts as an interface between two threads that need to exchange events.<br>
 * 
 * @param <K>
 *            The type of key that owns the elements to poll.
 * @param <E>
 *            The type of elements that will be queued in this poller.
 * @author lhunath
 */
public class Poller<K, E> {

    private Map<K, Queue<E>> queues;


    /**
     * Offer a new element to be polled.
     * 
     * @param key
     *            The key resposible for the element to add.
     * @param element
     *            The new element to offer to the Poller's queue for the given key.
     */
    public void offer(K key, E element) {

        Queue<E> queue = queues.get( key );
        if (queue == null)
            queues.put( key, queue = new ConcurrentLinkedQueue<E>() );

        queue.offer( element );
    }

    /**
     * Check whether there are any elements available for a given key.
     * 
     * @param owner
     *            The key resposible for the requested element.
     * @return An element polled for the given key, or null when that key does not have a queue or has no elements
     *         queued.
     */
    public E poll(K owner) {

        if (owner != null && queues.containsKey( owner ))
            return queues.get( owner ).poll();

        return null;
    }

    /**
     * Check which key has available elements.
     * 
     * @return A key with elements that can be polled, or null if there is no such key.
     */
    public K pollKey() {

        for (K key : queues.keySet())
            if (!queues.get( key ).isEmpty())
                return key;

        return null;
    }
}
