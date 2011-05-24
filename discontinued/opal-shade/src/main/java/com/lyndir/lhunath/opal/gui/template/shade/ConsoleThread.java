package com.lyndir.lhunath.opal.gui.template.shade;

import com.lyndir.lhunath.opal.system.BaseConfig;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.*;
import javax.swing.*;


/**
 * Read line-based data from an input stream and write it out on a component.
 *
 * @author lhunath
 */
public class ConsoleThread extends Thread {

    private static final Logger logger = Logger.get( ConsoleThread.class );

    private final InputStreamReader in;
    private final JTextArea         console;

    /**
     * Create a new {@link ConsoleThread} instance.
     *
     * @param in      The source of the data to write in the console.
     * @param console The component to output the console data to.
     */
    ConsoleThread(InputStream in, JTextArea console) {

        super( "Redirect stdout to Virtual Console" );
        setDaemon( true );

        this.in = new InputStreamReader( in );
        this.console = console;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        try {
            int bytesRead;
            char[] buf = new char[BaseConfig.BUFFER_SIZE];

            while ((bytesRead = in.read( buf )) > 0) {
                console.append( new String( buf, 0, bytesRead ) );

                /* Scroll to the bottom. */
                console.setCaretPosition( console.getDocument().getLength() );
            }
        }
        catch (IOException e) {
            logger.err( e, "Could not read from the console source." );
        }
    }
}
