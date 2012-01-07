package com.lyndir.lhunath.opal.system;

import com.google.common.io.ByteStreams;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.IOUtils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Formatter;
import org.jetbrains.annotations.Nullable;


/**
 * <i>09 05, 2011</i>
 *
 * @author lhunath
 */
public abstract class CodeUtils {

    static final Logger logger = Logger.get( CodeUtils.class );

    public static byte[] digest(final MessageDigests digest, final String input, final Charset charset) {

        return digest( digest.get(), input, charset );
    }

    public static byte[] digest(final MessageDigest digest, final String input, final Charset charset) {

        return digest( digest, input.getBytes( charset ) );
    }

    public static byte[] digest(final MessageDigests digest, final byte[] input) {

        return digest( digest.get(), input );
    }

    public static byte[] digest(final MessageDigest digest, final byte[] input) {

        try {
            return ByteStreams.getDigest( IOUtils.supply( input ), digest );
        }
        catch (IOException e) {
            throw logger.bug( e );
        }
    }

    public static String hex(@Nullable final byte[] data) {

        return hex( data, false );
    }

    public static String hex(@Nullable final byte[] data, final boolean pretty) {

        StringBuilder bytes = new StringBuilder( data == null? 0: (data.length + (pretty? 1: 0)) * 2 );
        Formatter formatter = new Formatter( bytes );
        String format = String.format( "%%02X%s", pretty? ":": "" );

        if (data != null)
            for (final byte b : data) {
                formatter.format( format, b );
            }
        if (pretty && bytes.length() > 0)
            bytes.deleteCharAt( bytes.length() - 1 );

        return bytes.toString();
    }

    public static byte[] unhex(@Nullable final String hexString) {

        if (hexString == null)
            return new byte[0];

        byte[] deviceToken = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2)
            deviceToken[i / 2] = Integer.valueOf( hexString.substring( i, i + 2 ), 16 ).byteValue();

        return deviceToken;
    }
}
