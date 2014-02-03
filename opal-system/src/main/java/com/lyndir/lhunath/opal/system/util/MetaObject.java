package com.lyndir.lhunath.opal.system.util;

import javax.annotation.Nullable;


/**
 * <i>06 08, 2011</i>
 *
 * @author lhunath
 */
@ObjectMeta
public abstract class MetaObject {

    @Override
    public int hashCode() {

        return ObjectUtils.hashCode( this );
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(@Nullable final Object obj) {

        return ObjectUtils.equals( this, obj );
    }

    @Override
    public String toString() {

        return ObjectUtils.toString( this );
    }
}
