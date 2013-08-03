package com.lyndir.lhunath.opal.system.i18n.internal;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.collection.SSupplier;
import com.lyndir.lhunath.opal.system.i18n.*;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.logging.exception.AlreadyCheckedException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import javax.annotation.Nullable;


/**
 * <h2>{@link MessagesInvocationHandler}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 24, 2010</i> </p>
 *
 * @author lhunath
 */
public class MessagesInvocationHandler implements InvocationHandler, Serializable {

    static final         Logger                                       logger          = Logger.get( MessagesInvocationHandler.class );
    private static final Map<Class<?>, Function<Supplier<String>, ?>> wrapperTypes    = Maps.newHashMap();
    private static final Deque<Supplier<Locale>>                      localeSuppliers = Lists.newLinkedList();

    static {
        registerLocaleSupplier( new Supplier<Locale>() {
            @Override
            public Locale get() {

                return Locale.getDefault();
            }
        } );
    }

    public static <T> void registerWrapperType(final Class<? super T> wrapperType,
                                               final Function<Supplier<String>, T> wrapperValueFactory) {

        wrapperTypes.put( wrapperType, wrapperValueFactory );
    }

    public static void registerLocaleSupplier(final Supplier<Locale> localeSupplier) {

        localeSuppliers.addFirst( localeSupplier );
    }

    @Nullable
    Class<?> baseClass;

    public MessagesInvocationHandler(@Nullable final Class<?> baseClass) {

        this.baseClass = baseClass;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
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
        ImmutableList.Builder<MethodArgument> methodArgsBuilder = ImmutableList.builder();
        if (args != null)
            for (int a = 0, argsLen = args.length; a < argsLen; ++a) {
                Object argValue = args[a];
                Annotation[] argAnnotations = method.getParameterAnnotations()[a];

                methodArgsBuilder.add( new MethodArgument( argValue, argAnnotations ) );
            }
        final List<MethodArgument> methodArgs = methodArgsBuilder.build();

        // Construct a model to allow lazy evaluation of the key's value.
        SSupplier<String> valueSupplier = new SSupplier<String>() {

            @Override
            public String get() {

                StringBuilder keyBuilder = new StringBuilder( methodName );
                logger.dbg( "Base key: %s", keyBuilder.toString() );

                final List<Object> localizationArgs = new LinkedList<>();
                for (final MethodArgument methodArg : methodArgs) {
                    Object argValue = methodArg.getUnwrappedValue();
                    List<Annotation> argAnnotations = methodArg.getAnnotations();
                    logger.dbg( "Considering argument %s", methodArg );

                    boolean useValue = true;
                    for (final Annotation argAnnotation : argAnnotations)
                        if (KeyAppender.class.isInstance( argAnnotation )) {
                            KeyAppender annotation = (KeyAppender) argAnnotation;
                            useValue = annotation.useValue();

                            if (argValue == null)
                                // Null argument => append nullKey if set.
                                appendKey( keyBuilder, annotation.nullKey() );

                                // Not Null argument
                            else {
                                // => append notNullKey if set.
                                appendKey( keyBuilder, annotation.notNullKey() );

                                if (annotation.value().length == 0)
                                    // if no KeyMatches => append arg value.
                                    appendKey( keyBuilder, argValue.toString() );

                                else
                                    // else (if KeyMatches) => evaluate KeyMatches and append accordingly.
                                    for (final KeyMatch match : annotation.value()) {
                                        logger.dbg( "With match: %s, ", match );

                                        boolean matches = false;
                                        if (!matches && match.ifNum() != KeyMatch.NUM_UNSET)
                                            if (Number.class.isInstance( argValue ) && match.ifNum() == ((Number) argValue).doubleValue())
                                                matches = true;
                                        if (!matches && match.ifString() != KeyMatch.STRING_UNSET)
                                            if (match.ifString().equals( argValue.toString() ))
                                                matches = true;
                                        if (!matches && match.ifClass() != KeyMatch.CLASS_UNSET)
                                            if (match.ifClass().equals( argValue ))
                                                matches = true;

                                        if (matches)
                                            appendKey( keyBuilder, match.key() );
                                        else if (match.elseKey() != KeyMatch.STRING_UNSET)
                                            appendKey( keyBuilder, match.elseKey() );
                                    }
                            }
                        } else if (BooleanKeyAppender.class.isInstance( argAnnotation )) {
                            BooleanKeyAppender annotation = (BooleanKeyAppender) argAnnotation;
                            useValue = false;

                            checkArgument( Boolean.class.isInstance( argValue ),
                                           "BooleanKeyAppender for method %s, expects a Boolean value but found: %s", methodName,
                                           argValue );

                            if (Boolean.TRUE.equals( argValue ))
                                appendKey( keyBuilder, annotation.y() );
                            else if (Boolean.FALSE.equals( argValue ))
                                appendKey( keyBuilder, annotation.n() );
                            else
                                throw new AlreadyCheckedException();
                        }

                    if (useValue) {
                        logger.dbg( "Using argument value." );
                        localizationArgs.add( methodArg.getLocalizedUnwrappedValue() );
                    }
                }

                String key = keyBuilder.toString();
                logger.dbg( "Resolving localization value of key: %s, in baseClass: %s, with arguments: %s", //
                            key, baseClass, localizationArgs );

                // Find the resource bundle for the current locale and the given baseName.
                Locale locale = null;
                for (final Supplier<Locale> localeSupplier : localeSuppliers)
                    if ((locale = localeSupplier.get()) != null)
                        break;
                ResourceBundle resourceBundle = XMLResourceBundle.getXMLBundle( baseClass.getCanonicalName(), locale,
                                                                                baseClass.getClassLoader() );

                // Format the localization key with the arguments.
                try {
                    return MessageFormat.format( resourceBundle.getString( key ), localizationArgs.toArray() );
                }
                catch (MissingResourceException e) {
                    //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
                    throw new MissingResourceException( String.format( "Missing resource for: %s, at key: %s.", baseClass, e.getKey() ),
                                                        baseClass.getCanonicalName(), e.getKey() );
                }
            }

            private StringBuilder appendKey(final StringBuilder keyBuilder, final String keyPart) {

                if (keyPart != null && !keyPart.isEmpty()) {
                    logger.dbg( "Appending key part: %s", keyPart );
                    keyBuilder.append( '.' ).append( keyPart );
                }

                return keyBuilder;
            }
        };

        // If the method expects an wrapped object, return that.
        if (Supplier.class.isAssignableFrom( method.getReturnType() ))
            return valueSupplier;
        for (final Map.Entry<Class<?>, Function<Supplier<String>, ?>> classFunctionEntry : wrapperTypes.entrySet())
            if (classFunctionEntry.getKey().isAssignableFrom( method.getReturnType() ))
                return classFunctionEntry.getValue().apply( valueSupplier );

        // Otherwise just resolve the key's value straight away.
        return valueSupplier.get();
    }
}
