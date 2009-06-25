/*
 *   Copyright 2009, Maarten Billemont
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
 * <h2>{@link ResourceEnum}<br>
 * <sub>An {@link Enum} that simply provides a value for a key.</sub></h2>
 * 
 * <p>
 * The key is the {@link #name()} of the enum constant, the value is the return value of {@link #value()}.
 * </p>
 * 
 * <p>
 * <i>Mar 29, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public interface ResourceEnum {

    public String name();

    public Object value();
}
