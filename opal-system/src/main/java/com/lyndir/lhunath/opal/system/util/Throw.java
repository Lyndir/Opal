package com.lyndir.lhunath.opal.system.util;

/**
 * <i>05 25, 2011</i>
 *
 * @author lhunath
 */
public class Throw {

    public static RuntimeException propagate(final Throwable t, final String message, final Object... arguments) {

        return new RuntimeException( message == null || arguments.length == 0? message: String.format( message, arguments ), t );
    }
}
