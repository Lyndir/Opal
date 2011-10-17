package com.lyndir.lhunath.opal.wayward.model;

import static com.google.common.base.Preconditions.*;
import static com.lyndir.lhunath.opal.system.util.TypeUtils.*;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.collection.SSupplier;
import com.lyndir.lhunath.opal.system.util.TypeUtils;
import java.io.Serializable;
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

    private static final ThreadLocal<IModel<?>>     modelObjectModel      = new ThreadLocal<IModel<?>>();
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

    /**
     * Create a model that evaluates the bean expression from the first argument.
     *
     * @param object Put the {@link #bean(Object)} (or variants) expression that should be evaluated by this model here.
     * @param <T>    The type of the model's object.
     *
     * @return A model that will resolve its object using the bean expression.
     */
    @NotNull
    @SuppressWarnings({ "UnusedParameters" })
    public static <T> IModel<T> model(@Nullable final T object) {

        try {
            checkNotNull( modelObjectModel.get(), "No model owner, did you forget bean()?" );
            checkNotNull( modelObjectExpression.get(),
                          "No model expression, did you forget the expression on bean()'s return value? Model owner is: %s",
                          modelObjectModel.get() );

            return new PropertyModel<T>( modelObjectModel.get(), modelObjectExpression.get().toString() );
        }
        finally {
            modelObjectModel.remove();
            modelObjectExpression.remove();
        }
    }

    /**
     * Create a bean expression recorder that can be evaluated by a {@link #model(Object)}.
     * <p/>
     * You use it like this:<br />
     * <code>model(bean(user).getName());</code>
     *
     * @param object The bean on which the expression should be evaluated.
     * @param <T>    The type of the bean object.
     *
     * @return A proxy object of the bean's type that will record a bean expression.
     */
    public static <T> T bean(@NotNull final T object) {

        @SuppressWarnings({ "unchecked" })
        Class<T> type = (Class<T>) object.getClass();

        return bean( type, new SSupplier<T>() {
            @Override
            public T get() {

                return object;
            }
        } );
    }

    /**
     * Create a bean expression recorder that can be evaluated by a {@link #model(Object)}.
     * <p/>
     * You use it like this:<br />
     * <pre>
     * model(bean(User.class, new SSupplier&lt;User&gt;() {
     *             User get() {
     *                 return userService.loadUser( username );
     *             }
     *         }).getName());
     * </pre>
     *
     * @param type     The type of bean that the supplier will supply.
     * @param supplier Supplies the bean on which the expression should be evaluated.
     * @param <T>      The type of the bean object.
     *
     * @return A proxy object of the bean's type that will record a bean expression.
     */
    public static <T, S extends Supplier<T> & Serializable> T bean(@NotNull final Class<T> type, @NotNull final S supplier) {

        return bean( type, new AbstractReadOnlyModel<T>() {
            @Override
            public T getObject() {

                return supplier.get();
            }
        } );
    }

    /**
     * Create a bean expression recorder that can be evaluated by a {@link #model(Object)}.
     * <p/>
     * You use it like this:<br />
     * <pre>
     * model(bean(User.class, new LoadableDetachableModel&lt;User&gt;() {
     *             User load() {
     *                 return userService.loadUser( username );
     *             }
     *         }).getName());
     * </pre>
     *
     * @param type  The type of bean that the model will supply.
     * @param model Supplies the bean on which the expression should be evaluated.
     * @param <T>   The type of the bean object.
     *
     * @return A proxy object of the bean's type that will record a bean expression.
     */
    public static <T> T bean(@NotNull final Class<T> type, @NotNull final IModel<T> model) {

        checkState( modelObjectModel.get() == null,
                    "Model owner already set, did you forget model() for a previous bean()?  Previous owner is: %s",
                    modelObjectModel.get() );

        modelObjectModel.set( model );
        modelObjectExpression.set( new StringBuilder() );

        return ofEnhanced( type );
    }

    /**
     * Create a proxy object that will record all methods invoked on it for use with {@link #model(Object)}.
     *
     * @param type The type of methods that can be recorded.
     * @param <T>  The type of the proxy object.
     *
     * @return A recording proxy object.
     */
    @Nullable
    private static <T> T ofEnhanced(@NotNull final Class<T> type) {

        if (Modifier.isFinal( type.getModifiers() ))
            return null;

        return newProxyInstance( type, new InvocationHandler() {
            @Nullable
            @Override
            @SuppressWarnings({ "ProhibitedExceptionDeclared" })
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {

                checkNotNull( modelObjectModel );
                checkNotNull( modelObjectExpression );

                modelObjectExpression.get().append( '.' ).append( TypeUtils.propertyName( method ) );
                return ofEnhanced( method.getReturnType() );
            }
        } );
    }
}
