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
public class LoggingEventHandler
        implements ComponentListener, ContainerListener, FocusListener, AncestorListener, HierarchyBoundsListener,
        HierarchyListener, WindowFocusListener, WindowListener, WindowStateListener, PropertyChangeListener,
        MouseInputListener, MouseWheelListener, KeyListener, InputMethodListener {

    private static final Logger logger = Logger.get( LoggingEventHandler.class );

    private final String              name;


    /**
     * Watch all non-input events that can occur on the given component.
     *
     * @param name
     *            The name to use in the log output for identifying this component.
     * @param c
     *            The component to watch events on.
     */
    public static void watchComponentEvents(String name, JComponent c) {

        LoggingEventHandler handler = new LoggingEventHandler( name );
        c.addAncestorListener( handler );
        c.addComponentListener( handler );
        c.addContainerListener( handler );
        c.addFocusListener( handler );
        c.addHierarchyBoundsListener( handler );
        c.addHierarchyListener( handler );
        c.addPropertyChangeListener( handler );
    }

    /**
     * Watch all events that can occur on the given window.
     *
     * @param name
     *            The name to use in the log output for identifying this window.
     * @param w
     *            The window to watch events on.
     */
    public static void watchWindowEvents(String name, Window w) {

        LoggingEventHandler handler = new LoggingEventHandler( name );
        w.addComponentListener( handler );
        w.addContainerListener( handler );
        w.addFocusListener( handler );
        w.addHierarchyBoundsListener( handler );
        w.addHierarchyListener( handler );
        w.addPropertyChangeListener( handler );
        w.addWindowFocusListener( handler );
        w.addWindowListener( handler );
        w.addWindowStateListener( handler );
    }

    /**
     * Watch all input events that can occur on the given component.
     *
     * @param name
     *            The name to use in the log output for identifying this component.
     * @param c
     *            The component to watch events on.
     */
    public static void watchInputEvents(String name, Component c) {

        LoggingEventHandler handler = new LoggingEventHandler( name );
        c.addInputMethodListener( handler );
        c.addKeyListener( handler );
        c.addMouseListener( handler );
        c.addMouseMotionListener( handler );
        c.addMouseWheelListener( handler );
    }

    /**
     * Create a new {@link LoggingEventHandler} instance.
     *
     * @param name
     *            The name to use for the components handled by this handler.
     */
    public LoggingEventHandler(String name) {

        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentHidden(ComponentEvent e) {

        logger.dbg( "%s: hidden [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentMoved(ComponentEvent e) {

        logger.dbg( "%s: moved [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentResized(ComponentEvent e) {

        logger.dbg( "%s: sized [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentShown(ComponentEvent e) {

        logger.dbg( "%s: shown [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentAdded(ContainerEvent e) {

        logger.dbg( "%s: added [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void componentRemoved(ContainerEvent e) {

        logger.dbg( "%s: removed [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusGained(FocusEvent e) {

        logger.dbg( "%s: focussed [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusLost(FocusEvent e) {

        logger.dbg( "%s: unfocussed [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorAdded(AncestorEvent e) {

        logger.dbg( "%s: ancestor added [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorMoved(AncestorEvent e) {

        logger.dbg( "%s: ancestor moved [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorRemoved(AncestorEvent e) {

        logger.dbg( "%s: ancestor removed [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorMoved(HierarchyEvent e) {

        logger.dbg( "%s: hierarchy ancestor moved [Id: %d, Flags: %d]", name, e.getID(), e.getChangeFlags() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ancestorResized(HierarchyEvent e) {

        logger.dbg( "%s: hierarchy ancestor sized [Id: %d, Flags: %d]", name, e.getID(), e.getChangeFlags() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hierarchyChanged(HierarchyEvent e) {

        logger.dbg( "%s: hierarchy changed [Id: %d, Flags: %d]", name, e.getID(), e.getChangeFlags() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowGainedFocus(WindowEvent e) {

        logger.dbg( "%s: window focussed [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowLostFocus(WindowEvent e) {

        logger.dbg( "%s: window unfocussed [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowActivated(WindowEvent e) {

        logger.dbg( "%s: window activated [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosed(WindowEvent e) {

        logger.dbg( "%s: window closed [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(WindowEvent e) {

        logger.dbg( "%s: window closing [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeactivated(WindowEvent e) {

        logger.dbg( "%s: window deactivated [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowDeiconified(WindowEvent e) {

        logger.dbg( "%s: window deiconified [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowIconified(WindowEvent e) {

        logger.dbg( "%s: window iconified [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowOpened(WindowEvent e) {

        logger.dbg( "%s: window opened [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void windowStateChanged(WindowEvent e) {

        logger.dbg( "%s: window state [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                    e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {

        logger.dbg( "%s: property [Name: %s, Value: From %s to %s]", name, e.getPropertyName(), e.getOldValue(),
                    e.getNewValue() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        logger.dbg( "%s: clicked [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                    e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {

        logger.dbg( "%s: entered [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                    e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {

        logger.dbg( "%s: exited [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                    e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {

        logger.dbg( "%s: pressed [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                    e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        logger.dbg( "%s: released [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                    e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        logger.dbg( "%s: dragged [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                    e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(MouseEvent e) {

        logger.dbg( "%s: moved [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                    name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                    e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        logger.dbg(
                    "%s: wheeled [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), ScrollType: %d, ScrollAmount: %d, ScrollRotation: %d, Time: %dms ago]",
                    name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                    e.getY(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation(), System.currentTimeMillis()
                                                                                            - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyPressed(KeyEvent e) {

        logger.dbg( "%s: key pressed [Id: %d, Char: %s (%d), Location: %d, Time: %dms ago]", name, e.getID(),
                    e.getKeyChar(), e.getKeyCode(), e.getKeyLocation(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyReleased(KeyEvent e) {

        logger.dbg( "%s: key released [Id: %d, Char: %s (%d), Location: %d, Time: %dms ago]", name, e.getID(),
                    e.getKeyChar(), e.getKeyCode(), e.getKeyLocation(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void keyTyped(KeyEvent e) {

        logger.dbg( "%s: key typed [Id: %d, Char: %s (%d), Location: %d, Time: %dms ago]", name, e.getID(),
                    e.getKeyChar(), e.getKeyCode(), e.getKeyLocation(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void caretPositionChanged(InputMethodEvent e) {

        logger.dbg( "%s: caret [Id: %d, Chars: %d, Text: %s, Time: %dms ago]", name, e.getID(),
                    e.getCommittedCharacterCount(), e.getText(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inputMethodTextChanged(InputMethodEvent e) {

        logger.dbg( "%s: text changed [Id: %d, Chars: %d, Text: %s, Time: %dms ago]", name, e.getID(),
                    e.getCommittedCharacterCount(), e.getText(), System.currentTimeMillis() - e.getWhen() );
    }
}
