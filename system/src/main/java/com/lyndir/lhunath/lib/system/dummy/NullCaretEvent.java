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

import javax.swing.event.CaretEvent;


/**
 * <i>NullCaretEvent - A dummy {@link CaretEvent} object.</i><br>
 * <br>
 * This is a convenience way of triggering caret events with only specifying an object source.<br>
 * <br>
 * NOTE: This might cause odd behaviour if the method triggered with this object relies on properties other than the
 * source object.<br>
 * <br>
 *
 * @author lhunath
 */
public class NullCaretEvent extends CaretEvent {

    private static final long serialVersionUID = 1L;


    /**
     * Create a new NullCaretEvent instance.
     *
     * @param source The source of the caret event.
     */
    public NullCaretEvent(final Object source) {

        super( source );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDot() {

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMark() {

        return 0;
    }

}
