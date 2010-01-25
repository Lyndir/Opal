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

import java.util.ArrayList;
import java.util.List;


/**
 * <i>{@link Emitter} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 *
 * @param <E>
 *            The type of event that can be triggered.
 * @author lhunath
 */
public abstract class Emitter<E> {

    private final List<Receiver<E>> receivers;


    /**
     * Create a new {@link Emitter} instance.
     */
    protected Emitter() {

        receivers = new ArrayList<Receiver<E>>();
    }

    /**
     * Add a receiver that will receive messages emitted by this {@link Emitter}.
     *
     * @param receiver
     *            The object that will receive this object's messages.
     */
    public void addReceiver(Receiver<E> receiver) {

        receivers.add( receiver );
    }

    /**
     * Trigger an event so that all {@link Receiver}s process it.
     *
     * @param event
     *            The event that needs to be sent out.
     *
     * @return <code>true</code> if at least one {@link Receiver} successfully processed the event.
     */
    protected boolean trigger(E event) {

        boolean success = false;
        for (Receiver<E> receiver : receivers)
            success &= receiver.fire( event );

        return success;
    }
}
