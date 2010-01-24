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
package com.lyndir.lhunath.lib.gui.zui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * TODO: {@link PShape}<br>
 *
 * @author lhunath
 */
public abstract class PShape extends PNode {

    private java.awt.Shape shape;

    protected abstract java.awt.Shape createShape();

    /**
     * Update the shape of this node. Called whenever the bounds change.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return <code>true</code> if this method modified the shape.
     */
    protected abstract boolean scaleShape(double x, double y, double w, double h);

    /**
     * Create a new {@link PShape} instance.
     */
    public PShape() {

        super();

        setPaint( Color.BLACK );
    }

    /**
     * @return The shape represented by this object.
     */
    public java.awt.Shape getShape() {

        if (shape == null)
            shape = createShape();

        return shape;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean intersects(Rectangle2D aBounds) {

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
    public boolean setBounds(double x, double y, double w, double h) {

        return super.setBounds( x, y, w, h ) && scaleShape( x, y, w, h );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(PPaintContext aPaintContext) {

        Graphics2D g2 = aPaintContext.getGraphics();
        g2.setPaint( getPaint() );
        g2.draw( getShape() );
    }
}
