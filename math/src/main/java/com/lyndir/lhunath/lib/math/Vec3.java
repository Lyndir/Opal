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
package com.lyndir.lhunath.lib.math;

/**
 * <i>Vec3 - A three dimensional vector.</i><br>
 * <br>
 * The Vec3 object represents a three dimensional vector in a space.<br>
 * <br>
 *
 * @author lhunath
 */
public class Vec3 extends Vec2 {

    /**
     * Z-Axis coordinate.
     */
    private double z;


    /**
     * Convert a planar vector (2D) into a spatial vector (3D).
     *
     * @param vector
     *            The 2D vector to be converted.
     */
    public Vec3(Vec2 vector) {

        this( vector, 0 );
    }

    /**
     * Convert a planar vector (2D) into a spatial vector (3D); placing it at a specified depth in space.
     *
     * @param vector
     *            The 2D vector to be converted.
     * @param z
     *            The depth at which to place the vector in space.
     */
    public Vec3(Vec2 vector, double z) {

        this( vector.getX(), vector.getY(), z );
    }

    /**
     * Create a new three dimensional vector in the origin.
     */
    public Vec3() {

        this( 0, 0, 0 );
    }

    /**
     * Create a new three dimensional vector.
     *
     * @param x
     *            The x-coordinate of the new vector.
     * @param y
     *            The y-coordinate of the new vector.
     * @param z
     *            The z-coordinate of the new vector.
     */
    public Vec3(double x, double y, double z) {

        super( x, y );
        setZ( z );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3 clone() throws CloneNotSupportedException {

        Vec3 newVec3 = (Vec3) super.clone();
        newVec3.setX( x );
        newVec3.setY( y );
        newVec3.setZ( z );

        return newVec3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double lengthSq() {

        return super.lengthSq() + getZ() * getZ();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double normalize() {

        double l = super.normalize();
        setZ( getZ() / l );

        return l;
    }

    /**
     * Rotate this vector over an angle along the specified axis.
     *
     * @param a
     *            The angle over which to rotate.
     * @param ax
     *            The axis around which to rotate.
     * @return A reference to this vector, after it has been updated.
     */
    public Vec3 rotate(Angle a, Axis ax) {

        if (a != null && ax != null)
            switch (ax) {

                case X: {
                    Vec2 rotated = new Vec2( getY(), getZ() ).rotate( a );
                    setY( rotated.getX() );
                    setZ( rotated.getY() );
                    break;
                }
                case Y: {
                    Vec2 rotated = new Vec2( getX(), getZ() ).rotate( a );
                    setX( rotated.getX() );
                    setZ( rotated.getY() );
                    break;
                }
                case Z:
                    rotate( a );
                    break;
                case O:
                break;

                default:
                    throw new RuntimeException( "Cannot rotate over the given axis." );
            }

        return this;
    }

    /**
     * Add another vector to this one.
     *
     * @param vector
     *            A vector that needs to be added to this.
     * @return A reference to the this vector, after it has been updated.
     */
    public Vec3 add(Vec3 vector) {

        if (vector == null)
            return this;

        setX( getX() + vector.getX() );
        setY( getY() + vector.y );
        setZ( getZ() + vector.z );

        return this;
    }

    /**
     * Subtract another vector from this one.
     *
     * @param vector
     *            The vector which will be subtracted from this.
     * @return A reference to the this vector, after it has been updated.
     */
    public Vec3 substract(Vec3 vector) {

        if (vector == null)
            return this;

        setX( getX() - vector.x );
        setY( getY() - vector.y );
        setZ( getZ() - vector.getZ() );

        return this;
    }

    /**
     * Multiply this vector with the coefficients of another.
     *
     * @param vector
     *            The vector whose coefficients will be used for the multiplication.
     * @return A reference to the this vector, after it has been updated.
     */
    public Vec3 multiply(Vec3 vector) {

        if (vector == null)
            return this;

        setX( getX() * vector.x );
        setY( getY() * vector.getY() );
        setZ( getZ() * vector.z );

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vec3 multiply(double c) {

        super.multiply( c );
        setZ( getZ() * c );

        return this;
    }

    /**
     * Multiply this vector with another vector using the cross product.<br>
     * <br>
     * <i>The length of the cross product of this vector with a given one is the area of the parallelogram having this
     * and the given vector as sides.<br>
     *
     * <pre>
     *          _______
     *          \      \  - Consider this paralellogram's horizontal side (either of the two) as a vector,
     *           \______\     and the vertical side as another. Their cross product returns the area.
     * </pre>
     *
     * <br>
     * The resulting vector is perpendicular to both vectors. The direction it will be pointing in is theoretically
     * undefined, but defined by convention as: <q>An easy way to compute the direction of the resultant vector is the
     * "right-hand rule." If the coordinate system is right-handed, one simply points the forefinger in the direction of
     * the first operand and the middle finger in the direction of the second operand. Then, the resultant vector is
     * coming out of the thumb.</q> <br>
     * <br>
     * It will thus always be perpendicular to the plane formed by the given vectors (this is the same plane as formed
     * by the above parallelogram).<br>
     * This means that the product of the resultant of a cross product with a third gives the volume of the
     * parallelepiped thus formed.</i>
     *
     * @param vector
     *            The vector with which this vector must be multiplied.
     * @return A new vector, perpendicular to both given vectors, as described above.
     */
    public Vec3 crossMultiply(Vec3 vector) {

        if (vector == null)
            return new Vec3();

        return new Vec3( getY() * vector.getZ() - getZ() * vector.getY(), getZ() * vector.getX() - getX() * vector.getZ(), getX() * vector.getY() - getY() * vector.getX() );
    }

    /**
     * Multiply this vector with another vector using the dot product.<br>
     * <br>
     * <i>The dot product returns the length of the projection of this vector on the given one.<br>
     * As a result of this; the dot product of two perpendicular vectors is 0.<br>
     * <br>
     * Any vector in a plane, multiplied in this way with the plane's normal; will therefore result in 0.</i>
     *
     * @param vector
     *            The vector with which this vector must be multiplied.
     * @return The result of the dot product of this vector with the given one.
     */
    public double dotMultiply(Vec3 vector) {

        if (vector == null)
            return 0;

        return getX() * vector.getX() + getY() * vector.getY() + getZ() * vector.getZ();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return "vec(" + getX() + ", " + getY() + ", " + getZ() + ')';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        return o instanceof Vec3 && getX() == ((Vec3) o).getX() && getY() == ((Vec3) o).getY() && getZ() == ((Vec3) o).getZ();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return (int) (getX() + getY() + getZ());
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
