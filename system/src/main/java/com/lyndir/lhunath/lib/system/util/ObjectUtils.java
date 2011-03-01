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
package com.lyndir.lhunath.lib.system.util;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * <h2>{@link ObjectUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>Mar 22, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class ObjectUtils {

    private static final Pattern NON_PRINTABLE     = Pattern.compile( "\\p{Print}" );
    private static final int     MAX_DECODE_LENGTH = 100;

    /**
     * Check whether two objects are equal according to {@link #equals(Object)}.
     *
     * <p> <b>NOTE:</b> This method is <code>null</code>-safe and two <code>null</code> objects are also considered equal. </p>
     *
     * <p> <b>NOTE:</b> This method attempts to aid in type safety of the objects that are being compared. </p>
     *
     * @param <A>    The type of the first parameter.  It should be of the same type or a subtype (more concrete type) of the second
     *               parameter.
     * @param <B>    The type of the second parameter. The type of this parameter must be the same type or more generic
     *               assignment-compatible to that of the first parameter.
     * @param first  The first object, or <code>null</code>.
     * @param second The second object, or <code>null</code>.
     *
     * @return <code>true</code> if both objects are <code>null</code> or if neither are and {@link #equals(Object)} considers them equal.
     */
    public static <B, A extends B> boolean isEqual(final A first, final B second) {

        return first == second || first != null && first.equals( second );
    }

    /**
     * @param value        The value to return, if it isn't <code>null</code> .
     * @param defaultValue The value to return if <code>value</code>  is <code>null</code> .
     * @param <T>          The type of object to return.
     *
     * @return One of two values.
     */
    public static <T> T getOrDefault(T value, T defaultValue) {

        if (value != null)
            return value;

        return defaultValue;
    }

    /**
     * Version of {@link #getOrDefault(Object, Object)} that loads the default value lazily.
     *
     * @param value                The value to return, if it isn't <code>null</code> .
     * @param defaultValueSupplier Provides the value to return if <code>value</code>  is <code>null</code>.  The supplier is only consulted
     *                             if necessary.
     * @param <T>                  The type of object to return.
     *
     * @return One of two values.
     */
    public static <T> T getOrDefault(T value, Supplier<T> defaultValueSupplier) {

        if (value != null)
            return value;

        return defaultValueSupplier.get();
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

        if (o.getClass() == byte[].class) {
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

        if (o instanceof Map) {
            StringBuilder description = new StringBuilder().append( '[' );
            for (Map.Entry<?, ?> deviceContextEntry : ((Map<?, ?>) o).entrySet()) {
                if (description.length() > 1)
                    description.append( ", " );

                description.append( deviceContextEntry.getKey() ).append( '=' ).append( deviceContextEntry.getValue() );
            }

            return description.append( ']' ).toString();
        }

        return String.valueOf( o );
    }

    /**
     * Generate a nice {@link Object#toString()} description of the given object using its fields.  The fields used depend on how the
     * object's type has been annotated with {@link ObjectMeta}.
     *
     * @param o The object for which a toString description should be generated (inside your {@link Object#equals(Object)}, use
     *          <code>this</code>).
     *
     * @return A description of specific field and values of the given object.
     */
    public static String toString(final Object o) {

        StringBuilder toString = new StringBuilder( "{" );
        String name = o.getClass().getSimpleName();
        if (name == null)
            name = o.getClass().getName().replace( ".*\\.", "" );
        toString.append( name );

        StringBuilder fieldsString = forEachFieldWithMeta( ObjectMeta.For.toString, o.getClass(),
                new Function<TypeUtils.LastResult<Field, StringBuilder>, StringBuilder>() {
                    @Override
                    public StringBuilder apply(final TypeUtils.LastResult<Field, StringBuilder> lastResult) {

                        Field field = lastResult.getCurrent();
                        StringBuilder fieldsString = lastResult.getLastResult();

                        try {
                            ObjectMeta fieldMeta = field.getAnnotation( ObjectMeta.class );
                            String name = null;
                            if (fieldMeta != null)
                                name = fieldMeta.name();
                            if (name == null || name.isEmpty())
                                name = field.getName();

                            if (fieldsString == null)
                                fieldsString = new StringBuilder( ": " );
                            else
                                fieldsString.append( ", " );

                            fieldsString.append( name ).append( '=' ).append( describe( field.get( o ) ) );
                        }
                        catch (IllegalAccessException ignored) {
                        }

                        return fieldsString;
                    }
                } );
        if (fieldsString != null)
            toString.append( fieldsString );

        return toString.append( '}' ).toString();
    }

    /**
     * Generate a decent generic {@link Object#hashCode()} for the given object using its fields.  The fields used depend on how the
     * object's type has been annotated with {@link ObjectMeta}.
     *
     * @param o The object for which a hashCode should be generated (inside your {@link Object#equals(Object)}, use <code>this</code>).
     *
     * @return A hashCode of specific field values of the given object.
     */
    public static int hashCode(final Object o) {

        return forEachFieldWithMeta( ObjectMeta.For.hashCode, o.getClass(), new Function<TypeUtils.LastResult<Field, Integer>, Integer>() {
            @Override
            public Integer apply(final TypeUtils.LastResult<Field, Integer> lastResult) {

                try {
                    return Arrays.hashCode( new int[] { lastResult.getLastResult(), lastResult.getCurrent().get( o ).hashCode() } );
                }
                catch (IllegalAccessException ignored) {
                    return lastResult.getLastResult();
                }
            }
        } );
    }

    /**
     * Determine whether superObject and subObject are equal.  This method returns true only when subObject is of a subtype of superObject
     * and all superObject type's fields selected for inclusion by {@link ObjectMeta} annotations have equal values as those of subObject.
     *
     * @param superObject The object that should be compared to subObject (inside your {@link Object#equals(Object)}, use
     *                    <code>this</code>).
     * @param subObject   The object that should be compared to superObject.
     *
     * @return <code>true</code> if both objects are equal according to this method's rules.
     */
    public static boolean equals(final Object superObject, final Object subObject) {

        if (superObject == subObject)
            return true;
        if (superObject == null || subObject == null)
            return false;
        if (!superObject.getClass().isAssignableFrom( subObject.getClass() ))
            return false;

        return forEachFieldWithMeta( ObjectMeta.For.equals, superObject.getClass(),
                new Function<TypeUtils.LastResult<Field, Boolean>, Boolean>() {
                    @Override
                    public Boolean apply(final TypeUtils.LastResult<Field, Boolean> lastResult) {

                        try {
                            if (!Objects.equal( lastResult.getCurrent().get( superObject ), lastResult.getCurrent().get( subObject ) ))
                                return false;
                        }
                        catch (IllegalAccessException ignored) {
                        }

                        return true;
                    }
                } );
    }

    private static <R, T> R forEachFieldWithMeta(final ObjectMeta.For meta, final Class<T> type,
                                                 final Function<TypeUtils.LastResult<Field, R>, R> function) {

        return TypeUtils.forEachSubtypeOf( type, new Function<TypeUtils.LastResult<Class<? super T>, R>, R>() {
            @Override
            public R apply(final TypeUtils.LastResult<Class<? super T>, R> lastTypeResult) {

                Class<? super T> subType = lastTypeResult.getCurrent();
                final R typeResult = lastTypeResult.getLastResult();
                final boolean usedByType = usesMeta( meta, subType );

                return TypeUtils.forEachFieldOf( subType, new Function<TypeUtils.LastResult<Field, R>, R>() {
                    @Override
                    public R apply(final TypeUtils.LastResult<Field, R> lastFieldResult) {

                        Field field = lastFieldResult.getCurrent();
                        R result = lastFieldResult.getLastResult();
                        if (result == null)
                            result = typeResult;

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

                        field.setAccessible( true );
                        return function.apply( new TypeUtils.LastResult<Field, R>( field, result ) );
                    }
                }, false );
            }
        } );
    }

    private static boolean usesMeta(ObjectMeta.For meta, Class<?> type) {

        return usesMeta( meta, type.getAnnotation( ObjectMeta.class ) );
    }

    private static boolean usesMeta(ObjectMeta.For meta, Field field) {

        return usesMeta( meta, field.getAnnotation( ObjectMeta.class ) );
    }

    private static boolean usesMeta(ObjectMeta.For meta, ObjectMeta metaAnnotation) {

        if (metaAnnotation == null)
            return false;

        List<ObjectMeta.For> uses = ImmutableList.copyOf( metaAnnotation.useFor() );
        List<ObjectMeta.For> ignores = ImmutableList.copyOf( metaAnnotation.ignoreFor() );

        if (ignores.contains( ObjectMeta.For.all ) || ignores.contains( meta ))
            return false;

        return uses.contains( ObjectMeta.For.all ) || uses.contains( meta );
    }
}
