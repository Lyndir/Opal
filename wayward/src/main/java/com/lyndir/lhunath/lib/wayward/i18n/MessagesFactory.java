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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.lib.system.logging.Logger;
import org.apache.wicket.Session;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;


/**
 * <h2>{@link MessagesFactory}<br>
 * <sub>[in short] (TODO).</sub></h2>
 *
 * <p>
 * <i>Mar 26, 2010</i>
 * </p>
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
     * @param baseClass             The class on which to base the name resource name to load the resource bundle from. The baseName of
     *                              the resource bundle will be the {@link Class#getSimpleName()} and the class' classloader will be used
     *                              to resolve it.
     *
     * @return The localization proxy that provides localized values.
     */
    public static <M> M create(final Class<M> localizationInterface, final Class<?> baseClass) {

        // Create a localization interface proxy.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] proxyInterfaces = {localizationInterface, Serializable.class};
        return localizationInterface.cast( Proxy.newProxyInstance( classLoader, proxyInterfaces,
                                                                   new MessagesInvocationHandler( baseClass ) ) );
    }


    static class MessagesInvocationHandler implements InvocationHandler, Serializable {

        Class<?> baseClass;


        MessagesInvocationHandler(final Class<?> baseClass) {

            this.baseClass = baseClass;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) {

            // Figure out what bundle to load from where and what the key is.
            if (baseClass == null) {
                Class<?> methodType = method.getDeclaringClass();
                baseClass = methodType.getEnclosingClass();
                checkNotNull( baseClass,
                              "Must be an inner class of the class by the name of the resource bundle or manually specify the context class." );
            }
            StringBuilder keyBuilder = new StringBuilder( method.getName() );
            logger.dbg( "Base key: %s", keyBuilder.toString() );

            final List<Object> methodArgs = new LinkedList<Object>();
            if (args != null)
                for (int a = 0; a < args.length; ++a) {
                    Object arg = args[a];
                    List<Annotation> argAnnotations = ImmutableList.of( method.getParameterAnnotations()[a] );
                    boolean useValue = true;
                    logger.dbg( "Considering arg: %s, with annotations: %s.", arg, argAnnotations );

                    for (final Annotation argAnnotation : argAnnotations)
                        if (KeyAppender.class.isAssignableFrom( argAnnotation.getClass() )) {
                            KeyAppender annotation = (KeyAppender) argAnnotation;
                            useValue = annotation.useValue();

                            if (arg == null) {
                                // Null argument => append nullKey if set.

                                if (!annotation.nullKey().equals( KeyAppender.STRING_UNSET ))
                                    appendKey( keyBuilder, annotation.nullKey() );
                            }

                            // Not Null argument
                            else if (!annotation.notNullKey().equals( KeyAppender.STRING_UNSET ))
                                // => append notNullKey if set.
                                appendKey( keyBuilder, annotation.notNullKey() );

                            else if (annotation.value().length == 0)
                                // else if no KeyMatches => append arg value.
                                appendKey( keyBuilder, arg.toString() );

                            else
                                // else (if KeyMatches) => evaluate KeyMatches and append accordingly.
                                for (final KeyMatch match : annotation.value()) {
                                    logger.dbg( "With match: %s, ", match );

                                    if (match.ifNum() == Double.parseDouble( arg.toString() )
                                        || match.ifString().equals( arg ) || match.ifClass().equals( arg ))
                                        appendKey( keyBuilder, match.key() );
                                    else if (!match.elseKey().equals( KeyMatch.STRING_UNSET ))
                                        appendKey( keyBuilder, match.elseKey() );
                                }
                        } else if (BooleanKeyAppender.class.isAssignableFrom( argAnnotation.getClass() )) {
                            BooleanKeyAppender annotation = (BooleanKeyAppender) argAnnotation;
                            useValue = false;
                            logger.dbg( "Has appender: %s, ", annotation );

                            if (Boolean.TRUE.equals( arg ))
                                appendKey( keyBuilder, annotation.y() );
                            else if (Boolean.FALSE.equals( arg ))
                                appendKey( keyBuilder, annotation.n() );
                        }

                    if (useValue) {
                        logger.dbg( "Using argument value." );
                        methodArgs.add( arg );
                    }
                }

            final String key = keyBuilder.toString();
            logger.dbg( "Resolving localization value of key: %s, in baseName: %s, with arguments: %s", //
                        key, baseClass.getSimpleName(), methodArgs );

            // Construct a model to allow lazy evaluation of the key's value.
            IModel<String> valueModel = new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {

                    logger.dbg( "Resolving localization value of key: %s, in baseClass: %s, with arguments: %s", //
                                key, baseClass, methodArgs );

                    // Find the resource bundle for the current locale and the given baseName.
                    ResourceBundle resourceBundle = XMLResourceBundle.getXMLBundle( baseClass.getCanonicalName(),
                                                                                    Session.get().getLocale(),
                                                                                    baseClass.getClassLoader() );

                    // Evaluate IModel arguments.
                    List<Object> valueArgs = Lists.transform( methodArgs, new Function<Object, Object>() {

                        @Override
                        public Object apply(final Object from) {

                            if (from == null)
                                return null;

                            if (IModel.class.isAssignableFrom( from.getClass() ))
                                return ((IModel<?>) from).getObject();

                            return from;
                        }
                    } );

                    // Format the localization key with the arguments.
                    return MessageFormat.format( resourceBundle.getString( key ), valueArgs.toArray() );
                }
            };

            // If the method expects a model, return that.
            if (IModel.class.isAssignableFrom( method.getReturnType() ))
                return valueModel;

            // Otherwise just resolve the key's value straight away.
            return valueModel.getObject();
        }

        private static StringBuilder appendKey(final StringBuilder keyBuilder, final String keyPart) {

            logger.dbg( "Appending key part: %s", keyPart );
            return keyBuilder.append( '.' ).append( keyPart );
        }
    }
}
