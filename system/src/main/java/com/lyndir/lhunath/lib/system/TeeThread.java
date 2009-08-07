/*
 *   Copyright 2007, Maarten Billemont
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;

import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <h2>{@link TeeThread} - [in short] (TODO).</h2>
 * <p>
 * [description / usage].
 * </p>
 * <p>
 * <i>Dec 16, 2007</i>
 * </p>
 * 
 * @author mbillemo
 */
public class TeeThread extends Thread {

    private static final Logger logger = Logger.get( TeeThread.class );

    private InputStream         source;
    private OutputStream[]      destinations;


    /**
     * Create a new {@link TeeThread} instance.
     * 
     * @param source
     *            The data source.
     * @param destinations
     *            The destination streams to write the source data to.
     */
    public TeeThread(InputStream source, OutputStream... destinations) {

        super( "Tee Thread" );
        setDaemon( true );

        this.source = source;
        this.destinations = destinations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        try {
            int bytesRead;
            byte[] buf = new byte[BaseConfig.BUFFER_SIZE];

            while ((bytesRead = source.read( buf )) > 0)
                for (OutputStream destination : destinations)
                    destination.write( buf, 0, bytesRead );

        } catch (IOException e) {
            if (!(source instanceof PipedInputStream))
                logger.err( e, "Could not read from the console source." );
        }
    }
}
