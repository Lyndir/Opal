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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import com.lyndir.lhunath.lib.system.localization.UseBundle.UnspecifiedBundle;
import com.lyndir.lhunath.lib.system.logging.Logger;


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

    static final Logger logger = Logger.get( LocalizerFactory.class );


    public static <L> L getLocalizer(Class<L> localizationInterface, Object context) {

        // Do some validation on the localization interface.
        if (!localizationInterface.isInterface())
            throw new IllegalArgumentException( MessageFormat.format(
                    "Localization interface must be an interface: {0}", localizationInterface ) );

        if (!(localizationInterface.isAnnotationPresent( UseBundle.class ) || localizationInterface.isAnnotationPresent( UseLocalizationProvider.class )))
            throw new IllegalArgumentException( MessageFormat.format(
                    "Localization interface must be annotated with {0} or {1}: {2}", //
                    UseBundle.class, UseLocalizationProvider.class, localizationInterface ) );

        for (Method method : localizationInterface.getDeclaredMethods())
            if (!method.isAnnotationPresent( UseKey.class ))
                throw new IllegalArgumentException( MessageFormat.format(
                        "Method must be annotated with {0}: {1} of {2}", UseKey.class, method, localizationInterface ) );

        // Create a localization interface proxy.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        L queryObject = localizationInterface.cast( Proxy.newProxyInstance( classLoader, new Class[] {
                localizationInterface, Serializable.class }, new LocalizationInvocationHandler( context ) ) );

        return queryObject;
    }


    static class LocalizationInvocationHandler implements InvocationHandler, Serializable {

        private Object context;


        public LocalizationInvocationHandler(Object context) {

            this.context = context;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {

            UseKey useKeyAnnotation = method.getAnnotation( UseKey.class );
            if (useKeyAnnotation == null)
                throw new IllegalStateException( MessageFormat.format( "Need a {0} annotation on {1}", UseKey.class,
                        method ) );

            String localizationKey = useKeyAnnotation.value();
            if (localizationKey == null || localizationKey.isEmpty())
                localizationKey = method.getName();

            Class<?> methodType = method.getDeclaringClass();
            UseBundle useBundleAnnotation = methodType.getAnnotation( UseBundle.class );
            if (useBundleAnnotation != null) {
                Class<? extends ResourceBundle> bundleType = useBundleAnnotation.type();
                String bundleResource = null;
                if (bundleType != null && !bundleType.equals( UnspecifiedBundle.class ))
                    bundleResource = bundleType.getCanonicalName();
                else
                    bundleResource = useBundleAnnotation.resource();
                if (bundleResource == null || bundleResource.isEmpty())
                    throw new IllegalStateException( MessageFormat.format(
                            "No #type or #resource was specified on the {0} annotation for {1}", UseBundle.class,
                            method ) );

                ResourceBundle bundle = ResourceBundle.getBundle( bundleResource );

                if (String.class.isAssignableFrom( method.getReturnType() )) {
                    String localizedValueFormat = bundle.getString( localizationKey );
                    return MessageFormat.format( localizedValueFormat, args );
                }

                if (method.getParameterTypes().length > 0)
                    throw new IllegalArgumentException( "Expected no arguments on " + method
                                                        + ": Can't format non-String return type." );

                return bundle.getObject( localizationKey );
            }

            UseLocalizationProvider useLocalizationProviderAnnotation = methodType.getAnnotation( UseLocalizationProvider.class );
            if (useLocalizationProviderAnnotation != null) {
                Class<? extends LocalizationProvider> localizationProvider = useLocalizationProviderAnnotation.value();

                return MessageFormat.format( localizationProvider.newInstance().getValueForKeyInContext(
                        localizationKey, context ), args );
            }

            throw new UnsupportedOperationException( MessageFormat.format( "No supported annotation found on: {0}",
                    methodType ) );
        }
    }
}
