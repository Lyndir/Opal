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
package com.lyndir.lhunath.lib.system;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.lyndir.lhunath.lib.system.dummy.NullOutputStream;
import com.lyndir.lhunath.lib.system.logging.Logger;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;


/**
 * <i>Shell - A convenience class to execute processes for different purposes.</i><br> <br>
 *
 * @author lhunath
 */
public class Shell {

    static final Logger logger = Logger.get( Shell.class );

    protected static final int BUFFER_SIZE = 4096;

    /**
     * Run an application or shell script and redirect its stdout and stderr to our stdout and stderr.
     *
     * @param block   Whether or not to block until the process has finished.
     * @param currDir The current directory for the child process.
     * @param cmd     The command to invoke for running the new process.
     *
     * @return The process object for the process that was started.
     *
     * @throws FileNotFoundException There is no file by the name of the first element of cmd in the given currDir.
     */
    public static Process exec(final boolean block, final File currDir, final String... cmd)
            throws FileNotFoundException {

        Process process = exec( System.out, System.err, currDir, cmd );

        if (block && process != null)
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
    public static int waitFor(final Process process) {

        if (process == null)
            return -256;

        try {
            process.waitFor();
        }
        catch (InterruptedException e) {
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
     *
     * @throws FileNotFoundException There is no file by the name of the first element of cmd in the given currDir.
     */
    public static Process exec(final OutputStream out, final OutputStream err, final File currDir, final String... cmd)
            throws FileNotFoundException {

        char[] buffer = new char[100];
        String[] execCmd = cmd;

        Reader reader = new FileReader( new File( currDir, cmd[0] ) );
        try {
            reader.read( buffer );
        }
        catch (IOException e) {
            logger.err( e, "Failed to open the file to execute!" );
            return null;
        }
        finally {
            Closeables.closeQuietly( reader );
        }

        /* Check whether this is a shell script and if so, what interpreter to use. */
        // FIXME: Hashbang can contain one argument
        String head = new String( buffer );
        int eol = head.indexOf( '\n' );
        if (eol > 0) {
            head = head.substring( 0, eol );
            if ("#!".equals( head.substring( 0, 2 ) )) {
                String shell = head.substring( 2 );
                LinkedList<String> cmdList = new LinkedList<String>( Arrays.asList( cmd ) );
                cmdList.addFirst( shell.trim() );
                execCmd = cmdList.toArray( cmd );
            }
        }

        try {
            @SuppressWarnings( { "CallToRuntimeExec" })
            final Process process = Runtime.getRuntime().exec( execCmd, null, currDir );

            new Thread( new Runnable() {

                @Override
                public void run() {

                    try {
                        ByteStreams.copy( process.getInputStream(), out );
                    }
                    catch (IOException e) {
                        logger.err( e, "Couldn't read from process or write to stdout!" );
                    }
                    Closeables.closeQuietly( process.getInputStream() );
                }
            }, cmd[0] + " stdout" ).start();

            new Thread( new Runnable() {

                @Override
                public void run() {

                    try {
                        ByteStreams.copy( process.getErrorStream(), err );
                    }
                    catch (IOException e) {
                        logger.err( e, "Couldn't read from process or write to stderr!" );
                    }
                    Closeables.closeQuietly( process.getErrorStream() );
                }
            }, cmd[0] + " stderr" ).start();

            return process;
        }
        catch (IOException e) {
            logger.err( e, "Could not start process %s!", cmd[0] );
            return null;
        }
    }

    /**
     * Run an application or shell script and read its standard output and standard error into a string.
     *
     * @param currDir The current directory for the child process.
     * @param cmd     The command to invoke for running the new process.
     *
     * @return The standard output and standard error of the process.
     */
    public static String execRead(final File currDir, final String... cmd) {

        try {
            PipedOutputStream output = new PipedOutputStream();
            exec( output, new NullOutputStream(), currDir, cmd );

            return CharStreams.toString( new InputStreamReader( new PipedInputStream( output ) ) );
        }
        catch (FileNotFoundException e) {
            logger.err( e, "Command to execute was not found!" );
        }
        catch (IOException e) {
            logger.err( e, "Failed to read from the process!" );
        }

        return null;
    }
}
