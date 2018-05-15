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
package com.lyndir.lhunath.opal.plain;

import java.io.*;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import jlibdiff.Diff;
import jlibdiff.Hunk;


/**
 * TODO: Needs cleanup.
 *
 * @author lhunath
 */
public abstract class DiffUtils {

    @SuppressWarnings("HardcodedLineSeparator")
    private static final Pattern LINE          = Pattern.compile( ".*[\r\n]+" );
    private static final Pattern NOT_ADD_CHUNK = Pattern.compile( "(?m)^[^>].*" );
    private static final Pattern NOT_REM_CHUNK = Pattern.compile( "(?m)^[^<].*" );
    private static final Pattern ADD_CHUNK     = Pattern.compile( "(?m)^>" );
    private static final Pattern REM_CHUNK     = Pattern.compile( "(?m)^<" );

    /**
     * Get the contextual difference between the data from two streams.
     *
     * @param from Original stream
     * @param to   Modified stream
     *
     * @return The diff string.
     *
     * @throws IOException jlibdiff fails.
     */
    public static String getDiff(final InputStream from, final InputStream to, final Charset charset)
            throws IOException {

        try (BufferedReader fromReader = new BufferedReader( new InputStreamReader( from, charset ) ); //
             BufferedReader toReader = new BufferedReader( new InputStreamReader( to, charset ) )) {
            return getDiff( fromReader, toReader );
        }
    }

    /**
     * Get the contextual difference between the data from two streams.
     *
     * @param from Original stream
     * @param to   Modified stream
     *
     * @return The diff string.
     *
     * @throws IOException jlibdiff fails.
     */
    public static String getDiff(final BufferedReader from, final BufferedReader to)
            throws IOException {

        Diff diff = new Diff();
        diff.diffBuffer( from, to );

        return renderDiff( diff );
    }

    /**
     * Get the contextual difference between the data from two strings.
     *
     * @param from Original stream
     * @param to   Modified stream
     *
     * @return The diff string.
     *
     * @throws IOException jlibdiff fails.
     */
    public static String getDiff(final String from, final String to)
            throws IOException {

        Diff diff = new Diff();
        diff.diffString( from, to );

        return renderDiff( diff );
    }

    /**
     * Render the difference as configured in the given diff object.
     *
     * @param diff The diff that should be rendered.
     *
     * @return A HTML-formatted representation of the given diff.
     */
    private static String renderDiff(final Diff diff) {

        @SuppressWarnings({ "cast", "unchecked" })
        Iterable<Hunk> hunks = diff.getHunks();
        StringBuilder out = new StringBuilder( "<pre>" );

        for (final Hunk hunk : hunks) {
            String chunk = LINE.matcher( hunk.convert().trim() ).replaceFirst( "" );
            String chunkAdd = ADD_CHUNK.matcher( NOT_ADD_CHUNK.matcher( chunk ).replaceAll( "" ).trim() ).replaceAll( "+" );
            String chunkDel = REM_CHUNK.matcher( NOT_REM_CHUNK.matcher( chunk ).replaceAll( "" ).trim() ).replaceAll( "-" );

            String diffFormat = "<span style='color: %s'>%s</span>";
            if (!chunkDel.isEmpty())
                out.append( String.format( diffFormat, "#993333", chunkDel ) ).append( System.lineSeparator() );
            if (!chunkAdd.isEmpty())
                out.append( String.format( diffFormat, "#339933", chunkAdd ) ).append( System.lineSeparator() );
        }

        return out.append( "</pre>" ).toString();
    }
}
