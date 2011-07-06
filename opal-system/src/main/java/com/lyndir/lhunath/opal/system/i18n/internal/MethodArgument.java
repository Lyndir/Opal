package com.lyndir.lhunath.opal.system.i18n.internal;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.lyndir.lhunath.opal.system.i18n.Localized;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link MethodArgument}<br> <sub>A container for metadata on method arguments.</sub></h2>
 *
 * <p>When the value for the argument is requested; it is first unwrapped.  That means, in several steps; the value object is transformed
 * in
 * another that is "contained" by it. <ul><li>If it is an IModel, the model's object is substituted.</li> <li>If it is Localized, the
 * object's #getLocalizedInstance is substituted.</li> </ul> </p>
 *
 * <p> <i>07 23, 2010</i> </p>
 *
 * @author lhunath
 */
public class MethodArgument implements Serializable {

    private static final Map<Class<?>, Function<Object, ?>> unwrapperTypes = Maps.newHashMap();

    private final Object           value;
    private final List<Annotation> annotations;

    @SuppressWarnings({ "unchecked" })
    public static <T> void registerWrapperType(final Class<T> wrapperType, final Function<T, ?> valueUnwrapperFactory) {

        unwrapperTypes.put( wrapperType, (Function<Object, ?>) valueUnwrapperFactory );
    }

    public MethodArgument(final Object value, final List<Annotation> annotations) {

        this.value = value;
        this.annotations = annotations;
    }

    public MethodArgument(final Object value, final Annotation... annotations) {

        this( value, ImmutableList.copyOf( annotations ) );
    }

    public Object getValue() {

        return value;
    }

    @Nullable
    public Object getUnwrappedValue() {

        // Time to unwrap the value.
        Object unwrappedValue = getValue();
        if (unwrappedValue == null)
            return null;
        if (Supplier.class.isInstance( unwrappedValue ))
            unwrappedValue = ((Supplier<?>) unwrappedValue).get();
        for (final Map.Entry<Class<?>, Function<Object, ?>> unwrapperType : unwrapperTypes.entrySet()){
            if (unwrapperType.getKey().isInstance( unwrappedValue ))
                unwrappedValue = unwrapperType.getValue().apply( unwrappedValue );
        }

        return unwrappedValue;
    }

    @Nullable
    public Object getLocalizedUnwrappedValue() {

        // Time to unwrap the value.
        Object unwrappedValue = getUnwrappedValue();
        if (unwrappedValue == null)
            return null;
        if (Localized.class.isInstance( unwrappedValue ))
            unwrappedValue = ((Localized) unwrappedValue).getLocalizedInstance();

        return unwrappedValue;
    }

    public List<Annotation> getAnnotations() {

        return annotations;
    }

    @Override
    public String toString() {

        return String.format( "{MethodArg: unwrapped=%s, annotations=%s}", getUnwrappedValue(), getAnnotations() );
    }
}
