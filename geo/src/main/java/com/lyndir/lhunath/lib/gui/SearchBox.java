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
package com.lyndir.lhunath.lib.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * <i>{@link SearchBox} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 *
 * @author lhunath
 */
public abstract class SearchBox extends JTextField implements FocusListener, KeyListener {

    private boolean cleared;
    private int minChars;
    private final String subject;
    private int hit = 1;


    /**
     * Create a new {@link SearchBox} instance.
     *
     * @param subject The name of what will be searched for (appears in the gray text).
     */
    protected SearchBox(String subject) {

        this.subject = subject;

        setHorizontalAlignment( CENTER );
        addFocusListener( this );
        addKeyListener( this );
        minChars = 3;

        /* Emulate lost focus to set the initial text and color. */
        focusLost( null );
    }

    /**
     * Create a new {@link SearchBox} instance.
     *
     * @param subject  The name of what will be searched for (appears in the gray text).
     * @param minChars Minimum amount of characters required to activate the search.
     */
    protected SearchBox(String subject, int minChars) {

        this( subject );
        setMinChars( minChars );
    }

    /**
     * This method will be called when the search string changes. Implement this to make the search string take effect
     * and return <code>true</code> if it did.
     *
     * @param text The text in the box.
     *
     * @return <code>true</code> will make the search text black, <code>false</code> will make the search text red.
     */
    protected abstract boolean isMatch(String text);

    /**
     * Retrieve the current string in the search box (trimmed).
     *
     * @return The trimmed search string.
     */
    @Override
    public String getText() {

        return super.getText().trim();
    }

    /**
     * Retrieve the minChars of this {@link SearchBox}.
     *
     * @return Guess.
     */
    public int getMinChars() {

        return minChars;
    }

    /**
     * Set the minChars of this {@link SearchBox}.
     *
     * @param minChars Guess.
     */
    public void setMinChars(int minChars) {

        this.minChars = minChars;
    }

    /**
     * Check whether there's any relevant search text in the field. This ignores the default gray search message.
     *
     * @return <code>true</code> if no search text is in the field.
     */
    public boolean isClear() {

        return getForeground().equals( Color.gray ) || getText().length() == 0;
    }

    /**
     * @return The hit of this {@link SearchBox}.
     */
    public int getHit() {

        return hit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusGained(FocusEvent e) {

        if (cleared)
            return;

        setText( "" ); //$NON-NLS-1$
        cleared = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusLost(FocusEvent e) {

        if (getText().length() != 0)
            return;

        setText( ("Search " + subject).trim() ); //$NON-NLS-1$
        setForeground( Color.gray );
        cleared = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            hit++;
        else
            hit = 1;

        if (!String.valueOf( e.getKeyChar() ).matches( "\\w" ))
            return;

        if (getText().length() > 0 && getText().length() < minChars || !isMatch( getText().trim() ))
            setForeground( Color.red );
        else
            setForeground( Color.black );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(KeyEvent e) {

        /* Not needed. */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent e) {

        /* Not needed. */
    }
}
