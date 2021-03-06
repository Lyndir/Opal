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
package com.lyndir.lhunath.opal.system.dummy;

import java.awt.*;
import java.awt.event.ItemEvent;


/**
 * <i>NullItemEvent - A dummy {@link ItemEvent} object.</i><br> <br> This is a convenience way of triggering item events with only
 * specifying an object source.<br> <br> NOTE: This might cause odd behaviour if the method triggered with this object relies on properties
 * other than the source object.<br> <br>
 *
 * @author lhunath
 */
public class NullItemEvent extends ItemEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new NullItemEvent instance.
     *
     * @param source the {@code ItemSelectable} object that originated the event
     */
    public NullItemEvent(final ItemSelectable source) {

        this( source, 0, 0 );
    }

    /**
     * Create a new NullItemEvent instance.
     *
     * @param source      the {@code ItemSelectable} object that originated the event
     * @param id          an integer that identifies the event type
     * @param stateChange an integer that indicates whether the item was selected or deselected
     */
    public NullItemEvent(final ItemSelectable source, final int id, final int stateChange) {

        super( source, id, null, stateChange );
    }
}
