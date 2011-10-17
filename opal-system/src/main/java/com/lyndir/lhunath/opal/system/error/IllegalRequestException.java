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
package com.lyndir.lhunath.opal.system.error;

import com.lyndir.lhunath.opal.system.i18n.MessagesFactory;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link IllegalRequestException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 14, 2010</i> </p>
 *
 * @author lhunath
 */
public class IllegalRequestException extends Exception {

    static final Messages msgs = MessagesFactory.create( Messages.class );

    /**
     * @param message The message that explains why the request request was illegal.
     */
    public IllegalRequestException(final String message) {

        this( message, null );
    }

    /**
     * @param cause   An optional exception that caused this one.
     * @param message The message that explains why the request request was illegal.
     */
    public IllegalRequestException(final String message, @Nullable final Throwable cause) {

        super( message, cause );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalizedMessage() {

        return msgs.message();
    }

    interface Messages {

        String message();
    }
}
