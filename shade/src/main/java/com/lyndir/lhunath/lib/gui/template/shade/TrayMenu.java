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
package com.lyndir.lhunath.lib.gui.template.shade;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;


/**
 * <i>{@link TrayMenu} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 *
 * @author lhunath
 */
public class TrayMenu extends JPopupMenu implements MouseListener {

    protected final Component invoker;


    /**
     * Create a new {@link TrayMenu} instance.
     *
     * @param invoker
     *            The component that is responsible for the invocation of this menu.
     */
    public TrayMenu(Component invoker) {

        this.invoker = invoker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(final MouseEvent e) {

        /* Popup Button. */
        if (e.getModifiers() == Event.META_MASK) {
            Point point = e.getPoint();
            SwingUtilities.convertPointToScreen( point, e.getComponent() );
            setLocation( point );
            setInvoker( invoker );
            setVisible( true );

            SwingUtilities.invokeLater( new Runnable() {

                private static final int PADDING      = 10;
                private final Dimension  screenBounds = Toolkit.getDefaultToolkit().getScreenSize();


                @Override
                public void run() {

                    int x = e.getX(), y = e.getY();
                    if (x + getWidth() + PADDING > screenBounds.getWidth())
                        x -= getWidth() - PADDING;
                    if (y + getHeight() + PADDING > screenBounds.getHeight())
                        y -= getHeight() - PADDING;

                    setLocation( x, y );
                }
            } );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    /* Not interested. */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {

    /* Not interested. */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(final MouseEvent e) {

    /* Not interested. */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {

    /* Not interested. */
    }
}
