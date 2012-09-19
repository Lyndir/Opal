package com.lyndir.lhunath.opal.system.util;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.lang.reflect.InvocationHandler;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.VisibilityPredicate;
import net.sf.cglib.proxy.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;


/**
 * <h2>{@link TypeUtils}<br> <sub>[in short] (TODO).</sub></h2>
 * <p/>
 * <p> <i>10 20, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class TypeUtils {

    static final Logger logger = Logger.get( TypeUtils.class );

    private static final Pattern FIRST_LETTER = Pattern.compile( "(\\w)\\w{2,}\\." );
    private static final Pattern THROWS       = Pattern.compile( " throws [^\\(\\)]*" );

    private static final Objenesis objenesis = new ObjenesisStd();

    /**
     * Load the named class.
     *
     * @param typeName The name of the class that should be loaded.
     * @param <T>      The type of class that the operation should yield (note: unchecked).
     *
     * @return A class object or <code>null</code> if the type could not be found.
     */
    @Nullable
    @SuppressWarnings({ "unchecked" })
    public static <T> Class<T> findClass(final String typeName) {

        try {
            return (Class<T>) Thread.currentThread().getContextClassLoader().loadClass( typeName );
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Load the named class.
     *
     * @param typeName The name of the class that should be loaded.
     * @param <T>      The type of class that the operation should yield (note: unchecked).
     *
     * @return A class object.
     *
     * @throws RuntimeException In case the named class cannot be found in the thread's context classloader.
     */
    @NotNull
    @SuppressWarnings({ "unchecked" })
    public static <T> Class<T> loadClass(final String typeName) {

        try {
            return (Class<T>) Thread.currentThread().getContextClassLoader().loadClass( typeName );
        }
        catch (ClassNotFoundException e) {
            throw Throwables.propagate( e );
        }
    }

    /**
     * Instantiate the given class or turn any exceptions into runtime exceptions.
     *
     * @param type The class that should be instantiated with the default constructor.
     * @param <T>  The type of instance that the operation should yield.
     *
     * @return An instance object of the named type.
     *
     * @throws RuntimeException In case the named class cannot be found in the thread's context classloader or the class cannot be
     *                          instantiated or constructed.
     */
    public static <T> T newInstance(final Class<T> type) {

        try {
            return type.getConstructor().newInstance();
        }
        catch (InstantiationException e) {
            throw Throwables.propagate( e );
        }
        catch (IllegalAccessException e) {
            throw Throwables.propagate( e );
        }
        catch (InvocationTargetException e) {
            throw Throwables.propagate( e );
        }
        catch (NoSuchMethodException e) {
            throw Throwables.propagate( e );
        }
    }

    /**
     * Load and instantiate the named class or turn any exceptions into runtime exceptions.
     *
     * @param typeName The name of the class that should be loaded and instantiated with the default constructor.
     * @param <T>      The type of instance that the operation should yield (note: unchecked).
     *
     * @return An instance object of the named type.
     *
     * @throws RuntimeException In case the named class cannot be found in the thread's context classloader or the class cannot be
     *                          instantiated or constructed.
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> T newInstance(final String typeName) {

        try {
            return (T) loadClass( typeName ).getConstructor().newInstance();
        }
        catch (InstantiationException e) {
            throw Throwables.propagate( e );
        }
        catch (IllegalAccessException e) {
            throw Throwables.propagate( e );
        }
        catch (InvocationTargetException e) {
            throw Throwables.propagate( e );
        }
        catch (NoSuchMethodException e) {
            throw Throwables.propagate( e );
        }
    }

    /**
     * Creates a proxy instance of the given type that triggers the given {@code invocationHandler} whenever a method is invoked on it.
     *
     * The instance is created without invoking its constructor.
     *
     * @param type              The class that defines the methods that can be invoked on the proxy.
     * @param invocationHandler The handler that will be invoked for each method invoked on the proxy.
     * @param <T>               The type of the proxy object.
     *
     * @return An instance of the given <code>type</code> .
     */
    public static <T> T newProxyInstance(final Class<T> type, final InvocationHandler invocationHandler) {

        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            @SuppressWarnings({ "ProhibitedExceptionDeclared" })
            public Object intercept(final Object o, final Method method, final Object[] objects, final MethodProxy methodProxy)
                    throws Throwable {

                return invocationHandler.invoke( o, method, objects );
            }
        };

        Enhancer enhancer = newEnhancer( type );
        enhancer.setCallbackType( interceptor.getClass() );

        Class<?> mockClass = enhancer.createClass();
        Enhancer.registerCallbacks( mockClass, new Callback[]{ interceptor } );

        // cglib code that normally gets called in the constructor.  Since we instantiated without calling the constructor call it manually.
        Factory mock = (Factory) objenesis.newInstance( mockClass );
        mock.getCallback( 0 );

        return type.cast( mock );
    }

    private static Enhancer newEnhancer(final Class<?> type) {

        return new Enhancer() {
            {
                setSuperclass( type );
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void filterConstructors(final Class sc, final List constructors) {

                CollectionUtils.filter( constructors, new VisibilityPredicate( sc, true ) );
            }
        };
    }

    /**
     * Recursively search a type's inheritance hierarchy for an annotation.
     *
     * @param type           The class whose hierarchy to search.
     * @param annotationType The annotation type to search for.
     * @param <A>            The annotation type.
     *
     * @return The annotation of the given annotation type in the given type's hierarchy or <code>null</code> if the type's hierarchy
     *         contains no classes that have the given annotation type set.
     */
    @Nullable
    public static <A extends Annotation> A findAnnotation(final Class<?> type, final Class<A> annotationType) {

        A annotation = type.getAnnotation( annotationType );
        if (annotation != null)
            return annotation;

        for (final Class<?> subType : type.getInterfaces()) {
            annotation = findAnnotation( subType, annotationType );
            if (annotation != null)
                return annotation;
        }
        if (type.getSuperclass() != null) {
            annotation = findAnnotation( type.getSuperclass(), annotationType );
            if (annotation != null)
                return annotation;
        }

        return null;
    }

    /**
     * Recursively search a type's inheritance hierarchy for all instances of the given annotation.
     *
     * @param type           The class whose hierarchy to search.
     * @param annotationType The annotation type to search for.
     * @param <A>            The annotation type.
     *
     * @return A mapping of the given type and its super types to a mapping of that type or one of its implemented interfaces to the
     *         instance of the given annotation the type declares.
     *         <code>[TT = T or a supertype of T -> [ TTT = TT or interface of TT -> annotation on TTT ]]</code>
     */
    @NotNull
    public static <T, A extends Annotation> Map<Class<? super T>, Map<Class<?>, A>> getAnnotations(final Class<T> type,
                                                                                                   final Class<A> annotationType) {

        ImmutableMap.Builder<Class<? super T>, Map<Class<?>, A>> typeHierarchyAnnotations = ImmutableMap.builder();
        ImmutableMap.Builder<Class<?>, A> typeAnnotations = ImmutableMap.builder();

        A annotation = type.getAnnotation( annotationType );
        if (annotation != null)
            typeAnnotations.put( type, annotation );
        for (final Class<?> typeInterface : type.getInterfaces()) {
            annotation = findAnnotation( typeInterface, annotationType );
            if (annotation != null)
                typeAnnotations.put( typeInterface, annotation );
        }
        typeHierarchyAnnotations.put( type, typeAnnotations.build() );

        if (type.getSuperclass() != null)
            typeHierarchyAnnotations.putAll( getAnnotations( type.getSuperclass(), annotationType ) );

        return typeHierarchyAnnotations.build();
    }

    /**
     * Recursively search a method's inheritance hierarchy for an annotation.
     *
     * @param method         The method whose hierarchy to search.
     * @param annotationType The annotation type to search for.
     * @param <A>            The annotation type.
     *
     * @return The annotation of the given annotation type in the given method's hierarchy or <code>null</code> if the method's hierarchy
     *         contains no methods that have the given annotation type set.
     */
    @Nullable
    public static <A extends Annotation> A findAnnotation(final Method method, final Class<A> annotationType) {

        A annotation = method.getAnnotation( annotationType );
        if (annotation != null) {
            //logger.debug( "Found annotation {} on {}", annotationType, method );
            return annotation;
        }

        //logger.debug( "Digging down method for annotation {}, {}", annotationType, method );

        Method superclassMethod = null;
        Class<?> superclass = method.getDeclaringClass();
        while ((superclass = superclass.getSuperclass()) != null)
            try {
                //logger.debug( "Trying for method {} in {}", method.getName(), superclass );
                superclassMethod = superclass.getMethod( method.getName(), method.getParameterTypes() );
            }
            catch (NoSuchMethodException ignored) {
            }
        if (superclassMethod == null) {
            //logger.debug( "Gave up for annotation {} (reached end of hierarchy)", annotationType );
            return null;
        }

        return findAnnotation( superclassMethod, annotationType );
    }

    /**
     * Perform an operation for each type in a given type's hierarchy and/or their interfaces, starting from the type itself.
     *
     * @param type              The type whose hierarchy to descend.
     * @param typeFunction      The operation to perform on each type in the hierarchy.  Evaluated before the interfaceFunction on the
     *                          type.
     * @param interfaceFunction The operation to perform on each interface of each type in the hierarchy. Evaluated after the typeFunction
     *                          on the type.
     * @param firstResult       The lastResult that will be given in the first invocation of the operation function.  It'll also be the
     *                          return value if no functions are invoked.
     * @param <T>               The type whose hierarchy to descend.
     * @param <R>               The type of the result that the operation should generate.
     *
     * @return The final result produced by the last execution of the operation.
     */
    public static <T, R> R forEachSuperTypeOf(@NotNull final Class<T> type,
                                              @Nullable final Function<LastResult<Class<?>, R>, R> typeFunction,
                                              @Nullable final Function<LastResult<Class<?>, R>, R> interfaceFunction,
                                              @Nullable final R firstResult) {

        R lastResult = firstResult;
        try {
            for (Class<? super T> currentType = type; currentType != null; currentType = currentType.getSuperclass()) {
                if (typeFunction != null)
                    lastResult = typeFunction.apply( new LastResult<Class<?>, R>( currentType, lastResult ) );

                if (interfaceFunction != null)
                    for (final Class<?> interfaceType : currentType.getInterfaces())
                        lastResult = interfaceFunction.apply( new LastResult<Class<?>, R>( interfaceType, lastResult ) );
            }
        }
        catch (BreakException e) {
            lastResult = e.<R>getResult();
        }

        return lastResult;
    }

    /**
     * Perform an operation for each field declared in a given type.
     *
     * @param type        The type whose declared fields to iterate.
     * @param function    The operation to perform on each of the declared fields.
     * @param firstResult The lastResult that will be given in the first invocation of the operation function. It'll also be the return
     *                    value if no functions are invoked.
     * @param descend     <code>true</code> if the given type's hierarchy should also be descended to iterate fields declared by subtypes.
     * @param <T>         The type whose declared fields to iterate.
     * @param <R>         The type of the result that the operation should generate.
     *
     * @return The final result produced by the last execution of the operation.
     */
    public static <T, R> R forEachFieldOf(final Class<T> type, final ObjectUtils.NFunctionNN<LastResult<Field, R>, R> function,
                                          @Nullable final R firstResult, final boolean descend) {

        Function<LastResult<Class<?>, R>, R> eachFieldFunction = new Function<LastResult<Class<?>, R>, R>() {
            @Override
            public R apply(final LastResult<Class<?>, R> lastResult) {

                R result = lastResult.getLastResult();
                try {
                    for (final Field field : lastResult.getCurrent().getDeclaredFields())
                        if (!field.isSynthetic() && !Modifier.isStatic( field.getModifiers() )) {
                            logger.trc( "Iteration of %s: %s", lastResult.getCurrent(), field );
                            result = function.apply( new LastResult<Field, R>( field, result ) );
                        }
                }
                catch (BreakException e) {
                    result = e.<R>getResult();
                }

                return result;
            }
        };

        if (descend)
            return forEachSuperTypeOf( type, eachFieldFunction, null, firstResult );

        return eachFieldFunction.apply( new LastResult<Class<?>, R>( type, firstResult ) );
    }

    public static Field findFirstField(final Object owner, final Object value) {

        return forEachFieldOf( owner.getClass(), new ObjectUtils.NFunctionNN<LastResult<Field, Field>, Field>() {
            @Override
            public Field apply(@NotNull final LastResult<Field, Field> from) {

                try {
                    from.getCurrent().setAccessible( true );
                    if (ObjectUtils.equals( from.getCurrent().get( owner ), value ))
                        throw new BreakException( from.getCurrent() );
                }
                catch (IllegalAccessException e) {
                    throw logger.bug( e );
                }

                return from.getLastResult();
            }
        }, null, true );
    }

    /**
     * Recursively search a type's inheritance hierarchy for an annotation.
     *
     * @param type           The class whose hierarchy to search.
     * @param annotationType The annotation type to search for.
     *
     * @return true if the annotation exists in the type's hierarchy.
     */
    public static boolean hasAnnotation(final Class<?> type, final Class<? extends Annotation> annotationType) {

        return findAnnotation( type, annotationType ) != null;
    }

    /**
     * Find the enum value of the given enum type with the given identifier (name).
     *
     * @param type  The enum type for which to obtain a value.
     * @param value The name of the enum value to obtain.
     * @param <T>   The enum type for which to obtain a value.
     *
     * @return The enum value.
     *
     * @throws IllegalArgumentException if the given enum type has no member named by the given value.
     */
    public static <T extends Enum<T>> T valueOfEnum(final Class<T> type, final String value) {

        return Enum.valueOf( type, value );
    }

    /**
     * Type-forced version of {@link #valueOfEnum(Class, String)}.  Does not require the class to be an Enum class.  Really only useful if
     * you've got a <code>Class<?></code> and you have no clue what enum is in it and you've already done a {@link Class#isEnum()} to
     * verify
     * that it really is an enum.
     *
     * @param type  The enum type for which to obtain a value.
     * @param value The name of the enum value to obtain.
     * @param <T>   The enum type for which to obtain a value.
     *
     * @return The enum value.
     *
     * @see #valueOfEnum(Class, String)
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> T unsafeValueOfEnum(final Class<T> type, final String value) {

        return type.cast( valueOfEnum( (Class<Enum>) type, value ) );
    }

    @SuppressWarnings({ "unchecked" })
    public static <T extends Enum<T>> Class<T> checkEnum(final Class<?> type) {

        checkArgument( type.isEnum(), "%s is not an enum.", type );
        return (Class<T>) type;
    }

    public static String propertyName(Method method) {

        String methodName = method.getName();
        if ((methodName.startsWith( "get" ) || methodName.startsWith( "set" )) && methodName.length() > 3)
            methodName = methodName.substring( 3 );
        else if (methodName.startsWith( "is" ) && methodName.length() > 2)
            methodName = methodName.substring( 2 );

        //noinspection StringConcatenation
        return methodName.substring( 0, 1 ).toLowerCase() + methodName.substring( 1 );
    }

    /**
     * Compress the generic form of the method's signature. Trim off throws declarations.<br> java.lang.method -> j~l~method
     *
     * @param signature The signature that needs to be compressed.
     *
     * @return The compressed signature.
     */
    public static String compressSignature(final CharSequence signature) {

        String compressed = FIRST_LETTER.matcher( signature ).replaceAll( "$1~" );
        return THROWS.matcher( compressed ).replaceFirst( "" );
    }

    public static class LastResult<C, R> {

        private final C current;
        private final R lastResult;

        public LastResult(final C current, final R lastResult) {

            this.current = current;
            this.lastResult = lastResult;
        }

        public C getCurrent() {

            return current;
        }

        @Nullable
        public R getLastResult() {

            return lastResult;
        }
    }


    public static class BreakException extends RuntimeException {

        private final transient Object result;

        public BreakException(final Object result) {

            this.result = result;
        }

        @SuppressWarnings({ "unchecked" })
        public <R> R getResult() {

            return (R) result;
        }
    }
}
