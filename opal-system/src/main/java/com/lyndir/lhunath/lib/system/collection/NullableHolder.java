package com.lyndir.lhunath.lib.system.collection;

import com.lyndir.lhunath.lib.system.util.ObjectMeta;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import java.io.Serializable;
import org.jetbrains.annotations.Nullable;


/**
 * Simple object container, used for sharing an object reference.
 *
 * <i>05 13, 2011</i>
 *
 * @author lhunath
 */
@ObjectMeta
public class NullableHolder<T> implements Serializable {

    private T value;

    public NullableHolder(@Nullable T value) {

        this.value = value;
    }

    @Nullable
    public T get() {

        return value;
    }

    public void set(@Nullable final T value) {

        this.value = value;
    }

    @Override
    public int hashCode() {

        return ObjectUtils.hashCode( this );
    }

    @Override
    public boolean equals(final Object obj) {

        return ObjectUtils.equals( this, obj );
    }

    @Override
    public String toString() {

        return ObjectUtils.toString( this );
    }
}
