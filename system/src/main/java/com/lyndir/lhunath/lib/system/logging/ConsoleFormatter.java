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

import java.util.logging.Level;


/**
 * <i>ConsoleFormatter - A log output formatter which keeps the format of {@link LogFormatter} as it is.</i><br> <br> It just adds a
 * carriage-return new-line at the end of log lines, fit for most console output.<br> <br>
 *
 * @author lhunath
 */
public class ConsoleFormatter extends LogFormatter {

    /**
     * Create a new ConsoleFormatter instance.
     *
     * @param verbosity Whether to use verbose mode.
     */
    public ConsoleFormatter(final boolean verbosity) {

        super( verbosity );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setColors() {

        levelColor.put( null, "\r\n" );
        levelColor.put( Level.SEVERE, "" );
        levelColor.put( Level.WARNING, "" );
        levelColor.put( Level.INFO, "" );
        levelColor.put( Level.CONFIG, "" );
        levelColor.put( Level.FINE, "" );
        levelColor.put( Level.FINER, "" );
        levelColor.put( Level.FINEST, "" );
    }
}
