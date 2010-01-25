/*
 *   Copyright 2008, Maarten Billemont
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
package com.lyndir.lhunath.lib.system;

import java.io.Serializable;


/**
 * <h2>{@link ConfigChangedListener}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * @param <T>
 *            The type of config entries.
 *
 *            <p>
 *            <i>Mar 26, 2008</i>
 *            </p>
 *
 * @author mbillemo
 */
public interface ConfigChangedListener<T extends Serializable> {

    /**
     * A configuration entry has been modified.
     *
     * @param configEntry
     *            The config entry that triggered this listener.
     * @param oldValue
     *            The old value of the triggered entry.
     * @param newValue
     *            The new value of the triggered entry.
     */
    void configValueChanged(BaseConfig<T> configEntry, T oldValue, T newValue);
}
