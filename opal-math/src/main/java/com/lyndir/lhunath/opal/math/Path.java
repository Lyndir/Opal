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

import java.awt.geom.RectangularShape;


/**
 * <i>{@link Path} - [in short] (TODO).</i><br> <br> [description / usage].<br> <br>
 *
 * @author lhunath
 */
public class Path {

    private final boolean srcHorizontal;
    private final boolean dstHorizontal;
    private final Vec2D   src;
    private final Vec2D   dst;
    private final Vec2D   size;
    private final Vec2D   offset;

    @Override
    public String toString() {

        return "{Path: " + src + (srcHorizontal? " - ": " | ") + " --> " + (dstHorizontal? " - ": " | ") + dst + '}';
    }

    /**
     * Create a new {@link Path} instance.
     *
     * @param src           The vector that points at the origin of this path.
     * @param dst           The vector that points at the destination of this path.
     * @param offset        The vector that points at the lower bound of this path's bounding box.
     * @param size          The vector that indicates the diagonal distance of the path's bounding box.
     * @param srcHorizontal Whether this path exits its source horizontally.
     * @param dstHorizontal Whether this path enters its destination horizontally.
     */
    public Path(final Vec2D src, final Vec2D dst, final Vec2D offset, final Vec2D size, final boolean srcHorizontal,
                final boolean dstHorizontal) {

        this.src = src;
        this.dst = dst;
        this.offset = offset;
        this.size = size;
        this.srcHorizontal = srcHorizontal;
        this.dstHorizontal = dstHorizontal;
    }

    /**
     * Calculate geometry needed for defining a path between two rectangular shapes that does not intersect with either shape by attaching
     * the path to the center of either top, left, bottom or right side of each shape.
     *
     * @param srcBounds The rectangle that the path originates from.
     * @param dstBounds The rectangle that the path arrives at.
     *
     * @return A {@link Path} object that provides the geometry needed to define the requested path.
     */
    public static Path calculatePath(final RectangularShape srcBounds, final RectangularShape dstBounds) {

        Vec2D src;
        Vec2D dst;
        boolean srcHorizontal = false, dstHorizontal = false;

        /* left side of connector is SRC */
        if (dstBounds.getCenterX() > srcBounds.getCenterX()) {

            if (dstBounds.getCenterY() > srcBounds.getCenterY()) {
                /* top side of connector is SRC */
                src = new Vec2D( srcBounds.getCenterX(), srcBounds.getMaxY() );
                srcHorizontal = false;

                dst = new Vec2D( dstBounds.getCenterX(), dstBounds.getY() );
                dstHorizontal = false;
            } else if (dstBounds.getCenterY() == srcBounds.getCenterY()) {
                /* top side of connector is both SRC and DST -> horizontal line. */
                src = new Vec2D( srcBounds.getMaxX(), srcBounds.getCenterY() );
                srcHorizontal = true;

                dst = new Vec2D( dstBounds.getX(), dstBounds.getCenterY() );
                dstHorizontal = true;
            } else {
                /* top side of connector is DST */
                src = new Vec2D( srcBounds.getCenterX(), srcBounds.getY() );
                srcHorizontal = false;

                dst = new Vec2D( dstBounds.getCenterX(), dstBounds.getMaxY() );
                dstHorizontal = false;
            }
        }
        /* left side of connector is both SRC and DST -> vertical line. */
        else if (dstBounds.getCenterX() == srcBounds.getCenterX()) {

            if (dstBounds.getCenterY() > srcBounds.getCenterY()) {
                /* top side of connector is SRC -> vertical line. */
                src = new Vec2D( srcBounds.getCenterX(), srcBounds.getMaxY() );
                srcHorizontal = false;

                dst = new Vec2D( dstBounds.getCenterX(), dstBounds.getY() );
                dstHorizontal = false;
            } else if (dstBounds.getCenterY() == srcBounds.getCenterY())
                /* top side of connector is both SRC and DST -> centers collapse, don't draw. */ {
                src = new Vec2D();
                dst = new Vec2D();
            } else {
                /* top side of connector is DST -> vertical line. */
                src = new Vec2D( srcBounds.getCenterX(), srcBounds.getY() );
                srcHorizontal = false;

                dst = new Vec2D( dstBounds.getCenterX(), dstBounds.getMaxY() );
                dstHorizontal = false;
            }
        } else if (dstBounds.getCenterY() > srcBounds.getCenterY()) {
            /* top side of connector is SRC */
            src = new Vec2D( srcBounds.getCenterX(), srcBounds.getMaxY() );
            srcHorizontal = false;

            dst = new Vec2D( dstBounds.getCenterX(), dstBounds.getY() );
            dstHorizontal = false;
        } else if (dstBounds.getCenterY() == srcBounds.getCenterY()) {
            /* top side of connector is both SRC and DST -> horizontal line. */
            src = new Vec2D( srcBounds.getX(), srcBounds.getCenterY() );
            srcHorizontal = true;

            dst = new Vec2D( dstBounds.getMaxX(), dstBounds.getCenterY() );
            dstHorizontal = true;
        } else {
            /* top side of connector is DST */
            src = new Vec2D( srcBounds.getCenterX(), srcBounds.getY() );
            srcHorizontal = false;

            dst = new Vec2D( dstBounds.getCenterX(), dstBounds.getMaxY() );
            dstHorizontal = false;
        }

        boolean srcYInDst = srcBounds.getY() >= dstBounds.getY() && srcBounds.getY() <= dstBounds.getMaxY();
        boolean dstYInSrc = dstBounds.getY() >= srcBounds.getY() && dstBounds.getY() <= srcBounds.getMaxY();
        if (srcYInDst || dstYInSrc) {

            if (dstBounds.getCenterX() > srcBounds.getCenterX()) {
                src = new Vec2D( srcBounds.getMaxX(), srcBounds.getCenterY() );
                srcHorizontal = true;

                dst = new Vec2D( dstBounds.getX(), dstBounds.getCenterY() );
                dstHorizontal = true;
            } else if (dstBounds.getCenterX() == srcBounds.getCenterX()) {
                src = new Vec2D();
                dst = new Vec2D();
            } else {
                src = new Vec2D( srcBounds.getX(), srcBounds.getCenterY() );
                srcHorizontal = true;

                dst = new Vec2D( dstBounds.getMaxX(), dstBounds.getCenterY() );
                dstHorizontal = true;
            }
        } else if (srcBounds.getMaxX() < dstBounds.getX() || srcBounds.getX() > dstBounds.getMaxX())
            if (dstBounds.getCenterX() > srcBounds.getCenterX()) {
                src = new Vec2D( srcBounds.getMaxX(), srcBounds.getCenterY() );
                srcHorizontal = true;

                dst = new Vec2D( dstBounds.getX(), dstBounds.getCenterY() );
                dstHorizontal = true;
            } else if (dstBounds.getCenterX() == srcBounds.getCenterX()) {
                src = new Vec2D();
                dst = new Vec2D();
            } else {
                src = new Vec2D( srcBounds.getX(), srcBounds.getCenterY() );
                srcHorizontal = true;

                dst = new Vec2D( dstBounds.getMaxX(), dstBounds.getCenterY() );
                dstHorizontal = true;
            }

        Vec2D offset = new Vec2D( Math.min( src.getX(), dst.getX() ), Math.min( src.getY(), dst.getY() ) );
        Vec2D size = new Vec2D( Math.abs( dst.getX() - src.getX() ), Math.abs( dst.getY() - src.getY() ) );
        // path.size = new Vec2D( Math.max( Math.abs( path.dst.x - path.src.x ), 1 ), Math.max(
        // Math.abs( path.dst.y - path.src.y ), 1 ) );

        return new Path( src, dst, offset, size, srcHorizontal, dstHorizontal );
    }

    /**
     * Retrieve the srcHorizontal of this {@link Path}.
     *
     * @return Guess.
     */
    public boolean isSrcHorizontal() {

        return srcHorizontal;
    }

    /**
     * Retrieve the dstHorizontal of this {@link Path}.
     *
     * @return Guess.
     */
    public boolean isDstHorizontal() {

        return dstHorizontal;
    }

    /**
     * Retrieve the src of this {@link Path}.
     *
     * @return Guess.
     */
    public Vec2D getSrc() {

        return src;
    }

    /**
     * Retrieve the dst of this {@link Path}.
     *
     * @return Guess.
     */
    public Vec2D getDst() {

        return dst;
    }

    /**
     * Retrieve the offset of this {@link Path}.
     *
     * @return Guess.
     */
    public Vec2D getOffset() {

        return offset;
    }

    /**
     * Retrieve the size of this {@link Path}.
     *
     * @return Guess.
     */
    public Vec2D getSize() {

        return size;
    }
}
