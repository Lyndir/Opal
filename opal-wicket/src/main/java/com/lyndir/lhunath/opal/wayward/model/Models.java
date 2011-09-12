package com.lyndir.lhunath.opal.wayward.model;

import static com.google.common.base.Preconditions.*;
import static com.lyndir.lhunath.opal.system.util.TypeUtils.newProxyInstance;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.util.TypeUtils;
import java.lang.reflect.*;
import java.util.List;
import org.apache.wicket.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link Models}<br> <sub>[in short] (TODO).</sub></h2>
 * <p/>
 * <p> <i>07 25, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class Models {

    private static final ThreadLocal<Object>        modelObjectOwner      = new ThreadLocal<Object>();
    private static final ThreadLocal<StringBuilder> modelObjectExpression = new ThreadLocal<StringBuilder>();

    public static <T extends Enum<T>> LoadableDetachableModel<List<T>> listEnum(final Class<T> type) {

        return new LoadableDetachableModel<List<T>>() {

            @Override
            protected List<T> load() {

                return ImmutableList.copyOf( type.getEnumConstants() );
            }
        };
    }

    public static LoadableDetachableModel<List<String>> listUnknownEnum(final Class<?> type) {

        return new LoadableDetachableModel<List<String>>() {

            @Override
            protected List<String> load() {

                Object[] enumConstants = type.getEnumConstants();
                if (enumConstants == null)
                    return null;

                ImmutableList.Builder<String> enumItems = ImmutableList.builder();
                for (final Object enumItem : enumConstants)
                    enumItems.add( ((Enum<?>) enumItem).name() );

                return enumItems.build();
            }
        };
    }

    public static <T> LoadableDetachableModel<T> supplied(final Supplier<T> supplier) {

        return new LoadableDetachableModel<T>() {

            @Override
            protected T load() {

                return supplier.get();
            }
        };
    }

    public static <T> IModel<T> unsupportedOperation() {

        return new AbstractReadOnlyModel<T>() {

            @Override
            public T getObject() {

                throw new UnsupportedOperationException();
            }
        };
    }

    @NotNull
    @SuppressWarnings({ "UnusedParameters", "unchecked" })
    public static <T> IModel<T> model(@Nullable final T object) {

        try {
            checkNotNull( modelObjectOwner.get(), "No model owner, did you forget of()?" );
            checkNotNull( modelObjectExpression.get(),
                          "No model expression, did you forget the expression on of()'s return value? Model owner is: %s",
                          modelObjectOwner.get() );

            return new PropertyModel<T>( modelObjectOwner.get(), modelObjectExpression.get().toString() );
        }
        finally {
            modelObjectOwner.remove();
            modelObjectExpression.remove();
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static <T> T of(@NotNull final T object) {

        checkState( modelObjectOwner.get() == null,
                    "Model owner already set, did you forget model() for a previous of()?  Previous owner is: %s", modelObjectOwner.get() );
        modelObjectExpression.remove();

        return (T) newProxyInstance( object.getClass(), new InvocationHandler() {
            @Nullable
            @Override
            @SuppressWarnings({ "ProhibitedExceptionDeclared" })
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {

                checkState( modelObjectOwner.get() == null );
                checkState( modelObjectExpression.get() == null );

                modelObjectOwner.set( object );
                modelObjectExpression.set( new StringBuilder( TypeUtils.propertyName( method ) ) );
                return ofEnhanced( method.getReturnType() );
            }
        } );
    }

    @Nullable
    @SuppressWarnings({ "unchecked" })
    private static <T> T ofEnhanced(@NotNull final Class<T> type) {

        if (Modifier.isFinal( type.getModifiers() ))
            return null;

        return newProxyInstance( type, new InvocationHandler() {
            @Nullable
            @Override
            @SuppressWarnings({ "ProhibitedExceptionDeclared" })
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {

                checkNotNull( modelObjectOwner );
                checkNotNull( modelObjectExpression );

                modelObjectExpression.get().append( '.' ).append( TypeUtils.propertyName( method ) );
                return ofEnhanced( method.getReturnType() );
            }
        } );
    }
}
