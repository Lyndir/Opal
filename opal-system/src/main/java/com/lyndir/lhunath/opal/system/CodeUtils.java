package com.lyndir.lhunath.opal.system;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Formatter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>09 05, 2011</i>
 *
 * @author lhunath
 */
public abstract class CodeUtils {

    static final Logger logger = Logger.get( CodeUtils.class );

    @Nullable
    public static byte[] digest(final MessageDigests digest, @Nullable final String input, final Charset charset) {

        return digest( digest.get(), input, charset );
    }

    @Nullable
    public static byte[] digest(final MessageDigest digest, @Nullable final String input, final Charset charset) {

        return (input == null)? null: digest( digest, input.getBytes( charset ) );
    }

    @Nullable
    public static byte[] digest(final MessageDigests digest, @Nullable final byte[] input) {

        return digest( digest.get(), input );
    }

    @Nullable
    public static byte[] digest(final MessageDigest digest, @Nullable final byte[] input) {

        return (input == null)? null: digest.digest( input );
    }

    @Nullable
    public static String encodeHex(@Nullable final byte[] data) {

        return encodeHex( data, false );
    }

    @Nullable
    public static String encodeHex(@Nullable final byte[] data, final boolean pretty) {

        if (data == null)
            return null;

        StringBuilder bytes = new StringBuilder( (data.length + (pretty? 1: 0)) * 2 );
        try (Formatter formatter = new Formatter( bytes )) {
            String format = String.format( "%%02X%s", pretty? ":": "" );

                for (final byte b : data)
                    formatter.format( format, b );
        }
        if (pretty && (bytes.length() > 0))
            bytes.deleteCharAt( bytes.length() - 1 );

        return bytes.toString();
    }

    @Nullable
    public static byte[] decodeHex(@Nullable final String hexString) {

        if (hexString == null)
            return null;

        byte[] deviceToken = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2)
            deviceToken[i / 2] = Integer.valueOf( hexString.substring( i, i + 2 ), 16 ).byteValue();

        return deviceToken;
    }

    /**
     * Encode URL arguments into a URL template.  Arguments are injected after UTF-8 based URL encoding.
     *
     * @param urlFormat      The URL template to inject the arguments into.  {@code {}} gets replaced by its respective argument.
     * @param urlParam1      The first URL template argument.
     * @param otherURLParams More URL template arguments, if needed.
     *
     * @return A URL that is the result of injecting the arguments into the URL template.
     */
    @Nonnull
    public static URL encodeURL(final String urlFormat, final String urlParam1, final String... otherURLParams) {

        StringBuilder url = new StringBuilder();
        int lastURLFormatOffset = 0, urlFormatIndex = 0;
        for (int urlFormatOffset = 0; //
             (urlFormatOffset = urlFormat.indexOf( "{}", urlFormatOffset )) != -1; //
             lastURLFormatOffset = urlFormatOffset += 2, ++urlFormatIndex) {

            url.append( urlFormat, lastURLFormatOffset, urlFormatOffset );

            if (urlFormatIndex == 0) {
                url.append( encodeURL( urlParam1 ) );
            } else {
                Preconditions.checkElementIndex( urlFormatIndex - 1, otherURLParams.length, "Not enough URL encoding parameters given." );
                url.append( encodeURL( otherURLParams[urlFormatIndex - 1] ) );
            }
        }

        try {
            return new URL( url.toString() );
        }
        catch (final MalformedURLException e) {
            logger.err( e, "The URL template does not appear to specify a valid URL: %s", urlFormat );
            throw new IllegalArgumentException( e );
        }
    }

    @Nonnull
    public static String encodeURL(final String plainString) {

        return encodeURL( plainString, Charsets.UTF_8 );
    }

    @Nonnull
    private static String encodeURL(final String plainString, final Charset encoding) {

        try {
            return URLEncoder.encode( plainString, encoding.displayName() );
        }
        catch (final UnsupportedEncodingException e) {
            throw logger.bug( e, "Given encoding not supported: %s", encoding );
        }
    }
}
