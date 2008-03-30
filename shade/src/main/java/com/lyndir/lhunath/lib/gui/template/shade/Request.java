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
package com.lyndir.lhunath.lib.gui.template.shade;

import java.io.Serializable;

/**
 * <i>{@link Request} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * 
 * @author lhunath
 */
public interface Request extends Serializable {

    /**
     * Retrieve the priority this element has in the autorun queue.
     * 
     * @return lower runs before higher.
     */
    public int getAutorunPriority();
}
