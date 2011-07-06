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
package com.lyndir.lhunath.opal.security;

import com.lyndir.lhunath.opal.system.i18n.KeyAppender;
import com.lyndir.lhunath.opal.system.i18n.Localized;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;


/**
 * <h2>{@link Permission}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public enum Permission implements Localized {

    /**
     * This permission grants a {@link Subject} no access to the objects it applies to.
     *
     * <p> <b>NOTE:</b> This permission can't be provided. </p>
     */
    NONE,

    /**
     * This causes the {@link Subject}'s permissions to be resolved against the parent of the objects it applies to.
     *
     * <p> <b>NOTE:</b> This permission can't be provided. </p>
     */
    INHERIT,

    /**
     * This permission grants a {@link Subject} the ability to read the objects it applies to.
     */
    VIEW,

    /**
     * This permission grants a {@link Subject} the ability to modify the objects it applies to.
     */
    CONTRIBUTE,

    /**
     * This permission grants a {@link Subject} the ability to manipulate the security constraints of the objects it applies to.
     */
    ADMINISTER( VIEW, CONTRIBUTE );

    private static final Messages msgs = MessagesFactory.create( Messages.class );

    private final Permission[] provided;

    Permission(final Permission... provided) {

        this.provided = provided;
    }

    /**
     * @return Other permissions provided (granted) by this one.
     */
    public Permission[] getProvided() {

        return provided;
    }

    @Override
    public String getLocalizedType() {

        return msgs.type();
    }

    @Override
    public String getLocalizedInstance() {

        return msgs.description( this );
    }

    /**
     * @param of The secureObject that this permission applies to.
     *
     * @return An explanation of the effects of this permission.
     */
    public String info(final SecureObject<?> of) {

        return msgs.info( this, of, of.getParent() );
    }

    private interface Messages {

        /**
         * @return The name of this type.
         */
        String type();

        /**
         * @param permission The permission to explain.
         *
         * @return A description of the given permission.
         */
        String description(@KeyAppender Permission permission);

        /**
         * @param permission The permission to explain.
         * @param current    The object on which this permission applies.
         * @param parent     The parent object on which this permission applies.
         *
         * @return An information text explaining what this permission grants.
         */
        String info(@KeyAppender Permission permission, SecureObject<?> current, SecureObject<?> parent);
    }
}
