/*
 *   Copyright 2008, Maarten Billemont
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

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <h2>{@link LoggingEventHandler}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * [description / usage].
 * </p>
 *
 * <p>
 * <i>Apr 19, 2008</i>
 * </p>
 *
 * @author mbillemo
 */
public class LoggingEventHandler implements ComponentListener, ContainerListener, FocusListener, AncestorListener,
        HierarchyBoundsListener, HierarchyListener, WindowFocusListener, WindowListener, WindowStateListener,
        PropertyChangeListener, MouseInputListener, MouseWheelListener, KeyListener, InputMethodListener {

    private static final Logger logger = Logger.get( LoggingEventHandler.class );

    private final String name;


    /**
     * Watch all non-input events that can occur on the given component.
     *
     * @param name      The name to use in the log output for identifying this component.
     * @param component The component to watch events on.
     */
    public static void watchComponentEvents(final String name, final JComponent component) {

        LoggingEventHandler handler = new LoggingEventHandler( name );
        component.addAncestorListener( handler );
        component.addComponentListener( handler );
        component.addContainerListener( handler );
        component.addFocusListener( handler );
        component.addHierarchyBoundsListener( handler );
        component.addHierarchyListener( handler );
        component.addPropertyChangeListener( handler );
    }

    /**
     * Watch all events that can occur on the given window.
     *
     * @param name   The name to use in the log output for identifying this window.
     * @param window The window to watch events on.
     */
    public static void watchWindowEvents(final String name, final Window window) {

        LoggingEventHandler handler = new LoggingEventHandler( name );
        window.addComponentListener( handler );
        window.addContainerListener( handler );
        window.addFocusListener( handler );
        window.addHierarchyBoundsListener( handler );
        window.addHierarchyListener( handler );
        window.addPropertyChangeListener( handler );
        window.addWindowFocusListener( handler );
        window.addWindowListener( handler );
        window.addWindowStateListener( handler );
    }

    /**
     * Watch all input events that can occur on the given component.
     *
     * @param name      The name to use in the log output for identifying this component.
     * @param component The component to watch events on.
     */
    public static void watchInputEvents(final String name, final Component component) {

        LoggingEventHandler handler = new LoggingEventHandler( name );
        component.addInputMethodListener( handler );
        component.addKeyListener( handler );
        component.addMouseListener( handler );
        component.addMouseMotionListener( handler );
        component.addMouseWheelListener( handler );
    }

    /**
     * Create a new {@link LoggingEventHandler} instance.
     *
     * @param name The name to use for the components handled by this handler.
     */
    public LoggingEventHandler(final String name) {

        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentHidden(final ComponentEvent event) {

        logger.dbg( "%s: hidden [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentMoved(final ComponentEvent event) {

        logger.dbg( "%s: moved [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentResized(final ComponentEvent event) {

        logger.dbg( "%s: sized [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentShown(final ComponentEvent event) {

        logger.dbg( "%s: shown [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentAdded(final ContainerEvent event) {

        logger.dbg( "%s: added [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentRemoved(final ContainerEvent event) {

        logger.dbg( "%s: removed [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusGained(final FocusEvent event) {

        logger.dbg( "%s: focused [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusLost(final FocusEvent event) {

        logger.dbg( "%s: unfocused [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorAdded(final AncestorEvent event) {

        logger.dbg( "%s: ancestor added [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorMoved(final AncestorEvent event) {

        logger.dbg( "%s: ancestor moved [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorRemoved(final AncestorEvent event) {

        logger.dbg( "%s: ancestor removed [Id: %d]", name, event.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorMoved(final HierarchyEvent event) {

        logger.dbg( "%s: hierarchy ancestor moved [Id: %d, Flags: %d]", name, event.getID(), event.getChangeFlags() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorResized(final HierarchyEvent event) {

        logger.dbg( "%s: hierarchy ancestor sized [Id: %d, Flags: %d]", name, event.getID(), event.getChangeFlags() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hierarchyChanged(final HierarchyEvent event) {

        logger.dbg( "%s: hierarchy changed [Id: %d, Flags: %d]", name, event.getID(), event.getChangeFlags() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowGainedFocus(final WindowEvent event) {

        logger.dbg( "%s: window focused [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowLostFocus(final WindowEvent event) {

        logger.dbg( "%s: window unfocused [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowActivated(final WindowEvent event) {

        logger.dbg( "%s: window activated [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosed(final WindowEvent event) {

        logger.dbg( "%s: window closed [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(final WindowEvent event) {

        logger.dbg( "%s: window closing [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeactivated(final WindowEvent event) {

        logger.dbg( "%s: window deactivated [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeiconified(final WindowEvent event) {

        logger.dbg( "%s: window deiconified [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowIconified(final WindowEvent event) {

        logger.dbg( "%s: window iconified [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowOpened(final WindowEvent event) {

        logger.dbg( "%s: window opened [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowStateChanged(final WindowEvent event) {

        logger.dbg( "%s: window state [Id: %d, State: From %d to %d]", name, event.getID(), event.getOldState(),
                    event.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(final PropertyChangeEvent event) {

        logger.dbg( "%s: property [Name: %s, Value: From %s to %s]", name, event.getPropertyName(), event.getOldValue(),
                    event.getNewValue() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(final MouseEvent event) {

        logger.dbg( "%s: clicked [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, event.getID(), event.getModifiers(), event.getModifiersEx(), event.getButton(),
                    event.getClickCount(), event.getX(), event.getY(), System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(final MouseEvent event) {

        logger.dbg( "%s: entered [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, event.getID(), event.getModifiers(), event.getModifiersEx(), event.getButton(),
                    event.getClickCount(), event.getX(), event.getY(), System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(final MouseEvent event) {

        logger.dbg( "%s: exited [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, event.getID(), event.getModifiers(), event.getModifiersEx(), event.getButton(),
                    event.getClickCount(), event.getX(), event.getY(), System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(final MouseEvent event) {

        logger.dbg( "%s: pressed [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, event.getID(), event.getModifiers(), event.getModifiersEx(), event.getButton(),
                    event.getClickCount(), event.getX(), event.getY(), System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(final MouseEvent event) {

        logger.dbg( "%s: released [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, event.getID(), event.getModifiers(), event.getModifiersEx(), event.getButton(),
                    event.getClickCount(), event.getX(), event.getY(), System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(final MouseEvent event) {

        logger.dbg( "%s: dragged [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, event.getID(), event.getModifiers(), event.getModifiersEx(), event.getButton(),
                    event.getClickCount(), event.getX(), event.getY(), System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(final MouseEvent event) {

        logger.dbg( "%s: moved [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]", name,
                    event.getID(), event.getModifiers(), event.getModifiersEx(), event.getButton(),
                    event.getClickCount(), event.getX(), event.getY(), System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseWheelMoved(final MouseWheelEvent event) {

        logger.dbg(
                "%s: wheeled [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), ScrollType: %d, ScrollAmount: %d, ScrollRotation: %d, Time: %dms ago]",
                name, event.getID(), event.getModifiers(), event.getModifiersEx(), event.getButton(),
                event.getClickCount(), event.getX(), event.getY(), event.getScrollType(), event.getScrollAmount(),
                event.getWheelRotation(), System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(final KeyEvent event) {

        logger.dbg( "%s: key pressed [Id: %d, Char: %s (%d), Location: %d, Time: %dms ago]", name, event.getID(),
                    event.getKeyChar(), event.getKeyCode(), event.getKeyLocation(),
                    System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(final KeyEvent event) {

        logger.dbg( "%s: key released [Id: %d, Char: %s (%d), Location: %d, Time: %dms ago]", name, event.getID(),
                    event.getKeyChar(), event.getKeyCode(), event.getKeyLocation(),
                    System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(final KeyEvent event) {

        logger.dbg( "%s: key typed [Id: %d, Char: %s (%d), Location: %d, Time: %dms ago]", name, event.getID(),
                    event.getKeyChar(), event.getKeyCode(), event.getKeyLocation(),
                    System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void caretPositionChanged(final InputMethodEvent event) {

        logger.dbg( "%s: caret [Id: %d, Chars: %d, Text: %s, Time: %dms ago]", name, event.getID(),
                    event.getCommittedCharacterCount(), event.getText(), System.currentTimeMillis() - event.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputMethodTextChanged(final InputMethodEvent event) {

        logger.dbg( "%s: text changed [Id: %d, Chars: %d, Text: %s, Time: %dms ago]", name, event.getID(),
                    event.getCommittedCharacterCount(), event.getText(), System.currentTimeMillis() - event.getWhen() );
    }
}
