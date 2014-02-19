/*
 *   Copyright 2005-2007 Maarten Billemont
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
package com.lyndir.lhunath.opal.math;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.lyndir.lhunath.opal.system.collection.Cache;
import com.lyndir.lhunath.opal.system.util.NNSupplier;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Objects;
import javax.annotation.Nonnull;


/**
 * <i>Vec2 - A two dimensional vector.</i><br> <br> <p> TODO: Optimize by caching calculations (much like Angle)? </p> <br> The Vec2 object
 * represents a two dimensional vector in a plane.<br> <br>
 *
 * @author lhunath
 */
public class Vec2 implements Serializable {

    private static final long                                         serialVersionUID = 0;
    private static final Table<Integer, Integer, SoftReference<Vec2>> instanceCache    = HashBasedTable.create( 100, 100 );

    /**
     * X-Axis coordinate.
     */
    private final int x;

    /**
     * Y-Axis coordinate.
     */
    private final int y;

    /**
     * Cached.
     */
    private transient Double  length;
    private transient Integer lengthSq;
    private transient Vec2    normal;

    /**
     * Create a new two dimensional vector at the origin.
     */
    public static Vec2 create() {
        return create( 0, 0 );
    }

    /**
     * Create a new two dimensional vector.
     *
     * @param x The x-coordinate of the new vector.
     * @param y The y-coordinate of the new vector.
     */
    public static Vec2 create(final int x, final int y) {
        SoftReference<Vec2> cachedInstance = instanceCache.get( x, y );
        if (cachedInstance != null) {
            Vec2 instance = cachedInstance.get();
            if (instance != null)
                return instance;
        }

        return new Vec2( x, y );
    }

    Vec2(final int x, final int y) {
        this.x = x;
        this.y = y;

        instanceCache.put( x, y, new SoftReference<>( this ) );
    }

    /**
     * @return The horizontal destination of this vector.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The vertical destination of this vector.
     */
    public int getY() {
        return y;
    }

    public int getDX(final Vec2 other) {
        return other.getX() - getX();
    }

    public int getDY(final Vec2 other) {
        return other.getY() - getY();
    }

    public int distanceTo(final Vec2 other) {
        return Cache.getOrLoad( this, "dv", other, new NNSupplier<Integer>() {
            @Nonnull
            @Override
            public Integer get() {
                int dx = getDX( other ), dy = getDY( other );
                return (Math.abs( dx ) + Math.abs( dy ) + Math.abs( dx + dy )) / 2;
            }
        } );
    }

    /**
     * Calculate the length of the vector.
     *
     * @return The length of this vector.
     */
    public double length() {
        if (length != null)
            return length;

        return length = Math.sqrt( lengthSq() );
    }

    /**
     * Calculate the squared length of the vector.<br> <br> It is advised to use this function in favor of {@link #length()} due to
     * performance.
     *
     * @return The squared length of this vector.
     */
    public int lengthSq() {
        if (lengthSq != null)
            return lengthSq;

        return lengthSq = getX() * getX() + getY() * getY();
    }

    /**
     * Normalize this vector.
     *
     * @return A new, normalized version of this vector; pointing in the same direction with length 1.
     */
    public Vec2 normalize() {
        if (normal != null)
            return normal;

        return normal = multiply( 1 / length() );
    }

    /**
     * Rotate this vector over an angle.
     *
     * @param a The angle over which to rotate.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 rotate(final Angle a) {
        if (a == null)
            return this;

        return Cache.getOrLoad( this, "rotate", a, new NNSupplier<Vec2>() {
            @Nonnull
            @Override
            public Vec2 get() {
                return copyWith( (int) (getX() * a.sin() + getY() * a.cos()), //
                                 (int) (getX() * a.cos() - getY() * a.sin()) );
            }
        } );
    }

    /**
     * Add another vector to this one.
     *
     * @param vector The vector which will be added to this.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 translate(final Vec2 vector) {
        if (vector == null)
            return this;

        return translate( vector.getX(), vector.getY() );
    }

    /**
     * Add another vector to this one.
     *
     * @param dx The amount by which to translate the x coordinate.
     * @param dy The amount by which to translate the y coordinate.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 translate(final int dx, final int dy) {
        return copyWith( getX() + dx, getY() + dy );
    }

    /**
     * Multiply this vector with the coefficients of another.
     *
     * @param vector The vector whose coefficients will be used for the multiplication.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 multiply(final Vec2 vector) {
        if (vector == null)
            return this;

        return copyWith( getX() * vector.getX(), getY() * vector.y );
    }

    /**
     * Multiply this vector with a scalar number.
     *
     * @param multiplier The scalar value with which to multiply this vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 multiply(final double multiplier) {
        return copyWith( (int) (getX() * multiplier), (int) (getY() * multiplier) );
    }

    /**
     * Inverse the direction of this vector.<br> This is basically the same as {@link #multiply(double)} with -1.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 inverse() {
        return multiply( -1 );
    }

    /**
     * Multiply this vector with another vector using the cross product.<br> <br> <i>The length of the cross product of this vector with a
     * given one is the area of the parallelogram having this and the given vector as sides.</i>
     *
     * <pre>
     *    _______
     *    \      \  - Consider this parallelogram's horizontal side (either of the two) as a vector,
     *     \______\     and the vertical side as another. Their cross product returns the area.
     * </pre>
     *
     * @param vector The vector with which this vector will be multiplied.
     *
     * @return The result of the cross product of this vector with the given one.
     */
    public int crossMultiply(final Vec2 vector) {
        if (vector == null)
            return 0;

        return getX() * vector.getY() - getY() * vector.getX();
    }

    /**
     * Multiply this vector with another vector using the dot product. <i>The dot product returns the length of the projection of this
     * vector on the given one.<br> As a result of this; the dot product of two perpendicular vectors is 0.</i>
     *
     * @param vector The vector with which this vector will be multiplied.
     *
     * @return The result of the dot product of this vector with the given one.
     */
    public int dotMultiply(final Vec2 vector) {
        if (vector == null)
            return 0;

        return getX() * vector.getX() + getY() * vector.getY();
    }

    @Override
    public String toString() {
        return strf( "vec(%d, %d)", getX(), getY() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( getX(), getY() );
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Vec2))
            return false;

        Vec2 o = (Vec2) obj;
        return getX() == o.getX() && getY() == o.getY();
    }

    /**
     * @param newX The x coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 copyWithX(final int newX) {
        return copyWith( newX, getY() );
    }

    /**
     * @param newY The y coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 copyWithY(final int newY) {
        return copyWith( getX(), newY );
    }

    /**
     * @param newX The x coordinate to use for the new vector.
     * @param newY The y coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec2 copyWith(final int newX, final int newY) {
        return create( newX, newY );
    }
}
