package com.lyndir.lhunath.lib.system.util;

/**
 * <h2>{@link ConversionUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>02 10, 2011</i> </p>
 *
 * @author lhunath
 */
public class ConversionUtils {

    /**
     * Convert an object into a long in a semi-safe way. We parse {@link Object#toString()} and choose to return <code>null</code> rather
     * than throw an exception if the result is not a valid {@link Long}.
     *
     * @param object The object that may represent an long.
     *
     * @return The resulting integer.
     */
    public static Long toLong(final Object object) {

        try {
            if (object == null || object instanceof Long)
                return (Long) object;

            return Long.valueOf( object.toString() );
        }
        catch (NumberFormatException ignored) {
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
    public static Integer toInteger(final Object object) {

        try {
            if (object == null || object instanceof Integer)
                return (Integer) object;

            return Integer.valueOf( object.toString() );
        }
        catch (NumberFormatException ignored) {
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
    public static Double toDouble(final Object object) {

        try {
            if (object == null || object instanceof Double)
                return (Double) object;

            return Double.parseDouble( object.toString() );
        }
        catch (NumberFormatException ignored) {
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
    public static String toString(final Object object) {

        if (object == null || object instanceof String)
            return (String) object;

        return object.toString();
    }
}
