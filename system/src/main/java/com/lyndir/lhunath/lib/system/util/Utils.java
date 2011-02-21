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

import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.lyndir.lhunath.lib.system.logging.Logger;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * <i>Utils - A big collection of convenience methods.</i><br> <br> My toolbox.<br> <br>
 *
 * @author lhunath
 */
public class Utils {

    private static final Logger logger = Logger.get( Utils.class );

    /**
     * Calendar fields in order.
     */
    public static final int[] calendarFields = {
            Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH, Calendar.MONTH,
            Calendar.YEAR };

    /**
     * Description of the calendar fields.
     */
    public static final Map<Integer, String> calendarDesc = new HashMap<Integer, String>() {

        private static final long serialVersionUID = 1L;

        {
            put( Calendar.MILLISECOND, "Millisecond" );
            put( Calendar.SECOND, "Second" );
            put( Calendar.MINUTE, "Minute" );
            put( Calendar.HOUR_OF_DAY, "Hour" );
            put( Calendar.DAY_OF_MONTH, "Day" );
            put( Calendar.MONTH, "Month" );
            put( Calendar.YEAR, "Year" );
        }};

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
        }};

    /**
     * Ratio of the long part of the golden section.
     */
    public static final double GOLDEN = 0.618;

    /**
     * Inverted ratio of the long part of the golden section.
     */
    public static final double GOLDEN_INV = 1 / GOLDEN;
    private static final Pattern FIRST_LETTER = Pattern.compile( "(\\w)\\w{2,}\\." );
    private static final Pattern THROWS = Pattern.compile( " throws [^\\(\\)]*" );
    private static final Pattern TLD = Pattern.compile( "^.*?([^\\.]+\\.[^\\.]+)$" );
    private static final Pattern TRAILING_SLASHES = Pattern.compile( "/+$" );
    private static final Pattern NON_FINAL_PATH = Pattern.compile( "^.*/" );
    private static final Pattern LETTERS = Pattern.compile( "[a-zA-Z]" );
    private static final Pattern PATH_SEPARATORS = Pattern.compile( "[\\\\/]+" );
    private static final Pattern PROTOCOL = Pattern.compile( "^[^:]+:" );
    private static final Pattern WINDOWS = Pattern.compile( "Windows.*" );
    private static final Pattern LINUX = Pattern.compile( "Linux.*" );
    private static final Pattern MACOS = Pattern.compile( "Mac.*" );
    private static final Pattern SUNOS = Pattern.compile( "SunOS.*" );

    /**
     * Convert the given object into a {@link String} using {@link Object#toString()}. If the <code>null</code> is passed, don't return a
     * string "null", but return <code>null</code> explicitly.
     *
     * @param object The object to convert into a string.
     *
     * @return The string representation of the object.
     */
    public static String toString(final Object object) {

        if (object == null)
            return null;

        if (object instanceof String)
            return (String) object;

        return object.toString();
    }

    /**
     * Calculate the MD5 hash for the given file.
     *
     * @param file The file to calculate the sum for.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getMD5(final File file) {

        return getDigest( file, Digest.MD5 );
    }

    /**
     * Calculate a digest hash for the given string.
     *
     * @param data The data to calculate the sum for.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getMD5(final String data) {

        return getDigest( new ByteArrayInputStream( data.getBytes() ), Digest.MD5 );
    }

    /**
     * Calculate a digest hash for the given bytes.
     *
     * @param data The data to calculate the sum for.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getMD5(final byte[] data) {

        return getDigest( new ByteArrayInputStream( data ), Digest.MD5 );
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param file       The file to calculate the sum for.
     * @param digestType The digest to calculate.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getDigest(final File file, final Digest digestType) {

        InputStream fileStream = null, bufferedFileStream = null;
        try {
            fileStream = new FileInputStream( file );
            bufferedFileStream = new BufferedInputStream( fileStream );
            return getDigest( bufferedFileStream, digestType );
        }
        catch (FileNotFoundException e) {
            logger.err( e, "File %s does not exist.  Can't calculate %s", file, digestType.getName() );
            return null;
        }
        finally {
            Closeables.closeQuietly( fileStream );
            Closeables.closeQuietly( bufferedFileStream );
        }
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param data       The data to calculate the sum for.
     * @param digestType The digest to calculate.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getDigest(final String data, final Digest digestType) {

        return getDigest( new ByteArrayInputStream( data.getBytes() ), digestType );
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param data       The data to calculate the sum for.
     * @param digestType The digest to calculate.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getDigest(final byte[] data, final Digest digestType) {

        return getDigest( new ByteArrayInputStream( data ), digestType );
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param in         The stream to read the data from needed to calculate the sum.
     * @param digestType The digest to calculate.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    public static String getDigest(final InputStream in, final Digest digestType) {

        try {
            MessageDigest digest = null;
            if (digestType != Digest.CRC32)
                digest = MessageDigest.getInstance( digestType.getName() );

            byte[] buffer = new byte[256];

            Checksum checksum = new CRC32();
            for (int len; (len = in.read( buffer )) >= 0;)
                if (digest == null)
                    checksum.update( buffer, 0, len );
                else
                    digest.update( buffer, 0, len );

            StringBuilder digestHex = new StringBuilder();
            if (digest == null)
                digestHex.append( String.format( "%08x", checksum.getValue() ) );
            else
                for (final byte b : digest.digest())
                    digestHex.append( String.format( "%02x", b ) );

            return digestHex.toString();
        }
        catch (NoSuchAlgorithmException e) {
            logger.err( e, "%s is unsupported!", digestType.getName() );
        }
        catch (IOException e) {
            logger.err( e, "Couldn't read file to digest!" );
        }

        return null;
    }

    /**
     * Digests that can be calculated with the {@link Utils#getDigest(File, Digest)} method.
     */
    public enum Digest {

        /**
         * The CRC-32 message digest algorithm.
         */
        CRC32,

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
         * Hash algorithms defined in the FIPS PUB 180-2.<br> SHA-256 is a 256-bit hash function intended to provide 128 bits of security
         * against collision attacks.
         */
        SHA256,

        /**
         * Hash algorithms defined in the FIPS PUB 180-2.<br> A 384-bit hash may be obtained by truncating the SHA-512 output.
         */
        SHA384,

        /**
         * Hash algorithms defined in the FIPS PUB 180-2.<br> SHA-512 is a 512-bit hash function intended to provide 256 bits of security.
         */
        SHA512;

        /**
         * Return the official name for this digest.
         *
         * @return Guess.
         */
        public String getName() {

            return name().replace( '_', '-' );
        }

        /**
         * Try to guess which type of hash the given digest is. This works fairly flawlessly for every supported hash except for {@link
         * Digest#MD2}; since its hash string is not exclusively different from that of {@link Digest#MD5}. {@link Digest#MD5} will be the
         * result of this function in this case.
         *
         * @param digest The hexadecimal-formatted hash string to estimate the type of.
         *
         * @return The guessed digest type.
         *
         * @throws IllegalArgumentException If the digest could not be guessed.
         */
        public static Digest guessType(final String digest)
                throws IllegalArgumentException {

            switch (digest.length()) {
                case 8:
                    return CRC32;
                case 32:
                    return MD5;
                case 40:
                    return SHA1;
                case 64:
                    return SHA256;
                case 96:
                    return SHA384;
                case 512:
                    return SHA512;
                default:
                    throw logger.err( "The digest in the argument is cannot be recognized: %s", digest )
                            .toError( IllegalArgumentException.class );
            }
        }
    }

    /**
     * Check whether the given string is in a valid URL format.
     *
     * @param url The string that could represent a URL.
     *
     * @return Guess.
     */
    public static boolean isUrl(final String url) {

        try {
            @SuppressWarnings({ "unused", "UnusedAssignment" })
            URL unused = new URL( url );
            return true;
        }
        catch (MalformedURLException ignored) {
            return false;
        }
    }

    /**
     * Convenience method of building a URL without the annoying exception thing.
     *
     * @param url The URL string.
     *
     * @return The URL string in a URL object.
     */
    public static URL toUrl(final String url) {

        try {
            return url == null || url.length() == 0? null: new URL( url );
        }
        catch (MalformedURLException e) {
            logger.err( e, "Malformed URL: '%s'!", url );
            return null;
        }
    }

    /**
     * Extend the java library search path by adding the path of a library that will need to be loaded at some point in the application's
     * life cycle.
     *
     * @param libName The name of the library that will be loaded.
     */
    public static void initNativeLibPath(final String libName) {

        String libFileName = libName;
        if (WINDOWS.matcher( System.getProperty( "os.name" ) ).matches())
            libFileName = libName + ".dll";
        else if (LINUX.matcher( System.getProperty( "os.name" ) ).matches())
            libFileName = "lib" + libName + ".so";
        else if (MACOS.matcher( System.getProperty( "os.name" ) ).matches())
            libFileName = "lib" + libName + ".jnilib";
        else if (SUNOS.matcher( System.getProperty( "os.name" ) ).matches())

            if ("x86".equals( System.getProperty( "os.arch" ) ))
                libFileName = "lib" + libName + "_sun_x86.so";
            else
                libFileName = "lib" + libName + "_sun_sparc.so";
        else
            logger.wrn( "Unrecognised OS: %s", System.getProperty( "os.name" ) );

        File libFile = new File( "lib/native/" + libFileName );
        if (!libFile.exists())
            logger.wrn( "Native library %s not supported for your OS (%s).", libName, System.getProperty( "os.name" ) );
        else
            System.setProperty( "java.library.path", System.getProperty( "java.library.path" ) + ':' + libFile.getParent() );
    }

    /**
     * A sane way of retrieving an entry from a {@link ZipFile} based on its /-delimited path name.
     *
     * @param zipFile    The {@link ZipFile} to retrieve the entry for.
     * @param zippedName The /-delimited pathname of the entry.
     *
     * @return The {@link ZipEntry} for the pathname or <code>null</code> if none was present.
     */
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
     * Get the name of the field in the given owner that contains the given object.
     *
     * @param owner      The object that contains the field.
     * @param fieldValue The current value of the field you want to get the name for.
     *
     * @return The name of the field.
     */
    public static String getFieldName(final Object owner, final Object fieldValue) {

        for (final Field field : owner.getClass().getDeclaredFields())
            try {
                field.setAccessible( true );
                Object value = field.get( owner );
                if (ObjectUtils.isEqual( fieldValue, value ))
                    return field.getName();
            }

            catch (IllegalArgumentException ignored) {
            }
            catch (IllegalAccessException ignored) {
            }

        return null;
    }

    /**
     * Search for the given pattern in the given file. The search is line-based and does not take newlines into account, nor does it have
     * the ability to cross them. The group parameter specifies which capture group from the pattern to return. Use 0 as group value to
     * return the whole line that matched the pattern.<br> <br> If the given file is a directory, a recursive search will take place and a
     * newline-separated list of matches will be returned.
     *
     * @param pattern The pattern to search for.
     * @param file    The file to search in.
     * @param group   The group number in the pattern to return; or 0 to return the whole matching line.
     *
     * @return The matching line or given group in the matching line.
     */
    public static String grep(final String pattern, final File file, final int group) {

        return grep( Pattern.compile( pattern ), file, group );
    }

    /**
     * Search for the given pattern in the given file. The search is line-based and does not take newlines into account, nor does it have
     * the ability to cross them. The group parameter specifies which capture group from the pattern to return. Use 0 as group value to
     * return the whole line that matched the pattern.<br> <br> If the given file is a directory, a recursive search will take place and a
     * newline-separated list of matches will be returned.
     *
     * @param pattern The pattern to search for.
     * @param file    The file to search in.
     * @param group   The group number in the pattern to return; or 0 to return the whole matching line.
     *
     * @return The matching line or given group in the matching line.
     */
    public static String grep(final Pattern pattern, final File file, final int group) {

        StringBuilder resultBuilder = new StringBuilder();

        if (file.isDirectory())
            for (final File child : file.listFiles())
                resultBuilder.append( resultBuilder.length() > 0? "\n": "" ).append( grep( pattern, child, group ) );
        else if (file.isFile())
            try {
                FileReader fileReader = new FileReader( file );
                try {
                    for (final String line : CharStreams.readLines( fileReader )) {
                        Matcher matcher = pattern.matcher( line );
                        if (matcher.find())
                            return matcher.group( group );
                    }
                }
                finally {
                    Closeables.closeQuietly( fileReader );
                }
            }
            catch (FileNotFoundException e) {
                logger.bug( e, "File '%s' not found even though we checked for its existence.", file );
            }
            catch (IOException e) {
                logger.err( e, "Couldn't read file '%s'!", file );
            }
        else
            logger.wrn( "File %s was not found.  Could not grep it.", file );

        return resultBuilder.toString();
    }

    /**
     * Recursively iterate the given {@link Collection} and all {@link Collection}s within it. When an element is encountered that equals
     * the given {@link Object}, the method returns <code>true</code>.
     *
     * @param collection The collection to iterate recursively.
     * @param o          The object to look for in the collection.
     *
     * @return <code>true</code> if the object was found anywhere within the collection or any of its sub-collections.
     */
    public static boolean recurseContains(final Iterable<?> collection, final Object o) {

        for (final Object co : collection)
            if (co.equals( o ))
                return true;
            else if (co instanceof Collection<?> && recurseContains( (Iterable<?>) co, o ))
                return true;

        return false;
    }

    /**
     * Format suffix for the given calendar field.
     *
     * @param field {@link Calendar} field.
     *
     * @return The suffix in the format specification of the given field.
     */
    public static String calendarSuffix(final int field) {

        return LETTERS.matcher( calendarFormat.get( field ) ).replaceAll( "" );
    }

    /**
     * Check whether the given array contains the given search object.
     *
     * @param array  The array to search through.
     * @param search The object to search for in the array.
     *
     * @return <code>true</code> if the search object was found in the array.
     */
    public static <T, U extends T> boolean inArray(final T[] array, final U search) {

        for (final Object element : array)
            if (ObjectUtils.isEqual( search, element ))
                return true;

        return false;
    }

    /**
     * Compress the generic form of the method's signature. Trim off throws declarations.<br> java.lang.method -> j~l~method
     *
     * @param signature The signature that needs to be compressed.
     *
     * @return The compressed signature.
     */
    public static String compressSignature(final CharSequence signature) {

        String compressed = FIRST_LETTER.matcher( signature ).replaceAll( "$1~" );
        return THROWS.matcher( compressed ).replaceFirst( "" );
    }

    /**
     * Trim all <code>trim</code> strings off of the <code>source</code> string, operating only on the left side.
     *
     * @param source The source object that needs to be converted to a string and trimmed.
     * @param trim   The object that needs to be converted to a string and is what will be trimmed off.
     *
     * @return The result of the trimming.
     */
    public static String ltrim(final Object source, final Object trim) {

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
     * @param source The source object that needs to be converted to a string and trimmed.
     * @param trim   The object that needs to be converted to a string and is what will be trimmed off.
     *
     * @return The result of the trimming.
     */
    public static String rtrim(final Object source, final Object trim) {

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
     * @param source The source object that needs to be converted to a string and trimmed.
     * @param trim   The object that needs to be converted to a string and is what will be trimmed off.
     *
     * @return The result of the trimming.
     */
    public static String trim(final Object source, final Object trim) {

        return rtrim( ltrim( source, trim ), trim );
    }

    /**
     * @param home The {@link URL} that needs to be converted to a short string version.
     *
     * @return A concise representation of the URL showing only the root domain and final part of the path.
     */
    public static String shortUrl(final URL home) {

        String shortHome = TLD.matcher( home.getHost() ).replaceFirst( "$1" );
        String path = NON_FINAL_PATH.matcher( TRAILING_SLASHES.matcher( home.getPath() ).replaceFirst( "" ) ).replaceFirst( "" );

        return shortHome + ':' + path;
    }
}
