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
package com.lyndir.lhunath.opal.system;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.lyndir.lhunath.opal.system.dummy.NullOutputStream;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import javax.annotation.Nullable;


/**
 * <i>Shell - A convenience class to execute processes for different purposes.</i><br> <br>
 *
 * @author lhunath
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class Shell {

    private static final Logger logger      = Logger.get( Shell.class );
    private static final int    BUFFER_SIZE = 4096;

    /**
     * Run an application or shell script and redirect its stdout and stderr to our stdout and stderr.
     *
     * @param block   Whether or not to block until the process has finished.
     * @param currDir The current directory for the child process.
     * @param cmd     The command to invoke for running the new process.
     *
     * @return The process object for the process that was started.
     */
    @Nullable
    public static Process exec(final boolean block, final File currDir, final String... cmd) {

        Process process = exec( System.out, System.err, currDir, cmd );

        if (block && (process != null))
            waitFor( process );

        return process;
    }

    /**
     * Wait for the given process to exit and return its exit status.
     *
     * @param process The process to wait for.
     *
     * @return The exit status of the given process.
     */
    public static int waitFor(@Nullable final Process process) {

        if (process == null)
            return -256;

        try {
            process.waitFor();
        }
        catch (final InterruptedException e) {
            logger.err( e, "Process interrupted!" );
            process.destroy();
        }

        return process.exitValue();
    }

    /**
     * Run an application or shell script and redirect its stdout and stderr to the given output streams. The call will not block.
     *
     * @param out     The stream to write the process' standard output into.
     * @param err     The stream to write the process' standard error into.
     * @param currDir The current directory for the child process.
     * @param cmd     The command to invoke for running the new process.
     *
     * @return The process object for the process that was started.
     */
    @Nullable
    public static Process exec(final OutputStream out, final OutputStream err, final File currDir, final String... cmd) {

        int      bytesRead;
        char[]   buffer  = new char[100];
        String[] execCmd = cmd;

        try (Reader reader = new InputStreamReader( new FileInputStream( new File( currDir, cmd[0] ) ), Charsets.US_ASCII )) {
            bytesRead = reader.read( buffer );
        }
        catch (final IOException e) {
            logger.err( e, "Failed to open the file to execute!" );
            return null;
        }

        /* Check whether this is a shell script and if so, what interpreter to use. */
        // FIXME: Hashbang can contain one argument
        String head = new String( buffer, 0, bytesRead );
        int    eol  = head.indexOf( System.lineSeparator() );
        if (eol > 0) {
            head = head.substring( 0, eol );
            if ("#!".equals( head.substring( 0, 2 ) )) {
                String             shell   = head.substring( 2 );
                LinkedList<String> cmdList = new LinkedList<>( Arrays.asList( cmd ) );
                cmdList.addFirst( shell.trim() );
                execCmd = cmdList.toArray( cmd );
            }
        }

        try {
            @SuppressWarnings({ "CallToRuntimeExec" })
            final Process process = Runtime.getRuntime().exec( execCmd, null, currDir );

            new Thread( new Runnable() {

                @Override
                public void run() {

                    try (InputStream inputStream = process.getInputStream()) {
                        ByteStreams.copy( inputStream, out );
                    }
                    catch (final IOException e) {
                        logger.err( e, "Couldn't read from process or write to stdout!" );
                    }
                }
            }, cmd[0] + " stdout" ).start();

            new Thread( new Runnable() {

                @Override
                public void run() {

                    try (InputStream errorStream = process.getErrorStream()) {
                        ByteStreams.copy( errorStream, err );
                    }
                    catch (final IOException e) {
                        logger.err( e, "Couldn't read from process or write to stderr!" );
                    }
                }
            }, cmd[0] + " stderr" ).start();

            return process;
        }
        catch (final IOException e) {
            logger.err( e, "Could not start process %s!", cmd[0] );
            return null;
        }
    }

    /**
     * Run an application or shell script and read its standard output and standard error into a string.
     *
     * @param charset The character set to decode the process' output with.
     * @param currDir The current directory for the child process.
     * @param cmd     The command to invoke for running the new process.
     *
     * @return The standard output and standard error of the process.
     */
    @Nullable
    public static String execRead(final Charset charset, final File currDir, final String... cmd) {

        try {
            PipedOutputStream output = new PipedOutputStream();
            exec( output, new NullOutputStream(), currDir, cmd );

            return CharStreams.toString( new InputStreamReader( new PipedInputStream( output ), charset ) );
        }
        catch (final FileNotFoundException e) {
            logger.err( e, "Command to execute was not found!" );
        }
        catch (final IOException e) {
            logger.err( e, "Failed to read from the process!" );
        }

        return null;
    }
}
