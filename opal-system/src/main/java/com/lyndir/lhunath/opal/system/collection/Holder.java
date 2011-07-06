package com.lyndir.lhunath.opal.system.collection;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Supplier;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;


/**
 * Simple object container, used for sharing an object reference.
 * <p/>
 * <i>05 13, 2011</i>
 *
 * @author lhunath
 */
public class Holder<T extends Serializable> extends MetaObject implements Supplier<T>, Serializable {

    private T value;

    public Holder(@NotNull final T value) {

        this.value = checkNotNull( value );
    }

    @NotNull
    @Override
    public T get() {

        return value;
    }

    public void set(@NotNull final T value) {

        this.value = checkNotNull( value );
    }
}
