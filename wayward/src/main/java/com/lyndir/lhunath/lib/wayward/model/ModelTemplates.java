package com.lyndir.lhunath.lib.wayward.model;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link ModelTemplates}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 25, 2010</i> </p>
 *
 * @author lhunath
 */
public class ModelTemplates {

    public static <T> IModel<T> unsupportedOperation() {

        return new AbstractReadOnlyModel<T>() {

            @Override
            public T getObject() {

                throw new UnsupportedOperationException();
            }
        };
    }
}
