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

import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


/**
 * <i>{@link DragListener} - An adapter class that implements functionality needed to make the component dragable in its
 * container.</i><br>
 * <br>
 * The constructor defines the component that will be dragged. Use {@link #install()} to recursively add listeners to
 * the component and its children.<br>
 * <br>
 *
 * @author lhunath
 */
public class DragListener extends MouseAdapter implements MouseMotionListener {

    private Point     startDrag;
    private Container dragComponent;
    private Point     startLoc;


    /**
     * Create a new {@link DragListener} instance.
     *
     * @param dragComponent
     *            The component that shall be dragged using this listener.
     */
    public DragListener(Container dragComponent) {

        this.dragComponent = dragComponent;
    }

    /**
     * Install the listeners on this {@link DragListener}'s component, recursively.
     */
    public void install() {

        install( dragComponent );
    }

    /**
     * Install the listeners on the given container, recursively.
     *
     * @param container
     *            The container in which to install this {@link DragListener}.
     */
    public void install(Container container) {

        for (Component c : container.getComponents()) {
            c.addMouseListener( this );
            c.addMouseMotionListener( this );
            if (c instanceof Container)
                install( (Container) c );
        }
    }

    /**
     * Remove the listeners from this {@link DragListener}'s component, recursively.
     */
    public void uninstall() {

        uninstall( dragComponent );
    }

    /**
     * Remove the listeners from the given container, recursively.
     *
     * @param container
     *            The container from which to remove this {@link DragListener}.
     */
    public void uninstall(Container container) {

        for (Component c : container.getComponents()) {
            c.removeMouseListener( this );
            c.removeMouseMotionListener( this );
            if (c instanceof Container)
                uninstall( (Container) c );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {

        /* Popup Button. */
        if (e.getModifiers() == Event.META_MASK && e.getSource() instanceof Component) {

            /* Start vectors. */
            startLoc = dragComponent.getLocationOnScreen();
            startDrag = e.getPoint();

            /* Make the drag vector relative to the screen, not its parent the component. */
            Point sourcePos = ((Component) e.getSource()).getLocationOnScreen();
            startDrag.translate( sourcePos.x, sourcePos.y );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        startDrag = startLoc = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (startDrag != null) {

            /* Calculate the event point on screen. */
            Point point = e.getPoint(); // FIXME: After changing the size of the component, this
            // bugs out.
            Point pointRef = e.getComponent().getLocationOnScreen();
            point.translate( pointRef.x, pointRef.y );

            /* Calculate the drag vector compared to the start of the drag. */
            point.translate( -startDrag.x, -startDrag.y );

            /* Calculate the new dragComponent's location compared to the start of the drag. */
            point.translate( startLoc.x, startLoc.y );

            /* Place the component on this calculated position. */
            dragComponent.setLocation( point );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(MouseEvent e) {

    /* Don't care. */
    }
}
