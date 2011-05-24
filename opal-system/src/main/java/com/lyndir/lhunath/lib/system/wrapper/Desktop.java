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


/**
 * @author mbillemo
 */
public class Desktop extends Wrapper {

    static {
        initWrapper( Desktop.class, "java.awt.Desktop" );
    }

    /**
     * @return See {@link "http://java.sun.com/javase/6/docs/api/java/awt/Desktop.html#getDesktop()"}
     *
     * @throws UnsupportedOperationException If the native class wrapped by this class is not available.
     */
    public static Desktop getDesktop()
            throws UnsupportedOperationException {

        return new Desktop( invoke( Desktop.class, null, "getDesktop" ) );
    }

    /**
     * @return <code>true</code> If the Java6 Desktop class is available and supported by the current environment.
     */
    public static boolean isDesktopSupported() {

        try {
            return (Boolean) invoke( Desktop.class, null, "isDesktopSupported" );
        }

        catch (Exception ignored) {
            return false;
        }
    }

    /**
     * @param action The wrapped action to check support for.
     *
     * @return <code>true</code> If the Java6 Desktop class is available and the given action is supported by the current environment.
     */
    public boolean isSupported(final Action action) {

        try {
            Object desktopAction = mapEnumValue( action, getClass( "java.awt.Desktop.Action" ) );
            return (Boolean) invoke( "isSupported", new Class[]{ getClass( "java.awt.Desktop.Action" ) }, desktopAction );
        }

        catch (Exception ignored) {
        }

        return false;
    }

    private Desktop(final Object wrappedInstance) {

        super( wrappedInstance );
    }

    /**
     * Browse to the given {@link URI} natively.
     *
     * @param uri The {@link URI} to browse to.
     *
     * @throws NullPointerException          - if uri is null
     * @throws UnsupportedOperationException - if the current platform does not support the Desktop.Action.BROWSE action
     * @throws IOException                   - if the user default browser is not found, or it fails to be launched, or the default handler
     *                                       application failed to be launched
     * @throws SecurityException             - if a security manager exists and it denies the AWTPermission("showWindowWithoutWarningBanner")
     *                                       permission, or the calling thread is not allowed to create a subprocess; and not invoked from
     *                                       within an applet or Java Web Started application
     * @throws IllegalArgumentException      - if the necessary permissions are not available and the URI can not be converted to a URL
     */
    @SuppressWarnings({ "unused", "RedundantThrows" })
    public void browse(final URI uri)
            throws NullPointerException, UnsupportedOperationException, IOException, SecurityException, IllegalArgumentException {

        invoke( "browse", new Class[]{ URI.class }, uri );
    }

    /**
     * Edit the given file natively.
     *
     * @param file The {@link File} to edit.
     *
     * @throws NullPointerException          - if the specified file is null
     * @throws IllegalArgumentException      - if the specified file doesn't exist
     * @throws UnsupportedOperationException - if the current platform does not support the Desktop.Action#EDIT action
     * @throws IOException                   - if the specified file has no associated editor, or the associated application fails to be
     *                                       launched
     * @throws SecurityException             - if a security manager exists and its SecurityManager#checkRead(java.lang.String) method
     *                                       denies read access to the file, or SecurityManager#checkWrite(java.lang.String) method denies
     *                                       write access to the file, or it denies the AWTPermission("showWindowWithoutWarningBanner")
     *                                       permission, or the calling thread is not allowed to create a subprocess
     */
    @SuppressWarnings({ "unused", "RedundantThrows" })
    public void edit(final File file)
            throws NullPointerException, IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {

        invoke( "edit", new Class[]{ File.class }, file );
    }

    /**
     * Send an email natively.
     *
     * @throws UnsupportedOperationException - if the current platform does not support the Desktop.Action#MAIL action
     * @throws IOException                   - if the user default mail client is not found, or it fails to be launched
     * @throws SecurityException             - if a security manager exists and it denies the AWTPermission("showWindowWithoutWarningBanner")
     *                                       permission, or the calling thread is not allowed to create a subprocess
     */
    @SuppressWarnings({ "unused", "RedundantThrows" })
    public void mail()
            throws UnsupportedOperationException, IOException, SecurityException {

        invoke( "mail" );
    }

    /**
     * Send an email to the given {@link URI} natively.
     *
     * @param mailtoURI The {@link URI} address to direct the mail to.
     *
     * @throws NullPointerException          - if the specified URI is null
     * @throws IllegalArgumentException      - if the URI scheme is not "mailto"
     * @throws UnsupportedOperationException - if the current platform does not support the Desktop.Action.MAIL action
     * @throws IOException                   - if the user default mail client is not found or fails to be launched
     * @throws SecurityException             - if a security manager exists and it denies the AWTPermission("showWindowWithoutWarningBanner")
     *                                       permission, or the calling thread is not allowed to create a subprocess
     */
    @SuppressWarnings({ "unused", "RedundantThrows" })
    public void mail(final URI mailtoURI)
            throws NullPointerException, IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {

        invoke( "mail", new Class[]{ URI.class }, mailtoURI );
    }

    /**
     * Open the given file as configured natively.
     *
     * @param file The {@link File} to open.
     *
     * @throws NullPointerException          - if file is null
     * @throws IllegalArgumentException      - if the specified file doesn't exist
     * @throws UnsupportedOperationException - if the current platform does not support the Desktop.Action#OPEN action
     * @throws IOException                   - if the specified file has no associated application or the associated application fails to
     *                                       be
     *                                       launched
     * @throws SecurityException             - if a security manager exists and its SecurityManager#checkRead(java.lang.String) method
     *                                       denies read access to the file, or it denies the AWTPermission("showWindowWithoutWarningBanner")
     *                                       permission, or the calling thread is not allowed to create a subprocess
     */
    @SuppressWarnings({ "unused", "RedundantThrows" })
    public void open(final File file)
            throws NullPointerException, IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {

        invoke( "open", new Class[]{ File.class }, file );
    }

    /**
     * Print the given {@link File} as configured natively.
     *
     * @param file The {@link File} to print.
     *
     * @throws NullPointerException          - if the specified file is null
     * @throws IllegalArgumentException      - if the specified file doesn't exist
     * @throws UnsupportedOperationException - if the current platform does not support the Desktop.Action#PRINT action
     * @throws IOException                   - if the specified file has no associated application that can be used to print it
     * @throws SecurityException             - if a security manager exists and its SecurityManager#checkRead(java.lang.String) method
     *                                       denies read access to the file, or its SecurityManager#checkPrintJobAccess() method denies the
     *                                       permission to print the file, or the calling thread is not allowed to create a subprocess
     */
    @SuppressWarnings({ "unused", "RedundantThrows" })
    public void print(final File file)
            throws NullPointerException, IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {

        invoke( "print", new Class[]{ File.class }, file );
    }

    /**
     * <h2>{@link Action}<br> <sub>Wrapper class for the Java6 Desktop.Action enum.</sub></h2>
     *
     * <p> <i>Apr 9, 2008</i> </p>
     *
     * @author mbillemo
     */
    public enum Action {

        /**
         * Opening default web browser.
         */
        BROWSE,

        /**
         * Opening default editor for files.
         */
        EDIT,

        /**
         * Opening default email client.
         */
        MAIL,

        /**
         * Opening a file with the default application for it.
         */
        OPEN,

        /**
         * Printing a file with the default printer.
         */
        PRINT
    }
}
