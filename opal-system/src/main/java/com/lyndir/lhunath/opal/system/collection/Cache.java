package com.lyndir.lhunath.opal.system.collection;

import com.google.common.collect.Maps;
import com.lyndir.lhunath.opal.system.util.NNSupplier;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * @author lhunath, 2/17/2014
 */
public abstract class Cache {

    private static final Map<Object, Map<Object, Map<Object, SoftReference<Object>>>> cache = new WeakHashMap<>();

    /**
     * Load an object out of the global cache by looking it up based on the scope and key.
     *
     * @param key   The key to identify the lookup value.
     * @param loader The loader used to create the value if a cached value doesn't exist yet.
     * @param <T>    The type of the value.
     *
     * @return The value, cached or loaded from the loader.
     *
     * @throws ClassCastException If the cached value isn't of the expected type.
     */
    public static <T> T getOrLoad(@Nonnull final Object key, @Nonnull final NNSupplier<T> loader) {
        return getOrLoad( key, null, null, loader );
    }
    /**
     * Load an object out of the global cache by looking it up based on the scope and key.
     *
     * @param key1   The first key to identify the lookup value.
     * @param key2   The second key to identify the lookup value.
     * @param loader The loader used to create the value if a cached value doesn't exist yet.
     * @param <T>    The type of the value.
     *
     * @return The value, cached or loaded from the loader.
     *
     * @throws ClassCastException If the cached value isn't of the expected type.
     */
    public static <T> T getOrLoad(@Nonnull final Object key1, @Nullable final Object key2, @Nonnull final NNSupplier<T> loader) {
        return getOrLoad( key1, key2, null, loader );
    }

    /**
     * Load an object out of the global cache by looking it up based on the scope and key.
     *
     * @param key1   The first key to identify the lookup value.
     * @param key2   The second key to identify the lookup value.
     * @param key3   The third key to identify the lookup value.
     * @param loader The loader used to create the value if a cached value doesn't exist yet.
     * @param <T>    The type of the value.
     *
     * @return The value, cached or loaded from the loader.
     *
     * @throws ClassCastException If the cached value isn't of the expected type.
     */
    public static <T> T getOrLoad(@Nonnull final Object key1, @Nullable final Object key2, @Nullable final Object key3, @Nonnull final NNSupplier<T> loader) {
        Map<Object, Map<Object, SoftReference<Object>>> key1Cache = cache.get( key1 );
        if (key1Cache == null)
            cache.put( key1, key1Cache = new WeakHashMap<>() );

        Map<Object, SoftReference<Object>> key2Cache = key1Cache.get( key2 );
        if (key2Cache == null)
            key1Cache.put( key2, key2Cache = new WeakHashMap<>() );

        SoftReference<Object> key3Cache = key2Cache.get( key3 );
        if (key3Cache != null) {
            @SuppressWarnings("unchecked")
            T object = (T) key3Cache.get();
            if (object != null)
                return object;
        }

        T object = loader.get();
        key2Cache.put( key3, new SoftReference<Object>( object ) );
        return object;
    }
}
