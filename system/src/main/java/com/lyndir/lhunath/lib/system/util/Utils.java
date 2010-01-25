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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.StringBuilder;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.lyndir.lhunath.lib.system.BaseConfig;
import com.lyndir.lhunath.lib.system.Reflective;
import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <i>Utils - A big collection of convenience methods.</i><br>
 * <br>
 * My toolbox.<br>
 * <br>
 *
 * @author lhunath
 */
public class Utils {

    private static final Logger              logger         = Logger.get( Utils.class );

    /**
     * The default character set.
     */
    public static final Charset              charset        = Charset.forName( "UTF-8" );

    /**
     * Calendar fields in order.
     */
    public static final int[]                calendarFields = { Calendar.MILLISECOND, Calendar.SECOND,
            Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR };

    /**
     * Description of the calendar fields.
     */
    public static final Map<Integer, String> calendarDesc   = new HashMap<Integer, String>() {

                                                                private static final long serialVersionUID = 1L;

                                                                {
                                                                    put( Calendar.MILLISECOND, "Millisecond" );
                                                                    put( Calendar.SECOND, "Second" );
                                                                    put( Calendar.MINUTE, "Minute" );
                                                                    put( Calendar.HOUR_OF_DAY, "Hour" );
                                                                    put( Calendar.DAY_OF_MONTH, "Day" );
                                                                    put( Calendar.MONTH, "Month" );
                                                                    put( Calendar.YEAR, "Year" );
                                                                }
                                                            };

    /**
     * {@link SimpleDateFormat} of the calendar fields.
     */
    public static final Map<Integer, String> calendarFormat = new HashMap<Integer, String>() {

                                                                private static final long serialVersionUID = 1L;

                                                                {
                                                                    put( Calendar.MILLISECOND, "SSSS" );
                                                                    put( Calendar.SECOND, "ss." );
                                                                    put( Calendar.MINUTE, "mm:" );
                                                                    put( Calendar.HOUR_OF_DAY, "HH:" );
                                                                    put( Calendar.DAY_OF_MONTH, "dd " );
                                                                    put( Calendar.MONTH, "MM/" );
                                                                    put( Calendar.YEAR, "yyyy/" );
                                                                }
                                                            };

    /**
     * Default buffer size.
     */
    public static final int                        BUFFER_SIZE    = 4096;

    /**
     * Ratio of the long part of the golden section.
     */
    public static final double               GOLDEN         = 0.618;

    /**
     * Inverted ratio of the long part of the golden section.
     */
    public static final double               GOLDEN_INV     = 1 / GOLDEN;


    /**
     * Get the charset that should be used for all encoding and decoding of externalized messages and communication.
     *
     * @return Guess.
     */
    public static Charset getCharset() {

        return charset;
    }

    /**
     * Convert an object into a double in a semi-safe way. We parse {@link Object#toString()} and choose to return
     * <code>null</code> rather than throw an exception if the result is not a valid {@link Double}.
     *
     * @param object
     *            The object that may represent a double.
     * @return The resulting double.
     */
    public static Double parseDouble(Object object) {

        try {
            if (object == null)
                return null;

            if (object instanceof Double)
                return (Double) object;

            return Double.parseDouble( object.toString() );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * Convert an object into an integer in a semi-safe way. We parse {@link Object#toString()} and choose to return
     * <code>null</code> rather than throw an exception if the result is not a valid {@link Integer}.
     *
     * @param object
     *            The object that may represent an integer.
     * @return The resulting integer.
     */
    public static Integer parseInt(Object object) {

        try {
            if (object == null)
                return null;

            if (object instanceof Integer)
                return (Integer) object;

            return Integer.valueOf( object.toString() );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * Convert an object into a long in a semi-safe way. We parse {@link Object#toString()} and choose to return
     * <code>null</code> rather than throw an exception if the result is not a valid {@link Long}.
     *
     * @param object
     *            The object that may represent an long.
     * @return The resulting integer.
     */
    public static Long parseLong(Object object) {

        try {
            if (object == null)
                return null;

            if (object instanceof Long)
                return (Long) object;

            return Long.valueOf( object.toString() );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    /**
     * Convert the given object into a {@link String} using {@link Object#toString()}. If the <code>null</code> is
     * passed, don't return a string "null", but return <code>null</code> explicitly.
     *
     * @param object
     *            The object to convert into a string.
     * @return The string representation of the object.
     */
    public static String toString(Object object) {

        if (object == null)
            return null;

        if (object instanceof String)
            return (String) object;

        return object.toString();
    }

    /**
     * Calculate the MD5 hash for the given file.
     *
     * @param file
     *            The file to calculate the sum for.
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getMD5(File file) {

        return getDigest( file, Digest.MD5 );
    }

    /**
     * Calculate a digest hash for the given string.
     *
     * @param data
     *            The data to calculate the sum for.
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getMD5(String data) {

        return getDigest( new ByteArrayInputStream( data.getBytes() ), Digest.MD5 );
    }

    /**
     * Calculate a digest hash for the given bytes.
     *
     * @param data
     *            The data to calculate the sum for.
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getMD5(byte[] data) {

        return getDigest( new ByteArrayInputStream( data ), Digest.MD5 );
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param file
     *            The file to calculate the sum for.
     * @param digestType
     *            The digest to calculate.
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getDigest(File file, Digest digestType) {

        try {
            return getDigest( new BufferedInputStream( new FileInputStream( file ) ), digestType );
        } catch (FileNotFoundException e) {
            logger.err( e, "File %s does not exist.  Can't calculate %s", file, digestType.getName() );
            return null;
        }
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param data
     *            The data to calculate the sum for.
     * @param digestType
     *            The digest to calculate.
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getDigest(String data, Digest digestType) {

        return getDigest( new ByteArrayInputStream( data.getBytes() ), digestType );
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param data
     *            The data to calculate the sum for.
     * @param digestType
     *            The digest to calculate.
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getDigest(byte[] data, Digest digestType) {

        return getDigest( new ByteArrayInputStream( data ), digestType );
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param in
     *            The stream to read the data from needed to calculate the sum.
     * @param digestType
     *            The digest to calculate.
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getDigest(InputStream in, Digest digestType) {

        Checksum checksum = new CRC32();
        try {
            MessageDigest digest = null;
            if (digestType != Digest.CRC_32)
                digest = MessageDigest.getInstance( digestType.getName() );

            byte[] buffer = new byte[BUFFER_SIZE];

            for (int len; (len = in.read( buffer )) >= 0;)
                if (digest == null)
                    checksum.update( buffer, 0, len );
                else
                    digest.update( buffer, 0, len );

            StringBuilder digestHex = new StringBuilder();
            if (digest == null)
                digestHex.append( String.format( "%08x", checksum.getValue() ) );
            else
                for (byte b : digest.digest())
                    digestHex.append( String.format( "%02x", b ) );

            return digestHex.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.err( e, "%s is unsupported!", digestType.getName() );
        } catch (IOException e) {
            logger.err( e, "Couldn't read file to digest!" );
        }

        return null;
    }


    /**
     * Digests that can be calculated with the {@link Utils#getDigest(File, Digest)}
     * method.
     */
    public enum Digest {

        /**
         * The CRC-32 message digest algorithm.
         */
        CRC_32,

        /**
         * The MD2 message digest algorithm as defined in RFC 1319.
         */
        MD2,

        /**
         * The MD5 message digest algorithm as defined in RFC 1321.
         */
        MD5,

        /**
         * Hash algorithms defined in the FIPS PUB 180-2.
         */
        SHA1,

        /**
         * Hash algorithms defined in the FIPS PUB 180-2.<br>
         * SHA-256 is a 256-bit hash function intended to provide 128 bits of security against collision attacks.
         */
        SHA_256,

        /**
         * Hash algorithms defined in the FIPS PUB 180-2.<br>
         * A 384-bit hash may be obtained by truncating the SHA-512 output.
         */
        SHA_384,

        /**
         * Hash algorithms defined in the FIPS PUB 180-2.<br>
         * SHA-512 is a 512-bit hash function intended to provide 256 bits of security.
         */
        SHA_512;

        /**
         * Return the official name for this digest.
         *
         * @return Guess.
         */
        public String getName() {

            return name().replace( '_', '-' );
        }

        /**
         * Try to guess which type of hash the given digest is. This works fairly flawlessly for every supported hash
         * except for {@link Digest#MD2}; since its hash string is not exclusively different from that of
         * {@link Digest#MD5}. {@link Digest#MD5} will be the result of this function in this case.
         *
         * @param digest
         *            The hexadecimal-formatted hash string to estimate the type of.
         * @return The guessed digest type.
         * @throws IllegalArgumentException
         *             If the digest could not be guessed.
         */
        public static Digest guessType(String digest)
                throws IllegalArgumentException {

            switch (digest.length()) {
                case 8:
                    return CRC_32;
                case 32:
                    return MD5;
                case 40:
                    return SHA1;
                case 64:
                    return SHA_256;
                case 96:
                    return SHA_384;
                case 512:
                    return SHA_512;
            }

            throw new IllegalArgumentException( "The digest in the argument is cannot be recougnized: '" + digest + '\'' );
        }
    }


    /**
     * Get a {@link File} object for a resource.
     *
     * @param resource
     *            The filename of the resource. This is relative to the classpath of the context classloader.
     * @return Guess.
     */
    public static File res(String resource) {

        URL url = Thread.currentThread().getContextClassLoader().getResource( resource );
        if (url == null)
            return null;

        return res( url );
    }

    /**
     * Get a {@link File} object for a resource.
     *
     * @param url
     *            The url of the resource.
     * @return Guess.
     */
    public static File res(URL url) {

        /* In case the URI is invalid. */
        URI uri;
        String path = null;
        try {
            path = URLDecoder.decode( url.toExternalForm(), charset.name() ).replaceFirst( "^[^:]+:", "" );
        } catch (UnsupportedEncodingException ignored) {
            /* Ignore. */
        }

        /* Attempt to convert the URL to a URI. */
        try {
            uri = url.toURI();
        } catch (URISyntaxException ignored) {
            return new File( path ); // Known occurances: None.
        }

        /* Use the URI to create a file object. */
        try {
            return new File( uri );
        } catch (IllegalArgumentException ignored) {
            return new File( path ); // Known occurances: Windows SMB.
        }
    }

    /**
     * Check whether the given string is in a valid URL format.
     *
     * @param url
     *            The string that could represent a URL.
     * @return Guess.
     */
    public static boolean isUrl(String url) {

        try {
            //noinspection ResultOfObjectAllocationIgnored
            new URL( url );
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    /**
     * Convenience method of building a URL without the annoying exception thing.
     *
     * @param url
     *            The URL string.
     * @return The URL string in a URL object.
     */
    public static URL url(String url) {

        try {
            return url == null || url.length() == 0? null: new URL( url );
        } catch (MalformedURLException e) {
            logger.err( e, "Malformed URL: '%s'!", url );
            return null;
        }
    }

    /**
     * Read a stream in and return it as a string. This method will block until the stream is closed or reaches EOF.
     * This method will close both streams before it returns. It uses the default buffer size.
     *
     * @param reader
     *            The reader to get the data from.
     *
     * @return The stream's data as a string decoded with the default character set.
     *
     * @throws IOException
     *
     * @see BaseConfig#BUFFER_SIZE
     */
    public static String readReader(Reader reader)
            throws IOException {

        return readReader( reader, BaseConfig.BUFFER_SIZE, null );
    }

    /**
     * Read a stream in and return it as a string. This method will block until the stream is closed or reaches EOF.
     * This method will close both streams before it returns.
     *
     * @param reader
     *            The reader to get the data from.
     * @param bufferSize
     *            The size of the buffer to use for reading.
     * @param callback
     *            The callback object to notify each time a chunk of data was written.
     *
     * @return The stream's data as a string decoded with the default character set.
     *
     * @throws IOException
     */
    public static String readReader(Reader reader, int bufferSize, StreamCallback callback)
            throws IOException {

        return readReader( reader, bufferSize, callback, true );
    }

    /**
     * Read a stream in and return it as a string. This method will block until the stream is closed or reaches EOF.
     * This method will close the reader before it returns.
     *
     * @param reader
     *            The reader to get the data from.
     * @param bufferSize
     *            The size of the buffer to use for reading.
     * @param callback
     *            The callback object to notify each time a chunk of data was written.
     * @param autoclose
     *            Whether or not to close all streams involved automatically after completion.
     *
     * @return The stream's data as a string decoded with the default character set.
     *
     * @throws IOException
     */
    public static String readReader(Reader reader, int bufferSize, StreamCallback callback, boolean autoclose)
            throws IOException {

        StringWriter output = new StringWriter();
        char[] buffer = new char[bufferSize];

        try {
            int bytesWritten = 0;
            for (int bytesRead; (bytesRead = reader.read( buffer )) > 0;) {
                output.write( buffer, 0, bytesRead );
                bytesWritten += bytesRead;

                if (callback != null)
                    callback.wroteChunk( bytesWritten );
            }
        } finally {
            if (autoclose)
                reader.close();
        }

        return output.toString();
    }

    /**
     * Read a stream in and return it as a string. This method will block until the stream is closed. This method will
     * close both streams before it returns. It uses the default buffer size.
     *
     * @param stream
     *            The stream to get the data from.
     *
     * @return The stream's data as a string decoded with the default character set.
     *
     * @throws IOException
     *
     * @see BaseConfig#BUFFER_SIZE
     */
    public static byte[] readStream(InputStream stream)
            throws IOException {

        return readStream( stream, BaseConfig.BUFFER_SIZE, null );
    }

    /**
     * Read a stream in and return it as a string. This method will block until the stream is closed. This method will
     * close both streams before it returns.
     *
     * @param stream
     *            The stream to get the data from.
     * @param bufferSize
     *            The size of the buffer to use for reading.
     * @param callback
     *            The callback object to notify each time a chunk of data was written.
     *
     * @return The stream's data as a string decoded with the default character set.
     *
     * @throws IOException
     */
    public static byte[] readStream(InputStream stream, int bufferSize, StreamCallback callback)
            throws IOException {

        return readStream( stream, bufferSize, callback, true );
    }

    /**
     * Read a stream in and return it as a string. This method will block until the stream is closed.
     *
     * @param stream
     *            The reader to get the data from.
     * @param bufferSize
     *            The size of the buffer to use for reading.
     * @param callback
     *            The callback object to notify each time a chunk of data was written.
     * @param autoclose
     *            Whether or not to close all streams involved automatically after completion.
     *
     * @return The stream's data as a string decoded with the default character set.
     *
     * @throws IOException
     */
    public static byte[] readStream(InputStream stream, int bufferSize, StreamCallback callback, boolean autoclose)
            throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];

        try {
            int bytesWritten = 0;
            for (int bytesRead; (bytesRead = stream.read( buffer )) > 0;) {
                output.write( buffer, 0, bytesRead );
                bytesWritten += bytesRead;

                if (callback != null)
                    callback.wroteChunk( bytesWritten );
            }
        } finally {
            if (autoclose)
                stream.close();
        }

        return output.toByteArray();
    }

    /**
     * Read a stream in and write it to the given stream in a BUFFERED byte-safe manner. This method will block until
     * the input stream is closed. This method will close all streams before it returns. It uses the default buffer
     * size.
     *
     * @param in
     *            The stream to get the data from.
     * @param out
     *            The destination of the output stream.
     *
     * @throws IOException
     *
     * @see BaseConfig#BUFFER_SIZE
     */
    public static void pipeStream(InputStream in, OutputStream out)
            throws IOException {

        pipeStream( in, BaseConfig.BUFFER_SIZE, out, null );
    }

    /**
     * Read a stream in and write it to the given stream in a BUFFERED byte-safe manner. This method will block until
     * the input stream is closed. This method will close all streams before it returns.
     *
     * @param in
     *            The stream to get the data from.
     * @param bufferSize
     *            The size of the buffer to use for reading.
     * @param out
     *            The destination of the output stream.
     * @param callback
     *            The callback object to notify each time a chunk of data was written.
     * @throws IOException
     */
    public static void pipeStream(InputStream in, int bufferSize, OutputStream out, StreamCallback callback)
            throws IOException {

        pipeStream( in, bufferSize, out, callback, true );
    }

    /**
     * Read a stream in and write it to the given stream in a BUFFERED byte-safe manner. This method will block until
     * the input stream is closed.
     *
     * @param in
     *            The stream to get the data from.
     * @param bufferSize
     *            The size of the buffer to use for reading.
     * @param out
     *            The destination of the output stream.
     * @param callback
     *            The callback object to notify each time a chunk of data was written.
     * @param autoclose
     *            Whether or not to close all streams involved automatically after completion.
     *
     * @throws IOException
     */
    public static void pipeStream(InputStream in, int bufferSize, OutputStream out, StreamCallback callback,
                                  boolean autoclose)
            throws IOException {

        try {
            int bytesWritten = 0;
            byte[] buffer = new byte[bufferSize];
            for (int bytesRead; (bytesRead = in.read( buffer )) > 0;) {
                out.write( buffer, 0, bytesRead );

                if (callback != null) {
                    bytesWritten += bytesRead;
                    callback.wroteChunk( bytesWritten );
                }
            }
        } finally {
            if (autoclose) {
                in.close();
                out.close();
            }
        }
    }


    /**
     * A callback interface that will be called while data is being written to allow evaluating the writing progress.
     */
    public interface StreamCallback {

        /**
         * A chunk of data has just been written. The size of the chunk is no greater than the write buffer used.
         *
         * @param totalBytesWritten
         *            The total amount of bytes that have been written so far.
         */
        void wroteChunk(double totalBytesWritten);
    }


    /**
     * Extend the java library search path by adding the path of a library that will need to be loaded at some point in
     * the application's lifecycle.
     *
     * @param libName
     *            The name of the library that will be loaded.
     */
    public static void initNativeLibPath(String libName) {

        String libFileName = libName;
        if (System.getProperty( "os.name" ).matches( "Windows.*" ))
            libFileName = libName + ".dll";
        else if (System.getProperty( "os.name" ).matches( "Linux.*" ))
            libFileName = "lib" + libName + ".so";
        else if (System.getProperty( "os.name" ).matches( "Mac.*" ))
            libFileName = "lib" + libName + ".jnilib";
        else if (System.getProperty( "os.name" ).matches( "SunOS.*" ))

            if ("x86".equals( System.getProperty( "os.arch" ) ))
                libFileName = "lib" + libName + "_sun_x86.so";
            else
                libFileName = "lib" + libName + "_sun_sparc.so";
        else
            logger.wrn( "Unrecougnised OS: %s", System.getProperty( "os.name" ) );

        File libFile = res( "/lib/native/" + libFileName );
        if (libFile == null)
            logger.wrn( "Native library %s not supported for your OS (%s).", libName, System.getProperty( "os.name" ) );
        else
            System.setProperty( "java.library.path", System.getProperty( "java.library.path" ) + ':'
                                                     + libFile.getParent() );
    }

    /**
     * A sane way of retrieving an entry from a {@link ZipFile} based on its /-delimited pathname.
     *
     * @param zipFile
     *            The {@link ZipFile} to retrieve the entry for.
     * @param zippedName
     *            The /-delimited pathname of the entry.
     * @return The {@link ZipEntry} for the pathname or <code>null</code> if none was present.
     */
    public static ZipEntry getZipEntry(ZipFile zipFile, String zippedName) {

        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().replaceAll( "[\\\\/]+", "/" ).equals( zippedName.replaceAll( "[\\\\/]+", "/" ) ))
                return entry;
        }

        return null;
    }

    /**
     * Get the name of the field in the given owner that contains the given object.
     *
     * @param owner
     *            The object that contains the field.
     * @param fieldValue
     *            The current value of the field you want to get the name for.
     * @return The name of the field.
     */
    public static String getFieldName(Reflective owner, Object fieldValue) {

        for (Field field : owner.getClass().getDeclaredFields())
            try {
                Object value = owner.getFieldValue( field );
                if (fieldValue == value)
                    return field.getName();
            }

            catch (IllegalArgumentException ignored) {
            } catch (IllegalAccessException ignored) {
            }

        return null;
    }

    /**
     * Search for the given pattern in the given file. The search is line-based and does not take newlines into account,
     * nor does it have the ability to cross them. The group parameter specifies which capture group from the pattern to
     * return. Use 0 as group value to return the whole line that matched the pattern.<br>
     * <br>
     * If the given file is a directory, a recursive search will take place and a newline-separated list of matches will
     * be returned.
     *
     * @param pattern
     *            The pattern to search for.
     * @param file
     *            The file to search in.
     * @param group
     *            The group number in the pattern to return; or 0 to return the whole matching line.
     * @return The matching line or given group in the matching line.
     */
    public static String grep(String pattern, File file, int group) {

        return grep( Pattern.compile( pattern ), file, group );
    }

    /**
     * Search for the given pattern in the given file. The search is line-based and does not take newlines into account,
     * nor does it have the ability to cross them. The group parameter specifies which capture group from the pattern to
     * return. Use 0 as group value to return the whole line that matched the pattern.<br>
     * <br>
     * If the given file is a directory, a recursive search will take place and a newline-separated list of matches will
     * be returned.
     *
     * @param pattern
     *            The pattern to search for.
     * @param file
     *            The file to search in.
     * @param group
     *            The group number in the pattern to return; or 0 to return the whole matching line.
     * @return The matching line or given group in the matching line.
     */
    public static String grep(Pattern pattern, File file, int group) {

        StringBuilder resultBuilder = new StringBuilder();

        if (file.isDirectory())
            for (File child : file.listFiles())
                resultBuilder.append( resultBuilder.length() > 0? "\n": "").append( grep( pattern, child, group ));
        else if (file.isFile()) {
            try {
                String content = readReader( new FileReader( file ), 4096, null, true );

                for (String line : content.split( "\n" )) {
                    Matcher matcher = pattern.matcher( line );
                    if (matcher.find())
                        return matcher.group( group );
                }
            } catch (FileNotFoundException e) {
                logger.bug( e, "File '%s' not found even though we checked for its existance.", file );
            } catch (IOException e) {
                logger.err( e, "Couldn't read file '%s'!", file );
            }
        } else
            logger.wrn( "File %s was not found.  Could not grep it.", file );

        return resultBuilder.toString();
    }

    /**
     * Recursively iterate the given {@link Collection} and all {@link Collection}s within it. When an element is
     * encountered that equals the given {@link Object}, the method returns <code>true</code>.
     *
     * @param c
     *            The collection to iterate recursively.
     * @param o
     *            The object to look for in the collection.
     * @return <code>true</code> if the object was found anywhere within the collection or any of its sub-collections.
     */
    public static boolean recurseContains(Collection<?> c, Object o) {

        for (Object co : c)
            if (co.equals( o ))
                return true;
            else if (co instanceof Collection<?> && recurseContains( (Collection<?>) co, o ))
                return true;

        return false;
    }

    /**
     * Format suffix for the given calendar field.
     *
     * @param field
     *            {@link Calendar} field.
     * @return The suffix in the format specification of the given field.
     */
    public static String calendarSuffix(int field) {

        return calendarFormat.get( field ).replaceAll( "[a-zA-Z]", "" );
    }

    /**
     * Check whether the given array contains the given search object.
     *
     * @param array
     *            The array to search through.
     * @param search
     *            The object to search for in the array.
     * @return <code>true</code> if the search object was found in the array.
     */
    public static boolean inArray(Object[] array, Object search) {

        for (Object element : array)
            if (element == search || element != null && element.equals( search ))
                return true;

        return false;
    }

    /**
     * Compress the generic form of the method's signature. Trim off throws declarations.<br>
     * java.lang.method -> j~l~method
     *
     * @param signature
     *            The signature that needs to be compressed.
     * @return The compressed signature.
     */
    public static String compressSignature(String signature) {

        String compressed = signature.replaceAll( "(\\w)\\w{2,}\\.", "$1~" );
        return compressed.replaceFirst( " throws [^\\(\\)]*", "" );
    }

    /**
     * Trim all <code>trim</code> strings off of the <code>source</code> string, operating only on the left side.
     *
     * @param source
     *            The source object that needs to be converted to a string and trimmed.
     * @param trim
     *            The object that needs to be converted to a string and is what will be trimmed off.
     * @return The result of the trimming.
     */
    public static String ltrim(Object source, Object trim) {

        if (source == null || trim == null)
            return source == null? null: source.toString();

        String sourceString = source.toString();
        String trimString = trim.toString();

        while (sourceString.startsWith( trimString ) && sourceString.length() > trimString.length())
            sourceString = sourceString.substring( trimString.length() );

        return sourceString;
    }

    /**
     * Trim all <code>trim</code> strings off of the <code>source</code> string, operating only on the left side.
     *
     * @param source
     *            The source object that needs to be converted to a string and trimmed.
     * @param trim
     *            The object that needs to be converted to a string and is what will be trimmed off.
     * @return The result of the trimming.
     */
    public static String rtrim(Object source, Object trim) {

        if (source == null || trim == null)
            return source == null? null: source.toString();

        String sourceString = source.toString();
        String trimString = trim.toString();

        while (sourceString.endsWith( trimString ) && sourceString.length() > trimString.length())
            sourceString = sourceString.substring( 0, sourceString.length() - trimString.length() );

        return sourceString;
    }

    /**
     * Trim all <code>trim</code> strings off of the <code>source</code> string, operating on both sides.
     *
     * @param source
     *            The source object that needs to be converted to a string and trimmed.
     * @param trim
     *            The object that needs to be converted to a string and is what will be trimmed off.
     * @return The result of the trimming.
     */
    public static String trim(Object source, Object trim) {

        return rtrim( ltrim( source, trim ), trim );
    }

    /**
     * @param home
     *            The {@link URL} that needs to be converted to a short string version.
     * @return A concise representation of the URL showing only the root domain and final path of the path.
     */
    public static String shortUrl(URL home) {

        String shortHome = home.getHost().replaceFirst( "^.*?([^\\.]+\\.[^\\.]+)$", "$1" );
        String path = home.getPath().replaceFirst( "/+$", "" ).replaceFirst( "^.*/", "" );

        return shortHome + ':' + path;
    }
}
