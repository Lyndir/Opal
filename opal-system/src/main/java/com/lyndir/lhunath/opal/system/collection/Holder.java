package com.lyndir.lhunath.opal.system.collection;

import static com.google.common.base.Preconditions.*;

import com.lyndir.lhunath.opal.system.util.MetaObject;
import java.io.Serializable;
import javax.annotation.Nonnull;


/**
 * Simple object container, used for sharing an object reference.
 * <p/>
 * <i>05 13, 2011</i>
 *
 * @author lhunath
 */
public class Holder<T extends Serializable> extends MetaObject implements SSupplier<T> {

    private T value;

    public Holder(@Nonnull final T value) {

        this.value = checkNotNull( value );
    }

    @Nonnull
    @Override
    public T get() {

        return value;
    }

    public void set(@Nonnull final T value) {

        this.value = checkNotNull( value );
    }
}
