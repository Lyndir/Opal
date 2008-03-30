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

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.lyndir.lhunath.lib.system.logging.Logger;

/**
 * @author mbillemo
 */
public class Desktop extends Wrapper {

    static {
        Logger.info( "wrapping Desktop" );
        initWrapper( Desktop.class, "java.awt.Desktop" );
    }

    /**
     * http://java.sun.com/javase/6/docs/api/java/awt/Desktop.html#getDesktop()
     */
    public static Desktop getDesktop() throws UnsupportedOperationException {

        return new Desktop( invoke( Desktop.class, null, "getDesktop", new Class[0] ) );
    }

    public static boolean isDesktopSupported() {

        try {
            return (Boolean) invoke( Desktop.class, null, "isDesktopSupported", new Class[0] );
        }

        catch (Exception e) {
            return false;
        }
    }

    public boolean isSupported(Action action) {

        try {
            Object desktopAction = mapEnumValue( action, getClass( "java.awt.Desktop.Action" ) );
            return (Boolean) invoke( "isSupported", new Class[] { getClass( "java.awt.Desktop.Action" ) },
                    desktopAction );
        } catch (Exception e) {}

        return false;
    }

    private Desktop(Object wrappedInstance) {

        super( wrappedInstance );
    }

    @SuppressWarnings("unused")
    public void browse(URI uri) throws IOException {

        invoke( "browse", new Class[] { URI.class }, uri );
    }

    @SuppressWarnings("unused")
    public void edit(File file) throws IOException {

        invoke( "edit", new Class[] { File.class }, file );
    }

    @SuppressWarnings("unused")
    public void mail() throws IOException {

        invoke( "mail", new Class[0] );
    }

    @SuppressWarnings("unused")
    public void mail(URI mailtoURI) throws IOException {

        invoke( "mail", new Class[] { URI.class }, mailtoURI );
    }

    @SuppressWarnings("unused")
    public void open(File file) throws IOException {

        invoke( "open", new Class[] { File.class }, file );
    }

    @SuppressWarnings("unused")
    public void print(File file) throws IOException {

        invoke( "print", new Class[] { File.class }, file );
    }

    public enum Action {
        BROWSE, EDIT, MAIL, OPEN, PRINT
    }
}
