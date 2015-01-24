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
package com.lyndir.lhunath.opal.gui.zui;

import org.piccolo2d.PCanvas;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import javax.swing.*;


/**
 * <i>{@link PMenu} - [in short] (TODO).</i><br> <br> [description / usage].<br> <br>
 *
 * @author lhunath
 */
public class PMenu extends PBox implements PInputEventListener {

    private Action action;

    /**
     * Create a new {@link PMenu} instance that will act as a <b>menu container</b.
     *
     * @param canvas The canvas on which this {@link PMenu} will act.
     * @param title  The title of the menu.
     */
    public PMenu(final PCanvas canvas, final String title) {

        super( canvas, title );

        setFont( getFont().deriveFont( Font.BOLD ).deriveFont( 15f ) );
    }

    /**
     * Create a new {@link PMenu} instance that will act as a <b>menu item</b>.
     *
     * @param canvas The canvas on which this {@link PMenu} will act.
     * @param action The action that will be triggered with this menu item.
     */
    public PMenu(final PCanvas canvas, final Action action) {

        super( canvas, action.getValue( Action.NAME ) == null? null: action.getValue( Action.NAME ).toString() );

        setIcon( (Icon) action.getValue( Action.SMALL_ICON ) );
        setOutlinePaint( null );
        this.action = action;
        setLocked( true );
    }

    /**
     * Create a new {@link PMenu} instance that will act as a <b>separator</b>.
     *
     * @param canvas The canvas on which this {@link PMenu} will act.
     */
    public PMenu(final PCanvas canvas) {

        super( canvas, null );

        setOutlinePaint( null );
        setPickable( false );
        setLocked( true );
    }

    /**
     * Add a new item to this {@link PMenu}.
     *
     * @param item The action that will be triggered with this item.
     */
    public void addItem(final Action item) {

        PMenu menuItem = new PMenu( canvas, item );
        addChild( menuItem );
    }

    /**
     * Add a new item to this {@link PMenu} that acts as a separator.
     */
    public void addSeparator() {

        PMenu menuItem = new PMenu( canvas );
        addChild( menuItem );
    }

    /**
     * Show this menu at the given position.
     *
     * @param position The position to show this menu in global canvas coordinates.
     */
    public void show(final Point2D position) {

        canvas.getCamera().addChild( this );
        setOffset( position );

        canvas.addInputEventListener( this );
    }

    /**
     * Hide this menu by removing it from the canvas.<br> <br> If invoked on a menu item, the item's menu will be hidden.
     */
    protected void hide() {

        if (getParent() instanceof PMenu)
            ((PMenu) getParent()).hide();

        else {
            canvas.removeInputEventListener( this );
            removeFromParent();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void processEvent(final PInputEvent event, final int type) {

        if (type == MouseEvent.MOUSE_CLICKED)
            hide();

        if (!(event.getPickedNode() instanceof PMenu) || !getChildrenReference().contains( event.getPickedNode() ))
            return;

        InputEvent sEvent = event.getSourceSwingEvent();
        Action childAction = ((PMenu) event.getPickedNode()).action;
        String command = childAction.getValue( Action.ACTION_COMMAND_KEY ) == null? null
                : childAction.getValue( Action.ACTION_COMMAND_KEY ).toString();

        if (type == MouseEvent.MOUSE_CLICKED && event.isLeftMouseButton())
            childAction.actionPerformed(
                    new ActionEvent( sEvent.getSource(), sEvent.getID(), command, sEvent.getWhen(), sEvent.getModifiers() ) );
    }
}
