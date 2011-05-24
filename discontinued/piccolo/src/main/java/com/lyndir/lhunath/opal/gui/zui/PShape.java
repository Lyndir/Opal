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
package com.lyndir.lhunath.opal.gui.zui;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;
import java.awt.*;
import java.awt.geom.Rectangle2D;


/**
 * TODO: {@link PShape}<br>
 *
 * @author lhunath
 */
public abstract class PShape extends PNode {

    private Shape shape;

    /**
     * @return Create the AWT shape that this PShape renders.
     */
    protected abstract Shape createShape();

    /**
     * Update the shape of this node. Called whenever the bounds change.
     *
     * @param x The new X-origin of the shape.
     * @param y The new Y-origin of the shape.
     * @param w The new width of the shape.
     * @param h The new height of the shape.
     *
     * @return <code>true</code> if this method modified the shape.
     */
    protected abstract boolean scaleShape(double x, double y, double w, double h);

    /**
     * Create a new {@link PShape} instance.
     */
    protected PShape() {

        setPaint( Color.BLACK );
    }

    /**
     * @return The shape represented by this object.
     */
    public Shape getShape() {

        if (shape == null)
            shape = createShape();

        return shape;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean intersects(final Rectangle2D aBounds) {

        return getShape().intersects( aBounds );
    }

    /**
     * Update the scale of the shape using current values for location and dimension.
     *
     * @return true on success.
     */
    protected boolean scaleShape() {

        return scaleShape( getX(), getY(), getWidth(), getHeight() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setBounds(final double x, final double y, final double w, final double h) {

        return super.setBounds( x, y, w, h ) && scaleShape( x, y, w, h );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(final PPaintContext paintContext) {

        Graphics2D g2 = paintContext.getGraphics();
        g2.setPaint( getPaint() );
        g2.draw( getShape() );
    }
}
