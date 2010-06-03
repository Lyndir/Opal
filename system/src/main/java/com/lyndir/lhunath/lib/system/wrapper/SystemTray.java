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
import java.beans.PropertyChangeListener;


/**
 * @author mbillemo
 */
public class SystemTray extends Wrapper {

    static {
        initWrapper( SystemTray.class, "java.awt.SystemTray" );
    }


    /**
     * @return The wrapped system's tray.
     */
    public static SystemTray getSystemTray() {

        return new SystemTray( invoke( SystemTray.class, null, "getSystemTray" ) );
    }

    /**
     * @return <code>true</code>: if the Java6 SystemTray class is available and the system tray is supported in the
     *         current environment.
     */
    public static boolean isSupported() {

        try {
            return (Boolean) invoke( SystemTray.class, null, "isSupported" );
        }

        catch (Exception ignored) {
            return false;
        }
    }

    private SystemTray(final Object wrappedInstance) {

        super( wrappedInstance );
    }

    /**
     * Add a tray icon to the system tray.
     *
     * @param trayIcon The wrapped TrayIcon.
     *
     * @throws AWTException
     */
    @SuppressWarnings({"unused", "RedundantThrows"})
    public void add(final TrayIcon trayIcon)
            throws AWTException {

        invoke( "add", new Class[] {getWrappedClass( TrayIcon.class )}, trayIcon.getWrappedInstance() );
    }

    /**
     * Add a {@link PropertyChangeListener} for the given property.
     *
     * @param propertyName The property to listen for.
     * @param listener     The listener to invoke.
     */
    public synchronized void addPropertyChangeListener(String propertyName, final PropertyChangeListener listener) {

        invoke( "addPropertyChangeListener", new Class[] {String.class, PropertyChangeListener.class}, propertyName,
                listener );
    }

    /**
     * Retrieve all active listeners for the given property.
     *
     * @param propertyName The property that is listened for.
     *
     * @return The {@link PropertyChangeListener}s registered for the given property.
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners(final String propertyName) {

        return (PropertyChangeListener[]) invoke( "getPropertyChangeListeners", new Class[] {String.class},
                                                  propertyName );
    }

    /**
     * @return The registered tray icons in the current system tray.
     */
    public TrayIcon[] getTrayIcons() {

        Object[] trayIcons = (Object[]) invoke( "getTrayIcons" );
        TrayIcon[] wrappedTrayIcons = new TrayIcon[trayIcons.length];

        for (int i = 0; i < trayIcons.length; ++i)
            wrappedTrayIcons[i] = new TrayIcon( trayIcons[i] );

        return wrappedTrayIcons;
    }

    /**
     * @return The size system trays will have.
     */
    public Dimension getTrayIconSize() {

        return (Dimension) invoke( "getTrayIconSize" );
    }

    /**
     * Unregister the given tray icon removing it from the system tray.
     *
     * @param trayIcon The tray icon to remove.
     */
    public void remove(final TrayIcon trayIcon) {

        invoke( "remove", new Class[] {getWrappedClass( TrayIcon.class )}, trayIcon.getWrappedInstance() );
    }

    /**
     * Stop listening for the given property with the given listener.
     *
     * @param propertyName The property to stop listening for with the given listener.
     * @param listener     The listener that should stop listening to the given property.
     */
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {

        invoke( "removePropertyChangeListener", new Class[] {String.class, PropertyChangeListener.class}, propertyName,
                listener );
    }
}
