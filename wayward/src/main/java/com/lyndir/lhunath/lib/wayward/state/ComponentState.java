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
package com.lyndir.lhunath.lib.wayward.state;

/**
 * <h2>{@link ComponentState}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 21, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public interface ComponentState {

    /**
     * @return <code>true</code>: if the session state requires the attention of this page.
     */
    boolean isNecessary();

    /**
     * @return <code>true</code>: The component that handles this {@link ComponentState} is being presented to the user.
     */
    boolean isActive();

    /**
     * When invoked, the implementation should cause itself to be presented to the user.
     */
    void activate();
}
