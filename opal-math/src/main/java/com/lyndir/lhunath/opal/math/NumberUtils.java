package com.lyndir.lhunath.opal.math;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;


/**
 * @author lhunath, 2/3/2014
 */
public abstract class NumberUtils {

    public static Number add(final Number first, final Number second) {
        if (first instanceof Double || second instanceof Double)
            return first.doubleValue() + second.doubleValue();
        if (first instanceof Float || second instanceof Float)
            return first.floatValue() + second.floatValue();
        if (first instanceof Long || second instanceof Long)
            return first.longValue() + second.longValue();
        if (first instanceof Integer || second instanceof Integer)
            return first.intValue() + second.intValue();
        if (first instanceof Short || second instanceof Short)
            return first.shortValue() + second.shortValue();
        if (first instanceof Byte || second instanceof Byte)
            return first.byteValue() + second.byteValue();

        throw new IllegalArgumentException( strf( "Unsupported number types: %s, and %s", first.getClass(), second.getClass() ) );
    }

    public static Number subtract(final Number first, final Number second) {
        if (first instanceof Double || second instanceof Double)
            return first.doubleValue() - second.doubleValue();
        if (first instanceof Float || second instanceof Float)
            return first.floatValue() - second.floatValue();
        if (first instanceof Long || second instanceof Long)
            return first.longValue() - second.longValue();
        if (first instanceof Integer || second instanceof Integer)
            return first.intValue() - second.intValue();
        if (first instanceof Short || second instanceof Short)
            return first.shortValue() - second.shortValue();
        if (first instanceof Byte || second instanceof Byte)
            return first.byteValue() - second.byteValue();

        throw new IllegalArgumentException( strf( "Unsupported number types: %s, and %s", first.getClass(), second.getClass() ) );
    }

    public static Number multiply(final Number first, final Number second) {
        if (first instanceof Double || second instanceof Double)
            return first.doubleValue() * second.doubleValue();
        if (first instanceof Float || second instanceof Float)
            return first.floatValue() * second.floatValue();
        if (first instanceof Long || second instanceof Long)
            return first.longValue() * second.longValue();
        if (first instanceof Integer || second instanceof Integer)
            return first.intValue() * second.intValue();
        if (first instanceof Short || second instanceof Short)
            return first.shortValue() * second.shortValue();
        if (first instanceof Byte || second instanceof Byte)
            return first.byteValue() * second.byteValue();

        throw new IllegalArgumentException( strf( "Unsupported number types: %s, and %s", first.getClass(), second.getClass() ) );
    }

    public static Number divide(final Number first, final Number second) {
        if (first instanceof Double || second instanceof Double)
            return first.doubleValue() / second.doubleValue();
        if (first instanceof Float || second instanceof Float)
            return first.floatValue() / second.floatValue();
        if (first instanceof Long || second instanceof Long)
            return first.longValue() / second.longValue();
        if (first instanceof Integer || second instanceof Integer)
            return first.intValue() / second.intValue();
        if (first instanceof Short || second instanceof Short)
            return first.shortValue() / second.shortValue();
        if (first instanceof Byte || second instanceof Byte)
            return first.byteValue() / second.byteValue();

        throw new IllegalArgumentException( strf( "Unsupported number types: %s, and %s", first.getClass(), second.getClass() ) );
    }

    public static Number modulo(final Number first, final Number second) {
        if (first instanceof Double || second instanceof Double)
            return first.doubleValue() % second.doubleValue();
        if (first instanceof Float || second instanceof Float)
            return first.floatValue() % second.floatValue();
        if (first instanceof Long || second instanceof Long)
            return first.longValue() % second.longValue();
        if (first instanceof Integer || second instanceof Integer)
            return first.intValue() % second.intValue();
        if (first instanceof Short || second instanceof Short)
            return first.shortValue() % second.shortValue();
        if (first instanceof Byte || second instanceof Byte)
            return first.byteValue() % second.byteValue();

        throw new IllegalArgumentException( strf( "Unsupported number types: %s, and %s", first.getClass(), second.getClass() ) );
    }
}
