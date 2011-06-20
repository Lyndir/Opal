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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.*;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ObjectMeta.For;
import com.lyndir.lhunath.opal.system.util.TypeUtils.LastResult;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link ObjectUtils}<br> <sub>[in short] (TODO).</sub></h2>
 * <p/>
 * <p> <i>Mar 22, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class ObjectUtils {

    static final Logger logger = Logger.get( ObjectUtils.class );

    private static final Pattern NON_PRINTABLE     = Pattern.compile( "[^\\p{Print}]" );
    private static final int     MAX_DECODE_LENGTH = 100;

    /**
     * Check whether two objects are equal according to {@link #equals(Object)}.
     * <p/>
     * <p> <b>NOTE:</b> This method is {@code null}-safe and two {@code null} objects are also considered equal. </p>
     * <p/>
     * <p> <b>NOTE:</b> This method attempts to aid in type safety of the objects that are being compared. </p>
     *
     * @param <C>    The type of the first parameter.  It should be of the same type or a subtype (more concrete type) of the second
     *               parameter.
     * @param <P>    The type of the second parameter. The type of this parameter must be the same type or more generic
     *               assignment-compatible to that of the first parameter.
     * @param subObject  The first object, or {@code null}.
     * @param superObject The second object, or {@code null}.
     *
     * @return {@code true} if both objects are {@code null} or if neither are and {@link #equals(Object)} considers them equal.
     */
    public static <P, C extends P> boolean isEqual(final C subObject, final P superObject) {

        //noinspection ObjectEquality
        return subObject == superObject || subObject != null && subObject.equals( superObject );
    }

    /**
     * Provide a description for the given object.  Certain types are handled specially to format the description in a concise and useful
     * manner.  Objects of types that are not handled specially yield the same result as {@link String#valueOf(Object)}.
     *
     * @param o The object to describe.
     *
     * @return A description of the given object.
     */
    public static String describe(final Object o) {

        if (o == null)
            return "<null>";

        if (o.getClass().equals( byte[].class )) {
            byte[] byteArray = (byte[]) o;
            StringBuilder toString = new StringBuilder( String.format( "<b[]: %dB, ", byteArray.length ) );

            // Decode some bytes.
            CharBuffer decodedBytes = Charsets.UTF_8
                    .decode( ByteBuffer.wrap( byteArray, 0, Math.min( byteArray.length, MAX_DECODE_LENGTH ) ) );
            String stripped = NON_PRINTABLE.matcher( decodedBytes ).replaceAll( "." );
            toString.append( stripped );

            // Append trimmed indicator if not all bytes were decoded.
            if (byteArray.length > MAX_DECODE_LENGTH)
                toString.append( "[...]" );

            return toString.append( '>' ).toString();
        }

        if (o.getClass().isArray())
            return Arrays.asList( (Object[]) o ).toString();

        if (o instanceof String)
            return String.format( "\"%s\"", o );

        if (o instanceof Map) {
            StringBuilder description = new StringBuilder().append( '[' );
            for (final Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
                if (description.length() > 1)
                    description.append( ", " );

                description.append( describe( entry.getKey() ) ).append( '=' ).append( describe( entry.getValue() ) );
            }

            return description.append( ']' ).toString();
        }

        if (o instanceof Collection) {
            Collection<?> collection = (Collection<?>) o;
            StringBuilder description = new StringBuilder().append( '[' );
            for (final Object entry : collection) {
                if (description.length() > 1)
                    description.append( ", " );

                description.append( describe( entry ) );
            }

            return description.append( ']' ).toString();
        }

        if (o instanceof X509Certificate) {
            X509Certificate x509Certificate = (X509Certificate) o;
            return String.format(
                    "{Cert: DN=%s, Issuer=%s}", x509Certificate.getSubjectX500Principal().getName(),
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
        String name = o.getClass().getSimpleName();
        if (name == null)
            name = o.getClass().getName().replace( ".*\\.", "" );
        toString.append( name );

        toString.append(
                forEachFieldWithMeta(
                        For.toString, o.getClass(), new Function<LastResult<Field, StringBuilder>, StringBuilder>() {
                            @Override
                            public StringBuilder apply(final LastResult<Field, StringBuilder> lastResult) {

                                Field field = lastResult.getCurrent();
                                StringBuilder fieldsString = lastResult.getLastResult();

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
                                catch (SecurityException ignored) {
                                }

                                try {
                                    fieldsString.append( name ).append( '=' ).append( describe( field.get( o ) ) );
                                }
                                catch (IllegalAccessException e) {
                                    logger.dbg( e, "Not accessible: %s", field );
                                }

                                return fieldsString;
                            }
                        }, new StringBuilder() ) );

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

        return ifNotNullElse(
                forEachFieldWithMeta(
                        For.hashCode, o.getClass(), new Function<LastResult<Field, Integer>, Integer>() {
                            @Override
                            public Integer apply(final LastResult<Field, Integer> lastResult) {

                                Field field = lastResult.getCurrent();
                                int lastHashCode = lastResult.getLastResult();
                                try {
                                    field.setAccessible( true );
                                }
                                catch (SecurityException ignored) {
                                }

                                try {
                                    Object value = field.get( o );
                                    if (value != null)
                                        return Arrays.hashCode( new int[]{ lastHashCode, value.hashCode() } );
                                }
                                catch (IllegalAccessException e) {
                                    logger.dbg( e, "Not accessible: %s", field );
                                }

                                return lastHashCode;
                            }
                        }, 0 ), System.identityHashCode( o ) );
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
    public static boolean equals(final Object superObject, final Object subObject) {

        //noinspection ObjectEquality
        if (superObject == subObject)
            return true;
        if (superObject == null || subObject == null)
            return false;
        if (!superObject.getClass().isAssignableFrom( subObject.getClass() ))
            return false;

        return forEachFieldWithMeta(
                For.equals, superObject.getClass(), new Function<LastResult<Field, Boolean>, Boolean>() {
                    @Override
                    public Boolean apply(final LastResult<Field, Boolean> lastResult) {

                        Field field = lastResult.getCurrent();
                        try {
                            field.setAccessible( true );
                        }
                        catch (SecurityException ignored) {
                        }

                        try {
                            if (!Objects.equal( field.get( superObject ), field.get( subObject ) ))
                                return false;
                        }
                        catch (IllegalAccessException e) {
                            logger.dbg( e, "Not accessible: %s", field );
                        }

                        return true;
                    }
                }, false /* There are no (accessible) fields to compare. */ );
    }

    private static <R, T> R forEachFieldWithMeta(final For meta, final Class<T> type, final Function<LastResult<Field, R>, R> function,
                                                 final R firstResult) {

        return TypeUtils.forEachSuperTypeOf(
                type, new Function<LastResult<Class<?>, R>, R>() {
                    @Override
                    public R apply(final LastResult<Class<?>, R> lastTypeResult) {

                        Class<?> subType = lastTypeResult.getCurrent();
                        final boolean usedByType = usesMeta( meta, subType );

                        return TypeUtils.forEachFieldOf(
                                subType, new Function<LastResult<Field, R>, R>() {
                                    @Override
                                    public R apply(final LastResult<Field, R> lastFieldResult) {

                                        Field field = lastFieldResult.getCurrent();
                                        R result = lastFieldResult.getLastResult();

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

                                        return function.apply( new LastResult<Field, R>( field, result ) );
                                    }
                                }, lastTypeResult.getLastResult(), false );
                    }
                }, null, firstResult );
    }

    private static <T> boolean usesMeta(final For meta, final Class<T> type) {

        for (final Entry<Class<? super T>, Map<Class<?>, ObjectMeta>> annotationEntry : TypeUtils.getAnnotations( type, ObjectMeta.class )
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
     * @param value     The value to return, if it isn't {@code null} .
     * @param nullValue The value to return if {@code value}  is {@code null} .
     * @param <T>       The type of object to return.
     *
     * @return One of two values.
     */
    @NotNull
    public static <T> T ifNotNullElse(@Nullable final T value, @NotNull final T nullValue) {

        if (value == null)
            return checkNotNull( nullValue );

        return value;
    }

    /**
     * @param value     The value to return, if it isn't {@code null} .
     * @param nullValue The value to return if {@code value} is {@code null} .
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
     * @param value             The value to return, if it isn't {@code null} .
     * @param nullValueSupplier Provides the value to return if {@code value}  is {@code null}.  The supplier is only
     *                          consulted
     *                          if necessary.
     * @param <T>               The type of object to return.
     *
     * @return One of two values.
     */
    @NotNull
    public static <T> T ifNotNullElse(@Nullable final T value, final Supplier<T> nullValueSupplier) {

        if (value != null)
            return value;

        return checkNotNull( nullValueSupplier.get() );
    }

    /**
     * Version of {@link #ifNotNullElse(Object, Object)} that loads the default value lazily.
     *
     * @param value             The value to return, if it isn't {@code null} .
     * @param nullValueSupplier Provides the value to return if {@code value}  is {@code null}.  The supplier is only
     *                          consulted
     *                          if necessary.
     * @param <T>               The type of object to return.
     *
     * @return One of two values.
     */
    @Nullable
    public static <T> T ifNotNullElseNullable(@Nullable final T value, final Supplier<T> nullValueSupplier) {

        if (value != null)
            return value;

        return nullValueSupplier.get();
    }

    /**
     * Apply a function to the first parameter if it is not {@code null} .  Otherwise, return {@code null} .
     *
     * @param from                 The value to transform if it is not {@code null} .
     * @param notNullValueFunction The function that defines the transform to apply to the {@code from}  value.
     * @param <F>                  The type of object to transform.
     * @param <T>                  The type of object to return.
     *
     * @return The transformed value, or {@code null}.
     */
    @Nullable
    public static <F, T> T ifNotNull(@Nullable final F from, final Function<F, T> notNullValueFunction) {

        if (from == null)
            return null;

        return notNullValueFunction.apply( from );
    }

    @NotNull
    public static <F, T> T ifNotNullElse(@Nullable final F from, final Function<F, T> notNullValueFunction, @NotNull final T nullValue) {

        if (from == null)
            return checkNotNull( nullValue );

        return checkNotNull( notNullValueFunction.apply( from ) );
    }

    @Nullable
    public static <F, T> T ifNotNullElseNullable(@Nullable final F from, final Function<F, T> notNullValueFunction, @NotNull final T nullValue) {

        if (from == null)
            return nullValue;

        return notNullValueFunction.apply( from );
    }
}
