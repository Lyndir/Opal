package com.lyndir.lhunath.lib.wayward.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.wicket.model.*;


/**
 * <h2>{@link Models}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 25, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class Models {

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
                for (Object enumItem : enumConstants)
                    enumItems.add( ((Enum<?>) enumItem).name() );

                return enumItems.build();
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
}
