package com.lyndir.lhunath.opal.system.collection;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.io.Serializable;
import org.jetbrains.annotations.Nullable;


/**
 * Simple object container, used for sharing an object reference.
 *
 * <i>05 13, 2011</i>
 *
 * @author lhunath
 */
public class NullableHolder<T> extends MetaObject implements Serializable {

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
}
