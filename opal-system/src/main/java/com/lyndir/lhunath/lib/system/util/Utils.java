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
package com.lyndir.lhunath.lib.system.util;

import com.lyndir.lhunath.lib.system.logging.Logger;
import java.io.File;
import java.util.regex.Pattern;


/**
 * @author lhunath
 */
public class Utils {

    private static final Logger logger = Logger.get( Utils.class );

    /**
     * Ratio of the long part of the golden section.
     */
    public static final double GOLDEN     = 0.618;
    /**
     * Inverted ratio of the long part of the golden section.
     */
    public static final double GOLDEN_INV = 1 / GOLDEN;

    private static final Pattern WINDOWS = Pattern.compile( "Windows.*" );
    private static final Pattern LINUX   = Pattern.compile( "Linux.*" );
    private static final Pattern MACOS   = Pattern.compile( "Mac.*" );
    private static final Pattern SUNOS   = Pattern.compile( "SunOS.*" );

    /**
     * Extend the java library search path by adding the path of a library that will need to be loaded at some point in the application's
     * life cycle.
     *
     * @param libName The name of the library that will be loaded.
     */
    public static void initNativeLibPath(final String libName) {

        String libFileName = libName;
        if (WINDOWS.matcher( System.getProperty( "os.name" ) ).matches())
            libFileName = String.format( "%s.dll", libName );
        else if (LINUX.matcher( System.getProperty( "os.name" ) ).matches())
            libFileName = String.format( "lib%s.so", libName );
        else if (MACOS.matcher( System.getProperty( "os.name" ) ).matches())
            libFileName = String.format( "lib%s.jnilib", libName );
        else if (SUNOS.matcher( System.getProperty( "os.name" ) ).matches())

            if ("x86".equals( System.getProperty( "os.arch" ) ))
                libFileName = String.format( "lib%s_sun_x86.so", libName );
            else
                libFileName = String.format( "lib%s_sun_sparc.so", libName );
        else
            logger.wrn( "Unrecognised OS: %s", System.getProperty( "os.name" ) );

        File libFile = new File( String.format( "lib/native/%s", libFileName ) );
        if (libFile.exists())
            logger.wrn( "Native library %s not supported for your OS (%s).", libName, System.getProperty( "os.name" ) );
        else
            System.setProperty(
                    "java.library.path", String.format( "%s:%s", System.getProperty( "java.library.path" ), libFile.getParent() ) );
    }
}
