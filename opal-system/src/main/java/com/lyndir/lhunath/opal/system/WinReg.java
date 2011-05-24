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

import com.lyndir.lhunath.opal.system.dummy.NullOutputStream;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <i>WinReg - A simple interface to the windows registry.</i><br> <br> This accesses the windows registry through the use of
 * C:\Windows\System32\reg.exe.<br> This limits platform compatibility to Windows XP+.<br> <br>
 *
 * @author lhunath
 */
public class WinReg {

    private static final Logger logger = LoggerFactory.getLogger( WinReg.class );

    /**
     * Check whether registry queries are supported on the running operating system.
     *
     * @return Guess.
     */
    public static boolean isSupported() {

        try {
            OutputStream out = new NullOutputStream();
            return Shell.waitFor( Shell.exec( out, out, new File( "C:\\Windows\\System32" ), "reg.exe", "query", "/?" ) ) == 0;
        }
        catch (FileNotFoundException ignored) {
            return false;
        }
    }

    /**
     * Query the windows registry at the given location for a response in the given type.
     *
     * @param <T>   See type.
     * @param key   The key name to query.
     * @param value The value in the given key to query.
     * @param type  The type of the reply (String or Integer).
     *
     * @return The reply from the windows registry parsed into the requested type.
     */
    public static <T> T query(final String key, final String value, final Class<T> type) {

        if (!(type.equals( String.class ) || type.equals( Integer.class )))
            throw new IllegalArgumentException( "Can only query the registry for String or Integer types." );

        String output = Shell.execRead( new File( "C:\\Windows\\System32" ), "reg.exe", "query", key, "/v", value );

        int pos;
        T result = null;

        if (type.equals( String.class )) {
            pos = output.indexOf( "REG_SZ" ) + "REG_SZ".length();
            if (pos < 6) {
                logger.warn( "Key %s:%s not found!", key, value );
                return type.cast( "" );
            }

            result = type.cast( output.substring( pos ).trim() );
        } else if (type.equals( Integer.class )) {
            pos = output.indexOf( "REG_DWORD" ) + "REG_DWORD".length();
            result = type.cast( Integer.parseInt( output.substring( pos ).trim() ) );
        }

        return result;
    }
}
