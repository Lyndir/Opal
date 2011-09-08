package com.lyndir.lhunath.opal.wayward.js;

import static com.google.common.base.Preconditions.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.regex.Pattern;


/**
 * <h2>{@link JSUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 31, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class JSUtils {

    static final         Gson    GSON       = new GsonBuilder().serializeNulls().create();
    private static final Pattern IDENTIFIER = Pattern.compile( "^([\\w$&&[^\\d]][\\w$]*\\.)*[\\w$&&[^\\d]][\\w$]*$" );

    public static String callFunction(final String function, final Object... args) {

        checkNotNull( function, "Name of function to invoke must not be null." );
        checkArgument(
                IDENTIFIER.matcher( function ).matches(), "Name of function '%s' must be a valid JavaScript identifier name.", function );

        StringBuilder jsBuilder = new StringBuilder( 256 );

        jsBuilder.append( function ).append( '(' );
        for (int i = 0; i < args.length; i++) {
            if (i != 0)
                jsBuilder.append( ',' );
            GSON.toJson( args[i], jsBuilder );
        }
        jsBuilder.append( ')' ).append( ';' );

        return jsBuilder.toString();
    }

    /**
     * @param o The object whose string representation should be converted for injection into JavaScript code.
     *
     * @return A JavaScript-quoted literal string.
     */
    public static String toString(final Object o) {

        return GSON.toJson( o );
    }

    public static String format(final String format, final Object... args) {

        Object[] jsArgs = new Object[args.length];
        for (int a = 0; a < args.length; ++a)
            jsArgs[a] = toString( args[a] );

        return String.format( format, jsArgs );
    }
}
