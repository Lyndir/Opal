package com.lyndir.lhunath.opal.system.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
    public static boolean recurseContains(@NotNull final Iterable<?> collection, @Nullable final Object o) {

        for (final Object co : collection)
            if (co.equals( o ) || co instanceof Collection<?> && recurseContains( (Iterable<?>) co, o ))
                return true;

        return false;
    }

    public static boolean isEqualElements(@NotNull final Collection<?> c1, @NotNull final Collection<?> c2) {

        if (c1.size() != c2.size())
            return false;

        Map<Object, Integer> objectsCount = Maps.newHashMap();
        for (Object c1Object : c1) {
            Integer count = objectsCount.get( c1Object );
            count = (count == null? 0: count) + 1;

            objectsCount.put( c1Object, count );
        }

        for (Object c2Object : c2) {
            Integer count = objectsCount.get( c2Object );
            count = (count == null? 0: count) - 1;

            if (count > 0)
                objectsCount.put( c2Object, count );
            else if (count == 0)
                objectsCount.remove( c2Object );
            else
                return false;
        }

        return objectsCount.isEmpty();
    }

    @Nullable
    public static <E> E firstElementOfType(@NotNull Class<E> type, @NotNull Collection<? super E> elements) {

        for (Object element : elements)
            if (type.isInstance( element ))
                return type.cast( element );

        return null;
    }

    @NotNull
    public static <E> List<E> elementsOfType(@NotNull Class<E> type, @NotNull Collection<? super E> elements) {

        List<E> elementsOfType = Lists.newLinkedList();
        for (Object element : elements)
            if (type.isInstance( element ))
                elementsOfType.add( type.cast( element ) );

        return elementsOfType;
    }
}
