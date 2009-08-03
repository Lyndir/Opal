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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyndir.lhunath.lib.system.dummy.NullOutputStream;


/**
 * <i>Shell - A convenience class to execute processes for different purposes.</i><br>
 * <br>
 * 
 * @author lhunath
 */
public class Shell {

    static final Logger        logger      = LoggerFactory.getLogger( Shell.class );
    protected static final int BUFFER_SIZE = 4096;


    /**
     * Run an application or shell script and redirect its stdout and stderr to our stdout and stderr.
     * 
     * @param block
     *            Whether or not to block until the process has finished.
     * @param currDir
     *            The current directory for the child process.
     * @param cmd
     *            The command to invoke for running the new process.
     * @return The process object for the process that was started.
     * @throws FileNotFoundException
     */
    public static Process exec(boolean block, File currDir, String... cmd)
            throws FileNotFoundException {

        Process process = exec( System.out, System.err, currDir, cmd );

        if (block && process != null)
            waitFor( process );

        return process;
    }

    /**
     * Wait for the given process to exit and return its exit status.
     * 
     * @param process
     *            The process to wait for.
     * @return The exit status of the given process.
     */
    public static int waitFor(Process process) {

        if (process == null)
            return -256;

        try {
            process.waitFor();
        } catch (InterruptedException err) {
            logger.error( "Process interrupted!", err );
            process.destroy();
        }

        return process.exitValue();
    }

    /**
     * Run an application or shell script and redirect its stdout and stderr to the given output streams. The call will
     * not block.<br>
     * <br>
     * Output and error streams will be closed, except if they are the application's standard output or standard error.
     * 
     * @param out
     *            The stream to write the process' standard output into.
     * @param err
     *            The stream to write the process' standard error into.
     * @param currDir
     *            The current directory for the child process.
     * @param cmd
     *            The command to invoke for running the new process.
     * @return The process object for the process that was started.
     * @throws FileNotFoundException
     */
    public static Process exec(final OutputStream out, final OutputStream err, File currDir, String... cmd)
            throws FileNotFoundException {

        char[] buffer = new char[100];
        FileReader reader = new FileReader( new File( currDir, cmd[0] ) );
        String[] execCmd = cmd;

        try {
            reader.read( buffer );
            reader.close();
        } catch (IOException e) {
            logger.error( "Failed to open the file to execute!", e );
            return null;
        }

        /* Check whether this is a shell script and if so, what interpreter to use. */
        // FIXME: Hashbang can contain one argument
        String head = new String( buffer );
        int eol = head.indexOf( "\n" );
        if (eol > 0) {
            head = head.substring( 0, eol );
            if (head.substring( 0, 2 ).equals( "#!" )) {
                String shell = head.substring( 2 );
                LinkedList<String> cmdList = new LinkedList<String>( Arrays.asList( cmd ) );
                cmdList.addFirst( shell.trim() );
                execCmd = cmdList.toArray( cmd );
            }
        }

        try {
            final Process process = Runtime.getRuntime().exec( execCmd, null, currDir );

            new Thread( cmd[0] + " stdout" ) {

                @Override
                public void run() {

                    try {
                        Utils.pipeStream( process.getInputStream(), BUFFER_SIZE, out, null, false );
                    } catch (IOException e) {
                        logger.error( "Couldn't read from process or write to stdout!", e );
                    }
                    try {
                        process.getInputStream().close();
                        if (!System.out.equals( out ) && !System.err.equals( err ))
                            out.close();
                    } catch (IOException e) {
                        /* Already closed. */
                    }
                }
            }.start();

            new Thread( cmd[0] + " stderr" ) {

                @Override
                public void run() {

                    try {
                        Utils.pipeStream( process.getErrorStream(), BUFFER_SIZE, err, null, false );
                    } catch (IOException e) {
                        logger.error( "Couldn't read from process or write to stderr!", e );
                    }
                    try {
                        process.getErrorStream().close();
                        if (!err.equals( System.out ) && !err.equals( System.err ))
                            err.close();
                    } catch (IOException e) {
                        /* Already closed. */
                    }
                }
            }.start();

            return process;
        } catch (IOException e) {
            logger.error( String.format( "Could not start process %s!", cmd[0] ), e );
            return null;
        }
    }

    /**
     * Run an application or shell script and read its standard output and standard error into a string.
     * 
     * @param currDir
     *            The current directory for the child process.
     * @param cmd
     *            The command to invoke for running the new process.
     * @return The standard output and standard error of the process.
     */
    public static String execRead(File currDir, String... cmd) {

        try {
            PipedOutputStream output = new PipedOutputStream();
            Shell.exec( output, new NullOutputStream(), currDir, cmd );

            return Utils.readReader( new InputStreamReader( new PipedInputStream( output ) ) );
        } catch (FileNotFoundException e) {
            logger.error( "Command to execute was not found!", e );
        } catch (IOException e) {
            logger.error( "Failed to read from the process!", e );
        }

        return null;
    }
}
