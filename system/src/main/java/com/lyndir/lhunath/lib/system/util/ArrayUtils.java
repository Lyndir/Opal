package com.lyndir.lhunath.lib.system.util;

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
}
