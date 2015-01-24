package com.lyndir.lhunath.opal.system.util;

import static com.google.common.base.Preconditions.*;

import javax.annotation.Nonnull;


/**
 * @author lhunath, 2013-10-24
 */
public abstract class EnumUtils {

    /**
     * Find the enum value of the given enum type with the given name.
     *
     * @param type The enum type for which to obtain a value.
     * @param name The name of the enum value to obtain.
     * @param <T>  The enum type for which to obtain a value.
     *
     * @return The enum value.
     *
     * @throws IllegalArgumentException if the given enum type has no member named by the given value.
     */
    public static <T extends Enum<T>> T enumNamed(@Nonnull final Class<T> type, @Nonnull final String name) {

        return Enum.valueOf( type, name );
    }

    /**
     * Type-forced version of {@link #enumNamed(Class, String)}.  Does not require the class to be an Enum class.  Really only useful if
     * you've got a {@code Class} and you have no clue what enum is in it and you've already done a {@link Class#isEnum()} to
     * verify that it really is an enum.
     *
     * @param type The enum type for which to obtain a value.
     * @param name The name of the enum value to obtain.
     * @param <T>  The enum type for which to obtain a value.
     *
     * @return The enum value.
     *
     * @see #enumNamed(Class, String)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T unsafeEnumNamed(@Nonnull final Class<T> type, @Nonnull final String name) {

        Enum obj = enumNamed( (Class<Enum>) type, name );
        return type.cast( obj );
    }

    /**
     * Convert the given type to an enum type and check whether it actually is an enum.
     *
     * @param type The type to convert.
     * @param <T>  The desired enum type.
     *
     * @return The given class, typed as an enum.
     */
    @SuppressWarnings({ "unchecked" })
    public static <T extends Enum<T>> Class<T> checkEnum(@Nonnull final Class<?> type) {

        checkArgument( type.isEnum(), "%s is not an enum.", type );
        return (Class<T>) type;
    }

    /**
     * Return the enum with the smallest ordinal.
     *
     * @param first An enum value.
     * @param second Another enum value.
     * @param <T> The type of the enum values.
     *
     * @return The given enum value that has the smallest ordinal.
     */
    public static <T extends Enum<T>> T min(@Nonnull final T first, @Nonnull final T second) {
        return first.ordinal() < second.ordinal()? first: second;
    }

    /**
     * Return the enum with the largest ordinal.
     *
     * @param first An enum value.
     * @param second Another enum value.
     * @param <T> The type of the enum values.
     *
     * @return The given enum value that has the largest ordinal.
     */
    public static <T extends Enum<T>> T max(@Nonnull final T first, @Nonnull final T second) {
        return first.ordinal() > second.ordinal()? first: second;
    }
}
