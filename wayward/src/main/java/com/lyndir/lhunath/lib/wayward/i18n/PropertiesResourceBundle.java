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
package com.lyndir.lhunath.lib.wayward.i18n;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;


/**
 * <h2>{@link PropertiesResourceBundle}<br>
 * <sub>A {@link ResourceBundle} that loads its values from {@link Properties}.</sub></h2>
 *
 * <p>
 * <i>Mar 26, 2010</i>
 * </p>
 *
 * @author lhunath
 */
public class PropertiesResourceBundle extends ResourceBundle {

    private final Properties properties;


    PropertiesResourceBundle(final Properties props) {

        properties = props;
    }

    @Override
    protected Object handleGetObject(final String key) {

        return properties.getProperty( key );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<String> getKeys() {

        return Collections.enumeration( properties.stringPropertyNames() );
    }
}
