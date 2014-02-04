package com.lyndir.lhunath.opal.math;

import static com.lyndir.lhunath.opal.system.util.StringUtils.*;

import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public class Size implements Serializable {

    private static final long serialVersionUID = 0;

    private final int width;
    private final int height;

    public Size(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isInBounds(final Vec2 coordinate) {
        boolean isPastOrigin = coordinate.getX() >= 0 && coordinate.getY() >= 0;
        boolean isInSize = coordinate.getX() < width && coordinate.getY() < height;
        return isPastOrigin && isInSize;
    }

    public static Size max(@Nullable final Size size1, @Nonnull final Size size2) {
        if (size1 == null)
            return size2;

        return new Size( Math.max( size1.getWidth(), size2.getWidth() ), Math.max( size1.getHeight(), size2.getHeight() ) );
    }

    @Override
    public String toString() {
        return strf( "size(%s, %s)", width, height );
    }

    @Override
    public int hashCode() {
        return Objects.hash( width, height );
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Size))
            return false;

        Size o = (Size) obj;
        return width == o.width && height == o.height;
    }
}
