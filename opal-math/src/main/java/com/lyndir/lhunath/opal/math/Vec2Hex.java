package com.lyndir.lhunath.opal.math;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;

import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2/13/2014
 */
public class Vec2Hex extends Vec2 {

    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Size wrapSize;

    /**
     * Create a new two dimensional vector in a wrapping space.
     *
     * @param x        The x-coordinate of the new vector.
     * @param y        The y-coordinate of the new vector.
     * @param wrapSize The size of the wrapping space.
     */
    public Vec2Hex(final int x, final int y, final Size wrapSize) {

        super( x, y );

        this.wrapSize = wrapSize;
    }

    @Override
    public String toString() {

        return strf( "vec(%d, %d / %s)", getX(), getY(), getWrapSize() );
    }

    /**
     * @return The size at which operations on this vector wrap.
     */
    @Nonnull
    public Size getWrapSize() {
        return wrapSize;
    }

    @Override
    public int getDX(final Vec2 other) {
        int dx = super.getDX( other );

        // Take wrapping into account.
        if (wrapSize != null) {
            int width = wrapSize.getWidth();
            if (dx > width / 2) {
                dx -= width;
            } else if (dx < -width / 2) {
                dx += width;
            }
        }

        return dx;
    }

    @Override
    public int getDY(final Vec2 other) {
        int dy = super.getDY( other );

        // Take wrapping into account.
        if (wrapSize != null) {
            int height = wrapSize.getHeight();
            if (dy > height / 2) {
                dy -= height;
            } else if (dy < -height / 2) {
                dy += height;
            }
        }

        return dy;
    }

    @Override
    public Vec2Hex normalize() {
        return (Vec2Hex) super.normalize();
    }

    @Override
    public Vec2Hex rotate(final Angle a) {
        return (Vec2Hex) super.rotate( a );
    }

    @Override
    public Vec2Hex translate(final Vec2 vector) {
        return (Vec2Hex) super.translate( vector );
    }

    @Override
    public Vec2Hex translate(final int dx, final int dy) {
        return (Vec2Hex) super.translate( dx, dy );
    }

    @Override
    public Vec2Hex multiply(final Vec2 vector) {
        return (Vec2Hex) super.multiply( vector );
    }

    @Override
    public Vec2Hex multiply(final double multiplier) {
        return (Vec2Hex) super.multiply( multiplier );
    }

    @Override
    public Vec2Hex inverse() {
        return (Vec2Hex) super.inverse();
    }

    @Override
    public Vec2Hex copyWithX(final int newX) {
        return (Vec2Hex) super.copyWithX( newX );
    }

    @Override
    public Vec2Hex copyWithY(final int newY) {
        return (Vec2Hex) super.copyWithY( newY );
    }

    @Override
    public Vec2Hex copyWith(final int newX, final int newY) {

        // Wrap X & Y.
        // Whenever X wraps, Y advances by height / 2 to compensate for the hex grid visually.
        int width = wrapSize.getWidth();
        int height = wrapSize.getHeight();

        return new Vec2Hex( (newX % width + width + (newY / height) * (height / 2)) % width, (newY % height + height) % height, wrapSize );
    }
}
