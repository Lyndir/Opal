/*
 *     -= NetherPanel - Upload World of Warcraft data to web rosters =-
 *   
 *   Copyright (C) 2007 Maarten Billemont
 *   
 *   This program is free software; you can redistribute it and/or modify it under the terms of the
 *   GNU General Public License as published by the Free Software Foundation; either version 2 of the
 *   License, or (at your option) any later version.
 *   
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *   even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   General Public License for more details.
 *   
 *   You may not use this file except in compliance with the License. You may obtain a copy of the
 *   License at
 *   
 *       http://www.gnu.org/licenses/gpl.html
 */
package com.lyndir.lhunath.lib.plain;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import jlibdiff.Diff;
import jlibdiff.Hunk;

import com.lyndir.lhunath.lib.system.Utils;


/**
 * TODO: Needs cleanup.
 */
public class DiffUtils {

    /**
     * Get the contextual difference between the data from two streams.
     * 
     * @param from
     *            Original stream
     * @param to
     *            Modified stream
     * @return The diff string.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String getDiff(InputStream from, InputStream to)
            throws FileNotFoundException, IOException {

        return getDiff( new BufferedReader( new InputStreamReader( from ) ), new BufferedReader( new InputStreamReader(
                to ) ) );
    }

    /**
     * Get the contextual difference between the data from two streams.
     * 
     * @param from
     *            Original stream
     * @param to
     *            Modified stream
     * @return The diff string.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String getDiff(BufferedReader from, BufferedReader to)
            throws FileNotFoundException, IOException {

        Diff diff = new Diff();
        diff.diffBuffer( from, to );

        return renderDiff( diff );
    }

    /**
     * Get the contextual difference between the data from two strings.
     * 
     * @param from
     *            Original stream
     * @param to
     *            Modified stream
     * @return The diff string.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String getDiff(String from, String to)
            throws FileNotFoundException, IOException {

        Diff diff = new Diff();
        diff.diffString( from, to );

        return renderDiff( diff );
    }

    /**
     * Render the difference as configured in the given diff object.
     */
    private static String renderDiff(Diff diff) {

        @SuppressWarnings( { "cast", "unchecked" })
        Vector<Hunk> hunks = (Vector<Hunk>) diff.getHunks();
        StringBuffer out = new StringBuffer( "<pre>" );

        for (Hunk hunk : hunks) {
            String chunk = hunk.convert().trim().replaceFirst( ".*\n", "" );
            String chunkAdd = chunk.replaceAll( "(?m)^[^>].*", "" ).trim().replaceAll( "(?m)^>", "+" );
            String chunkDel = chunk.replaceAll( "(?m)^[^<].*", "" ).trim().replaceAll( "(?m)^<", "-" );

            String diffFormat = "<span style='color: %x'>%s</span>";
            if (chunkDel.length() > 0)
                out.append( String.format( diffFormat, Utils.DARK_RED.getRGB() - 0xff000000, chunkDel ) ).append( '\n' );
            if (chunkAdd.length() > 0)
                out.append( String.format( diffFormat, Utils.DARK_GREEN.getRGB() - 0xff000000, chunkAdd ) ).append(
                                                                                                                    '\n' );
        }

        return out.append( "</pre>" ).toString();
    }
}
