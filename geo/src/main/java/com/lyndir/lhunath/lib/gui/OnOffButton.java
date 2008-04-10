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

import javax.swing.Icon;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * <i>OnOffButton - A toggle button with configurable text for its toggle states.</i><br>
 * <br>
 * This button shows the onText specified in the constructor when it is selected and the offText when it is not.<br>
 * <br>
 * 
 * @author lhunath
 */
public class OnOffButton extends GButton implements ChangeListener {

    private String onText, offText;
    private Icon   onIcon, offIcon;


    /**
     * Create a new OnOffButton instance.
     */
    public OnOffButton() {

        this( "On", "Off" );
    }

    /**
     * Create a new OnOffButton instance.
     * 
     * @param onText
     *        The text that shows on the button when it is selected.
     * @param offText
     *        The text that shows on the button when it is not selected.
     */
    public OnOffButton(String onText, String offText) {

        this( onText, null, offText, null );
    }

    /**
     * Create a new OnOffButton instance.
     * 
     * @param onIcon
     *        The icon that shows on the button when it is selected.
     * @param offIcon
     *        The icon that shows on the button when it is not selected.
     */
    public OnOffButton(Icon onIcon, Icon offIcon) {

        this( null, onIcon, null, offIcon );
    }

    /**
     * Create a new OnOffButton instance.
     * 
     * @param onText
     *        The text that shows on the button when it is selected.
     * @param onIcon
     *        The icon that shows on the button when it is selected.
     * @param offText
     *        The text that shows on the button when it is not selected.
     * @param offIcon
     *        The icon that shows on the button when it is not selected.
     */
    public OnOffButton(String onText, Icon onIcon, String offText, Icon offIcon) {

        super( onIcon );
        setModel( new ToggleButtonModel() );

        this.onText = onText;
        this.onIcon = onIcon;
        this.offText = offText;
        this.offIcon = offIcon;

        addChangeListener( this );
        stateChanged( null );
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {

        if (isSelected()) {
            setText( onText );
            setIcon( onIcon );
        } else {
            setText( offText );
            setIcon( offIcon );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return "[ " + onText + " / " + offText + " ]";
    }
}
