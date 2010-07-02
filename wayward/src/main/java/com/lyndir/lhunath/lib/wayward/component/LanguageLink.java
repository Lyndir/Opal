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

import java.util.Locale;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;


/**
 * <h2>{@link LanguageLink}<br> <sub>A link that will change the language of the session.</sub></h2>
 *
 * <p> The tag this component is bound to will get an "active" CSS class set if this component represents the language currently in use by
 * the session. </p>
 *
 * <p> <i>Jan 20, 2010</i> </p>
 *
 * @author lhunath
 */
public class LanguageLink extends Link<String> {

    /**
     * @param id       The wicket ID.
     * @param language The 2-letter ISO language code.
     */
    public LanguageLink(final String id, final String language) {

        this( id, new Model<String>( language ) );
    }

    /**
     * @param id    The wicket ID.
     * @param model The 2-letter ISO language code.
     */
    public LanguageLink(final String id, final IModel<String> model) {

        super( id, model );

        add( new AttributeAppender( "class", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {

                return Session.get().getLocale().getLanguage().equals( getModelObject() )? "active": null;
            }
        }, ";" ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick() {

        Session.get().setLocale( new Locale( getModelObject() ) );
    }
}
