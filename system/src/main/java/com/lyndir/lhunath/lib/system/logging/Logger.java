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
package com.lyndir.lhunath.lib.system.logging;

import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import com.lyndir.lhunath.lib.system.Locale;

/**
 * <i>Logger - An interface to the java {@link java.util.logging.Logger} class.</i><br>
 * <br>
 * This is an attempt to making the java {@link java.util.logging.Logger} class easier to use.<br>
 * For basic use, just call the {@link #log(Level, String, Throwable, Object[])} method, or the convenience methods for
 * each log level.<br>
 * <br>
 * 
 * @author lhunath
 */
public class Logger {

    private static Map<String, Logger> loggers;

    private java.util.logging.Logger   javaLogger;
    private Map<LogListener, Level>    listeners;

    static {
        try {
            /* Configure the Java Logging backend. */
            InputStream stream = Logger.class.getResourceAsStream( "/res/Logger.properties" );
            if (stream != null)
                LogManager.getLogManager().readConfiguration( stream );

            /* Set up base handlers. */
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter( new ConsoleFormatter( true ) );
            handler.setLevel( Level.FINER );
            getGlobal().silence().addHandler( handler );

            /* Register us as the default handler for uncaught exceptions. */
            Thread.setDefaultUncaughtExceptionHandler( new UncaughtExceptionHandler() {

                public void uncaughtException(Thread t, Throwable e) {

                    /* Report all uncaught errors..
                     * Except for that annoying one caused by a buggy BasicPopupMenuUI.MouseGrabber. */
                    if (!"java.awt.TrayIcon cannot be cast to java.awt.Component".equals( e.getMessage() ))
                        Logger.error( e );
                }
            } );
        } catch (Exception e) {
            Logger.error( e, "Failed to properly initialize logger settings." );
        }
    }

    /**
     * Log a message with the given information.
     * 
     * @param level
     *        The severity level of the message.
     * @param message
     *        The description message.
     * @param error
     *        An optional throwable cause of this log message. Set to null if not applicable.
     * @param args
     *        Arguments to format the description message with.
     */
    public static void log(Level level, String message, Throwable error, Object... args) {

        try {
            message = Locale.explain( message, args );
            LogRecord record = new LogRecord( level, message );
            record.setThrown( error );

            getLogger().fireEvent( record );
            if (message == null && error == null)
                return;

            getGlobal().javaLogger.log( record );
        } catch (Exception e) {
            e.printStackTrace();
            uncaught( e );
        }
    }

    /**
     * Handle an uncaught {@link Throwable} that cannot be safely handled through the logging backend.
     * 
     * @param e
     *        The {@link Throwable} that needs to be handled.
     */
    public static void uncaught(Throwable e) {

        if (Thread.getDefaultUncaughtExceptionHandler() != null)
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException( Thread.currentThread(), e );
        else
            e.printStackTrace();
    }

    /**
     * Send a message about the correct working of the program.
     * 
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void fine(String message, Object... args) {

        log( Level.FINE, message, null, args );
    }

    /**
     * Send a message about the correct working of the program, when being verbose.
     * 
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void finer(String message, Object... args) {

        log( Level.FINER, message, null, args );
    }

    /**
     * Send a message about the correct working of the program, when being very verbose.
     * 
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void finest(String message, Object... args) {

        log( Level.FINEST, message, null, args );
    }

    /**
     * Send a message about the current configuration of the program.
     * 
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void config(String message, Object... args) {

        log( Level.CONFIG, message, null, args );
    }

    /**
     * Send a message informing the user about the state of the program.
     * 
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void info(String message, Object... args) {

        log( Level.INFO, message, null, args );
    }

    /**
     * Send a warning message stating that abnormal behaviour was detected.
     * 
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void warn(String message, Object... args) {

        log( Level.WARNING, message, null, args );
    }

    /**
     * Send an error message, notifying the user that something went seriously wrong.
     * 
     * @param exception
     *        The exception that occurred causing this error.
     */
    public static void error(Throwable exception) {

        log( Level.SEVERE, exception.getMessage(), exception, "" );
    }

    /**
     * Send an error message, notifying the user that something went seriously wrong.
     * 
     * @param exception
     *        The exception that occurred causing this error.
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void error(Throwable exception, String message, Object... args) {

        if (args.length > 0 || message != null && message.length() > 0)
            error( new RuntimeException( Locale.explain( message, args ), exception ) );
        else
            error( exception );
    }

    /**
     * Send an error message, notifying the user that something went seriously wrong.
     * 
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void error(String message, Object... args) {

        error( new RuntimeException( Locale.explain( message, args ) ) );
    }

    /**
     * Send an error message, notifying the user that something went so badly wrong that the program needs to be
     * terminated as a result.
     * 
     * @param exception
     *        The exception that occurred causing this error.
     */
    public static void fatal(Throwable exception) {

        error( exception );
        System.exit( 1 );
    }

    /**
     * Send an error message, notifying the user that something went so badly wrong that the program needs to be
     * terminated as a result.
     * 
     * @param exception
     *        The exception that occurred causing this error.
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void fatal(Throwable exception, String message, Object... args) {

        error( exception, message, args );
        System.exit( 1 );
    }

    /**
     * Send an error message, notifying the user that something went so badly wrong that the program needs to be
     * terminated as a result.
     * 
     * @param message
     *        The locale message identifier or format string to send.
     * @param args
     *        The arguments for the message format string.
     */
    public static void fatal(String message, Object... args) {

        error( message, args );
        System.exit( 1 );
    }

    /**
     * Print out a stack trace at the INFO level.
     */
    public static void trace() {

        log( Level.INFO, "Tracing.", new Throwable() );
    }

    /**
     * Add a log Handler to receive logging messages. By default, Loggers also send their output to their parent logger.
     * Typically the root Logger is configured with a set of Handlers that essentially act as default handlers for all
     * loggers.
     * 
     * @param handler
     *        a logging Handler
     */
    public void addHandler(ConsoleHandler handler) {

        javaLogger.addHandler( handler );
    }

    /**
     * Make the logger output to listeners.
     * 
     * @param listener
     *        The object that will be listening for log messages.
     * @param minLevel
     *        The minimal {@link Level} of log messages accepted by this listener.
     * @return The logger that was modified.
     */
    public Logger addListener(LogListener listener, Level minLevel) {

        if (listeners == null || listeners.isEmpty())
            listeners = new HashMap<LogListener, Level>();

        listeners.put( listener, minLevel );
        return this;
    }

    private void fireEvent(LogRecord record) {

        /* Fire all our listeners. */
        if (listeners != null)
            for (LogListener listener : listeners.keySet())
                /* Check to see if we're supposed to be ignoring this record. */
                if (record.getLevel().intValue() >= listeners.get( listener ).intValue()
                    && !listeners.get( listener ).equals( Level.OFF ))
                    listener.logMessage( record );

        /* Fire all listeners of our parents. */
        if (!equals( getGlobal() ))
            if (javaLogger.getParent() != null)
                getLogger( javaLogger.getParent() ).fireEvent( record );
            else if (!equals( getGlobal() ))
                getGlobal().fireEvent( record );
    }

    /**
     * Make the logger output to the specified target file.
     * 
     * @param target
     *        The output channel.
     * @return The logger that was modified.
     */
    public Logger logTo(String target) {

        try {
            javaLogger.addHandler( new FileHandler( target ) );
        } catch (Exception e) {
            javaLogger.severe( Locale.explain( "logOpenFailed", this, target ) );
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Change the {@link Level} at which this logger filters out messages. Any message with a {@link Level} lower than
     * the given one will be ignored by this logger.
     * 
     * @param level
     *        The minimum allowed {@link Level}.
     * @return This logger.
     */
    public Logger setLevel(Level level) {

        javaLogger.setLevel( level );
        return this;
    }

    /**
     * Remove all output destinations of a logger.
     * 
     * @return This logger.
     */
    public Logger silence() {

        for (Handler handler : javaLogger.getHandlers())
            javaLogger.removeHandler( handler );

        return this;
    }

    /**
     * Remove the specified destination of a logger.
     * 
     * @param silenceHandler
     *        The handler that must be removed.
     * @return This logger.
     */
    public Logger silence(Handler silenceHandler) {

        for (Handler handler : javaLogger.getHandlers())
            if (handler.equals( silenceHandler ))
                javaLogger.removeHandler( handler );

        return this;
    }

    /**
     * Retrieve the logger for the first non-system class in the current stack.
     * 
     * @return Guess.
     */
    public static Logger getLogger() {

        for (StackTraceElement element : new Throwable().getStackTrace()) {

            /* Skip all stack trace elements from com.lyndir.lhunath.lib.system.logging. */
            if (element.getClassName().startsWith( Logger.class.getPackage().getName() ))
                continue;

            return getLogger( java.util.logging.Logger.getLogger( element.getClassName() ) );
        }

        return getGlobal();
    }

    /**
     * Retrieve the global logger.
     * 
     * @return Guess.
     */
    public static Logger getGlobal() {

        return getLogger( java.util.logging.Logger.getLogger( java.util.logging.Logger.global.getName() ) );
    }

    /**
     * Retrieve the logger that uses the given Java Logger.
     * 
     * @param jLogger
     *        The Java Logger that is used by the requested logger.
     * @return Guess.
     */
    private static Logger getLogger(java.util.logging.Logger jLogger) {

        if (loggers == null)
            loggers = new HashMap<String, Logger>();

        Logger logger = null;
        if (jLogger != null) {
            /* Initialize this logger's level from its parent. */
            if (jLogger.getLevel() == null)
                if (jLogger.getName().equals( java.util.logging.Logger.global.getName() ))
                    jLogger.setLevel( Level.INFO );
                else
                    for (java.util.logging.Logger parent = jLogger.getParent(); parent != null; parent = jLogger.getParent())
                        if (parent.getLevel() != null) {
                            jLogger.setLevel( parent.getLevel() );
                            break;
                        }

            /* Get/put this logger from/in our logger cache. */
            logger = loggers.get( jLogger.getName() );
            if (logger == null)
                loggers.put( jLogger.getName(), logger = new Logger( jLogger ) );
        }

        return logger;
    }

    /**
     * Retrieve the logger for a certain class.
     * 
     * @param source
     *        The section that needs to be logged.
     * @return The logger.
     */
    public static Logger getLogger(Class<?> source) {

        if (loggers == null)
            loggers = new HashMap<String, Logger>();

        return getLogger( java.util.logging.Logger.getLogger( source == null ? null : source.getCanonicalName() ) );
    }

    /**
     * Create a new Logger instance.
     * 
     * @param name
     *        A name for the logger. This should be a dot-separated name and should normally be based on the package
     *        name or class name of the subsystem, such as java.net or javax.swing. It may be null for anonymous
     *        Loggers.
     * @param resourceBundleName
     *        name of ResourceBundle to be used for localizing messages for this logger. May be null if none of the
     *        messages require localization.
     */
    private Logger(String name, String resourceBundleName) {

        javaLogger = java.util.logging.Logger.getLogger( name, resourceBundleName );
    }

    /**
     * Create a new Logger instance.
     * 
     * @param logger
     *        The java logger this logger will be using.
     */
    private Logger(java.util.logging.Logger logger) {

        this( logger.getName(), logger.getResourceBundleName() );
    }

    /**
     * Retrieve the javaLogger of this Logger.
     * 
     * @return Guess.
     */
    public java.util.logging.Logger getJavaLogger() {

        return javaLogger;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {

        return String.format( "[%s] %s >%s", javaLogger.getLevel(), javaLogger.getName(), listeners );
    }
}
