package com.lyndir.lhunath.opal.wayward.i18n.internal;

import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.wayward.i18n.Localized;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.List;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link MethodArgument}<br> <sub>A container for metadata on method arguments.</sub></h2>
 *
 * <p>When the value for the argument is requested; it is first unwrapped.  That means, in several steps; the value object is transformed
 * in
 * another that is "contained" by it. <ul><li>If it is an IModel, the model's object is substituted.</li> <li>If it is Localized, the
 * object's #objectDescription is substituted.</li> </ul> </p>
 *
 * <p> <i>07 23, 2010</i> </p>
 *
 * @author lhunath
 */
public class MethodArgument implements Serializable {

    private final Object           value;
    private final List<Annotation> annotations;

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

    public Object getUnwrappedValue() {

        // Time to unwrap the value.
        Object unwrappedValue = getValue();
        if (unwrappedValue == null)
            return null;
        if (IModel.class.isInstance( unwrappedValue ))
            unwrappedValue = ((IModel<?>) unwrappedValue).getObject();

        return unwrappedValue;
    }

    public Object getLocalizedUnwrappedValue() {

        // Time to unwrap the value.
        Object unwrappedValue = getUnwrappedValue();
        if (unwrappedValue == null)
            return null;
        if (Localized.class.isInstance( unwrappedValue ))
            unwrappedValue = ((Localized) unwrappedValue).objectDescription();

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
