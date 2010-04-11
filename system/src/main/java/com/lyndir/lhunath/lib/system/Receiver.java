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

/**
 * <i>{@link Receiver} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 *
 * @author lhunath
 * @param <E>
 * The type of event that can be processed.
 */
public interface Receiver<E> {

    /**
     * An event was fired by the given source object.
     *
     * @param event The event that was fired.
     *
     * @return <code>false</code> if the event cannot be processed for some reason.
     */
    boolean fire(E event);
}
