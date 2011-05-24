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
package com.lyndir.lhunath.opal.wayward.component;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link LabelAjaxLink}<br> <sub>A {@link Link} with the body of a {@link Label}.</sub></h2>
 *
 * <p> <i>Mar 11, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class LabelAjaxLink extends AjaxLink<String> {

    /**
     * Create a new {@link LabelAjaxLink} instance.
     *
     * @param id    The wicket ID of this component.
     * @param model The model that provides the label text.
     */
    protected LabelAjaxLink(final String id, final IModel<String> model) {

        super( id, model );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {

        replaceComponentTagBody( markupStream, openTag, getDefaultModelObjectAsString() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onComponentTag(final ComponentTag tag) {

        super.onComponentTag( tag );
        // always transform the tag to <span></span> so even labels defined as <span/> render
        tag.setType( XmlTag.OPEN );
    }
}
