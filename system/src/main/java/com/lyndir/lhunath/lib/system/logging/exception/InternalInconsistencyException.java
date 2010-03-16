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
package com.lyndir.lhunath.lib.system.logging.exception;

/**
 * <h2>{@link InternalInconsistencyException}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * <i>Feb 28, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public class InternalInconsistencyException extends RuntimeException {

    /**
     * Create a new {@link InternalInconsistencyException} instance.
     * 
     * @param message
     *            Some context with what was going on or what caused this.
     */
    public InternalInconsistencyException(String message) {

        this( message, null );
    }

    /**
     * Create a new {@link InternalInconsistencyException} instance.
     * 
     * @param message
     *            Some context with what was going on or what caused this.
     * @param cause
     *            The optional exception cause of this.
     */
    public InternalInconsistencyException(String message, Throwable cause) {

        super( message, cause );
    }
}
