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

import static com.lyndir.lhunath.opal.system.util.StringUtils.strf;

import com.google.common.base.Preconditions;
import java.util.Objects;


/**
 * <i>Vec3 - A three dimensional vector.</i><br> <br> The Vec3 object represents a three dimensional vector in a space.<br> <br>
 *
 * @author lhunath
 */
public class Vec3D extends Vec2D {

    private static final long serialVersionUID = 0;

    /**
     * Z-Axis coordinate.
     */
    private final double z;

    public Vec3D() {

        this( 0, 0, 0 );
    }

    /**
     * Convert a planar vector (2D) into a spatial vector (3D).
     *
     * @param vector The 2D vector to be converted.
     */
    public Vec3D(final Vec2 vector) {

        this( vector, 0 );
    }

    /**
     * Convert a planar vector (2D) into a spatial vector (3D); placing it at a specified depth in space.
     *
     * @param vector The 2D vector to be converted.
     * @param z      The depth at which to place the vector in space.
     */
    public Vec3D(final Vec2 vector, final double z) {

        this( vector.getX(), vector.getY(), z );
    }

    /**
     * Create a new three dimensional vector.
     *
     * @param x The x-coordinate of the new vector.
     * @param y The y-coordinate of the new vector.
     * @param z The z-coordinate of the new vector.
     */
    public Vec3D(final double x, final double y, final double z) {

        super( x, y );
        this.z = z;
    }

    @Override
    public double lengthSq() {

        return super.lengthSq() + getZ() * getZ();
    }

    @Override
    public Vec3D normalize() {

        double length = length();
        return multiply( 1 / length );
    }

    /**
     * Rotate this vector over an angle along the specified axis.
     *
     * @param a  The angle over which to rotate.
     * @param ax The axis around which to rotate.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec3D rotate(final Angle a, final Axis ax) {

        switch (ax) {

            case X: {
                Vec2D rotated = new Vec2D( getY(), getZ() ).rotate( a );
                return new Vec3D( getX(), rotated.getX(), rotated.getY() );
            }
            case Y: {
                Vec2D rotated = new Vec2D( getX(), getZ() ).rotate( a );
                return new Vec3D( rotated.getX(), getY(), rotated.getY() );
            }
            case Z: {
                Vec2D rotated = rotate( a );
                return new Vec3D( rotated.getX(), rotated.getY(), getZ() );
            }
            case O:
                return this;

            default:
                throw new RuntimeException( "Cannot rotate over the given axis." );
        }
    }

    /**
     * Add another vector to this one.
     *
     * @param vector A vector that needs to be added to this.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec3D add(final Vec3D vector) {

        if (vector == null)
            return this;

        return new Vec3D( getX() + vector.getX(), getY() + vector.getY(), getZ() + vector.getZ() );
    }

    /**
     * Subtract another vector from this one.
     *
     * @param vector The vector which will be subtracted from this.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec3D subtract(final Vec3D vector) {

        if (vector == null)
            return this;

        return new Vec3D( getX() - vector.getX(), getY() - vector.getY(), getZ() - vector.getZ() );
    }

    /**
     * Multiply this vector with the coefficients of another.
     *
     * @param vector The vector whose coefficients will be used for the multiplication.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec3D multiply(final Vec3D vector) {

        if (vector == null)
            return this;

        return new Vec3D( getX() * vector.getX(), getY() * vector.getY(), getZ() * vector.getZ() );
    }

    @Override
    public Vec3D multiply(final double multiplier) {

        return new Vec3D( getX() * multiplier, getY() * multiplier, getZ() * multiplier );
    }

    /**
     * Multiply this vector with another vector using the cross product.<br> <br> The length of the cross product of this vector with a
     * given one is the area of the parallelogram having this and the given vector as sides.<br>
     *
     * <pre>
     *          _______
     *          \      \  - Consider this parallelogram's horizontal side (either of the two) as a vector,
     *           \______\     and the vertical side as another. Their cross product returns the area.
     * </pre>
     *
     * <br> The resulting vector is perpendicular to both vectors. The direction it will be pointing in is theoretically undefined, but
     * defined by convention as: <q>An easy way to compute the direction of the resultant vector is the "right-hand rule." If the
     * coordinate
     * system is right-handed, one simply points the forefinger in the direction of the first operand and the middle finger in the
     * direction
     * of the second operand. Then, the resultant vector is coming out of the thumb.</q> <br> <br> It will thus always be perpendicular to
     * the plane formed by the given vectors (this is the same plane as formed by the above parallelogram).<br> This means that the product
     * of the resultant of a cross product with a third gives the volume of the parallelepiped thus formed.
     *
     * @param vector The vector with which this vector must be multiplied.
     *
     * @return A new vector, perpendicular to both given vectors, as described above.
     */
    public Vec3D crossMultiply(final Vec3D vector) {

        Preconditions.checkNotNull( vector, "Given vector cannot be null." );

        return new Vec3D( getY() * vector.getZ() - getZ() * vector.getY(), getZ() * vector.getX() - getX() * vector.getZ(),
                         getX() * vector.getY() - getY() * vector.getX() );
    }

    /**
     * Multiply this vector with another vector using the dot product.<br> <br> <i>The dot product returns the length of the projection of
     * this vector on the given one.<br> As a result of this; the dot product of two perpendicular vectors is 0.<br> <br> Any vector in a
     * plane, multiplied in this way with the plane's normal; will therefore result in 0.</i>
     *
     * @param vector The vector with which this vector must be multiplied.
     *
     * @return The result of the dot product of this vector with the given one.
     */
    public double dotMultiply(final Vec3D vector) {

        if (vector == null)
            return 0;

        return getX() * vector.getX() + getY() * vector.getY() + getZ() * vector.getZ();
    }

    @Override
    public String toString() {

        return strf( "vec(%.2f, %.2f, %.2f)", getX(), getY(), getZ() );
    }

    @Override
    public boolean equals(final Object obj) {

        if (!super.equals( obj ))
            return false;
        if (!(obj instanceof Vec3D))
            return false;

        Vec3D o = (Vec3D) obj;
        return getZ() == o.getZ();
    }

    @Override
    public int hashCode() {

        return Objects.hash( getX(), getY(), getZ() );
    }

    /**
     * @return The depth destination of this vector.
     */
    public double getZ() {

        return z;
    }

    /**
     * @param newZ    The z coordinate to use for the new vector.
     *
     * @return A new vector with the resulting coordinates.
     */
    public Vec3D copyWithZ(final double newZ) {

        return new Vec3D( getX(), getY(), newZ );
    }
}
