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
package com.lyndir.lhunath.opal.system.logging.exception;

/**
 * <h2>{@link TodoException}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Feb 28, 2010</i> </p>
 *
 * @author lhunath
 */
@SuppressWarnings({ "UncheckedExceptionClass" })
public class TodoException extends UnsupportedOperationException {

    /**
     * A generic constructor indicating this part is not yet implemented.
     */
    public TodoException() {

        this( "TODO" );
    }

    /**
     * @param message Some context with what was going on or what caused this.
     */
    public TodoException(final String message) {

        super( message, null );
    }
}
