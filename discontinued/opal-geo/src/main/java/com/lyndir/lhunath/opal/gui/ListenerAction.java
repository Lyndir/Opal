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
package com.lyndir.lhunath.opal.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


/**
 * <i>{@link ListenerAction} - [in short] (TODO).</i><br> <br> [description / usage].<br> <br>
 *
 * @author lhunath
 */
public class ListenerAction extends AbstractAction {

    private final ActionListener listener;

    /**
     * Create a new {@link ListenerAction} instance.
     *
     * @param listener The listener that will be notified of this action.
     */
    public ListenerAction(final ActionListener listener) {

        this.listener = listener;
    }

    /**
     * Create a new {@link ListenerAction} instance.
     *
     * @param name     The name of the action.
     * @param listener The listener that will be notified of this action.
     */
    public ListenerAction(final String name, final ActionListener listener) {

        super( name );
        this.listener = listener;
    }

    /**
     * Create a new {@link ListenerAction} instance.
     *
     * @param name     The name of the action.
     * @param command  The string that will identify the action that must be taken.
     * @param icon     The icon of the action.
     * @param listener The listener that will be notified of this action.
     */
    public ListenerAction(final String name, final String command, final Icon icon, final ActionListener listener) {

        super( name, icon );
        this.listener = listener;
        setActionCommand( command );
    }

    /**
     * Specify an action command string for this action.
     *
     * @param command The string that will identify the action that must be taken.
     */
    public void setActionCommand(final String command) {

        putValue( ACTION_COMMAND_KEY, command );
    }

    /**
     * Specify an action command string for this action.
     *
     * @return The string that will identify the action that must be taken.
     */
    public String getActionCommand() {

        return getValue( ACTION_COMMAND_KEY ) == null? null: getValue( ACTION_COMMAND_KEY ).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        if (listener != null)
            listener.actionPerformed( e );
    }
}
