package com.lyndir.lhunath.lib.system.util;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <h2>{@link TypeUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>10 20, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class TypeUtils {

    static final Logger logger = LoggerFactory.getLogger( TypeUtils.class );

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
    public static <A extends Annotation> A findAnnotation(Class<?> type, Class<A> annotationType) {

        A annotation = type.getAnnotation( annotationType );
        if (annotation != null)
            return annotation;

        for (Class<?> subType : type.getInterfaces()) {
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
     * Recursively search a method's inheritance hierarchy for an annotation.
     *
     * @param method         The method whose hierarchy to search.
     * @param annotationType The annotation type to search for.
     * @param <A>            The annotation type.
     *
     * @return The annotation of the given annotation type in the given method's hierarchy or <code>null</code> if the method's hierarchy
     *         contains no methods that have the given annotation type set.
     */
    public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {

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
        if (superclass == null) {
            //logger.debug( "Gave up for annotation {} (reached end of hierarchy)", annotationType );
            return null;
        }

        return findAnnotation( superclassMethod, annotationType );
    }

    /**
     * Perform an operation for each type in a given type's hierarchy, starting from the type itself.
     *
     * @param type     The type whose hierarchy to descend.
     * @param function The operation to perform on each type in the hierarchy.
     * @param <T>      The type whose hierarchy to descend.
     * @param <R>      The type of the result that the operation should generate.
     *
     * @return The final result produced by the last execution of the operation.
     */
    public static <T, R> R forEachSubtypeOf(Class<T> type, Function<LastResult<Class<? super T>, R>, R> function) {

        R lastResult = null;
        for (Class<? super T> currentType = type; currentType.getSuperclass() != null; currentType = currentType.getSuperclass())
            lastResult = function.apply( new LastResult<Class<? super T>, R>( currentType, lastResult ) );

        return lastResult;
    }

    /**
     * Perform an operation for each field declared in a given type.
     *
     * @param type     The type whose declared fields to iterate.
     * @param function The operation to perform on each of the declared fields.
     * @param descend  <code>true</code> if the given type's hierarchy should also be descended to iterate fields declared by subtypes.
     * @param <T>      The type whose declared fields to iterate.
     * @param <R>      The type of the result that the operation should generate.
     *
     * @return The final result produced by the last execution of the operation.
     */
    public static <T, R> R forEachFieldOf(Class<T> type, final Function<LastResult<Field, R>, R> function, boolean descend) {

        Function<LastResult<Class<? super T>, R>, R> eachFieldFunction = new Function<LastResult<Class<? super T>, R>, R>() {
            @Override
            public R apply(final LastResult<Class<? super T>, R> lastResult) {

                R result = lastResult.getLastResult();
                for (Field field : lastResult.getCurrent().getDeclaredFields())
                    result = function.apply( new LastResult<Field, R>( field, result ) );

                return result;
            }
        };

        if (descend)
            return forEachSubtypeOf( type, eachFieldFunction );

        return eachFieldFunction.apply( new LastResult<Class<? super T>, R>( type, null ) );
    }

    /**
     * Recursively search a type's inheritance hierarchy for an annotation.
     *
     * @param type           The class whose hierarchy to search.
     * @param annotationType The annotation type to search for.
     *
     * @return true if the annotation exists in the type's hierarchy.
     */
    public static boolean hasAnnotation(Class<?> type, Class<? extends Annotation> annotationType) {

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
    public static <T extends Enum<T>> T valueOfEnum(Class<T> type, String value) {

        return Enum.valueOf( type, value );
    }

    /**
     * Type-forced version of {@link #valueOfEnum(Class, String)}.  Does not require the class to be an Enum class.  Really only useful if
     * you've got a <code>Class<?></code> and you have no clue what enum is in it and you've already done a {@link Class#isEnum()} to verify
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
    @SuppressWarnings( { "unchecked" })
    public static <T> T unsafeValueOfEnum(Class<T> type, String value) {

        return type.cast( valueOfEnum( (Class<Enum>) type, value ) );
    }

    @SuppressWarnings( { "unchecked" })
    public static <T extends Enum<T>> Class<T> checkEnum(final Class<?> type) {

        checkArgument( type.isEnum(), "%s is not an enum.", type );
        return (Class<T>) type;
    }

    public static class LastResult<C, R> {

        private final C current;
        private final R lastResult;

        public LastResult(C current, R lastResult) {

            this.current = current;
            this.lastResult = lastResult;
        }

        public C getCurrent() {

            return current;
        }

        public R getLastResult() {

            return lastResult;
        }
    }
}
