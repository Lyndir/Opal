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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

    private static final Logger          loggerLogger   = get( Logger.class );

    private static final Map<Class<?>, Logger> loggers = new HashMap<Class<?>, Logger>();

    private final org.slf4j.Logger             logger;

    private static final ThreadLocal<Throwable>       eventCause     = new ThreadLocal<Throwable>();
    private static final ThreadLocal<String>          eventFormat    = new ThreadLocal<String>();
    private static final ThreadLocal<Object[]>        eventArguments = new ThreadLocal<Object[]>();


    // Create a logger --

    public static Logger get(Class<?> type) {

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

        eventCause.set( cause );
        eventFormat.set( descriptionFormat );
        eventArguments.set( descriptionArguments );

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

        eventCause.set( cause );
        eventFormat.set( descriptionFormat );
        eventArguments.set( descriptionArguments );

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

        eventCause.set( cause );
        eventFormat.set( descriptionFormat );
        eventArguments.set( descriptionArguments );

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

        eventCause.set( cause );
        eventFormat.set( descriptionFormat );
        eventArguments.set( descriptionArguments );

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

        eventCause.set( cause );
        eventFormat.set( descriptionFormat );
        eventArguments.set( descriptionArguments );

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

        eventCause.set( cause );
        eventFormat.set( descriptionFormat );
        eventArguments.set( descriptionArguments );

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
     * The previous event details are kept in a thread-safe manner and local to this logger instance.
     *
     * @return An unchecked {@link Error}.
     */
    public RuntimeException toError() {

        if (eventCause.get() instanceof RuntimeException)
            return (RuntimeException) eventCause.get();

        return toError( RuntimeException.class );
    }

    /**
     * Generate the given {@link Throwable} of the previously logged event (and initialize its cause if set).
     *
     * The previous event details are kept in a thread-safe manner and local to this logger instance.
     *
     * @param errorClass
     *            The type of {@link Throwable} to generate for the previously logged event. This method relies on the
     *            fact that the given class has a constructor that takes a {@link String} argument (the message) and a
     *            {@link Throwable} argument (the cause) in that order.
     * @param args
     *            Optional additional arguments. These will be passed to the <code>errorClass</code> constructor, so
     *            make sure the class has a constructor that supports the given number and type of arguments. They
     *            should <b>follow</b> the message and cause arguments.
     * @param <E>
     *            The type of the requested exception must be a subclass of {@link Throwable}.
     *
     * @return The requested {@link Throwable}.
     */
    public <E extends Throwable> E toError(Class<E> errorClass, Object... args) {

        if (eventFormat.get() == null)
            throw new IllegalStateException( "No previous event set: can't rethrow one." );

        try {
            Class<?>[] types = new Class<?>[args.length + 2];
            Object[] arguments = new Object[args.length + 2];
            types[0] = String.class;
            arguments[0] = String.format( eventFormat.get(), eventArguments.get() );
            types[1] = Throwable.class;
            arguments[1] = eventCause.get();
            for (int a = 0; a < args.length; ++a) {
                arguments[a + 2] = args[a];
                if (arguments[a + 2] != null)
                    types[a + 2] = arguments[a + 2].getClass();
            }

            // Find all constructors of errorClass that match the argument types.
            // (Cast is necessary because generic arrays are unsafe in java.)
            @SuppressWarnings("unchecked")
            Constructor<E>[] errorConstructors = (Constructor<E>[]) errorClass.getConstructors();
            List<Constructor<E>> constructors = new LinkedList<Constructor<E>>();
            for (Constructor<E> constructor : errorConstructors) {
                if (constructor.getParameterTypes().length != types.length)
                    continue;

                boolean validConstructor = true;
                for (int t = 0; t < types.length; ++t) {
                    if (types[t] == null)
                        continue;
                    if (!constructor.getParameterTypes()[t].equals( types[t] )) {
                        validConstructor = false;
                        break;
                    }
                }

                if (validConstructor)
                    constructors.add( constructor );
            }
            if (constructors.isEmpty())
                throw loggerLogger.err( "No constructors found for %s that match argument types %s", //
                                        errorClass, Arrays.asList( types ) ) //
                .toError( IllegalArgumentException.class );
            if (constructors.size() > 1)
                throw loggerLogger.err( "Ambiguous argument types %s for constructors of %s.  Constructors: %s", //
                                        Arrays.asList( types ), errorClass, constructors ) //
                .toError( IllegalArgumentException.class );

            return constructors.get( 0 ).newInstance( arguments );
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
        }
    }

    // Internal operation --

    private Logger(Class<?> type) {

        logger = LoggerFactory.getLogger( type );
    }
}
