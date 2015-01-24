package com.lyndir.lhunath.opal.system.util;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;

import com.google.common.base.Function;
import java.util.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.error.BreakException;
import com.lyndir.lhunath.opal.system.error.InternalInconsistencyException;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.VisibilityPredicate;
import net.sf.cglib.proxy.*;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;


/**
 * <h2>{@link TypeUtils}<br> <sub>[in short] (TODO).</sub></h2>
 * <p>
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
     * @return A present class object or absent if the class could not be loaded.
     */
    @Nonnull
    @SuppressWarnings({ "unchecked" })
    public static <T> Optional<Class<T>> loadClass(final String typeName) {

        try {
            return Optional.of( (Class<T>) Thread.currentThread().getContextClassLoader().loadClass( typeName ) );
        }
        catch (final ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    /**
     * Instantiate the given class or turn any exceptions into runtime exceptions.
     *
     * @param type The class that should be instantiated with the default constructor.
     *
     * @return A present instance object of the named type or absent if the instance could not be created.
     */
    public static <T> Optional<T> newInstance(final Class<? extends T> type) {

        try {
            return Optional.of( type.getConstructor().newInstance() );
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoClassDefFoundError ignored) {
            return Optional.empty();
        }
    }

    /**
     * Load and instantiate the named class or turn any exceptions into runtime exceptions.
     *
     * @param typeName The name of the class that should be loaded and instantiated with the default constructor.
     * @param <T>      The type of instance that the operation should yield (note: unchecked).
     *
     * @return A present class instance or absent if the class could not be loaded.
     *
     * @throws RuntimeException In case the named class cannot be found in the thread's context classloader or the class cannot be
     *                          instantiated or constructed.
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> Optional<T> newInstance(final String typeName) {

        Optional<Class<T>> type = loadClass( typeName );
        if (!type.isPresent()) {
            return Optional.empty();
        }

        return newInstance( type.get() );
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
     * @return An instance of the given {@code type} .
     */
    @SuppressWarnings("UnnecessaryFullyQualifiedName")
    public static <T> T newProxyInstance(final Class<? extends T> type, final java.lang.reflect.InvocationHandler invocationHandler) {

        Callback interceptor = (MethodInterceptor) (o, method, objects, methodProxy) -> invocationHandler.invoke( o, method, objects );

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
            @SuppressWarnings({ "unchecked", "rawtypes" })
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
     * @return The annotation of the given annotation type in the given type's hierarchy or {@code null} if the type's hierarchy
     * contains no classes that have the given annotation type set.
     */
    @Nullable
    public static <A extends Annotation> A findAnnotation(final Class<?> type, final Class<? extends A> annotationType) {

        A annotation = type.getAnnotation( annotationType );
        if (annotation != null) {
            return annotation;
        }

        for (final Class<?> subType : type.getInterfaces()) {
            annotation = findAnnotation( subType, annotationType );
            if (annotation != null) {
                return annotation;
            }
        }
        if (type.getSuperclass() != null) {
            annotation = findAnnotation( type.getSuperclass(), annotationType );
            if (annotation != null) {
                return annotation;
            }
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
     * instance of the given annotation the type declares.
     * {@code [TT = T or a supertype of T -> [ TTT = TT or interface of TT -> annotation on TTT ]]}
     */
    @Nonnull
    public static <A extends Annotation> Map<Class<?>, Map<Class<?>, A>> getAnnotations(final Class<?> type,
                                                                                        final Class<? extends A> annotationType) {

        ImmutableMap.Builder<Class<?>, Map<Class<?>, A>> typeHierarchyAnnotations = ImmutableMap.builder();
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
     * @return The annotation of the given annotation type in the given method's hierarchy or {@code null} if the method's hierarchy
     * contains no methods that have the given annotation type set.
     */
    @Nullable
    public static <A extends Annotation> A findAnnotation(final Method method, final Class<? extends A> annotationType) {

        A annotation = method.getAnnotation( annotationType );
        if (annotation != null) {
            //logger.debug( "Found annotation {} on {}", annotationType, method );
            return annotation;
        }

        //logger.debug( "Digging down method for annotation {}, {}", annotationType, method );

        Method superclassMethod = null;
        Class<?> superclass = method.getDeclaringClass();
        while ((superclass = superclass.getSuperclass()) != null) {
            try {
                //logger.debug( "Trying for method {} in {}", method.getName(), superclass );
                superclassMethod = superclass.getMethod( method.getName(), method.getParameterTypes() );
            }
            catch (final NoSuchMethodException ignored) {
            }
        }
        if (superclassMethod == null) {
            //logger.debug( "Gave up for annotation {} (reached end of hierarchy)", annotationType );
            return null;
        }

        return findAnnotation( superclassMethod, annotationType );
    }

    public static <E> ImmutableList<Constructor<E>> findConstructors(final Class<? extends E> type, final Object... args) {
        ImmutableList.Builder<Constructor<E>> constructors = ImmutableList.builder();
        for (final Constructor<?> constructor : type.getDeclaredConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            boolean compatible = parameterTypes.length == args.length;
            if (!compatible) {
                logger.dbg( "constructor: %s, not compatible in parameter length: %d != %d", constructor, parameterTypes.length,
                            args.length );
                continue;
            }

            for (int i = 0; i < parameterTypes.length; i++) {
                if (!parameterTypes[i].isInstance( args[i] )) {
                    logger.dbg( "constructor: %s, not compatible in parameter type: !%s.isInstance(%s)", constructor, parameterTypes[i],
                                args[i] );
                    compatible = false;
                    break;
                }
            }
            if (!compatible) {
                continue;
            }

            // Constructor guaranteed of type <E> because it comes from Class<E> type.
            //noinspection unchecked
            constructors.add( (Constructor<E>) constructor );
        }

        return constructors.build();
    }

    public static <E> Constructor<E> getConstructor(final Class<? extends E> type, final Object... args) {
        ImmutableList<Constructor<E>> constructors = findConstructors( type, args );
        if (constructors.isEmpty()) {
            throw new InternalInconsistencyException( strf( "No constructors of type: %s, match argument types: %s", type,
                                                            Lists.transform( Arrays.asList( args ), new Function<Object, Object>() {
                                                                @Override
                                                                public Object apply(final Object input) {
                                                                    return input == null? "<null>": input.getClass();
                                                                }
                                                            } ) ) );
        }
        if (constructors.size() > 1) {
            throw new InternalInconsistencyException(
                    strf( "Multiple constructors of type: %s, match argument types: %s, candidates: %s", type,
                          Lists.transform( Arrays.asList( args ), new Function<Object, Object>() {
                              @Override
                              public Object apply(final Object input) {
                                  return input == null? "<null>": input.getClass();
                              }
                          } ), constructors ) );
        }

        return constructors.get( 0 );
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
    @Nullable
    public static <T, R> R forEachSuperTypeOf(@Nonnull final Class<T> type,
                                              @Nullable final NFunctionNN<LastResult<Class<?>, R>, R> typeFunction,
                                              @Nullable final NFunctionNN<LastResult<Class<?>, R>, R> interfaceFunction,
                                              @Nullable final R firstResult) {

        R lastResult = firstResult;
        try {
            for (Class<? super T> currentType = type; currentType != null; currentType = currentType.getSuperclass()) {
                if (typeFunction != null) {
                    lastResult = typeFunction.apply( new LastResult<>( currentType, lastResult ) );
                }
                if (interfaceFunction != null) {
                    for (final Class<?> interfaceType : currentType.getInterfaces()) {
                        lastResult = interfaceFunction.apply( new LastResult<>( interfaceType, lastResult ) );
                    }
                }
            }
        }
        catch (final BreakException e) {
            lastResult = e.getResult();
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
     * @param descend     {@code true} if the given type's hierarchy should also be descended to iterate fields declared by subtypes.
     * @param <T>         The type whose declared fields to iterate.
     * @param <R>         The type of the result that the operation should generate.
     *
     * @return The final result produced by the last execution of the operation.
     */
    @Nullable
    public static <T, R> R forEachFieldOf(final Class<? extends T> type, final NFunctionNN<LastResult<Field, R>, R> function,
                                          @Nullable final R firstResult, final boolean descend) {

        NFunctionNN<LastResult<Class<?>, R>, R> eachFieldFunction = new NFunctionNN<LastResult<Class<?>, R>, R>() {
            @Nullable
            @Override
            @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
            public R apply(@Nonnull final LastResult<Class<?>, R> lastResult) {

                R result = lastResult.getLastResult();
                try {
                    for (final Field field : lastResult.getCurrent().getDeclaredFields()) {
                        if (!field.isSynthetic() && !Modifier.isStatic( field.getModifiers() )) {
                            logger.trc( "Iteration of %s: %s", lastResult.getCurrent(), field );
                            result = function.apply( new LastResult<>( field, result ) );
                        }
                    }
                }
                catch (final BreakException e) {
                    result = e.getResult();
                }

                return result;
            }
        };

        if (descend) {
            return forEachSuperTypeOf( type, eachFieldFunction, null, firstResult );
        }

        return eachFieldFunction.apply( new LastResult<>( type, firstResult ) );
    }

    @Nullable
    public static Field findFirstField(final Object owner, final Object value) {

        return forEachFieldOf( owner.getClass(), input -> {

            try {
                input.getCurrent().setAccessible( true );
                if (ObjectUtils.equals( input.getCurrent().get( owner ), value )) {
                    throw new BreakException( input.getCurrent() );
                }
            }
            catch (final IllegalAccessException e) {
                throw logger.bug( e );
            }

            return input.getLastResult();
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

    public static String propertyName(@SuppressWarnings("TypeMayBeWeakened") final Method method) {

        String methodName = method.getName();
        if ((methodName.startsWith( "get" ) || methodName.startsWith( "set" )) && methodName.length() > 3) {
            methodName = methodName.substring( 3 );
        } else if (methodName.startsWith( "is" ) && methodName.length() > 2) {
            methodName = methodName.substring( 2 );
        }

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

        public LastResult(@Nonnull final C current, @Nullable final R lastResult) {

            this.current = current;
            this.lastResult = lastResult;
        }

        @Nonnull
        public C getCurrent() {

            return current;
        }

        @Nullable
        public R getLastResult() {

            return lastResult;
        }
    }
}
