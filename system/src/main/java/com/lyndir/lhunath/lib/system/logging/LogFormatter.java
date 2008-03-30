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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.lyndir.lhunath.lib.system.Utils;

/**
 * <i>LogFormatter - Formats log messages.</i><br>
 * <br>
 * This formatter attempts to format log messages to put most of the information in a structured log message without
 * looking too big or confusing.<br>
 * <br>
 * 
 * <pre>
 *   [ LEVEL | LINE:PACKAGE.CLASS.METHOD() ]-
 *    &gt;  MESSAGE
 * </pre>
 * 
 * <br>
 * 
 * @author lhunath
 */
public abstract class LogFormatter extends Formatter {

    private static final String[] skipPackages = { "com.lyndir.lhunath.lib", "java", "sun", "com.sun" };
    protected Map<Level, String>  levelColor;
    protected boolean             verbose;

    /**
     * Create a new LogFormatter instance.
     */
    public LogFormatter() {

        levelColor = new HashMap<Level, String>();
        setVerbose( true );
        setColors();
    }

    /**
     * Create a new LogFormatter instance.
     * 
     * @param verbosity
     *        Whether to use verbose mode or not (default: false).
     */
    public LogFormatter(boolean verbosity) {

        this();
        setVerbose( verbosity );
    }

    /**
     * Fill the {@link LogFormatter#levelColor} map with colors for each level.
     */
    protected abstract void setColors();

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(LogRecord record) {

        /* Initialize some convenience variables for this record. */
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Throwable error = record.getThrown();

        /* If this log message has a throwable, use it(s cause) to make the log output more accurate. */
        if (error != null) {
            Throwable e = error;
            while (e.getCause() != null && !(e.getCause() instanceof RuntimeException))
                e = e.getCause();
            stackTrace = e.getStackTrace();
        }

        /* Find the most likely source of the message. */
        StackTraceElement sourceElement = null;
        if (stackTrace.length > 0)
            sourceElement = stackTrace[0];
        for (StackTraceElement element : stackTrace)
            if (!isIgnored( (sourceElement = element).getClassName() ))
                break;

        /* Pretty print the source:                    Line:Package.Class.Method() */
        String realSource = "", relevSource = "";
        if (stackTrace.length > 0 && !stackTrace[0].equals( sourceElement ))
            realSource = String.format( "(%s:%d) %s.%s()", stackTrace[0].getFileName(), stackTrace[0].getLineNumber(),
                    Utils.compressSignature( stackTrace[0].getClassName() ), stackTrace[0].getMethodName() );
        if (sourceElement != null)
            relevSource = String.format( "(%s:%d) %s.%s()", sourceElement.getFileName(), sourceElement.getLineNumber(),
                    Utils.compressSignature( sourceElement.getClassName() ), sourceElement.getMethodName() );
        String source = realSource + (realSource.length() > 0 ? ", " : "") + relevSource;
        if (source.length() == 0)
            source = "[Unknown Source]";

        /* Generate a detail message for the problem:  # (File:Line) Exception: Message */
        String message = "";
        if (error != null)
            for (Throwable e = error; e != null; e = e.getCause())
                message = String.format( "(%s:%d) %s: %s\n", e.getStackTrace()[0].getFileName(),
                        e.getStackTrace()[0].getLineNumber(), e.getClass().getName(), e.getLocalizedMessage() )
                          + message;

        if (record.getMessage() != null && record.getMessage().length() > 0)
            message = record.getMessage() + "\n" + message;
        message = message.trim();

        /* Put it all together and write it to the buffer. */
        StringBuffer buffer = new StringBuffer();
        buffer.append( levelColor.get( record.getLevel() ) );
        String prefix = "$1 #  ";
        if (record.getLevel().intValue() > Level.INFO.intValue())
            buffer.append( String.format( "[ %7s | %-30s ]:\n", record.getLevel().getLocalizedName(), source ) );
        else
            prefix = String.format( "%s [ %-7s ]  ", prefix, record.getLevel().getLocalizedName() );
        buffer.append( message.replaceAll( "(\n|^)", prefix ) );

        /* Check if there's a stack trace that needs to be written. */
        if (isVerbose() && error != null)
            /* Write the stack trace to the buffer. */
            if (stackTrace.length > 0)
                for (StackTraceElement e : stackTrace)
                    buffer.append( String.format( "\n %s      %s", isIgnored( e.getClassName() ) ? '-' : '>', e ) );
        buffer.append( levelColor.get( null ) );

        return buffer.toString();
    }

    private boolean isIgnored(String classOrPackage) {

        for (String skipPackage : skipPackages)
            if (classOrPackage.startsWith( skipPackage ))
                return true;

        return false;
    }

    /**
     * Retrieve the verbosity of this LogFormatter.
     * 
     * @return Guess.
     */
    public boolean isVerbose() {

        return verbose;
    }

    /**
     * Set the verbosity of this LogFormatter.<br>
     * Verbose mode shows stack traces for errors.
     * 
     * @param verbose
     *        Guess.
     */
    public void setVerbose(boolean verbose) {

        this.verbose = verbose;
    }
}
