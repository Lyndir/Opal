package com.lyndir.lhunath.opal.system.util;

import com.google.common.io.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.*;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link IOUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 29, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class IOUtils {

    static final Logger logger = Logger.get( IOUtils.class );

    private static final Pattern PATH_SEPARATORS = Pattern.compile( "[\\\\/]+" );

    public static <T> InputSupplier<T> supply(final T supply) {

        return new InputSupplier<T>() {
            @Override
            public T getInput()
                    throws IOException {

                return supply;
            }
        };
    }

    public static InputSupplier<? extends InputStream> supply(final byte[] supply) {

        return new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput()
                    throws IOException {

                return new ByteArrayInputStream( supply );
            }
        };
    }

    /**
     * A sane way of retrieving an entry from a {@link ZipFile} based on its /-delimited path name.
     *
     * @param zipFile    The {@link ZipFile} to retrieve the entry for.
     * @param zippedName The /-delimited pathname of the entry.
     *
     * @return The {@link ZipEntry} for the pathname or <code>null</code> if none was present.
     */
    @Nullable
    public static ZipEntry getZipEntry(final ZipFile zipFile, final CharSequence zippedName) {

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (PATH_SEPARATORS.matcher( entry.getName() )
                               .replaceAll( "/" )
                               .equals( PATH_SEPARATORS.matcher( zippedName ).replaceAll( "/" ) ))
                return entry;
        }

        return null;
    }

    /**
     * Search for the given pattern in the given file. The search is line-based and does not take newlines into account, nor does it have
     * the ability to cross them. The group parameter specifies which capture group from the pattern to return. Use 0 as group value to
     * return the whole line that matched the pattern.
     * <p>If the given file is a directory, a recursive search will take place.</p>
     *
     * @param pattern The pattern to search for.
     * @param file    The file to search in.
     * @param group   The group number in the pattern to return; or 0 to return the whole matching line.
     *
     * @return A string of lines, each being the specified group of a match of the pattern in the file or in any of the files in the
     *         directory.  The string is newline-terminated unless no matches are found, in which case the string is empty.
     *
     * @throws IOException The file, or a file in the directory could not be read.
     */
    public static String grep(final String pattern, final File file, final int group)
            throws IOException {

        return grep( Pattern.compile( pattern ), file, group );
    }

    /**
     * Search for the given pattern in the given file. The search is line-based and does not take newlines into account, nor does it have
     * the ability to cross them. The group parameter specifies which capture group from the pattern to return. Use 0 as group value to
     * return the whole line that matched the pattern.
     * <p>If the given file is a directory, a recursive search will take place.</p>
     *
     * @param pattern The pattern to search for.
     * @param file    The file to search in.
     * @param group   The group number in the pattern to return; or 0 to return the whole matching line.
     *
     * @return A string of lines, each being the specified group of a match of the pattern in the file or in any of the files in the
     *         directory.  The string is newline-terminated unless no matches are found, in which case the string is empty.
     *
     * @throws IOException The file, or a file in the directory could not be read.
     */
    public static String grep(final Pattern pattern, final File file, final int group)
            throws IOException {

        if (file.isDirectory()) {
            StringBuilder resultBuilder = new StringBuilder();
            for (final File child : file.listFiles())
                resultBuilder.append( grep( pattern, child, group ) );

            return resultBuilder.toString();
        }

        FileReader reader = new FileReader( file );
        try {
            return grep( pattern, reader, group );
        }
        finally {
            Closeables.closeQuietly( reader );
        }
    }

    /**
     * Search for the given pattern in the given stream. The search is line-based and does not take newlines into account, nor does it have
     * the ability to cross them. The group parameter specifies which capture group from the pattern to return. Use 0 as group value to
     * return the whole line that matched the pattern.
     *
     * @param pattern The pattern to search for.
     * @param reader  The reader to search in.
     * @param group   The group number in the pattern to return; or 0 to return the whole matching line.
     *
     * @return A string of lines, each being the specified group of a match of the pattern in the stream.  The string is newline-terminated
     *         unless no matches are found, in which case the string is empty.
     *
     * @throws IOException Couldn't read from the reader.
     */
    public static String grep(final Pattern pattern, final Reader reader, final int group)
            throws IOException {

        StringBuilder resultBuilder = new StringBuilder();
        for (final String line : CharStreams.readLines( reader )) {
            Matcher matcher = pattern.matcher( line );
            if (matcher.find())
                resultBuilder.append( matcher.group( group ) ).append( '\n' );
        }

        return resultBuilder.toString();
    }
}
