package com.lyndir.lhunath.lib.system.util;

import com.lyndir.lhunath.lib.system.logging.Logger;
import java.net.MalformedURLException;
import java.net.URL;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link ConversionUtils}<br> <sub>[in short] (TODO).</sub></h2>
 * <p/>
 * <p> <i>02 10, 2011</i> </p>
 *
 * @author lhunath
 */
public class ConversionUtils {

    static final Logger logger = Logger.get( ConversionUtils.class );

    /**
     * Convert an object into a long in a semi-safe way. We parse {@link Object#toString()} and choose to return <code>null</code> rather
     * than throw an exception if the result is not a valid {@link Long}.
     *
     * @param object The object that may represent an long.
     *
     * @return The resulting integer.
     */
    @Nullable
    public static Long toLong(final Object object) {

        if (object == null || object instanceof Long)
            return (Long) object;

        try {
            return Long.valueOf( object.toString() );
        }
        catch (NumberFormatException e) {
            logger.err( e, "Malformed long: %s", object );
            return null;
        }
    }

    /**
     * Convert an object into an integer in a semi-safe way. We parse {@link Object#toString()} and choose to return <code>null</code>
     * rather than throw an exception if the result is not a valid {@link Integer}.
     *
     * @param object The object that may represent an integer.
     *
     * @return The resulting integer.
     */
    @Nullable
    public static Integer toInteger(final Object object) {

        if (object == null || object instanceof Integer)
            return (Integer) object;

        try {
            return Integer.valueOf( object.toString() );
        }
        catch (NumberFormatException e) {
            logger.err( e, "Malformed integer: %s", object );
            return null;
        }
    }

    /**
     * Convert an object into a double in a semi-safe way. We parse {@link Object#toString()} and choose to return <code>null</code> rather
     * than throw an exception if the result is not a valid {@link Double}.
     *
     * @param object The object that may represent a double.
     *
     * @return The resulting double.
     */
    @Nullable
    public static Double toDouble(final Object object) {

        if (object == null || object instanceof Double)
            return (Double) object;

        try {
            return Double.parseDouble( object.toString() );
        }
        catch (NumberFormatException e) {
            logger.err( e, "Malformed double: %s", object );
            return null;
        }
    }

    /**
     * Convert an object into a boolean in a safe way. If the given object is not <code>null</code> or a {@link Boolean}, we parse its
     * {@link Object#toString()} using {@link Boolean#parseBoolean(String)}.
     *
     * @param object The object that represents a boolean.
     *
     * @return The resulting boolean.
     */
    @Nullable
    public static Boolean toBoolean(final Object object) {

        if (object == null || object instanceof Boolean)
            return (Boolean) object;

        return Boolean.parseBoolean( object.toString() );
    }

    /**
     * Convert an object into a string in a safe way. If the given object is not <code>null</code> or a {@link String}, we use its {@link
     * Object#toString()}.
     *
     * @param object The object to convert into a string.
     *
     * @return The resulting string.
     */
    @Nullable
    public static String toString(final Object object) {

        if (object == null || object instanceof String)
            return (String) object;

        return object.toString();
    }

    /**
     * Convenience method of building a URL without the annoying exception thing.
     *
     * @param url The URL string.
     *
     * @return The URL string in a URL object.
     */
    @Nullable
    public static URL toUrl(final String url) {

        try {
            return url == null || url.length() == 0? null: new URL( url );
        }
        catch (MalformedURLException e) {
            logger.err( e, "Malformed URL: %s", url );
            return null;
        }
    }
}
