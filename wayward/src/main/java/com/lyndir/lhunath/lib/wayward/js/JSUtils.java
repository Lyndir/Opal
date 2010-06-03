package com.lyndir.lhunath.lib.wayward.js;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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

    static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final Pattern IDENTIFIER = Pattern.compile( "^([\\w$&&[^\\d]][\\w$]*\\.)*[\\w$&&[^\\d]][\\w$]*$" );

    public static String callFunction(final String function, final Object... args) {

        checkNotNull( function, "Name of function to invoke must not be null." );
        checkArgument( IDENTIFIER.matcher( function ).matches(), "Name of function '%s' must be a valid JavaScript identifier name.",
                       function );

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
}
