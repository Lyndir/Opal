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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.PopupMenu;
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

    public TrayIcon(Image image) {

        this( construct( TrayIcon.class, new Class[] { Image.class }, image ) );
    }

    public TrayIcon(Image image, String tooltip) {

        this( construct( TrayIcon.class, new Class[] { Image.class, String.class }, image, tooltip ) );
    }

    public TrayIcon(Image image, String tooltip, PopupMenu popup) {

        this( construct( TrayIcon.class, new Class[] { Image.class, String.class, PopupMenu.class }, image, tooltip,
                popup ) );
    }

    protected TrayIcon(Object wrappedInstance) {

        super( wrappedInstance );
    }

    public synchronized void addActionListener(ActionListener listener) {

        invoke( "addActionListener", new Class[] { ActionListener.class }, listener );
    }

    public synchronized void addMouseListener(MouseListener listener) {

        invoke( "addMouseListener", new Class[] { MouseListener.class }, listener );
    }

    public synchronized void addMouseMotionListener(MouseMotionListener listener) {

        invoke( "addMouseMotionListener", new Class[] { MouseMotionListener.class }, listener );
    }

    public void displayMessage(String caption, String text, MessageType messageType) {

        Class<?> wrappedEnumClass = getClass( "java.awt.TrayIcon.MessageType" );
        invoke( "displayMessage", new Class[] { String.class, String.class, wrappedEnumClass }, caption, text,
                mapEnumValue( messageType, wrappedEnumClass ) );
    }

    public String getActionCommand() {

        return (String) invoke( "getActionCommand", new Class[0] );
    }

    public synchronized ActionListener[] getActionListeners() {

        return (ActionListener[]) invoke( "getActionListeners", new Class[0] );
    }

    public void getImage() {

        invoke( "getImage", new Class[0] );
    }

    public synchronized MouseListener[] getMouseListeners() {

        return (MouseListener[]) invoke( "getMouseListeners", new Class[0] );
    }

    public synchronized MouseMotionListener[] getMouseMotionListeners() {

        return (MouseMotionListener[]) invoke( "getMouseMotionListeners", new Class[0] );
    }

    public PopupMenu getPopupMenu() {

        return (PopupMenu) invoke( "getPopupMenu", new Class[0] );
    }

    public Dimension getSize() {

        return (Dimension) invoke( "getSize", new Class[0] );
    }

    public String getToolTip() {

        return (String) invoke( "getToolTip", new Class[0] );
    }

    public boolean isImageAutoSize() {

        return (Boolean) invoke( "isImageAutoSize", new Class[0] );
    }

    public synchronized void removeActionListener(ActionListener listener) {

        invoke( "removeActionListener", new Class[] { ActionListener.class }, listener );
    }

    public synchronized void removeMouseListener(MouseListener listener) {

        invoke( "removeMouseListener", new Class[] { MouseListener.class }, listener );
    }

    public synchronized void removeMouseMotionListener(MouseMotionListener listener) {

        invoke( "removeMouseMotionListener", new Class[] { MouseMotionListener.class }, listener );
    }

    public void setActionCommand(String command) {

        invoke( "setActionCommand", new Class[] { String.class }, command );
    }

    public void setImage(Image image) {

        invoke( "setImage", new Class[] { Image.class }, image );
    }

    public void setImageAutoSize(boolean autosize) {

        invoke( "setImageAutoSize", new Class[] { Boolean.class }, autosize );
    }

    public void setPopupMenu(PopupMenu popup) {

        invoke( "setPopupMenu", new Class[] { PopupMenu.class }, popup );
    }

    public void setToolTip(String tooltip) {

        invoke( "setToolTip", new Class[] { String.class }, tooltip );
    }

    public enum MessageType {
        ERROR, INFO, NONE, WARNING
    }
}
