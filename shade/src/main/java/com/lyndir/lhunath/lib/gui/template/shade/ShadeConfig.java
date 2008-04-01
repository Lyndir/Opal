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
package com.lyndir.lhunath.lib.gui.template.shade;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import com.lyndir.lhunath.lib.gui.MyLookAndFeel;
import com.lyndir.lhunath.lib.math.Version;
import com.lyndir.lhunath.lib.system.BaseConfig;
import com.lyndir.lhunath.lib.system.Locale;
import com.lyndir.lhunath.lib.system.logging.ConsoleFormatter;
import com.lyndir.lhunath.lib.system.logging.LogFormatter;
import com.lyndir.lhunath.lib.system.logging.Logger;

/**
 * TODO: {@link ShadeConfig}<br>
 * 
 * @author lhunath
 * @param <T>
 */
public class ShadeConfig<T extends Serializable> extends BaseConfig<T> {

    /**
     * Version of the class. Augment this whenever the class type of a config entry field changes, or the context of a
     * field becomes incompatible.
     */
    public static final long                  serialVersionUID = 200L;

    /**
     * The release version of the application.
     */
    public static final Version               VERSION          = new Version( "0.0" );

    /**
     * The application's storage location.
     */
    public static File                        res;

    /**
     * The handler used to display logging messages on the console.
     */
    public static ConsoleHandler              console;

    /**
     * The formatter used to generate log messages on the console.
     */
    public static LogFormatter                formatter;

    /**
     * The application user interface.
     */
    public static AbstractUi                  ui;

    /**
     * The active look and feel.
     */
    public static BaseConfig<MyLookAndFeel>   theme            = create( MyLookAndFeel.class );

    /**
     * The local filename of the logos.
     */
    public static BaseConfig<ArrayList<File>> logos            = create( new ArrayList<File>() );

    /**
     * Show detailed errors and print stack traces on the console.
     */
    public static BaseConfig<Boolean>         verbose          = create( true );

    /**
     * Whether or not to take up the whole screen, or use a window.
     */
    public static BaseConfig<Boolean>         fullScreen       = create( false );

    /**
     * Whether or not to use the System Tray.
     */
    public static BaseConfig<Boolean>         sysTray          = create( false );

    /**
     * Always keep the jUniUploader window on top of others.
     */
    public static BaseConfig<Boolean>         alwaysOnTop      = create( false );

    /**
     * Start jUniUploader minimized (either in systray or on the taskbar).
     */
    public static BaseConfig<Boolean>         startMini        = create( false );

    static {
        /* Determine the resources dir. */
        res = new File( System.getProperty( "user.home", "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        if (!res.isDirectory())
            throw new RuntimeException( Locale.explain( "err.homeNotFound" ) ); //$NON-NLS-1$

        /* Setup the config internals. */
        initClass( ShadeConfig.class );

        /* Find the logger's console handler. */
        ConsoleHandler handler = null;
        for (Handler h : Logger.getGlobal().getJavaLogger().getHandlers())
            if (h instanceof ConsoleHandler) {
                handler = (ConsoleHandler) h;
                break;
            }
        if (handler == null)
            handler = new ConsoleHandler();

        /* Configure it for use with the config's formatter. */
        ShadeConfig.console = handler;
        ShadeConfig.formatter = new ConsoleFormatter( verbose.get() );

        /* Set the handlers up for use. */
        handler.setLevel( Level.ALL );
        handler.setFormatter( ShadeConfig.formatter );
        Logger.getGlobal().silence().addHandler( handler );
    }

    /**
     * Create a new Config instance.
     */
    protected ShadeConfig(T defaultValue) {

        super( defaultValue );
    }
}
