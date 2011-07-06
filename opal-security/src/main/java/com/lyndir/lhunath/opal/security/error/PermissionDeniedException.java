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
package com.lyndir.lhunath.opal.security.error;

import com.lyndir.lhunath.opal.security.Permission;
import com.lyndir.lhunath.opal.security.SecureObject;
import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import com.lyndir.lhunath.opal.system.util.ArrayUtils;


/**
 * <h2>{@link PermissionDeniedException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public class PermissionDeniedException extends Exception {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    private final Permission permission;
    private final SecureObject<?> secureObject;

    /**
     * @param permission    The permission level required on the object.
     * @param secureObject  The secure object to which access was denied.
     * @param messageFormat The message that explains why permission was denied in String#format syntax.
     * @param messageArgs   Additional arguments to expand into the message as defined by messageFormat.
     */
    public PermissionDeniedException(final Permission permission, final SecureObject<?> secureObject, final String messageFormat,
                                     final Object... messageArgs) {

        super( String.format( messageFormat + " in request for: %s@%s.", ArrayUtils.concat( messageArgs, permission, secureObject ) ) );

        this.permission = permission;
        this.secureObject = secureObject;
    }

    /**
     * @param cause         An optional exception that caused this one.
     * @param permission    The permission level required on the object.
     * @param secureObject  The secure object to which access was denied.
     * @param messageFormat The message that explains why permission was denied in String#format syntax.
     * @param messageArgs   Additional arguments to expand into the message as defined by messageFormat.
     */
    public PermissionDeniedException(final Throwable cause, final Permission permission, final SecureObject<?> secureObject,
                                     final String messageFormat, final Object... messageArgs) {

        this( permission, secureObject, messageFormat, messageArgs );
        initCause( cause );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalizedMessage() {

        return msgs.message( permission, secureObject );
    }

    interface Messages {

        String message(Permission permission, SecureObject<?> secureObject);
    }
}
