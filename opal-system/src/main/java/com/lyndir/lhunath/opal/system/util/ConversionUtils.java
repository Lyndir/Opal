package com.lyndir.lhunath.opal.system.util;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Optional;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


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
     * Convert an object into a long in a semi-safe way. We parse {@link Object#toString()} and return {@code null} rather than throw an
     * exception if the result is not a valid {@link Long}.
     *
     * @param object The object that may represent an long.
     *
     * @return The resulting integer.
     */
    public static Optional<Long> toLong(@Nullable final Object object) {

        if (object == null || object instanceof Long)
            return Optional.fromNullable( (Long) object );

        try {
            return Optional.of( Long.valueOf( object.toString() ) );
        }
        catch (NumberFormatException e) {
            logger.wrn( e, "Malformed long: %s", object );
            return Optional.absent();
        }
    }

    /**
     * Convert an object into a long in a semi-safe way. We parse {@link Object#toString()} and return 0 rather than throw an exception if
     * the result is not a valid {@link Long}.
     *
     * @param object The object that may represent an long.
     *
     * @return The resulting integer.
     */
    public static long toLongNN(@Nullable final Object object) {

        if (object == null)
            return 0;
        if (object instanceof Long)
            return (Long) object;

        try {
            return Long.valueOf( object.toString() );
        }
        catch (NumberFormatException e) {
            logger.wrn( e, "Malformed long: %s", object );
            return 0;
        }
    }

    /**
     * Convert an object into an integer in a semi-safe way. We parse {@link Object#toString()} and return {@code null} rather than throw
     * an
     * exception if the result is not a valid {@link Integer}.
     *
     * @param object The object that may represent an integer.
     *
     * @return The resulting integer.
     */
    @Nonnull
    public static Optional<Integer> toInteger(@Nullable final Object object) {

        if (object == null || object instanceof Integer)
            return Optional.fromNullable( (Integer) object);

        try {
            return Optional.of( Integer.valueOf( object.toString() ) );
        }
        catch (NumberFormatException e) {
            logger.wrn( e, "Malformed integer: %s", object );
            return Optional.absent();
        }
    }

    /**
     * Convert an object into an integer in a semi-safe way. We parse {@link Object#toString()} and return 0 rather than throw an exception
     * if the result is not a valid {@link Integer}.
     *
     * @param object The object that may represent an integer.
     *
     * @return The resulting integer.
     */
    public static int toIntegerNN(@Nullable final Object object) {

        if (object == null)
            return 0;
        if (Integer.class.isInstance( object ))
            return (Integer) object;
        if (Integer.TYPE.isInstance( object ))
            return (int) object;

        try {
            return Integer.valueOf( object.toString() );
        }
        catch (NumberFormatException e) {
            logger.wrn( e, "Malformed integer: %s", object );
            return 0;
        }
    }

    /**
     * Convert an object into a double in a semi-safe way. We parse {@link Object#toString()} and return {@code null} rather than throw an
     * exception if the result is not a valid {@link Double}.
     *
     * @param object The object that may represent a double.
     *
     * @return The resulting double.
     */
    @Nonnull
    public static Optional<Double> toDouble(@Nullable final Object object) {

        if (object == null || object instanceof Double)
            return Optional.fromNullable( (Double) object);

        try {
            return Optional.of( Double.parseDouble( object.toString() ) );
        }
        catch (NumberFormatException e) {
            logger.wrn( e, "Malformed double: %s", object );
            return Optional.absent();
        }
    }

    /**
     * Convert an object into a double in a semi-safe way. We parse {@link Object#toString()} and choose to return 0 rather than throw an
     * exception if the result is not a valid {@link Double}.
     *
     * @param object The object that may represent a double.
     *
     * @return The resulting double.
     */
    public static double toDoubleNN(@Nullable final Object object) {

        if (object == null)
            return 0;
        if (object instanceof Double)
            return (Double) object;

        try {
            return Double.valueOf( object.toString() );
        }
        catch (NumberFormatException e) {
            logger.wrn( e, "Malformed double: %s", object );
            return 0;
        }
    }

    /**
     * Convert an object into a boolean in a safe way. If the given object is not {@code null} or a {@link Boolean}, we parse its
     * {@link Object#toString()} using {@link Boolean#parseBoolean(String)}.
     *
     * NOTE: Strings that do not look like booleans will get converted into {@code false}.
     *
     * @param object The object that represents a boolean.
     *
     * @return The resulting boolean.
     */
    @Nonnull
    public static Optional<Boolean> toBoolean(@Nullable final Object object) {

        if (object == null || object instanceof Boolean)
            return Optional.fromNullable( (Boolean) object );

        return Optional.of( Boolean.parseBoolean( object.toString() ) );
    }

    /**
     * Convert an object into a boolean in a safe way. If the given object is {@code null}, the method yields {@code false}.  If the object
     * is not a {@link Boolean}, we parse its {@link Object#toString()} using {@link Boolean#parseBoolean(String)}.
     *
     * @param object The object that represents a boolean.
     *
     * @return The resulting boolean.
     */
    public static boolean toBooleanNN(@Nullable final Object object) {

        if (object == null)
            return false;
        if (object instanceof Boolean)
            return (Boolean) object;

        return Boolean.parseBoolean( object.toString() );
    }

    /**
     * Convert an object into a string in a safe way. If the given object is not {@code null} or a {@link String}, we use its {@link
     * Object#toString()}.
     *
     * @param object The object to convert into a string.
     *
     * @return The resulting string.
     */
    @Nonnull
    public static Optional<String> toString(@Nullable final Object object) {

        if (object == null || object instanceof String)
            return Optional.fromNullable( (String) object );

        return Optional.of( object.toString() );
    }

    /**
     * Convert an object into a string in a safe way. If the given object is {@code null}, the method yields an empty string.  If the
     * object is not a {@link String}, we use its {@link Object#toString()}.
     *
     * @param object The object to convert into a string.
     *
     * @return The resulting string.
     */
    @Nonnull
    public static String toStringNN(@Nullable final Object object) {

        if (object == null)
            return "";
        if (object instanceof String)
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
    @Nonnull
    public static Optional<URL> toURL(@Nullable final Object url) {

        if (url == null || url instanceof URL)
            return Optional.fromNullable( (URL) url );

        try {
            return Optional.of( new URL( url.toString() ) );
        }
        catch (MalformedURLException e) {
            logger.wrn( e, "Malformed URL: %s", url );
            return Optional.absent();
        }
    }

    /**
     * Convenience method of building a URL that throws runtime exceptions for any error conditions.
     *
     * @param url The URL string.
     *
     * @return The URL string in a URL object.
     *
     * @throws NullPointerException Given URL is null.
     * @throws RuntimeException     Given URL string is not a valid URL.
     */
    @Nonnull
    public static URL toURLNN(@Nonnull final Object url) {

        if (url instanceof URL)
            return (URL) url;

        try {
            return new URL( checkNotNull( url, "Missing URL." ).toString() );
        }
        catch (MalformedURLException e) {
            throw Throw.propagate( e, "Malformed URL: %s", url );
        }
    }
}
