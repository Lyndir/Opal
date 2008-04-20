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

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.MouseInputListener;

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
        MouseListener, MouseInputListener, MouseMotionListener, MouseWheelListener, KeyListener, InputMethodListener {

    private String name;


    /**
     * Watch all non-input events that can occur on the given component.
     * 
     * @param name
     *        The name to use in the log output for identifying this component.
     * @param c
     *        The component to watch events on.
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
     *        The name to use in the log output for identifying this window.
     * @param w
     *        The window to watch events on.
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
     *        The name to use in the log output for identifying this component.
     * @param c
     *        The component to watch events on.
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
     *        The name to use for the components handled by this handler.
     */
    public LoggingEventHandler(String name) {

        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public void componentHidden(ComponentEvent e) {

        Logger.config( "%s: hidden [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void componentMoved(ComponentEvent e) {

        Logger.config( "%s: moved [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void componentResized(ComponentEvent e) {

        Logger.config( "%s: sized [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void componentShown(ComponentEvent e) {

        Logger.config( "%s: shown [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void componentAdded(ContainerEvent e) {

        Logger.config( "%s: added [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void componentRemoved(ContainerEvent e) {

        Logger.config( "%s: removed [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void focusGained(FocusEvent e) {

        Logger.config( "%s: focussed [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void focusLost(FocusEvent e) {

        Logger.config( "%s: unfocussed [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void ancestorAdded(AncestorEvent e) {

        Logger.config( "%s: ancestor added [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void ancestorMoved(AncestorEvent e) {

        Logger.config( "%s: ancestor moved [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void ancestorRemoved(AncestorEvent e) {

        Logger.config( "%s: ancestor removed [Id: %d]", name, e.getID() );
    }

    /**
     * {@inheritDoc}
     */
    public void ancestorMoved(HierarchyEvent e) {

        Logger.config( "%s: hierarchy ancestor moved [Id: %d, Flags: %d]", name, e.getID(), e.getChangeFlags() );
    }

    /**
     * {@inheritDoc}
     */
    public void ancestorResized(HierarchyEvent e) {

        Logger.config( "%s: hierarchy ancestor sized [Id: %d, Flags: %d]", name, e.getID(), e.getChangeFlags() );
    }

    /**
     * {@inheritDoc}
     */
    public void hierarchyChanged(HierarchyEvent e) {

        Logger.config( "%s: hierarchy changed [Id: %d, Flags: %d]", name, e.getID(), e.getChangeFlags() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowGainedFocus(WindowEvent e) {

        Logger.config( "%s: window focussed [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowLostFocus(WindowEvent e) {

        Logger.config( "%s: window unfocussed [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowActivated(WindowEvent e) {

        Logger.config( "%s: window activated [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowClosed(WindowEvent e) {

        Logger.config( "%s: window closed [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowClosing(WindowEvent e) {

        Logger.config( "%s: window closing [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowDeactivated(WindowEvent e) {

        Logger.config( "%s: window deactivated [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowDeiconified(WindowEvent e) {

        Logger.config( "%s: window deiconified [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowIconified(WindowEvent e) {

        Logger.config( "%s: window iconified [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowOpened(WindowEvent e) {

        Logger.config( "%s: window opened [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void windowStateChanged(WindowEvent e) {

        Logger.config( "%s: window state [Id: %d, State: From %d to %d]", name, e.getID(), e.getOldState(),
                e.getNewState() );
    }

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent e) {

        Logger.config( "%s: property [Name: %s, Value: From %s to %s]", name, e.getPropertyName(), e.getOldValue(),
                e.getNewValue() );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {

        Logger.config( "%s: clicked [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseEntered(MouseEvent e) {

        Logger.config( "%s: entered [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseExited(MouseEvent e) {

        Logger.config( "%s: exited [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {

        Logger.config( "%s: pressed [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {

        Logger.config(
                "%s: released [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]", name,
                e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(), e.getY(),
                System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {

        Logger.config( "%s: dragged [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseMoved(MouseEvent e) {

        Logger.config( "%s: moved [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), Time: %dms ago]",
                name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                e.getY(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseWheelMoved(MouseWheelEvent e) {

        Logger.config(
                "%s: wheeled [Id: %d, Mod: %d, ModEx: %d, Button: %d, Clicks: %d, At: (%d,%d), ScrollType: %d, ScrollAmount: %d, ScrollRotation: %d, Time: %dms ago]",
                name, e.getID(), e.getModifiers(), e.getModifiersEx(), e.getButton(), e.getClickCount(), e.getX(),
                e.getY(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation(), System.currentTimeMillis()
                                                                                        - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void keyPressed(KeyEvent e) {

        Logger.config( "%s: key pressed [Id: %d, Char: %s (%d), Location: %d, Time: %dms ago]", name, e.getID(),
                e.getKeyChar(), e.getKeyCode(), e.getKeyLocation(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void keyReleased(KeyEvent e) {

        Logger.config( "%s: key released [Id: %d, Char: %s (%d), Location: %d, Time: %dms ago]", name, e.getID(),
                e.getKeyChar(), e.getKeyCode(), e.getKeyLocation(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void keyTyped(KeyEvent e) {

        Logger.config( "%s: key typed [Id: %d, Char: %s (%d), Location: %d, Time: %dms ago]", name, e.getID(),
                e.getKeyChar(), e.getKeyCode(), e.getKeyLocation(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void caretPositionChanged(InputMethodEvent e) {

        Logger.config( "%s: caret [Id: %d, Chars: %d, Text: %s, Time: %dms ago]", name, e.getID(),
                e.getCommittedCharacterCount(), e.getText(), System.currentTimeMillis() - e.getWhen() );
    }

    /**
     * {@inheritDoc}
     */
    public void inputMethodTextChanged(InputMethodEvent e) {

        Logger.config( "%s: text changed [Id: %d, Chars: %d, Text: %s, Time: %dms ago]", name, e.getID(),
                e.getCommittedCharacterCount(), e.getText(), System.currentTimeMillis() - e.getWhen() );
    }
}
