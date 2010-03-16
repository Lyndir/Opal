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
package com.lyndir.lhunath.lib.system.localization;

/**
 * <h2>{@link LocalizationProvider}<br>
 * <sub>[in short] (TODO).</sub></h2>
 * 
 * <p>
 * [description / usage].
 * </p>
 * 
 * <p>
 * <i>Jan 21, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public interface LocalizationProvider {

    /**
     * @param key
     *            The localization key that identifies the requesed value.
     * @param context
     *            An optional provider-specific context object that can help the provider resolve the value.
     * @return The value for the given localization key.
     */
    String getValueForKeyInContext(String key, Object context);
}
