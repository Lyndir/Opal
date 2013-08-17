package com.lyndir.lhunath.opal.system.error;

/**
 * <h2>{@link AlreadyCheckedException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 07, 2010</i> </p>
 *
 * @author lhunath
 */
@SuppressWarnings({ "UncheckedExceptionClass" })
public class AlreadyCheckedException extends InternalInconsistencyException {

    public AlreadyCheckedException() {

        super( "BUG: A previous check designed to prevent this exception should exist but must have failed." );
    }

    public AlreadyCheckedException(final String message) {

        super( message );
    }

    public AlreadyCheckedException(final Throwable cause) {

        super( "BUG: A previous check designed to prevent this exception should exist but must have failed.", cause );
    }

    public AlreadyCheckedException(final String message, final Throwable cause) {

        super( message, cause );
    }
}
