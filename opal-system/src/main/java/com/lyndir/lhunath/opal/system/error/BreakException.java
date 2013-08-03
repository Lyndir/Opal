/*
 * Copyright 2010, Maarten Billemont
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

import java.io.Serializable;


/**
* @author lhunath, 2013-08-02
*/
@SuppressWarnings("UncheckedExceptionClass")
public class BreakException extends RuntimeException {

    @SuppressWarnings("TransientFieldNotInitialized")
    private final transient Object result;

    public BreakException(final Object result) {

        this.result = result;
    }

    @SuppressWarnings({ "unchecked" })
    public <R> R getResult() {

        return (R) result;
    }
}
