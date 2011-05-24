package com.lyndir.lhunath.lib.wayward.navigation;

/**
 * <h2>{@link IncompatibleStateException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 24, 2010</i> </p>
 *
 * @author lhunath
 */
public class IncompatibleStateException extends Exception {

    /**
     * Use this to indicate an illegal attempt to load invalid state.  This constructor should only be used if this condition indicates a
     * bug in the program: The fact that incompatible state is being loaded is not being checked by an application-specific exception.
     */
    public IncompatibleStateException() {

        super( "BUG: Tried to load a state object that is incompatible with the current state; however, this incompatibility is not checked by a specific exception." );
    }

    /**
     * Use this to indicate non-critical invalid state.  The user has taken an action that cannot be serviced; the exception you provide
     * here should explain what really went wrong to the user.
     *
     * @param cause The cause describing what really went wrong.
     */
    public IncompatibleStateException(final Exception cause) {

        super( cause );
    }

    @Override
    public String getMessage() {

        if (getCause() != null)
            return getCause().getMessage();

        return super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {

        if (getCause() != null)
            return getCause().getLocalizedMessage();

        return super.getLocalizedMessage();
    }
}
