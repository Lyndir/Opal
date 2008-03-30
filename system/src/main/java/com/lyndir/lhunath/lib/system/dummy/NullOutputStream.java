/*
 *   Copyright 2005-2007 Maarten Billemont
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
package com.lyndir.lhunath.lib.system.dummy;

import java.io.OutputStream;

/**
 * <i>NullOutputStream - An output stream that does nothing when written to (ie. >/dev/null).</i><br>
 * <br>
 * Redirects output to nothingness..<br>
 * <br>
 * 
 * @author lhunath
 */
public class NullOutputStream extends OutputStream {

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) {

        return;
    }
}
