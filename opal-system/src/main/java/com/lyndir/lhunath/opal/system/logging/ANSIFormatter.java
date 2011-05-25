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
package com.lyndir.lhunath.opal.system.logging;

import java.util.logging.Level;


/**
 * <i>ANSIFormatter - Format log messages for console output on a console that supports ANSI Color Control Sequences.</i><br> <br> Colors
 * your log messages generated by {@link LogFormatter} using ANSI Color Control Sequences.<br> <br>
 *
 * @author lhunath
 */
public class ANSIFormatter extends LogFormatter {

    @Override
    protected void setColors() {

        levelColor.put( null, (char) 27 + "[0m\r\n" );
        levelColor.put( Level.SEVERE, (char) 27 + "[1;31m" );
        levelColor.put( Level.WARNING, (char) 27 + "[1;33m" );
        levelColor.put( Level.INFO, (char) 27 + "[1;36m" );
        levelColor.put( Level.CONFIG, (char) 27 + "[1;34m" );
        levelColor.put( Level.FINE, (char) 27 + "[1;32m" );
        levelColor.put( Level.FINER, (char) 27 + "[1;32m" );
        levelColor.put( Level.FINEST, (char) 27 + "[1;32m" );
    }
}