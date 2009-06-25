/*
 *   Copyright 2009, Maarten Billemont
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;


/**
 * <h2>{@link Logger}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>Mar 28, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class Logger {

    private static final Logger          loggerLogger = Logger.get( Logger.class );

    private static Map<Class<?>, Logger> loggers;

    private org.slf4j.Logger             logger;

    private Throwable                    eventCause;
    private String                       eventFormat;
    private Object[]                     eventArguments;


    // Create a logger --

    public static Logger get(Class<?> type) {

        if (loggers == null)
            loggers = new HashMap<Class<?>, Logger>();

        Logger logger = loggers.get( type );
        if (logger == null) {
            logger = new Logger( type );
            loggers.put( type, logger );
        }

        return logger;
    }

    // Event logging --

    /**
     * Log a progress trace event.
     * 
     * <p>
     * This level is for all events that describe the flow of execution.
     * </p>
     * 
     * @param cause
     *            A throwable that details the stack at the time of this event.
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger trc(Throwable cause, String descriptionFormat, Object... descriptionArguments) {

        if (logger.isTraceEnabled())
            if (cause == null)
                logger.trace( String.format( descriptionFormat, descriptionArguments ) );
            else
                logger.trace( String.format( descriptionFormat, descriptionArguments ), cause );

        eventCause = cause;
        eventFormat = descriptionFormat;
        eventArguments = descriptionArguments;

        return this;
    }

    /**
     * Log a progress trace event.
     * 
     * <p>
     * This level is for all events that describe the flow of execution.
     * </p>
     * 
     * @see #trc(Throwable, String, Object...)
     * 
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public void trc(String descriptionFormat, Object... descriptionArguments) {

        trc( null, descriptionFormat, descriptionArguments );
    }

    /**
     * Log a debugging event.
     * 
     * <p>
     * This level is for all events that visualize the application's state.
     * </p>
     * 
     * @param cause
     *            A throwable that details the stack at the time of this event.
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger dbg(Throwable cause, String descriptionFormat, Object... descriptionArguments) {

        if (logger.isDebugEnabled())
            if (cause == null)
                logger.debug( String.format( descriptionFormat, descriptionArguments ) );
            else
                logger.debug( String.format( descriptionFormat, descriptionArguments ), cause );

        eventCause = cause;
        eventFormat = descriptionFormat;
        eventArguments = descriptionArguments;

        return this;
    }

    /**
     * Log a debugging event.
     * 
     * <p>
     * This level is for all events that visualize the application's state.
     * </p>
     * 
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger dbg(String descriptionFormat, Object... descriptionArguments) {

        return dbg( null, descriptionFormat, descriptionArguments );
    }

    /**
     * Log an informative statement.
     * 
     * <p>
     * This level is for all events that detail an important evolution in the application's state.
     * </p>
     * 
     * @param cause
     *            A throwable that details the stack at the time of this event.
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger inf(Throwable cause, String descriptionFormat, Object... descriptionArguments) {

        if (logger.isInfoEnabled())
            logger.info( String.format( descriptionFormat, descriptionArguments ), cause );

        eventCause = cause;
        eventFormat = descriptionFormat;
        eventArguments = descriptionArguments;

        return this;
    }

    /**
     * Log an informative statement.
     * 
     * <p>
     * This level is for all events that detail an important evolution in the application's state.
     * </p>
     * 
     * @see #inf(Throwable, String, Object...)
     * 
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger inf(String descriptionFormat, Object... descriptionArguments) {

        return inf( null, descriptionFormat, descriptionArguments );
    }

    /**
     * Log an application warning.
     * 
     * <p>
     * This level is for all events that indicate a suboptimal / non-ideal flow.
     * </p>
     * 
     * @param cause
     *            A throwable that details the stack at the time of this event.
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger wrn(Throwable cause, String descriptionFormat, Object... descriptionArguments) {

        if (logger.isWarnEnabled())
            if (cause == null)
                logger.warn( String.format( descriptionFormat, descriptionArguments ) );
            else
                logger.warn( String.format( descriptionFormat, descriptionArguments ), cause );

        eventCause = cause;
        eventFormat = descriptionFormat;
        eventArguments = descriptionArguments;

        return this;
    }

    /**
     * Log an application warning.
     * 
     * <p>
     * This level is for all events that indicate a suboptimal / non-ideal flow.
     * </p>
     * 
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger wrn(String descriptionFormat, Object... descriptionArguments) {

        return wrn( null, descriptionFormat, descriptionArguments );
    }

    /**
     * Log an internal inconsistency.
     * 
     * <p>
     * This level is for all events that occur unexpectedly. They indicate a bug in the application's flow.
     * </p>
     * 
     * @see #bug(Throwable, String, Object...)
     * 
     * @param cause
     *            A throwable that details the stack at the time of this event.
     * 
     * @return Self, for chaining.
     */
    public Logger bug(Throwable cause) {

        return bug( cause, "Unexpected Error" );
    }

    /**
     * Log an internal inconsistency.
     * 
     * <p>
     * This level is for all events that occur unexpectedly. They indicate a bug in the application's flow.
     * </p>
     * 
     * @param cause
     *            A throwable that details the stack at the time of this event.
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger bug(Throwable cause, String descriptionFormat, Object... descriptionArguments) {

        if (logger.isErrorEnabled())
            if (cause == null)
                logger.error( String.format( descriptionFormat, descriptionArguments ) );
            else
                logger.error( String.format( descriptionFormat, descriptionArguments ), cause );

        eventCause = cause;
        eventFormat = descriptionFormat;
        eventArguments = descriptionArguments;

        return this;
    }

    /**
     * Log an internal inconsistency.
     * 
     * <p>
     * This level is for all events that occur unexpectedly. They indicate a bug in the application's flow.
     * </p>
     * 
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger bug(String descriptionFormat, Object... descriptionArguments) {

        return bug( null, descriptionFormat, descriptionArguments );
    }

    /**
     * Log an application error.
     * 
     * <p>
     * This level is for all events that indicate failure to comply with the request.
     * </p>
     * 
     * @param cause
     *            A throwable that details the stack at the time of this event.
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger err(Throwable cause, String descriptionFormat, Object... descriptionArguments) {

        if (logger.isErrorEnabled())
            if (cause == null)
                logger.error( String.format( descriptionFormat, descriptionArguments ) );
            else
                logger.error( String.format( descriptionFormat, descriptionArguments ), cause );

        eventCause = cause;
        eventFormat = descriptionFormat;
        eventArguments = descriptionArguments;

        return this;
    }

    /**
     * Log an application error.
     * 
     * <p>
     * This level is for all events that indicate failure to comply with the request.
     * </p>
     * 
     * @param descriptionFormat
     *            The format of the event message. See {@link String#format(String, Object...)}.
     * @param descriptionArguments
     *            The arguments to inject into the event message format.
     * 
     * @return Self, for chaining.
     */
    public Logger err(String descriptionFormat, Object... descriptionArguments) {

        return err( null, descriptionFormat, descriptionArguments );
    }

    // Event delegation --

    /**
     * Generate an unchecked {@link Error} of the previously logged event (and initialize its cause if set).
     * 
     * @return The previously logged event.
     */
    public Error toError() {

        return toError( Error.class );
    }

    public <E extends Throwable> E toError(Class<E> errorClass) {

        if (eventFormat == null)
            throw new IllegalStateException( "No previous event set: can't rethrow one." );

        try {
            if (eventCause == null) {
                Constructor<E> errorConstructor = errorClass.getConstructor( String.class );
                return errorConstructor.newInstance( String.format( eventFormat, eventArguments ) );
            }

            Constructor<E> errorConstructor = errorClass.getConstructor( String.class, Throwable.class );
            return errorConstructor.newInstance( String.format( eventFormat, eventArguments ), eventCause );
        }

        catch (IllegalArgumentException e) {
            throw loggerLogger.bug( e ).toError();
        } catch (InstantiationException e) {
            throw loggerLogger.bug( e ).toError();
        } catch (IllegalAccessException e) {
            throw loggerLogger.bug( e ).toError();
        } catch (InvocationTargetException e) {
            throw loggerLogger.bug( e ).toError();
        } catch (SecurityException e) {
            throw loggerLogger.bug( e ).toError();
        } catch (NoSuchMethodException e) {
            throw loggerLogger.bug( e ).toError();
        }
    }

    // Internal operation --

    private Logger(Class<?> type) {

        logger = LoggerFactory.getLogger( type );
    }
}
