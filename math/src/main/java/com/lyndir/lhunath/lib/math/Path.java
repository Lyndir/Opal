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
 * <i>{@link Path} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * 
 * @author lhunath
 */
public class Path {

    private boolean srcHorizontal;
    private boolean dstHorizontal;
    private Vec2    src;
    private Vec2    dst;
    private Vec2    size;
    private Vec2    offset;


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return "{Path: " + src + (srcHorizontal? " - ": " | ") + " --> " + (dstHorizontal? " - ": " | ") + dst + "}";
    }

    /**
     * Create a new {@link Path} instance.
     */
    public Path() {

        src = new Vec2();
        dst = new Vec2();
        size = new Vec2();
        offset = new Vec2();
    }

    /**
     * Calculate geometry needed for defining a path between two rectangular shapes that does not intersect with either
     * shape by attaching the path to the center of either top, left, bottom or right side of each shape.
     * 
     * @param srcBounds
     *            The rectangle that the path originates from.
     * @param dstBounds
     *            The rectangle that the path arrives at.
     * @return A {@link Path} object that provides the geometry needed to define the requested path.
     */
    public static Path calculatePath(RectangularShape srcBounds, RectangularShape dstBounds) {

        Path path = new Path();

        /* left side of connector is SRC */
        if (dstBounds.getCenterX() > srcBounds.getCenterX()) {

            if (dstBounds.getCenterY() > srcBounds.getCenterY()) {
                /* top side of connector is SRC */
                path.src.x = srcBounds.getCenterX();
                path.src.y = srcBounds.getMaxY();
                path.srcHorizontal = false;

                path.dst.x = dstBounds.getCenterX();
                path.dst.y = dstBounds.getY();
                path.dstHorizontal = false;
            } else if (dstBounds.getCenterY() == srcBounds.getCenterY()) {
                /* top side of connector is both SRC and DST -> horizontal line. */
                path.src.x = srcBounds.getMaxX();
                path.src.y = srcBounds.getCenterY();
                path.srcHorizontal = true;

                path.dst.x = dstBounds.getX();
                path.dst.y = dstBounds.getCenterY();
                path.dstHorizontal = true;
            } else {
                /* top side of connector is DST */
                path.src.x = srcBounds.getCenterX();
                path.src.y = srcBounds.getY();
                path.srcHorizontal = false;

                path.dst.x = dstBounds.getCenterX();
                path.dst.y = dstBounds.getMaxY();
                path.dstHorizontal = false;
            }
        }
        /* left side of connector is both SRC and DST -> vertical line. */
        else if (dstBounds.getCenterX() == srcBounds.getCenterX()) {

            if (dstBounds.getCenterY() > srcBounds.getCenterY()) {
                /* top side of connector is SRC -> vertical line. */
                path.src.x = srcBounds.getCenterX();
                path.src.y = srcBounds.getMaxY();
                path.srcHorizontal = false;

                path.dst.x = dstBounds.getCenterX();
                path.dst.y = dstBounds.getY();
                path.dstHorizontal = false;
            } else if (dstBounds.getCenterY() == srcBounds.getCenterY())
                /* top side of connector is both SRC and DST -> centers collapse, don't draw. */
                return path;

            else {
                /* top side of connector is DST -> vertical line. */
                path.src.x = srcBounds.getCenterX();
                path.src.y = srcBounds.getY();
                path.srcHorizontal = false;

                path.dst.x = dstBounds.getCenterX();
                path.dst.y = dstBounds.getMaxY();
                path.dstHorizontal = false;
            }
        } else if (dstBounds.getCenterY() > srcBounds.getCenterY()) {
            /* top side of connector is SRC */
            path.src.x = srcBounds.getCenterX();
            path.src.y = srcBounds.getMaxY();
            path.srcHorizontal = false;

            path.dst.x = dstBounds.getCenterX();
            path.dst.y = dstBounds.getY();
            path.dstHorizontal = false;
        } else if (dstBounds.getCenterY() == srcBounds.getCenterY()) {
            /* top side of connector is both SRC and DST -> horizontal line. */
            path.src.x = srcBounds.getX();
            path.src.y = srcBounds.getCenterY();
            path.srcHorizontal = true;

            path.dst.x = dstBounds.getMaxX();
            path.dst.y = dstBounds.getCenterY();
            path.dstHorizontal = true;
        } else {
            /* top side of connector is DST */
            path.src.x = srcBounds.getCenterX();
            path.src.y = srcBounds.getY();
            path.srcHorizontal = false;

            path.dst.x = dstBounds.getCenterX();
            path.dst.y = dstBounds.getMaxY();
            path.dstHorizontal = false;
        }

        if (srcBounds.getY() >= dstBounds.getY() && srcBounds.getY() <= dstBounds.getMaxY()
            || dstBounds.getY() >= srcBounds.getY() && dstBounds.getY() <= srcBounds.getMaxY()) {

            if (dstBounds.getCenterX() > srcBounds.getCenterX()) {
                path.src.x = srcBounds.getMaxX();
                path.src.y = srcBounds.getCenterY();
                path.srcHorizontal = true;

                path.dst.x = dstBounds.getX();
                path.dst.y = dstBounds.getCenterY();
                path.dstHorizontal = true;
            } else if (dstBounds.getCenterX() == srcBounds.getCenterX())
                return path;

            else {
                path.src.x = srcBounds.getX();
                path.src.y = srcBounds.getCenterY();
                path.srcHorizontal = true;

                path.dst.x = dstBounds.getMaxX();
                path.dst.y = dstBounds.getCenterY();
                path.dstHorizontal = true;
            }
        } else if (srcBounds.getMaxX() < dstBounds.getX() || srcBounds.getX() > dstBounds.getMaxX())
            if (dstBounds.getCenterX() > srcBounds.getCenterX()) {
                path.src.x = srcBounds.getMaxX();
                path.src.y = srcBounds.getCenterY();
                path.srcHorizontal = true;

                path.dst.x = dstBounds.getX();
                path.dst.y = dstBounds.getCenterY();
                path.dstHorizontal = true;
            } else if (dstBounds.getCenterX() == srcBounds.getCenterX())
                return path;

            else {
                path.src.x = srcBounds.getX();
                path.src.y = srcBounds.getCenterY();
                path.srcHorizontal = true;

                path.dst.x = dstBounds.getMaxX();
                path.dst.y = dstBounds.getCenterY();
                path.dstHorizontal = true;
            }

        path.offset = new Vec2( Math.min( path.src.x, path.dst.x ), Math.min( path.src.y, path.dst.y ) );
        path.size = new Vec2( Math.abs( path.dst.x - path.src.x ), Math.abs( path.dst.y - path.src.y ) );
        // path.size = new Vec2( Math.max( Math.abs( path.dst.x - path.src.x ), 1 ), Math.max(
        // Math.abs( path.dst.y - path.src.y ), 1 ) );

        return path;
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
