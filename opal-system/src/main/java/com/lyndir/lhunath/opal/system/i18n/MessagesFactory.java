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
package com.lyndir.lhunath.opal.system.i18n;

import com.lyndir.lhunath.opal.system.i18n.internal.MessagesInvocationHandler;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.Locale;
import javax.annotation.Nullable;


/**
 * This factory makes it trivial for you to do type-safe localization.
 * <p>
 * Begin by creating a static field in your class with the {@link #create(Class)} method.  The argument to this method is your field's
 * type. It should be an interface that describes the localization keys you want to access.  It generally makes sense to make this an inner
 * interface of your class.
 * <p>
 * The interface describes each localization key you want to access: A method in the interface references a localization key in your
 * localization bundle.  The method can accept arguments.  The arguments can be used to modify the localization key the method will access
 * (by annotating the argument with a {@link KeyAppender}) or the argument can be passed for expansion to the localization value.
 * <p>
 * The bundle whose localization data the interface references is determined by the base class passed in the {@link #create(Class, Class)}
 * call or the class in which your interface is declared if you used {@link #create(Class)}.  Once the base class is determined, an XML
 * file is loaded using the standard bundle loading mechanism using the base class's canonical name as the {@code baseName}, the
 * Wicket session's current {@link Locale} and the classloader that loaded the base class.  Effectively, this means it's best to put your
 * XML file next to your base class, with the same name.
 * <p>
 * As mentioned, you can use arguments to your interface methods to modify the key used to look up the localized return value in the
 * bundle. Any arguments annotated with {@link KeyAppender} will have their value appended to the key (which is initially the name of the
 * method), prefixed by a dot.  In arguments to the annotation, you can specify conditions that the value must meet in order to be appended
 * to the key.  Refer to {@link KeyAppender}'s documentation for more information.  {@link BooleanKeyAppender} works similarly, but is used
 * on boolean arguments and adds a fixed value to the key if the argument is {@code true} or {@code false}.
 * <p>
 * Any arguments that aren't appended to the key will get passed to the localization value for expansion.  This expansion is performed by
 * {@link MessageFormat#format(String, Object...)}, by passing the localization value as the format string and the method arguments as
 * format arguments.
 * <p>
 * You can type arguments and return values either with a concrete type or a concrete type wrapped in an IModel.  If the type is
 * not a model, the object is converted to a string using {@link #toString()}.  If the type is a model, the model's object is handled the
 * same way.  The advantage of using a model is that the object is only retrieved when (and each time) the localization value is being
 * generated.
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
     *                              resolve it.  If {@code null}, the class declaring the localization interface will be used.
     *
     * @return The localization proxy that provides localized values.
     */
    public static <M> M create(final Class<M> localizationInterface, @Nullable final Class<?> baseClass) {

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

        catch (final IllegalAccessException e) {
            throw logger.bug( e, "Field %s of class %s was inaccessible even though we tried setAccessible.", msgsFieldName,
                              object.getClass() );
        }
        catch (final NoSuchFieldException e) {
            throw new IllegalArgumentException( "Field " + msgsFieldName + " of class " + object.getClass() + " not found.", e );
        }
    }
}
