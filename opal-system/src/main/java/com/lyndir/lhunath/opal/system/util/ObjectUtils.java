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


package com.lyndir.lhunath.opal.system.util;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.*;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.lyndir.lhunath.opal.system.error.AlreadyCheckedException;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ObjectMeta.For;
import com.lyndir.lhunath.opal.system.util.TypeUtils.LastResult;
import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <h2>{@link ObjectUtils}<br> <sub>[in short] (TODO).</sub></h2>
 * <p>
 * <p> <i>Mar 22, 2010</i> </p>
 *
 * @author lhunath
 */
@SuppressWarnings({ "UnusedDeclaration" })
public abstract class ObjectUtils {

    static final Logger logger = Logger.get( ObjectUtils.class );

    private static final Pattern                             NON_PRINTABLE            = Pattern.compile( "[^\\p{Print}]" );
    private static final int                                 MAX_DECODE_LENGTH        = 100;
    private static final Map<For, ThreadLocal<Set<Integer>>> seen                     = Maps.newEnumMap( For.class );
    private static final int                                 HASHCODE_PRIME           = 524287;
    private static final Optional<Class<Object>>             persistentCollectionType = TypeUtils.loadClass(
            "org.hibernate.collection.PersistentCollection" );

    static {
        for (final For forMeta : For.values()) {
            seen.put( forMeta, new ThreadLocal<Set<Integer>>() {
                @Override
                protected Set<Integer> initialValue() {

                    return Sets.newHashSet();
                }
            } );
        }
    }

    private static final Pattern PACKAGE_NODE = Pattern.compile( "([^\\.])[^\\.]+\\." );
    private static final Pattern PACKAGE      = Pattern.compile( ".*\\." );

    /**
     * Check whether two objects are equal according to {@link #equals(Object)}.
     * <p>
     * <p> <b>NOTE:</b> This method is {@code null}-safe and two {@code null} objects are also considered equal. </p>
     * <p>
     * <p> <b>NOTE:</b> This method attempts to aid in type safety of the objects that are being compared. </p>
     *
     * @param <C>         The type of the first parameter.  It should be of the same type or a subtype (more concrete type) of the second
     *                    parameter.
     * @param <P>         The type of the second parameter. The type of this parameter must be the same type or more generic
     *                    assignment-compatible to that of the first parameter.
     * @param subObject   The first object, or {@code null}.
     * @param superObject The second object, or {@code null}.
     *
     * @return {@code true} if both objects are {@code null} or if neither are and {@link #equals(Object)} considers them equal.
     */
    public static <P, C extends P> boolean isEqual(@Nullable final C subObject, @Nullable final P superObject) {

        // Get the simple stuff out of the way.
        //noinspection ObjectEquality
        if (subObject == superObject)
            return true;
        if (subObject == null)
            return false;

        // Be smart about arrays.  In a really ugly and dumb instanceof-kind of way.  Feel free to think of a better way.
        if (subObject.getClass().isArray())
            return Arrays.deepEquals( new Object[]{ subObject }, new Object[]{ superObject } );

        // Use equals() for the rest.
        return subObject.equals( superObject );
    }

    /**
     * Provide a description for the given object.  Certain types are handled specially to format the description in a concise and useful
     * manner.  Objects of types that are not handled specially yield the same result as {@link String#valueOf(Object)}.
     *
     * @param o The object to describe.
     *
     * @return A description of the given object.
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static String describe(final Object o) {

        if (o == null)
            return "<null>";

        if (Class.class.isInstance( o )) {
            Class<?> type = (Class<?>) o;
            return String.format( "<C: %s>", PACKAGE_NODE.matcher( type.getName() ).replaceAll( "$1." ) );
        }

        if (byte[].class.isInstance( o )) {
            byte[] byteArray = (byte[]) o;
            StringBuilder toString = new StringBuilder( String.format( "<b[]: %dB, ", byteArray.length ) );

            // Decode some bytes.
            CharBuffer decodedBytes = Charsets.UTF_8.decode(
                    ByteBuffer.wrap( byteArray, 0, Math.min( byteArray.length, MAX_DECODE_LENGTH ) ) );
            String stripped = NON_PRINTABLE.matcher( decodedBytes ).replaceAll( "." );
            toString.append( stripped );

            // Append trimmed indicator if not all bytes were decoded.
            if (byteArray.length > MAX_DECODE_LENGTH)
                toString.append( "[...]" );

            return toString.append( '>' ).toString();
        }

        if (char[].class.isInstance( o )) {
            char[] charArray = (char[]) o;
            StringBuilder toString = new StringBuilder( String.format( "<c[]: #%d, ", charArray.length ) );

            // Decode some bytes.
            toString.append( charArray, 0, Math.min( charArray.length, MAX_DECODE_LENGTH ) );

            // Append trimmed indicator if not all bytes were decoded.
            if (charArray.length > MAX_DECODE_LENGTH)
                toString.append( "[...]" );

            return toString.append( '>' ).toString();
        }

        if (Object[].class.isInstance( o ))
            return String.format( "<O[]:%s>", Arrays.asList( (Object[]) o ).toString() );

        if (o instanceof String)
            return String.format( "\"%s\"", o );

        if (o instanceof Map) {
            StringBuilder description = new StringBuilder().append( "<M:[" );
            synchronized (o) {
                for (final Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
                    if (description.length() > 1)
                        description.append( "], [" );

                    description.append( describe( entry.getKey() ) ).append( '=' ).append( describe( entry.getValue() ) );
                }
            }

            return description.append( "]>" ).toString();
        }

        if (o instanceof Iterable) {
            Iterable<?> collection = (Iterable<?>) o;
            StringBuilder description = new StringBuilder().append( '[' );
            synchronized (collection) {
                for (final Object entry : collection) {
                    if (description.length() > 1)
                        description.append( ", " );

                    description.append( describe( entry ) );
                }
            }

            return description.append( ']' ).toString();
        }

        if (o instanceof X509Certificate) {
            X509Certificate x509Certificate = (X509Certificate) o;
            return String.format( "<Cert: DN=%s, Issuer=%s>", x509Certificate.getSubjectX500Principal().getName(),
                                  x509Certificate.getIssuerX500Principal().getName() );
        }

        return String.valueOf( o );
    }

    /**
     * Generate a nice {@link Object#toString()} description of the given object using its fields.  The fields used depend on how the
     * object's type has been annotated with {@link ObjectMeta}.
     *
     * @param o The object for which a toString description should be generated (inside your {@link Object#equals(Object)}, use
     *          {@code this}).
     *
     * @return A description of specific field and values of the given object.
     */
    public static String toString(final Object o) {

        StringBuilder toString = new StringBuilder( "{" );
        toString.append( PACKAGE.matcher( o.getClass().getName() ).replaceFirst( "" ) );

        int identityHashCode = System.identityHashCode( o );
        toString.append( '[' ).append( identityHashCode ).append( ']' );

        if (!seen.get( For.toString ).get().add( identityHashCode ))
            // Cyclic reference.
            return toString.append( '}' ).toString();

        try {
            toString.append(
                    forEachFieldWithMeta( For.toString, o.getClass(), new NFunctionNN<LastResult<Field, StringBuilder>, StringBuilder>() {
                        @Override
                        @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
                        public StringBuilder apply(@Nonnull final LastResult<Field, StringBuilder> lastResult) {

                            Field field = lastResult.getCurrent();
                            StringBuilder fieldsString = lastResult.getLastResult();
                            assert fieldsString != null;

                            if (!isValueAccessible( o ))
                                return fieldsString;

                            String name = null;
                            ObjectMeta fieldMeta = field.getAnnotation( ObjectMeta.class );
                            if (fieldMeta != null)
                                name = fieldMeta.name();
                            if (name == null || name.isEmpty())
                                name = field.getName();

                            if (fieldsString.length() == 0)
                                fieldsString.append( ": " );
                            else
                                fieldsString.append( ", " );

                            try {
                                field.setAccessible( true );
                            }
                            catch (final SecurityException ignored) {
                            }

                            try {
                                fieldsString.append( name ).append( '=' ).append( describe( field.get( o ) ) );
                            }
                            catch (final Throwable t) {
                                logger.dbg( t, "Couldn't load value for field: %s, in object: 0x%x", field, System.identityHashCode( o ) );
                            }

                            return fieldsString;
                        }
                    }, new StringBuilder() ) );
        }
        finally {
            seen.get( For.toString ).get().remove( identityHashCode );
        }

        return toString.append( '}' ).toString();
    }

    /**
     * Generate a decent generic {@link Object#hashCode()} for the given object using its fields.  The fields used depend on how the
     * object's type has been annotated with {@link ObjectMeta}.
     *
     * @param o The object for which a hashCode should be generated (inside your {@link Object#equals(Object)}, use {@code this}).
     *
     * @return A hashCode of specific field values of the given object.
     */
    public static int hashCode(final Object o) {

        int identityHashCode = System.identityHashCode( o );
        logger.trc( "%sHashCode for: %s (%d)", StringUtils.indent( seen.get( For.hashCode ).get().size() ), //
                    o.getClass().getName(), identityHashCode );

        if (seen.get( For.hashCode ).get().contains( identityHashCode )) {
            // Cyclic reference.
            logger.trc( "%s- Detected cycle, returning identity.", StringUtils.indent( seen.get( For.hashCode ).get().size() + 1 ),
                        identityHashCode );
            return identityHashCode;
        }

        try {
            seen.get( For.hashCode ).get().add( identityHashCode );
            return ifNotNullElse( forEachFieldWithMeta( For.hashCode, o.getClass(), new NFunctionNN<LastResult<Field, Integer>, Integer>() {
                @Override
                @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
                public Integer apply(@Nonnull final LastResult<Field, Integer> lastResult) {

                    Field field = lastResult.getCurrent();
                    Integer lastHashCode = lastResult.getLastResult();
                    if (lastHashCode == null)
                        lastHashCode = 0;

                    // Field's value
                    Object value = null;
                    try {
                        field.setAccessible( true );
                        value = field.get( o );
                    }
                    catch (SecurityException | IllegalAccessException ignored) {
                    }

                    // Field's value's hashCode
                    int hashCode = System.identityHashCode( value );
                    if (value != null && isValueAccessible( value ))
                        try {
                            if (value instanceof Iterable) {
                                // Best-effort special handling for Iterables in case they don't implement hashCode themselves.
                                // We just use the hashCode of the sorted hashCodes of the values.
                                ImmutableSortedSet.Builder<Integer> hashCodes = ImmutableSortedSet.naturalOrder();
                                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                                synchronized (value) {
                                    for (final Object o_ : (Iterable<?>) value)
                                        hashCodes.add( ObjectUtils.hashCode( o_ ) );
                                }
                                hashCode = hashCodes.build().hashCode();
                            } else
                                hashCode = value.hashCode();
                        }
                        catch (final Throwable t) {
                            logger.dbg( t, "Couldn't load hashCode for: %s, value: %s.  Falling back to identity hashCode.", field, value );
                        }

                    // Increment the total hashCode with this field's value's hashCode
                    int newHashCode = HASHCODE_PRIME * lastHashCode + hashCode;
                    logger.trc( "%s- %s=%d (hashCode -> %d)", StringUtils.indent( seen.get( For.hashCode ).get().size() ), //
                                field.getName(), hashCode, newHashCode );

                    return newHashCode;
                }
            }, null ), new NNSupplier<Integer>() {
                @Nonnull
                @Override
                public Integer get() {

                    // This object has no meta fields fit for hashCode usage.  Fall back to the object's own hashCode implementation.
                    return o.hashCode();
                }
            } );
        }
        finally {
            seen.get( For.hashCode ).get().remove( identityHashCode );
        }
    }

    /**
     * Some type-specific checks to see whether the object can be used.
     *
     * @param object The object to check.
     *
     * @return {@code true} if the object may be accessed.
     */
    private static boolean isValueAccessible(final Object object) {

        if (persistentCollectionType.isPresent() && persistentCollectionType.get().isInstance( object ))
            try {
                if (!(Boolean) object.getClass().getMethod( "wasInitialized" ).invoke( object ))
                    // Hack around Hibernate idiocy of manually logging its LazyInitializationException.
                    return false;
            }
            catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new AlreadyCheckedException( "Are you using an unsupported version of Hibernate?", e );
            }

        return true;
    }

    /**
     * Determine whether superObject and subObject are equal.  This method returns true only when subObject is of a subtype of superObject
     * and all superObject type's fields selected for inclusion by {@link ObjectMeta} annotations have equal values as those of subObject.
     *
     * @param superObject The object that should be compared to subObject (inside your {@link Object#equals(Object)}, use
     *                    {@code this}).
     * @param subObject   The object that should be compared to superObject.
     *
     * @return {@code true} if both objects are equal according to this method's rules.
     */
    public static boolean equals(@Nullable final Object superObject, @Nullable final Object subObject) {

        //noinspection ObjectEquality
        if (superObject == subObject)
            return true;
        if (superObject == null || subObject == null)
            return false;
        if (!superObject.getClass().isAssignableFrom( subObject.getClass() ))
            return false;

        int identityHashCode = System.identityHashCode( superObject );
        if (seen.get( For.equals ).get().contains( identityHashCode ))
            // Cyclic reference.  We return true as a way of "skipping this field".
            return true;

        try {
            seen.get( For.equals ).get().add( identityHashCode );
            return ifNotNullElse(
                    forEachFieldWithMeta( For.equals, superObject.getClass(), new NFunctionNN<LastResult<Field, Boolean>, Boolean>() {
                        @Nonnull
                        @Override
                        @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
                        public Boolean apply(@Nonnull final LastResult<Field, Boolean> lastResult) {

                            if (Boolean.FALSE.equals( lastResult.getLastResult() ))
                                // One 'false' means equals fails.  Don't bother with other fields.
                                return Boolean.FALSE;

                            Field field = lastResult.getCurrent();
                            try {
                                field.setAccessible( true );
                            }
                            catch (final SecurityException ignored) {
                            }

                            Object superValue = null, subValue = null;
                            try {
                                if (isValueAccessible( superObject ))
                                    superValue = field.get( superObject );
                            }
                            catch (final Throwable t) {
                                logger.dbg( t, "Couldn't load value for field: %s, in object: %s", field, superObject );
                            }
                            try {
                                if (isValueAccessible( subObject ))
                                    subValue = field.get( subObject );
                            }
                            catch (final Throwable t) {
                                logger.dbg( t, "Couldn't load value for field: %s, in object: %s", field, subObject );
                            }

                            return Objects.equal( superValue, subValue );
                        }
                    }, null ), false /* There are no (accessible) fields to compare. */ );
        }
        finally {
            seen.get( For.equals ).get().remove( identityHashCode );
        }
    }

    @Nullable
    private static <R, T> R forEachFieldWithMeta(final For meta, final Class<T> type, final NFunctionNN<LastResult<Field, R>, R> function,
                                                 @Nullable final R firstResult) {

        return TypeUtils.forEachSuperTypeOf( type, new NFunctionNN<LastResult<Class<?>, R>, R>() {
            @Override
            @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
            public R apply(@Nonnull final LastResult<Class<?>, R> lastTypeResult) {

                Class<?> subType = lastTypeResult.getCurrent();
                final boolean usedByType = usesMeta( meta, subType );

                return TypeUtils.forEachFieldOf( subType, new NFunctionNN<LastResult<Field, R>, R>() {
                    @Nullable
                    @Override
                    public R apply(@Nonnull final LastResult<Field, R> input) {

                        Field field = input.getCurrent();
                        R result = input.getLastResult();

                        if (Modifier.isStatic( field.getModifiers() ))
                            return result;
                        if (field.isAnnotationPresent( ObjectMeta.class )) {
                            boolean usedByField = usesMeta( meta, field );

                            if (!usedByField)
                                return result;
                        } else
                            // Field has no @ObjectMeta, default to type's decision.
                            if (!usedByType)
                                return result;

                        return function.apply( new LastResult<>( field, result ) );
                    }
                }, lastTypeResult.getLastResult(), false );
            }
        }, null, firstResult );
    }

    private static <T> boolean usesMeta(final For meta, final Class<T> type) {

        for (final Entry<Class<?>, Map<Class<?>, ObjectMeta>> annotationEntry : TypeUtils.getAnnotations( type, ObjectMeta.class )
                                                                                         .entrySet()) {
            Class<?> superType = annotationEntry.getKey();
            Map<Class<?>, ObjectMeta> superTypeAnnotations = annotationEntry.getValue();

            // Does the superType have an annotation?
            ObjectMeta superTypeAnnotation = superTypeAnnotations.get( superType );
            if (superTypeAnnotation != null) {
                if (superType == type || superTypeAnnotation.inherited())
                    // The superType is the type or is inheritable by the type, use its annotation.
                    return usesMeta( meta, superTypeAnnotation );

                // superType has an annotation, stop descending hierarchy.
                break;
            }

            // superType has no annotation, look at its implemented interfaces.
            for (final ObjectMeta annotation : superTypeAnnotations.values())
                if (superType == type || annotation.inherited())
                    // The superType is the type or is inheritable by the type, use its annotation.
                    if (usesMeta( meta, annotation ))
                        return true;
        }

        return false;
    }

    private static boolean usesMeta(final For meta, final Field field) {

        return usesMeta( meta, field.getAnnotation( ObjectMeta.class ) );
    }

    private static boolean usesMeta(final For meta, final ObjectMeta metaAnnotation) {

        if (metaAnnotation == null)
            return false;

        List<For> uses = ImmutableList.copyOf( metaAnnotation.useFor() );
        List<For> ignores = ImmutableList.copyOf( metaAnnotation.ignoreFor() );

        if (ignores.contains( For.all ) || ignores.contains( meta ))
            return false;

        return uses.contains( For.all ) || uses.contains( meta );
    }

    /**
     * @param value     The value to return, if it isn't {@code null}.
     * @param nullValue The value to return if {@code value} is {@code null}.
     * @param <T>       The type of object to return.
     *
     * @return One of two values.
     */
    @Nonnull
    public static <T> T ifNotNullElse(@Nullable final T value, @Nonnull final T nullValue) {

        if (value == null)
            return checkNotNull( nullValue );

        return value;
    }

    /**
     * @param value     The value to return, if it isn't {@code null}.
     * @param nullValue The value to return if {@code value} is {@code null}.
     * @param <T>       The type of object to return.
     *
     * @return One of two values.
     */
    @Nullable
    public static <T> T ifNotNullElseNullable(@Nullable final T value, @Nullable final T nullValue) {

        if (value == null)
            return nullValue;

        return value;
    }

    /**
     * Version of {@link #ifNotNullElse(Object, Object)} that loads the default value lazily.
     *
     * @param value             The value to return, if it isn't {@code null}.
     * @param nullValueSupplier Provides the value to return if {@code value} is {@code null}.  The supplier is only
     *                          consulted
     *                          if necessary.
     * @param <T>               The type of object to return.
     *
     * @return One of two values.
     */
    @Nonnull
    public static <T> T ifNotNullElse(@Nullable final T value, final NNSupplier<T> nullValueSupplier) {

        if (value != null)
            return value;

        return checkNotNull( nullValueSupplier.get() );
    }

    /**
     * Version of {@link #ifNotNullElse(Object, Object)} that loads the default value lazily.
     *
     * @param value             The value to return, if it isn't {@code null}.
     * @param nullValueSupplier Provides the value to return if {@code value} is {@code null}.  The supplier is only
     *                          consulted
     *                          if necessary.
     * @param <T>               The type of object to return.
     *
     * @return One of two values.
     */
    @Nullable
    public static <T> T ifNotNullElseNullable(@Nullable final T value, final NSupplier<T> nullValueSupplier) {

        if (value != null)
            return value;

        return nullValueSupplier.get();
    }

    /**
     * Apply an operation on the first argument if it is not {@code null}.  Otherwise, do nothing.
     *
     * @param value            The value to operate on if it is not {@code null}.
     * @param notNullOperation The function that defines the transform to apply to the {@code from} value.
     * @param <T>              The type of the value to operate on.
     */
    public static <T> void ifNotNull(@Nullable final T value, final NNOperation<T> notNullOperation) {

        if (value != null)
            notNullOperation.apply( value );
    }

    /**
     * Apply a function to the first parameter if it is not {@code null}.  Otherwise, return {@code null}.
     *
     * @param from                 The value to transform if it is not {@code null}.
     * @param notNullValueFunction The function that defines the transform to apply to the {@code from} value.
     * @param <F>                  The type of object to transform.
     * @param <T>                  The type of object to return.
     *
     * @return The transformed value, or {@code null}.
     */
    @Nullable
    public static <F, T> T ifNotNull(@Nullable final F from, final NFunctionNN<F, T> notNullValueFunction) {

        if (from == null)
            return null;

        return notNullValueFunction.apply( from );
    }

    /**
     * Apply a function to the first parameter if it is not {@code null}.  Otherwise, return {@code nullValue}.
     *
     * @param from                 The value to transform if it is not {@code null}.
     * @param notNullValueFunction The function that defines the transform to apply to the {@code from} value.
     * @param nullValue            The value to return if {@code value} is {@code null}.
     * @param <F>                  The type of object to transform.
     * @param <T>                  The type of object to return.
     *
     * @return The transformed value, or {@code nullValue}.
     */
    @Nonnull
    public static <F, T> T ifNotNullElse(@Nullable final F from, final NNFunctionNN<F, T> notNullValueFunction,
                                         @Nonnull final T nullValue) {

        if (from == null)
            return checkNotNull( nullValue );

        return checkNotNull( notNullValueFunction.apply( from ) );
    }

    /**
     * Apply a function to the first parameter if it is not {@code null}.  Otherwise, return {@code nullValue}.
     *
     * @param from                 The value to transform if it is not {@code null}.
     * @param notNullValueFunction The function that defines the transform to apply to the {@code from} value.
     * @param nullValue            The value to return if {@code value} is {@code null}.
     * @param <F>                  The type of object to transform.
     * @param <T>                  The type of object to return.
     *
     * @return The transformed value, or {@code nullValue}.
     */
    @Nullable
    public static <F, T> T ifNotNullElseNullable(@Nullable final F from, final NFunctionNN<F, T> notNullValueFunction,
                                                 @Nullable final T nullValue) {

        if (from == null)
            return nullValue;

        return notNullValueFunction.apply( from );
    }

    /**
     * Create a proxy object on which you can invoke methods of the given type.  The result of these methods will be the result of the
     * method invoked on the given object, or {@code null} if the given object is {@code null}.
     *
     * @param type   The type of the object.
     * @param object The value to transform if it is not {@code null}.
     * @param <T>    The type of the object.
     *
     * @return A proxy object of the given type.
     */
    @Nonnull
    public static <T> T ifNotNull(@Nonnull final Class<T> type, @Nullable final T object) {

        return ifNotNullElse( type, object, null );
    }

    /**
     * Create a proxy object on which you can invoke methods of the given type.  The result of these methods will be the result of the
     * method invoked on the given object, or {@code nullValue} if the given object is {@code null}.
     *
     * @param type      The type of methods that can be invoked.
     * @param object    The object to invoke the method on, if not {@code null}.
     * @param nullValue The result of the invoked methods, if {@code object} is {@code null}.
     *
     * @return A proxy object of the given type.
     */
    @Nonnull
    public static <T> T ifNotNullElse(@Nonnull final Class<T> type, @Nullable final T object, @Nullable final Object nullValue) {

        return type.cast( TypeUtils.newProxyInstance( type, new InvocationHandler() {
            @Nullable
            @Override
            @SuppressWarnings({ "ProhibitedExceptionDeclared" })
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {

                if (object == null)
                    return nullValue;

                method.setAccessible( true );
                return method.invoke( object, args );
            }
        } ) );
    }

    /**
     * Create a proxy object on which you can invoke methods of the given type.  If the given {@code object} is of the given type, the
     * result of these methods will be the result of the method invoked on the given object.  If it is not of the given type, the result of
     * these methods will be {@code null}.
     *
     * @param type   The type of methods that can be invoked.
     * @param object The object to invoke the method on, if it is of the correct {@code type}.
     *
     * @return A proxy object of the given type.
     */
    @Nonnull
    public static <T> T ifType(@Nonnull final Class<T> type, final Object object) {

        return TypeUtils.newProxyInstance( type, new InvocationHandler() {
            @Nullable
            @Override
            @SuppressWarnings({ "ProhibitedExceptionDeclared" })
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {

                if (type.isInstance( object ))
                    return method.invoke( object, args );

                return null;
            }
        } );
    }

    /**
     * Create a proxy object on which you can invoke methods of the given type.  If the given {@code object} is of the given type, the
     * result of these methods will be the result of the method invoked on the given object.  If it is not of the given type, the result of
     * these methods will be {@code badTypeValue}.
     *
     * @param type         The type of methods that can be invoked.
     * @param object       The object to invoke the method on, if it is of the correct {@code type}.
     * @param badTypeValue The result of the invoked methods, if {@code object} is not of the correct {@code type}.
     *
     * @return A proxy object of the given type.
     */
    @Nonnull
    public static <T> T ifTypeElse(@Nonnull final Class<T> type, final Object object, @Nonnull final Object badTypeValue) {

        return type.cast( TypeUtils.newProxyInstance( type, new InvocationHandler() {
            @Override
            @SuppressWarnings({ "ProhibitedExceptionDeclared" })
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {

                if (type.isInstance( object ))
                    return checkNotNull( method.invoke( object, args ) );

                return badTypeValue;
            }
        } ) );
    }

    /**
     * Create a proxy object on which you can invoke methods of the given type.  If the given {@code object} is of the given type, the
     * result of these methods will be the result of the method invoked on the given object.  If it is not of the given type, the result of
     * these methods will be {@code badTypeValue}.
     *
     * @param type         The type of methods that can be invoked.
     * @param object       The object to invoke the method on, if it is of the correct {@code type}.
     * @param badTypeValue The result of the invoked methods, if {@code object} is not of the correct {@code type}.
     *
     * @return A proxy object of the given type.
     */
    @Nonnull
    public static <T> T ifTypeElseNullable(@Nonnull final Class<T> type, final Object object, @Nullable final Object badTypeValue) {

        return type.cast( TypeUtils.newProxyInstance( type, new InvocationHandler() {
            @Nullable
            @Override
            @SuppressWarnings({ "ProhibitedExceptionDeclared" })
            public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {

                if (type.isInstance( object ))
                    return checkNotNull( method.invoke( object, args ) );

                return badTypeValue;
            }
        } ) );
    }
}
