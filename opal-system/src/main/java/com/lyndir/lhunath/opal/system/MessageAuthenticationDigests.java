package com.lyndir.lhunath.opal.system;

import com.google.common.io.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/**
 * <i>05 31, 2011</i>
 *
 * @author lhunath
 */
public enum MessageAuthenticationDigests {
    HmacMD5( "HmacMD5" ),
    HmacSHA1( "HmacSHA1" ),
    HmacSHA256( "HmacSHA256" ),
    HmacSHA384( "HmacSHA384" ),
    HmacSHA512( "HmacSHA512" );

    static final Logger logger = Logger.get( MessageAuthenticationDigests.class );

    private final String jcaName;

    MessageAuthenticationDigests(final String jcaName) {

        this.jcaName = jcaName;
    }

    public Mac get() {

        try {
            return Mac.getInstance( getJCAName() );
        }
        catch (NoSuchAlgorithmException e) {
            throw logger.bug( e );
        }
    }

    public String getJCAName() {

        return jcaName;
    }

    public byte[] of(final byte[] key, final byte[] bytes) {

        return of( key, IOUtils.supply( bytes ) );
    }

    public byte[] of( final byte[] key, final InputSupplier<? extends InputStream> supplier) {

        try {
            final Mac mac = get();
            mac.init( new SecretKeySpec( key, mac.getAlgorithm() ) );

            return ByteStreams.readBytes( supplier, new ByteProcessor<byte[]>() {
                @Override
                public boolean processBytes(final byte[] buf, final int off, final int len) {

                    mac.update( buf, off, len );
                    return true;
                }

                @Override
                public byte[] getResult() {

                    return mac.doFinal();
                }
            } );
        }
        catch (IOException e) {
            throw logger.bug( e );
        }
        catch (InvalidKeyException e) {
            throw logger.bug( e );
        }
    }

    public byte[] of(final byte[] key, final InputStream stream) {

        return of( key, IOUtils.supply( stream ) );
    }
}
