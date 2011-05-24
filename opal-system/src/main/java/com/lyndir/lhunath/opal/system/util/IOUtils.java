package com.lyndir.lhunath.opal.system.util;

import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.*;
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

    /**
     * Calculate the MD5 hash for the given file.
     *
     * @param file The file to calculate the sum for.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    @Nullable
    public static String findMD5(final File file) {

        return findDigest( file, Digest.MD5 );
    }

    /**
     * Calculate a digest hash for the given file.
     *
     * @param file       The file to calculate the sum for.
     * @param digestType The digest to calculate.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    @Nullable
    public static String findDigest(final File file, final Digest digestType) {

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
     * @param in         The stream to read the data from needed to calculate the sum.
     * @param digestType The digest to calculate.
     *
     * @return The hash as a string of hexadecimal characters.
     */
    @Nullable
    public static String getDigest(final InputStream in, final Digest digestType) {

        try {
            MessageDigest digest = null;
            if (digestType != Digest.CRC32)
                digest = MessageDigest.getInstance( digestType.getName() );

            byte[] buffer = new byte[256];

            Checksum checksum = new CRC32();
            for (int len; (len = in.read( buffer )) >= 0; )
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

    /**
     * Digests that can be calculated with the {@link IOUtils#findDigest(File, Digest)} method.
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
                    throw new IllegalArgumentException( "The digest in the argument is cannot be recognized: " + digest );
            }
        }
    }
}
