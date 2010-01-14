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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.lyndir.lhunath.lib.system.localization.UseBundle.UnspecifiedBundle;


/**
 * <h2>{@link LocalizerFactory}<br>
 * <sub>Create localizers from localization interfaces.</sub></h2>
 * 
 * <p>
 * This class builds localizers from interfaces annotated with the {@link UseBundle} and {@link UseKey} annotations.
 * </p>
 * 
 * <p>
 * This localizer can then be queried by invoking the methods provided by the interface used to create it. This way, you
 * can to obtain localized data from them as provided by the resource bundle that interface references in its
 * {@link UseBundle} annotation.
 * </p>
 * 
 * <p>
 * <i>Mar 28, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public abstract class LocalizerFactory {

    public static <L> L getLocalizer(Class<L> localizationProvider) {

        // Do some validation on the localization provider interface.
        if (!localizationProvider.isInterface())
            throw new IllegalArgumentException(
                    MessageFormat.format( "Localization provider must be an interface: {0}", localizationProvider ) );

        if (!(localizationProvider.isAnnotationPresent( UseBundle.class ) || localizationProvider.isAnnotationPresent( UseBundle.class )))
            throw new IllegalArgumentException(
                    MessageFormat.format( "Localization provider must be annotated with {0}: {1}", UseBundle.class,
                                          localizationProvider ) );

        for (Method method : localizationProvider.getDeclaredMethods())
            if (!method.isAnnotationPresent( UseKey.class ))
                throw new IllegalArgumentException(
                        MessageFormat.format( "Method must be annotated with {0}: {1} of {2}", UseKey.class, method,
                                              localizationProvider ) );

        // Create a localization provider proxy.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        L queryObject = localizationProvider.cast( Proxy.newProxyInstance( classLoader,
                                                                           new Class[] { localizationProvider },
                                                                           new LocalizationInvocationHandler() ) );

        return queryObject;
    }


    static class LocalizationInvocationHandler implements InvocationHandler {

        /**
         * {@inheritDoc}
         */
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {

            UseKey useKeyAnnotation = method.getAnnotation( UseKey.class );
            if (useKeyAnnotation == null)
                throw new IllegalStateException( MessageFormat.format( "Need a {0} annotation on {1}", UseKey.class,
                                                                       method ) );

            Class<?> methodType = method.getDeclaringClass();
            UseBundle useBundleAnnotation = methodType.getAnnotation( UseBundle.class );
            if (useBundleAnnotation == null)
                throw new IllegalStateException( MessageFormat.format( "Need a {0} annotation on {1}", UseBundle.class,
                                                                       method ) );

            String bundleKey = useKeyAnnotation.value();
            Class<? extends ResourceBundle> bundleType = useBundleAnnotation.type();
            String bundleResource = null;
            if (bundleType != null && !bundleType.equals( UnspecifiedBundle.class ))
                bundleResource = bundleType.getCanonicalName();
            else
                bundleResource = useBundleAnnotation.resource();
            if (bundleResource == null || bundleResource.isEmpty())
                throw new IllegalStateException(
                        MessageFormat.format( "No #type or #resource was specified on the {0} annotation for {1}",
                                              UseBundle.class, method ) );

            if (bundleKey == null || bundleKey.isEmpty())
                bundleKey = method.getName();

            ResourceBundle bundle = ResourceBundle.getBundle( bundleResource );

            if (String.class.isAssignableFrom( method.getReturnType() )) {
                String localizedValueFormat = bundle.getString( bundleKey );
                return MessageFormat.format( localizedValueFormat, args );
            }

            if (method.getParameterTypes().length > 0)
                throw new IllegalArgumentException( "Expected no arguments on " + method
                                                    + ": Can't format non-String return type." );

            return bundle.getObject( bundleKey );
        }
    }
}
