package com.lyndir.lhunath.lib.system.util;

import com.google.common.collect.ObjectArrays;
import java.util.*;


/**
 * <h2>{@link ArrayUtils}<br> <sub>[in short] (TODO).</sub></h2>
 * <p/>
 * <p> <i>06 03, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class ArrayUtils {

    public static boolean hasIndex(final int index, final Object[] array) {

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
    public static <T> T[] concat(final T[] array, final T... elements) {

        if (elements.length == 0)
            return array;

        // TODO: Optimize using array copying.
        List<T> elementList = new ArrayList<T>( array.length + elements.length );
        elementList.addAll( Arrays.asList( array ) );
        elementList.addAll( Arrays.asList( elements ) );
        return elementList.toArray( array );
    }

    public static <T> T[] of(final Class<T> type, final Collection<T> elements) {

        return unsafeCopyOf( type, elements.toArray() );
    }

    public static <T> T[] of(final T... elements) {

        return elements;
    }

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
     * @return <code>true</code> if the search object was found in the array.
     */
    public static <T, U extends T> boolean inArray(final T[] array, final U search) {

        for (final T element : array)
            if (ObjectUtils.isEqual( search, element ))
                return true;

        return false;
    }

    /**
     * Return a new array with all elements from the given array copied over, skipping the first <code>numberToSkip</code> elements.
     *
     * @param arrayType               The type of elements that the array houses.
     * @param commandNameAndArguments The original array that provides the objects to fill the new array with.
     * @param numberToSkip            The amount of elements to skip in the source array, from the start.
     * @param <T>                     The type of elements that the array houses.
     *
     * @return A new array with length <code>commandNameAndArguments.length - numberToSkip</code>
     */
    public static <T> T[] skip(final Class<T> arrayType, final T[] commandNameAndArguments, final int numberToSkip) {

        T[] trimmedArray = ObjectArrays.newArray( arrayType, commandNameAndArguments.length - numberToSkip );
        System.arraycopy( commandNameAndArguments, numberToSkip, trimmedArray, 0, trimmedArray.length );

        return trimmedArray;
    }

    /**
     * Return a new array with all elements from the given array copied over, except the last <code>numberToTrim</code> elements.
     *
     * @param arrayType               The type of elements that the array houses.
     * @param commandNameAndArguments The original array that provides the objects to fill the new array with.
     * @param numberToTrim            The amount of elements to skip in the source array, at the end.
     * @param <T>                     The type of elements that the array houses.
     *
     * @return A new array with length <code>commandNameAndArguments.length - numberToSkip</code>
     */
    public static <T> T[] trim(final Class<T> arrayType, final T[] commandNameAndArguments, final int numberToTrim) {

        T[] trimmedArray = ObjectArrays.newArray( arrayType, commandNameAndArguments.length - numberToTrim );
        System.arraycopy( commandNameAndArguments, 0, trimmedArray, 0, trimmedArray.length );

        return trimmedArray;
    }
}
