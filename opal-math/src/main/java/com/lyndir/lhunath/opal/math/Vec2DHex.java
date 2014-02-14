package com.lyndir.lhunath.opal.math;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;

import com.lyndir.lhunath.opal.system.util.ObjectMeta;
import javax.annotation.Nonnull;


/**
 * @author lhunath, 2/13/2014
 */
public class Vec2DHex extends Vec2D {

    @ObjectMeta(ignoreFor = ObjectMeta.For.toString)
    private final Size wrapSize;

    /**
     * Create a new two dimensional vector in a wrapping space.
     *
     * @param x        The x-coordinate of the new vector.
     * @param y        The y-coordinate of the new vector.
     * @param wrapSize The size of the wrapping space.
     */
    public Vec2DHex(final double x, final double y, final Size wrapSize) {

        super( x, y );

        this.wrapSize = wrapSize;
    }


    @Override
    public String toString() {

        return strf( "vec(%.2f, %.2f / %s)", getX(), getY(), getWrapSize() );
    }

    /**
     * @return The size at which operations on this vector wrap.
     */
    @Nonnull
    public Size getWrapSize() {
        return wrapSize;
    }

    @Override
    public double getDX(final Vec2D other) {
        double dx = super.getDX( other );

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
    public double getDY(final Vec2D other) {
        double dy = super.getDY( other );

        // Take wrapping into account.
        if (wrapSize != null) {
            double height = wrapSize.getHeight();
            if (dy > height / 2) {
                dy -= height;
            } else if (dy < -height / 2) {
                dy += height;
            }
        }

        return dy;
    }

    @Override
    public Vec2DHex normalize() {
        return (Vec2DHex) super.normalize();
    }

    @Override
    public Vec2DHex rotate(final Angle a) {
        return (Vec2DHex) super.rotate( a );
    }

    @Override
    public Vec2DHex translate(final Vec2D vector) {
        return (Vec2DHex) super.translate( vector );
    }

    @Override
    public Vec2DHex translate(final double dx, final double dy) {
        return (Vec2DHex) super.translate( dx, dy );
    }

    @Override
    public Vec2DHex multiply(final Vec2D vector) {
        return (Vec2DHex) super.multiply( vector );
    }

    @Override
    public Vec2DHex multiply(final double multiplier) {
        return (Vec2DHex) super.multiply( multiplier );
    }

    @Override
    public Vec2DHex inverse() {
        return (Vec2DHex) super.inverse();
    }

    @Override
    public Vec2DHex copyWithX(final double newX) {
        return (Vec2DHex) super.copyWithX( newX );
    }

    @Override
    public Vec2DHex copyWithY(final double newY) {
        return (Vec2DHex) super.copyWithY( newY );
    }

    @Override
    public Vec2DHex copyWith(final double newX, final double newY) {

        // Wrap X & Y.
        // Whenever X wraps, Y advances by height / 2 to compensate for the hex grid visually.
        int width = wrapSize.getWidth();
        int height = wrapSize.getHeight();

        return new Vec2DHex( (newX % width + width + (newY / height) * (height / 2)) % width, (newY % height + height) % height, wrapSize );
    }
}
