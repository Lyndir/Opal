package com.lyndir.lhunath.opal.system.util;

import com.google.common.collect.Maps;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.util.Collection;
import java.util.Map;


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

    public static boolean isEqualElements(final Collection<?> c1, final Collection<?> c2) {

        Map<Object, Integer> objectCount = Maps.newHashMap();
        for (Object c1Object : c1) {
            Integer count = objectCount.get( c1Object );
            count = (count == null? 0: count) + 1;

            objectCount.put( c1Object, count );
        }

        for (Object c2Object : c2) {
            Integer count = objectCount.get( c2Object );
            count = (count == null? 0: count) - 1;

            if (count > 0)
                objectCount.put( c2Object, count );
            else if (count == 0)
                objectCount.remove( c2Object );
        }

        return objectCount.isEmpty();
    }
}
