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
package com.lyndir.lhunath.lib.wayward.behavior;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link CSSClassAttributeAppender}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 12, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class CSSClassAttributeAppender extends AttributeAppender {

    private static final String CLASS_ATTRIBUTE = "class";
    private static final String CLASS_SEPARATOR = " ";


    /**
     * @param cssClassesModel A model that provides CSS classes to append to the element's <code>class</class> attribute.
     *
     * @return An appender which appends all the CSS classes in the collection from the given model to a component's
     *         HTML element.
     */
    public static CSSClassAttributeAppender ofList(final IModel<? extends Collection<String>> cssClassesModel) {

        return new CSSClassAttributeAppender( cssClassesModel, (Collection<?>) null );
    }

    /**
     * @param cssClassModel A model that provides a CSS class to append to the element's <code>class</class> attribute.
     *
     * @return An appender which appends the CSS class in the model to a component's HTML element.
     */
    public static CSSClassAttributeAppender ofString(final IModel<String> cssClassModel) {

        return new CSSClassAttributeAppender( cssClassModel, (String) null );
    }

    /**
     * @param cssClass The CSS class to append to the element's <code>class</class> attribute.
     */
    public CSSClassAttributeAppender(final String cssClass) {

        this( new AbstractReadOnlyModel<String>() {

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

        this( new AbstractReadOnlyModel<List<String>>() {

            @Override
            public List<String> getObject() {

                return Arrays.asList( cssClasses );
            }
        }, (Collection<?>) null );
    }

    private CSSClassAttributeAppender(final IModel<? extends Collection<String>> appendModel,
                                      @SuppressWarnings("unused") Collection<?> x) {

        this( new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {

                StringBuilder stringBuilder = new StringBuilder();

                for (String item : appendModel.getObject())
                    stringBuilder.append( item ).append( CLASS_SEPARATOR );

                return stringBuilder.deleteCharAt( stringBuilder.length() - 1 ).toString();
            }
        }, (String) null );
    }

    private CSSClassAttributeAppender(IModel<String> appendModel, @SuppressWarnings("unused") String x) {

        super( CLASS_ATTRIBUTE, true, appendModel, CLASS_SEPARATOR );
    }
}
