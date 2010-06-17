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
package com.lyndir.lhunath.lib.system.dummy;

import javax.swing.event.ListSelectionEvent;


/**
 * <i>NullListEvent - A dummy {@link ListSelectionEvent} object.</i><br> <br> This is a convenience way of triggering list selection events
 * with only specifying an object source.<br> <br> NOTE: This might cause odd behaviour if the method triggered with this object relies on
 * properties other than the source object.<br> <br>
 *
 * @author lhunath
 */
public class NullListEvent extends ListSelectionEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new NullListEvent instance.
     *
     * @param source The object upon which the list event takes place.
     */
    public NullListEvent(final Object source) {

        this( source, 0, 0, false );
    }

    /**
     * Create a new NullListEvent instance.
     *
     * @param source      The object upon which the list event takes place.
     * @param firstIndex  The first index in the range, &lt;= lastIndex
     * @param lastIndex   The last index in the range, &gt;= firstIndex
     * @param isAdjusting Whether or not this is one in a series of multiple events, where changes are still being made
     */
    public NullListEvent(final Object source, final int firstIndex, final int lastIndex, final boolean isAdjusting) {

        super( source, firstIndex, lastIndex, isAdjusting );
    }
}
