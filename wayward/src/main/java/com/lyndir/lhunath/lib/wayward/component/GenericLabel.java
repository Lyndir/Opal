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
package com.lyndir.lhunath.lib.wayward.component;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link GenericLabel}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Jan 18, 2010</i>
 * </p>
 *
 * @author lhunath
 * @param <T>
 * The model type.
 */
public class GenericLabel<T> extends GenericWebMarkupContainer<T> {

    /**
     * Create a new {@link GenericLabel} instance.
     *
     * @param id    The component's wicket ID.
     * @param model The component's model.
     */
    public GenericLabel(final String id, final IModel<T> model) {

        super( id, model );
    }

    /**
     * @see Component#onComponentTagBody(MarkupStream, ComponentTag)
     */
    @Override
    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

        replaceComponentTagBody( markupStream, openTag, getDefaultModelObjectAsString() );
    }

    /**
     * @see Component#onComponentTag(ComponentTag)
     */
    @Override
    protected void onComponentTag(final ComponentTag tag) {

        super.onComponentTag( tag );
        // always transform the tag to <span></span> so even labels defined as <span/> render
        tag.setType( XmlTag.OPEN );
    }
}
