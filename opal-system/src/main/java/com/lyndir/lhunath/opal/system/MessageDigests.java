package com.lyndir.lhunath.opal.system;

import com.google.common.io.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * <i>05 31, 2011</i>
 *
 * @author lhunath
 */
public enum MessageDigests {
    MD2( "MD2" ),
    MD5( "MD5" ),
    SHA1( "SHA-1" ),
    SHA256( "SHA-256" ),
    SHA384( "SHA-384" ),
    SHA512( "SHA-512" );

    static final Logger logger = Logger.get( MessageDigests.class );

    private final String jcaName;

    MessageDigests(final String jcaName) {

        this.jcaName = jcaName;
    }

    public MessageDigest get() {

        try {
            return MessageDigest.getInstance( getJCAName() );
        }
        catch (final NoSuchAlgorithmException e) {
            throw logger.bug( e );
        }
    }

    public String getJCAName() {

        return jcaName;
    }

    public byte[] of(final byte[] bytes) {

        return of( IOUtils.supply( bytes ) );
    }

    public byte[] of(final ByteSource supplier) {

        try {
            return get().digest( supplier.read() );
        }
        catch (final IOException e) {
            throw logger.bug( e );
        }
    }

    public byte[] of(final InputStream stream) {

        try {
            return get().digest( ByteStreams.toByteArray( stream ) );
        }
        catch (final IOException e) {
            throw logger.bug( e );
        }
    }
}
