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

import com.lyndir.lhunath.lib.math.Path;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 * TODO: PConnector<br>
 *
 * @author lhunath
 */
public class PConnector extends PNode {

    private PNode src, dst;
    private int stroke;
    private double srcX;
    private double srcY;
    private double dstX;
    private double dstY;
    private boolean srcHorizontal;
    private boolean dstHorizontal;

    /**
     * Create a new PConnector instance.
     */
    public PConnector() {

        setPickable( false );
        setBounds( 0, 0, 1, 1 );
        setPaint( Color.GRAY );
        setStrokeWidth( 4 );
    }

    /**
     * Create a new PConnector instance.
     *
     * @param src The connector's source.
     * @param dst The connector's destination.
     */
    public PConnector(final PNode src, final PNode dst) {

        this();

        this.src = src;
        this.dst = dst;
    }

    /**
     * Set the strokeWidth of this PConnector.
     *
     * @param strokeWidth Guess.
     */
    public void setStrokeWidth(final int strokeWidth) {

        stroke = strokeWidth;
    }

    /**
     * Retrieve the source of this {@link PConnector}.
     *
     * @return Guess.
     */
    public PNode getSrc() {

        return src;
    }

    /**
     * Retrieve the destination of this {@link PConnector}.
     *
     * @return Guess.
     */
    public PNode getDst() {

        return dst;
    }

    /**
     * Add a node connected through this connector, if possible.
     *
     * @param node The node to connect as either source or destination.
     *
     * @return true If a source or destination was unset and has now been assigned.
     */
    public boolean addNode(final PNode node) {

        if (src == null) {
            src = node;

            removeFromParent();
            PNode n = src;
            while (!(n instanceof PLayer))
                n = n.getParent();
            n.addChild( this );
            setOffset( src.getGlobalTranslation() );
        } else if (dst == null) {
            dst = node;
            calculateNewBounds();
        } else
            return false;

        return true;
    }

    /**
     * Remove a node connected through this connector, if present.
     *
     * @param node The node to remove as either source or destination.
     *
     * @return true If the given node was a source or destination node and has now been removed.
     */
    public boolean removeNode(final PNode node) {

        if (src == node) {
            src = dst;
            dst = null;

            removeFromParent();
            PNode n = src;
            while (n.getParent() != null)
                n = n.getParent();
            n.addChild( this );
            setOffset( src.getGlobalTranslation() );
        } else if (dst == node)
            dst = null;

        else
            return false;

        return true;
    }

    /**
     * Remove this connector from the field.
     */
    public void remove() {

        removeNode( dst );
        removeNode( src );
    }

    private boolean calculateNewBounds() {

        PBounds srcBounds = src.getGlobalBounds();
        PBounds dstBounds = dst.getGlobalBounds();
        Path path = Path.calculatePath( srcBounds, dstBounds );

        Point2D newOffset = path.getOffset().toPoint();
        if (!getOffset().equals( newOffset ))
            setOffset( newOffset );

        return setBounds( new Rectangle( -stroke / 2 - 2, -stroke / 2 - 2, path.getSize().x + stroke + 4, path.getSize().y + stroke + 4 ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paint(final PPaintContext paintContext) {

        if (src == null || dst == null)
            return;

        moveConnectorsToFront();
        Graphics2D g2 = (Graphics2D) paintContext.getGraphics().create();

        if (calculateNewBounds())
            return;

        srcX -= getXOffset();
        srcY -= getYOffset();
        dstX -= getXOffset();
        dstY -= getYOffset();

        if (src.getPaint() instanceof Color && dst.getPaint() instanceof Color)
            g2.setPaint( new GradientPaint( new Point2D.Double( srcX, srcY ), (Color) src.getPaint(), new Point2D.Double( dstX, dstY ),
                                            (Color) dst.getPaint() ) );
        else
            g2.setPaint( getPaint() );

        CubicCurve2D curve = new CubicCurve2D.Double( srcX, srcY, srcHorizontal? (srcX + dstX) / 2: srcX,
                                                      srcHorizontal? srcY: (srcY + dstY) / 2, dstHorizontal? (srcX + dstX) / 2: dstX,
                                                      dstHorizontal? dstY: (srcY + dstY) / 2, dstX, dstY );

        g2.clip( getBounds() );
        g2.setStroke( new BasicStroke( stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        g2.draw( curve );
        g2.dispose();
    }

    @SuppressWarnings({ "unchecked", "RawUseOfParameterizedType" })
    private void moveConnectorsToFront() {

        boolean sane = true;
        LinkedList children = new LinkedList( getParent().getChildrenReference() );
        ListIterator it = children.listIterator( children.size() );
        Object child;

        while (it.hasPrevious() && (child = it.previous()) != null)
            if (!(child instanceof PNode))
                sane = false;
            else if (!sane)
                ((PNode) child).moveToFront();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return "[x,y](" + getXOffset() + ", " + getYOffset() + ") @ [w,h](" + getWidth() + ", " + getHeight() + ")";
    }
}
