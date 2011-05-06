package com.lyndir.lhunath.lib.system.util;

import com.lyndir.lhunath.lib.system.logging.Logger;
import java.util.Collection;


/**
 * <i>05 06, 2011</i>
 *
 * @author lhunath
 */
public abstract class CollectionUtils {

    static final Logger logger = Logger.get( CollectionUtils.class );

    /**
     * Recursively iterate the given {@link Collection} and all {@link Collection}s within it. When an element is encountered that equals
     * the given {@link Object}, the method returns <code>true</code>.
     *
     * @param collection The collection to iterate recursively.
     * @param o          The object to look for in the collection.
     *
     * @return <code>true</code> if the object was found anywhere within the collection or any of its sub-collections.
     */
    public static boolean recurseContains(final Iterable<?> collection, final Object o) {

        for (final Object co : collection)
            if (co.equals( o ) || co instanceof Collection<?> && recurseContains( (Iterable<?>) co, o ))
                return true;

        return false;
    }
}
