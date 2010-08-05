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

import java.awt.geom.RectangularShape;


/**
 * <i>{@link Path} - [in short] (TODO).</i><br> <br> [description / usage].<br> <br>
 *
 * @author lhunath
 */
public class Path {

    private final boolean srcHorizontal;
    private final boolean dstHorizontal;
    private final Vec2    src;
    private final Vec2    dst;
    private final Vec2    size;
    private final Vec2    offset;

    /**
     * {@inheritDoc}
     */
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
    public Path(final Vec2 src, final Vec2 dst, final Vec2 offset, final Vec2 size, final boolean srcHorizontal, final boolean dstHorizontal) {

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

        Vec2 src = new Vec2();
        Vec2 dst = new Vec2();
        boolean srcHorizontal = false, dstHorizontal = false;

        /* left side of connector is SRC */
        if (dstBounds.getCenterX() > srcBounds.getCenterX()) {

            if (dstBounds.getCenterY() > srcBounds.getCenterY()) {
                /* top side of connector is SRC */
                src.setX( srcBounds.getCenterX() );
                src.setY( srcBounds.getMaxY() );
                srcHorizontal = false;

                dst.setX( dstBounds.getCenterX() );
                dst.setY( dstBounds.getY() );
                dstHorizontal = false;
            } else if (dstBounds.getCenterY() == srcBounds.getCenterY()) {
                /* top side of connector is both SRC and DST -> horizontal line. */
                src.setX( srcBounds.getMaxX() );
                src.setY( srcBounds.getCenterY() );
                srcHorizontal = true;

                dst.setX( dstBounds.getX() );
                dst.setY( dstBounds.getCenterY() );
                dstHorizontal = true;
            } else {
                /* top side of connector is DST */
                src.setX( srcBounds.getCenterX() );
                src.setY( srcBounds.getY() );
                srcHorizontal = false;

                dst.setX( dstBounds.getCenterX() );
                dst.setY( dstBounds.getMaxY() );
                dstHorizontal = false;
            }
        }
        /* left side of connector is both SRC and DST -> vertical line. */
        else if (dstBounds.getCenterX() == srcBounds.getCenterX()) {

            if (dstBounds.getCenterY() > srcBounds.getCenterY()) {
                /* top side of connector is SRC -> vertical line. */
                src.setX( srcBounds.getCenterX() );
                src.setY( srcBounds.getMaxY() );
                srcHorizontal = false;

                dst.setX( dstBounds.getCenterX() );
                dst.setY( dstBounds.getY() );
                dstHorizontal = false;
            } else if (dstBounds.getCenterY() == srcBounds.getCenterY())
                /* top side of connector is both SRC and DST -> centers collapse, don't draw. */
                ;

            else {
                /* top side of connector is DST -> vertical line. */
                src.setX( srcBounds.getCenterX() );
                src.setY( srcBounds.getY() );
                srcHorizontal = false;

                dst.setX( dstBounds.getCenterX() );
                dst.setY( dstBounds.getMaxY() );
                dstHorizontal = false;
            }
        } else if (dstBounds.getCenterY() > srcBounds.getCenterY()) {
            /* top side of connector is SRC */
            src.setX( srcBounds.getCenterX() );
            src.setY( srcBounds.getMaxY() );
            srcHorizontal = false;

            dst.setX( dstBounds.getCenterX() );
            dst.setY( dstBounds.getY() );
            dstHorizontal = false;
        } else if (dstBounds.getCenterY() == srcBounds.getCenterY()) {
            /* top side of connector is both SRC and DST -> horizontal line. */
            src.setX( srcBounds.getX() );
            src.setY( srcBounds.getCenterY() );
            srcHorizontal = true;

            dst.setX( dstBounds.getMaxX() );
            dst.setY( dstBounds.getCenterY() );
            dstHorizontal = true;
        } else {
            /* top side of connector is DST */
            src.setX( srcBounds.getCenterX() );
            src.setY( srcBounds.getY() );
            srcHorizontal = false;

            dst.setX( dstBounds.getCenterX() );
            dst.setY( dstBounds.getMaxY() );
            dstHorizontal = false;
        }

        if (srcBounds.getY() >= dstBounds.getY() && srcBounds.getY() <= dstBounds.getMaxY()
            || dstBounds.getY() >= srcBounds.getY() && dstBounds.getY() <= srcBounds.getMaxY()) {

            if (dstBounds.getCenterX() > srcBounds.getCenterX()) {
                src.setX( srcBounds.getMaxX() );
                src.setY( srcBounds.getCenterY() );
                srcHorizontal = true;

                dst.setX( dstBounds.getX() );
                dst.setY( dstBounds.getCenterY() );
                dstHorizontal = true;
            } else if (dstBounds.getCenterX() == srcBounds.getCenterX())
                ;

            else {
                src.setX( srcBounds.getX() );
                src.setY( srcBounds.getCenterY() );
                srcHorizontal = true;

                dst.setX( dstBounds.getMaxX() );
                dst.setY( dstBounds.getCenterY() );
                dstHorizontal = true;
            }
        } else if (srcBounds.getMaxX() < dstBounds.getX() || srcBounds.getX() > dstBounds.getMaxX())
            if (dstBounds.getCenterX() > srcBounds.getCenterX()) {
                src.setX( srcBounds.getMaxX() );
                src.setY( srcBounds.getCenterY() );
                srcHorizontal = true;

                dst.setX( dstBounds.getX() );
                dst.setY( dstBounds.getCenterY() );
                dstHorizontal = true;
            } else if (dstBounds.getCenterX() == srcBounds.getCenterX())
                ;

            else {
                src.setX( srcBounds.getX() );
                src.setY( srcBounds.getCenterY() );
                srcHorizontal = true;

                dst.setX( dstBounds.getMaxX() );
                dst.setY( dstBounds.getCenterY() );
                dstHorizontal = true;
            }

        Vec2 offset = new Vec2( Math.min( src.getX(), dst.getX() ), Math.min( src.getY(), dst.getY() ) );
        Vec2 size = new Vec2( Math.abs( dst.getX() - src.getX() ), Math.abs( dst.getY() - src.getY() ) );
        // path.size = new Vec2( Math.max( Math.abs( path.dst.x - path.src.x ), 1 ), Math.max(
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
    public Vec2 getSrc() {

        return src;
    }

    /**
     * Retrieve the dst of this {@link Path}.
     *
     * @return Guess.
     */
    public Vec2 getDst() {

        return dst;
    }

    /**
     * Retrieve the offset of this {@link Path}.
     *
     * @return Guess.
     */
    public Vec2 getOffset() {

        return offset;
    }

    /**
     * Retrieve the size of this {@link Path}.
     *
     * @return Guess.
     */
    public Vec2 getSize() {

        return size;
    }
}
