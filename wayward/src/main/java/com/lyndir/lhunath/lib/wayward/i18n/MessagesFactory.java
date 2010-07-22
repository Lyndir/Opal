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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.lib.system.collection.Pair;
import com.lyndir.lhunath.lib.system.logging.Logger;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.*;
import org.apache.wicket.Session;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;


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

            // Convert all non-serializable data into something serializable.
            final String methodName = method.getName();
            final List<Object> argValues = Lists.newLinkedList();
            ImmutableList.Builder<Pair<Object, ? extends List<Annotation>>> argValuesAnnotationsBuilder = ImmutableList.builder();
            for (int a = 0, argsLen = args.length; a < argsLen; ++a) {
                Object argValue = args[a];
                Annotation[] argAnnotations = method.getParameterAnnotations()[a];

                argValues.add( argValue );
                argValuesAnnotationsBuilder.add( Pair.of( argValue, ImmutableList.copyOf( argAnnotations ) ) );
            }
            final List<Pair<Object, ? extends List<Annotation>>> argValuesAnnotations = argValuesAnnotationsBuilder.build();

            // Construct a model to allow lazy evaluation of the key's value.
            IModel<String> valueModel = new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {

                    StringBuilder keyBuilder = new StringBuilder( methodName );
                    logger.dbg( "Base key: %s", keyBuilder.toString() );

                    // Evaluate IModel and Localized arguments.
                    List<Object> valueArgs = Lists.transform( argValues, new Function<Object, Object>() {

                        @Override
                        public Object apply(final Object from) {

                            if (from == null)
                                return null;

                            Object arg = from;
                            if (IModel.class.isInstance( arg ))
                                arg = ((IModel<?>) arg).getObject();
                            if (Localized.class.isInstance( arg ))
                                arg = ((Localized) arg).objectDescription();

                            return arg;
                        }
                    } );

                    final List<Object> methodArgs = new LinkedList<Object>();
                    for (final Pair<Object, ? extends List<Annotation>> argValuesAnnotation : argValuesAnnotations) {
                        Object argValue = argValuesAnnotation.getKey();
                        List<Annotation> argAnnotations = argValuesAnnotation.getValue();
                        boolean useValue = true;
                        logger.dbg( "Considering argument with value: %s, annotations: %s.", argValue, argAnnotations );

                        for (final Annotation argAnnotation : argAnnotations)
                            if (KeyAppender.class.isInstance( argAnnotation )) {
                                KeyAppender annotation = (KeyAppender) argAnnotation;
                                useValue = annotation.useValue();

                                if (argValue == null) {
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
                                    appendKey( keyBuilder, argValue.toString() );

                                else
                                    // else (if KeyMatches) => evaluate KeyMatches and append accordingly.
                                    for (final KeyMatch match : annotation.value()) {
                                        logger.dbg( "With match: %s, ", match );

                                        if (match.ifNum() == Double.parseDouble( argValue.toString() ) || match.ifString().equals(
                                                argValue ) || match
                                                .ifClass()
                                                .equals( argValue ))
                                            appendKey( keyBuilder, match.key() );
                                        else if (!match.elseKey().equals( KeyMatch.STRING_UNSET ))
                                            appendKey( keyBuilder, match.elseKey() );
                                    }
                            } else if (BooleanKeyAppender.class.isInstance( argAnnotation )) {
                                BooleanKeyAppender annotation = (BooleanKeyAppender) argAnnotation;
                                useValue = false;
                                logger.dbg( "Has appender: %s, ", annotation );

                                if (Boolean.TRUE.equals( argValue ))
                                    appendKey( keyBuilder, annotation.y() );
                                else if (Boolean.FALSE.equals( argValue ))
                                    appendKey( keyBuilder, annotation.n() );
                            } else if (LocalizedType.class.isInstance( argAnnotation )) {
                                checkArgument( Localized.class.isInstance( argValue ),
                                               "Can't evaluate @LocalizedType on an object that does not implement Localized." );

                                Localized localizedArg = (Localized) argValue;
                                argValue = localizedArg.typeDescription();
                            }

                        if (useValue) {
                            logger.dbg( "Using argument value." );
                            methodArgs.add( argValue );
                        }
                    }

                    String key = keyBuilder.toString();
                    logger.dbg( "Resolving localization value of key: %s, in baseClass: %s, with arguments: %s", //
                                key, baseClass, methodArgs );

                    // Find the resource bundle for the current locale and the given baseName.
                    ResourceBundle resourceBundle = XMLResourceBundle.getXMLBundle( baseClass.getCanonicalName(), Session.get().getLocale(),
                                                                                    baseClass.getClassLoader() );

                    // Format the localization key with the arguments.
                    try {
                        return MessageFormat.format( resourceBundle.getString( key ), methodArgs.toArray() );
                    }
                    catch (MissingResourceException e) {
                        //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
                        throw new MissingResourceException( String.format( "Missing resource for: %s, at key: %s.", baseClass, e.getKey() ),
                                                            baseClass.getCanonicalName(), e.getKey() );
                    }
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
