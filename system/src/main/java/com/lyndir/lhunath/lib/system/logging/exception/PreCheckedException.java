package com.lyndir.lhunath.lib.system.logging.exception;

/**
 * <h2>{@link PreCheckedException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 07, 2010</i> </p>
 *
 * @author lhunath
 */
public class PreCheckedException extends InternalInconsistencyException {

    public PreCheckedException(final Throwable cause) {

        super( "A check exists designed to prevent this exception.", cause );
    }

    public PreCheckedException(final String message, final Throwable cause) {

        super( message, cause );
    }
}
