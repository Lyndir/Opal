package com.lyndir.lhunath.lib.system.collection;

import static com.google.common.base.Preconditions.*;

import com.lyndir.lhunath.lib.system.util.ObjectMeta;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;


/**
 * Simple object container, used for sharing an object reference.
 * <p/>
 * <i>05 13, 2011</i>
 *
 * @author lhunath
 */
@ObjectMeta
public class Holder<T> implements Serializable {

    private T value;

    public Holder(@NotNull T value) {

        this.value = checkNotNull( value );
    }

    @NotNull
    public T get() {

        return value;
    }

    public void set(@NotNull final T value) {

        this.value = checkNotNull( value );
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
