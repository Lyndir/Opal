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

import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.wayward.i18n.internal.MessagesInvocationHandler;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;


/**
 * <h2>{@link MessagesFactory}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 26, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class MessagesFactory {

    static final Logger logger = Logger.get( MessagesFactory.class );

    /**
     * @param localizationInterface The interface that declares the localization keys.
     * @param <M>                   The type of the localization interface.
     *
     * @return The localization proxy that provides localized values.
     */
    public static <M> M create(final Class<M> localizationInterface) {

        return create( localizationInterface, null );
    }

    /**
     * @param localizationInterface The interface that declares the localization keys.
     * @param <M>                   The type of the localization interface.
     * @param baseClass             The class on which to base the name resource name to load the resource bundle from. The baseName of the
     *                              resource bundle will be the {@link Class#getSimpleName()} and the class' classloader will be used to
     *                              resolve it.  If <code>null</code>, the class declaring the localization interface will be used.
     *
     * @return The localization proxy that provides localized values.
     */
    public static <M> M create(final Class<M> localizationInterface, final Class<?> baseClass) {

        // Create a localization interface proxy.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] proxyInterfaces = { localizationInterface, Serializable.class };
        return localizationInterface.cast(
                Proxy.newProxyInstance( classLoader, proxyInterfaces, new MessagesInvocationHandler( baseClass ) ) );
    }

    /**
     * @param object                The object on which the msgs field should be initialized.
     * @param localizationInterface The interface to use for looking up localization keys.
     */
    public static void initialize(final Object object, final Class<?> localizationInterface) {

        initialize( object, "msgs", localizationInterface );
    }

    /**
     * @param object                The object that should be initialized.
     * @param msgsFieldName         The name of the field in which the messages proxy should be injected.
     * @param localizationInterface The interface to use for looking up localization keys.
     */
    public static void initialize(final Object object, final String msgsFieldName, final Class<?> localizationInterface) {

        // Manually reinitialize the msgs field.
        try {
            Field field = object.getClass().getDeclaredField( msgsFieldName );
            field.setAccessible( true );
            field.set( object, create( localizationInterface ) );
            field.setAccessible( false );
        }

        catch (IllegalAccessException e) {
            logger.bug( e, "Field %s of class %s was inaccessible even though we tried setAccessible.", msgsFieldName, object.getClass() );
        }
        catch (NoSuchFieldException e) {
            throw logger.err( e, "Field %s of class %s not found.", msgsFieldName, object.getClass() )
                        .toError( IllegalArgumentException.class );
        }
    }
}
