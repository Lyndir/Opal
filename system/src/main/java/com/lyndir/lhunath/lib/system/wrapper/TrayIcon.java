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

package com.lyndir.lhunath.lib.system.wrapper;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/**
 * <i>TrayIcon - </i><br>
 * <br>
 * <p>
 * TODO: Document this class further.
 * </p>
 * <br>
 * The TrayIcon object ...<br>
 * <br>
 *
 * @author mbillemo
 */
public class TrayIcon extends Wrapper {

    static {
        initWrapper( TrayIcon.class, "java.awt.TrayIcon" );
    }


    /**
     * Create a new {@link TrayIcon} instance.
     *
     * @param image The image to use to depict this tray icon in the system tray.
     */
    public TrayIcon(final Image image) {

        this( construct( TrayIcon.class, new Class[] {Image.class}, image ) );
    }

    /**
     * Create a new {@link TrayIcon} instance.
     *
     * @param image   The image to use to depict this tray icon in the system tray.
     * @param tooltip The tooltip to show when hovering over the tray icon.
     */
    public TrayIcon(final Image image, final String tooltip) {

        this( construct( TrayIcon.class, new Class[] {Image.class, String.class}, image, tooltip ) );
    }

    /**
     * Create a new {@link TrayIcon} instance.
     *
     * @param image   The image to use to depict this tray icon in the system tray.
     * @param tooltip The tooltip to show when hovering over the tray icon.
     * @param popup   The popup menu to show when right-clicking the tray icon.
     */
    public TrayIcon(final Image image, final String tooltip, final PopupMenu popup) {

        this( construct( TrayIcon.class, new Class[] {Image.class, String.class, PopupMenu.class}, image, tooltip,
                         popup ) );
    }

    /**
     * @param wrappedInstance The real {@link java.awt.TrayIcon} object.
     */
    protected TrayIcon(final Object wrappedInstance) {

        super( wrappedInstance );
    }

    /**
     * @param listener The {@link ActionListener} to add to this {@link TrayIcon}.
     */
    public synchronized void addActionListener(final ActionListener listener) {

        invoke( "addActionListener", new Class[] {ActionListener.class}, listener );
    }

    /**
     * @param listener The {@link MouseListener} to add to this {@link TrayIcon}.
     */
    public synchronized void addMouseListener(final MouseListener listener) {

        invoke( "addMouseListener", new Class[] {MouseListener.class}, listener );
    }

    /**
     * @param listener The {@link MouseMotionListener} to add to this {@link TrayIcon}.
     */
    public synchronized void addMouseMotionListener(final MouseMotionListener listener) {

        invoke( "addMouseMotionListener", new Class[] {MouseMotionListener.class}, listener );
    }

    /**
     * Show a notification message by this tray icon.
     *
     * @param caption     The title of the message.
     * @param text        The body of the message.
     * @param messageType The type of message.
     */
    public void displayMessage(final String caption, final String text, final MessageType messageType) {

        Class<?> wrappedEnumClass = getClass( "java.awt.TrayIcon$MessageType" );
        invoke( "displayMessage", new Class[] {String.class, String.class, wrappedEnumClass}, caption, text,
                mapEnumValue( messageType, wrappedEnumClass ) );
    }

    /**
     * @return The action command string set for this {@link TrayIcon}'s action events.
     */
    public String getActionCommand() {

        return (String) invoke( "getActionCommand" );
    }

    /**
     * @return The registered {@link ActionListener}s.
     */
    public synchronized ActionListener[] getActionListeners() {

        return (ActionListener[]) invoke( "getActionListeners" );
    }

    /**
     * @return The image currently set for this {@link TrayIcon}.
     */
    public Image getImage() {

        return (Image) invoke( "getImage" );
    }

    /**
     * @return The registered {@link MouseListener}s.
     */
    public synchronized MouseListener[] getMouseListeners() {

        return (MouseListener[]) invoke( "getMouseListeners" );
    }

    /**
     * @return The registered {@link MouseMotionListener}s.
     */
    public synchronized MouseMotionListener[] getMouseMotionListeners() {

        return (MouseMotionListener[]) invoke( "getMouseMotionListeners" );
    }

    /**
     * @return The popup menu that shows on right-clicking this {@link TrayIcon}.
     */
    public PopupMenu getPopupMenu() {

        return (PopupMenu) invoke( "getPopupMenu" );
    }

    /**
     * @return The current size of this {@link TrayIcon}.
     */
    public Dimension getSize() {

        return (Dimension) invoke( "getSize" );
    }

    /**
     * @return The current tooltip of this {@link TrayIcon}.
     */
    public String getToolTip() {

        return (String) invoke( "getToolTip" );
    }

    /**
     * @return <code>true</code> if the {@link TrayIcon}'s image is automatically scaled.
     */
    public boolean isImageAutoSize() {

        return (Boolean) invoke( "isImageAutoSize" );
    }

    /**
     * @param listener The listener to unregister from this {@link TrayIcon}.
     */
    public synchronized void removeActionListener(final ActionListener listener) {

        invoke( "removeActionListener", new Class[] {ActionListener.class}, listener );
    }

    /**
     * @param listener The {@link MouseListener} to unregister from this {@link TrayIcon}.
     */
    public synchronized void removeMouseListener(final MouseListener listener) {

        invoke( "removeMouseListener", new Class[] {MouseListener.class}, listener );
    }

    /**
     * @param listener The {@link MouseMotionListener} to unregister from this {@link TrayIcon}.
     */
    public synchronized void removeMouseMotionListener(final MouseMotionListener listener) {

        invoke( "removeMouseMotionListener", new Class[] {MouseMotionListener.class}, listener );
    }

    /**
     * @param command The action command string to set on action events.
     */
    public void setActionCommand(final String command) {

        invoke( "setActionCommand", new Class[] {String.class}, command );
    }

    /**
     * @param image The image to use for depicting this {@link TrayIcon}.
     */
    public void setImage(final Image image) {

        invoke( "setImage", new Class[] {Image.class}, image );
    }

    /**
     * @param autosize <code>true</code>: auto scale the image used to depict the {@link TrayIcon}.
     */
    public void setImageAutoSize(final boolean autosize) {

        invoke( "setImageAutoSize", new Class[] {boolean.class}, autosize );
    }

    /**
     * @param popup The menu to show when right-clicking the {@link TrayIcon}.
     */
    public void setPopupMenu(final PopupMenu popup) {

        invoke( "setPopupMenu", new Class[] {PopupMenu.class}, popup );
    }

    /**
     * @param tooltip The tooltip message to show when hovering the {@link TrayIcon}.
     */
    public void setToolTip(final String tooltip) {

        invoke( "setToolTip", new Class[] {String.class}, tooltip );
    }


    /**
     * <h2>{@link MessageType}<br>
     * <sub>Types of message notifications to show by the tray icon.</sub></h2>
     *
     * <p>
     * <i>Apr 9, 2008</i>
     * </p>
     *
     * @author mbillemo
     */
    public enum MessageType {

        /**
         * An error message.
         */
        ERROR,

        /**
         * An informational message.
         */
        INFO,

        /**
         * No message type or icon.
         */
        NONE,

        /**
         * A warning message.
         */
        WARNING
    }
}
