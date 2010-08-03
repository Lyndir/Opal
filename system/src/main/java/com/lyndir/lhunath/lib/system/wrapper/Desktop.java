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
            return (Boolean) invoke( "isSupported", new Class[] { getClass( "java.awt.Desktop.Action" ) }, desktopAction );
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
     * @throws IOException
     */
    @SuppressWarnings( { "unused", "RedundantThrows" })
    public void browse(final URI uri)
            throws IOException {

        invoke( "browse", new Class[] { URI.class }, uri );
    }

    /**
     * Edit the given file natively.
     *
     * @param file The {@link File} to edit.
     *
     * @throws IOException
     */
    @SuppressWarnings( { "unused", "RedundantThrows" })
    public void edit(final File file)
            throws IOException {

        invoke( "edit", new Class[] { File.class }, file );
    }

    /**
     * Send an email natively.
     *
     * @throws IOException
     */
    @SuppressWarnings( { "unused", "RedundantThrows" })
    public void mail()
            throws IOException {

        invoke( "mail" );
    }

    /**
     * Send an email to the given {@link URI} natively.
     *
     * @param mailtoURI The {@link URI} address to direct the mail to.
     *
     * @throws IOException
     */
    @SuppressWarnings( { "unused", "RedundantThrows" })
    public void mail(final URI mailtoURI)
            throws IOException {

        invoke( "mail", new Class[] { URI.class }, mailtoURI );
    }

    /**
     * Open the given file as configured natively.
     *
     * @param file The {@link File} to open.
     *
     * @throws IOException
     */
    @SuppressWarnings( { "unused", "RedundantThrows" })
    public void open(final File file)
            throws IOException {

        invoke( "open", new Class[] { File.class }, file );
    }

    /**
     * Print the given {@link File} as configured natively.
     *
     * @param file The {@link File} to print.
     *
     * @throws IOException
     */
    @SuppressWarnings( { "unused", "RedundantThrows" })
    public void print(final File file)
            throws IOException {

        invoke( "print", new Class[] { File.class }, file );
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
