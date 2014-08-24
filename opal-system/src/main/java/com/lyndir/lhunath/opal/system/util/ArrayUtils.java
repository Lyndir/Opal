package com.lyndir.lhunath.opal.system.util;

import com.google.common.collect.ObjectArrays;
import java.util.Collection;


/**
 * <h2>{@link ArrayUtils}<br> <sub>[in short] (TODO).</sub></h2>
 * <p>
 * <p> <i>06 03, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class ArrayUtils {

    public static boolean hasIndex(final int index, final Object... array) {

        return array != null && array.length >= index + 1;
    }

    /**
     * Concatenate two arrays or append elements to an array.
     *
     * @param array    The array that provides the first set of elements.
     * @param elements The second set of elements.
     * @param <T>      The type of the array elements.
     *
     * @return A new array that contains all elements from the first parameter with the other elements appended to it.  If no additional
     *         elements are given, returns the first array as it is (<b>not a copy!</b>).
     */
    @SafeVarargs
    public static <T> T[] concat(final T[] array, final T... elements) {

        if (elements.length == 0)
            return array;

        T[] concatenation = ObjectArrays.newArray( array, array.length + elements.length );
        System.arraycopy( array, 0, concatenation, 0, array.length );
        System.arraycopy( elements, 0, concatenation, array.length, elements.length );

        return concatenation;
    }

    public static byte[] concatBytes(final byte[] array, final byte... elements) {

        if (elements.length == 0)
            return array;

        byte[] concatenation = new byte[array.length + elements.length];
        System.arraycopy( array, 0, concatenation, 0, array.length );
        System.arraycopy( elements, 0, concatenation, array.length, elements.length );

        return concatenation;
    }

    public static byte[] concatBytes(final byte[] array, final int... elements) {

        if (elements.length == 0)
            return array;

        byte[] concatenation = new byte[array.length + elements.length];
        System.arraycopy( array, 0, concatenation, 0, array.length );
        for (int e = 0; e < elements.length; ++e)
            concatenation[array.length + e] = (byte) elements[e];

        return concatenation;
    }

    public static <T> T[] copyOf(final Class<T> type, final Collection<T> elements) {

        return unsafeCopyOf( type, elements.toArray() );
    }

    @SafeVarargs
    public static <T> T[] of(final T... elements) {

        return elements;
    }

    public static byte[] ofBytes(final byte... elements) {

        return elements;
    }

    public static byte[] ofBytes(final int... elements) {

        byte[] array = new byte[elements.length];
        for (int e = 0; e < elements.length; ++e)
            array[e] = (byte) elements[e];

        return array;
    }

    @SuppressWarnings({ "SuspiciousSystemArraycopy" })
    public static <T> T[] unsafeCopyOf(final Class<T> type, final Object... elements) {

        T[] array = ObjectArrays.newArray( type, elements.length );
        System.arraycopy( elements, 0, array, 0, elements.length );

        return array;
    }

    /**
     * Check whether the given array contains the given search object.
     *
     * @param array  The array to search through.
     * @param search The object to search for in the array.
     *
     * @return {@code true} if the search object was found in the array.
     */
    public static <T, U extends T> boolean inArray(final T[] array, final U search) {

        for (final T element : array)
            if (search.equals( element ))
                return true;

        return false;
    }

    /**
     * Return a new array with all elements from the given array copied over, skipping the first {@code numberToSkip} elements.
     *
     * @param arrayType               The type of elements that the array houses.
     * @param commandNameAndArguments The original array that provides the objects to fill the new array with.
     * @param numberToSkip            The amount of elements to skip in the source array, from the start.
     * @param <T>                     The type of elements that the array houses.
     *
     * @return A new array with length {@code commandNameAndArguments.length - numberToSkip}
     */
    public static <T> T[] skip(final Class<T> arrayType, final T[] commandNameAndArguments, final int numberToSkip) {

        T[] trimmedArray = ObjectArrays.newArray( arrayType, commandNameAndArguments.length - numberToSkip );
        System.arraycopy( commandNameAndArguments, numberToSkip, trimmedArray, 0, trimmedArray.length );

        return trimmedArray;
    }

    /**
     * Return a new array with all elements from the given array copied over, except the last {@code numberToTrim} elements.
     *
     * @param arrayType               The type of elements that the array houses.
     * @param commandNameAndArguments The original array that provides the objects to fill the new array with.
     * @param numberToTrim            The amount of elements to skip in the source array, at the end.
     * @param <T>                     The type of elements that the array houses.
     *
     * @return A new array with length {@code commandNameAndArguments.length - numberToSkip}
     */
    public static <T> T[] trim(final Class<T> arrayType, final T[] commandNameAndArguments, final int numberToTrim) {

        T[] trimmedArray = ObjectArrays.newArray( arrayType, commandNameAndArguments.length - numberToTrim );
        System.arraycopy( commandNameAndArguments, 0, trimmedArray, 0, trimmedArray.length );

        return trimmedArray;
    }
}
