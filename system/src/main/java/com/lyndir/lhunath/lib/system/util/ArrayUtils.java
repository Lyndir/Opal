package com.lyndir.lhunath.lib.system.util;

import com.google.common.collect.ObjectArrays;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <h2>{@link ArrayUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
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

    public static <T> T[] of(final T... elements) {

        return elements;
    }

    public static <T> T[] unsafeCopyOf(final Class<T> type, final Object... elements) {

        T[] array = ObjectArrays.newArray( type, elements.length );
        System.arraycopy( elements, 0, array, 0, elements.length );

        return array;
    }
}
