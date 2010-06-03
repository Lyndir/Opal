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

import com.lyndir.lhunath.lib.wayward.js.JSUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;


/**
 * <h2>{@link JSLink}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 12, 2010</i> </p>
 *
 * @author lhunath
 */
public class JSLink extends AttributeModifier {

    static final String CLASS_ATTRIBUTE = "onClick";

    /**
     * @param function The name of the javascript function to invoke.
     * @param args     The arguments to pass to the javascript function.
     */
    public JSLink(final String function, final Object... args) {

        this( new LoadableDetachableModel<String>() {
            @Override
            protected String load() {

                return JSUtils.callFunction( function, args );
            }
        } );
    }

    /**
     * @param jsModel The model that provides the javascript to invoke when the component is clicked.
     */
    public JSLink(final IModel<String> jsModel) {

        super( CLASS_ATTRIBUTE, true, jsModel );
    }
}
