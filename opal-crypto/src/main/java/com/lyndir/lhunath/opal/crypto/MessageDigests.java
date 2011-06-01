package com.lyndir.lhunath.opal.crypto;

import com.lyndir.lhunath.opal.system.logging.Logger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * <i>05 31, 2011</i>
 *
 * @author lhunath
 */
public enum MessageDigests {
    MD2,
    MD5,
    SHA1,
    SHA256,
    SHA386,
    SHA512;

    static final Logger logger = Logger.get( MessageDigests.class );

    public MessageDigest get() {

        try {
            return MessageDigest.getInstance( name() );
        }
        catch (NoSuchAlgorithmException e) {
            throw logger.bug( e );
        }
    }
}
