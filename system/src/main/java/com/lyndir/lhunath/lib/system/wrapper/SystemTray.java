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

import java.awt.AWTException;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;

/**
 * @author mbillemo
 */
public class SystemTray extends Wrapper {

    static {
        initWrapper( SystemTray.class, "java.awt.SystemTray" );
    }

    public static SystemTray getSystemTray() {

        return new SystemTray( invoke( SystemTray.class, null, "getSystemTray", new Class[0] ) );
    }

    public static boolean isSupported() {

        try {
            return (Boolean) invoke( SystemTray.class, null, "isSupported", new Class[0] );
        }

        catch (Exception e) {
            return false;
        }
    }

    private SystemTray(Object wrappedInstance) {

        super( wrappedInstance );
    }

    @SuppressWarnings("unused")
    public void add(TrayIcon trayIcon) throws AWTException {

        invoke( "add", new Class[] { getWrappedClass( TrayIcon.class ) }, trayIcon.getWrappedInstance() );
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {

        invoke( "addPropertyChangeListener", new Class[] { String.class, PropertyChangeListener.class }, propertyName,
                listener );
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {

        return (PropertyChangeListener[]) invoke( "getPropertyChangeListeners", new Class[] { String.class },
                propertyName );
    }

    public TrayIcon[] getTrayIcons() {

        Object[] trayIcons = (Object[]) invoke( "getTrayIcons", new Class[0] );
        TrayIcon[] wrappedTrayIcons = new TrayIcon[trayIcons.length];

        for (int i = 0; i < trayIcons.length; ++i)
            wrappedTrayIcons[i] = new TrayIcon( trayIcons[i] );

        return wrappedTrayIcons;
    }

    public Dimension getTrayIconSize() {

        return (Dimension) invoke( "getTrayIconSize", new Class[0] );
    }

    public void remove(TrayIcon trayIcon) {

        invoke( "remove", new Class[] { getWrappedClass( TrayIcon.class ) }, trayIcon.getWrappedInstance() );
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {

        invoke( "removePropertyChangeListener", new Class[] { String.class, PropertyChangeListener.class },
                propertyName, listener );
    }
}
