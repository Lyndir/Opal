/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.opal.wayward.behavior;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import java.util.*;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link CSSClassAttributeAppender}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 12, 2010</i> </p>
 *
 * @author lhunath
 */
public class CSSClassAttributeAppender extends AttributeAppender {

    private static final String CLASS_ATTRIBUTE = "class";
    private static final String CLASS_SEPARATOR = " ";

    /**
     * @param cssClassesModel A model that provides CSS classes to append to the element's <code>class</class> attribute.
     *
     * @return An appender which appends all the CSS classes in the collection from the given model to a component's HTML element.
     */
    public static CSSClassAttributeAppender ofList(final IModel<? extends Collection<String>> cssClassesModel) {

        // noinspection RedundantCast
        return new CSSClassAttributeAppender( cssClassesModel, (Collection<?>) null );
    }

    /**
     * @param cssClassModels Models that provides CSS classes to append to the element's <code>class</class> attribute.
     *
     * @return An appender which appends all the CSS classes in the collection from the given model to a component's HTML element.
     */
    public static CSSClassAttributeAppender of(final IModel<?>... cssClassModels) {

        return ofList(
                new AbstractReadOnlyModel<Collection<String>>() {

                    @Override
                    public Collection<String> getObject() {

                        return Collections2.transform(
                                Arrays.asList( cssClassModels ), new Function<IModel<?>, String>() {

                            @Nullable
                            @Override
                            public String apply(final IModel<?> from) {

                                return from.getObject() == null? null: String.valueOf( from.getObject() );
                            }
                        } );
                    }
                } );
    }

    /**
     * @param cssClassModels Models that provides CSS classes to append to the element's <code>class</class> attribute.
     *
     * @return An appender which appends all the CSS classes in the collection from the given model to a component's HTML element.
     */
    public static CSSClassAttributeAppender of(final String... cssClassModels) {

        return new CSSClassAttributeAppender( cssClassModels );
    }

    /**
     * @param cssClassModel A model that provides a CSS class to append to the element's <code>class</class> attribute.
     *
     * @return An appender which appends the CSS class in the model to a component's HTML element.
     */
    public static CSSClassAttributeAppender ofString(final IModel<String> cssClassModel) {

        // noinspection RedundantCast
        return new CSSClassAttributeAppender( cssClassModel, (String) null );
    }

    /**
     * @param cssClass The CSS class to append to the element's <code>class</class> attribute.
     */
    public CSSClassAttributeAppender(final String cssClass) {

        // noinspection RedundantCast
        this(
                new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {

                        return cssClass;
                    }
                }, (String) null );
    }

    /**
     * @param cssClasses An array of CSS classes to append to the element's <code>class</class> attribute.
     */
    public CSSClassAttributeAppender(final String... cssClasses) {

        // noinspection RedundantCast
        this(
                new AbstractReadOnlyModel<List<String>>() {

                    @Override
                    public List<String> getObject() {

                        return Arrays.asList( cssClasses );
                    }
                }, (Collection<?>) null );
    }

    private CSSClassAttributeAppender(final IModel<? extends Collection<String>> appendModel,
                                      @SuppressWarnings("unused") final Collection<?> x) {

        // noinspection RedundantCast
        this(
                new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {

                        StringBuilder stringBuilder = new StringBuilder();

                        for (final String item : appendModel.getObject())
                            if (item != null && item.length() > 0)
                                stringBuilder.append( item ).append( CLASS_SEPARATOR );

                        if (stringBuilder.length() > 0)
                            stringBuilder.deleteCharAt( stringBuilder.length() - 1 );

                        return stringBuilder.toString();
                    }
                }, (String) null );
    }

    private CSSClassAttributeAppender(final IModel<String> appendModel, @SuppressWarnings("unused") final String x) {

        super( CLASS_ATTRIBUTE, true, appendModel, CLASS_SEPARATOR );
    }
}
