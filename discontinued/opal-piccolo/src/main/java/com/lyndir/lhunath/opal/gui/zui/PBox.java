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

import com.lyndir.lhunath.opal.math.Vec2;
import com.lyndir.lhunath.opal.math.Vec2D;
import com.lyndir.lhunath.opal.system.util.UIUtils;
import com.lyndir.lhunath.opal.system.util.Utils;
import org.piccolo2d.PCanvas;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import javax.swing.*;
import org.piccolo2d.util.PPaintContext;


/**
 * <i>PBox - [in short] (TODO).</i><br> <br> [description / usage].<br> <br>
 *
 * @author lhunath
 */
public class PBox extends PShape {

    private static final double PADDING = 10;

    private static final Vec2 tipOffset = Vec2.create( -10, 30 );

    protected final PCanvas canvas;
    private         PBox    tooltipNode;
    private         String  title;
    private         Paint   outlinePaint;
    private         Paint   textPaint;
    private         boolean autoSize;
    private         boolean locked;
    private         int     ratio;

    private Font font;

    private Icon icon;

    /**
     * Create a new PBox instance.
     *
     * @param canvas   The canvas in which this node is contained.
     * @param title    The title text of this box.
     * @param location The offset relative to the top left corner of the canvas for this box.
     */
    public PBox(final PCanvas canvas, final String title, final Point location) {

        this( canvas, title );

        setOffset( location );
    }

    /**
     * Create a new PBox instance.
     *
     * @param canvas The canvas in which this node is contained.
     * @param title  The title text of this box.
     */
    public PBox(final PCanvas canvas, final String title) {

        this( canvas );

        setTitle( title );
    }

    /**
     * Create a new PBox instance.
     *
     * @param canvas The canvas in which this node is contained.
     */
    public PBox(final PCanvas canvas) {

        this.canvas = canvas;

        setPaint( Color.gray );
        setTextPaint( Color.black );
        setOutlinePaint( Color.black );
        setFont( UIManager.getFont( "Label.font" ) );

        setSize( new Dimension( 1, 1 ) );
        setAutoSize( true );
        setLocked( false );
        setRatio( 0 );
    }

    /**
     * Lock this box into its parent. A locked box will group into its parent {@link PBox} and extend its minimum size.
     *
     * @param locked Set whether this box is locked into its parent.
     */
    public void setLocked(final boolean locked) {

        this.locked = locked;
        if (locked)
            setAutoSize( false );
    }

    /**
     * Retrieve the locked state of this PBox.
     *
     * @return Guess.
     */
    public boolean isLocked() {

        return locked;
    }

    /**
     * @return The paint used to draw the outline.
     */
    public Paint getOutlinePaint() {

        return outlinePaint;
    }

    /**
     * Define the paint used to draw the outline.
     *
     * @param paint Set this to null to disable outline painting.
     */
    public void setOutlinePaint(final Paint paint) {

        outlinePaint = paint;
    }

    /**
     * @return The paint used to draw the text.
     */
    public Paint getTextPaint() {

        return textPaint;
    }

    /**
     * Define the paint used to draw the text.
     *
     * @param paint Guess.
     */
    public void setTextPaint(final Paint paint) {

        textPaint = paint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Shape createShape() {

        return new RoundRectangle2D.Double( getX(), getY(), getWidth(), getHeight(), PADDING, PADDING );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean scaleShape(final double x, final double y, final double w, final double h) {

        ((RoundRectangle2D) getShape()).setRoundRect( x, y, w, h, PADDING, PADDING );
        return true;
    }

    /**
     * Assign an icon to display along with this node.
     *
     * @param icon The icon to use.
     */
    public void setIcon(final Icon icon) {

        this.icon = icon;
    }

    /**
     * Get the icon displayed along with this node.
     *
     * @return The icon used.
     */
    public Icon getIcon() {

        return icon;
    }

    /**
     * Set the tooltip of this PBox.
     *
     * @param tooltip Guess.
     */
    public void setToolTip(final String tooltip) {

        if (tooltip == null) {
            showTooltip( null );
            tooltipNode = null;
            return;
        }

        if (tooltipNode == null) {
            tooltipNode = new PBox( canvas, tooltip );
            tooltipNode.setFont( Font.decode( "Monospaced-Italic-14" ) );
            tooltipNode.setOutlinePaint( outlinePaint );
            tooltipNode.setTextPaint( textPaint );
            tooltipNode.setPickable( false );
            tooltipNode.setLocked( false );

            if (getPaint() instanceof Color)
                tooltipNode.setPaint( UIUtils.setAlpha( (Color) getPaint(), 200 ) );
            else
                tooltipNode.setPaint( getPaint() );
        } else
            tooltipNode.setTitle( tooltip );
    }

    /**
     * Display the tooltip at the given position relative to this node.
     *
     * @param offset The position relative to this node of the top left corner of the tooltip.<br> Set this to null to hide the tooltip.
     */
    public void showTooltip(Point2D offset) {

        if (tooltipNode == null)
            return;

        tooltipNode.removeFromParent();
        if (offset == null)
            return;

        Point2D cameraOffset = canvas.getCamera().viewToLocal( localToGlobal( offset ) );
        Vec2 offsetVec = Vec2.create( (int)cameraOffset.getX(), (int)cameraOffset.getY() ).translate( tipOffset );
        offset.setLocation( offsetVec.getX(), offsetVec.getY() );
        if (!tooltipNode.getOffset().equals( offset ))
            tooltipNode.setOffset( offset );

        canvas.getCamera().addChild( tooltipNode );
    }

    /**
     * Retrieve the title of this PBox.
     *
     * @return Guess.
     */
    public String getTitle() {

        return title;
    }

    /**
     * Set the title of this PBox.
     *
     * @param title Guess.
     */
    public void setTitle(final String title) {

        if (title == null)
            this.title = title;

        else if (title.equals( this.title ))
            return;

        this.title = title;
        invalidatePaint();
    }

    /**
     * @return Whether this box is being automatically sized.
     */
    public boolean isAutoSize() {

        return autoSize;
    }

    /**
     * Set the autoSize of this PBox.
     *
     * @param autoSize Guess.
     */
    public void setAutoSize(final boolean autoSize) {

        if (this.autoSize == autoSize)
            return;

        this.autoSize = autoSize;

        if (autoSize)
            invalidateFullBounds();
    }

    /**
     * Set the size of this PBox and disable autosizing.
     *
     * @param size Guess.
     */
    public void setSize(final Dimension2D size) {

        setAutoSize( false );

        if (getBounds().getSize().equals( size ))
            return;

        setBounds( getX(), getY(), size.getWidth(), size.getHeight() );
    }

    /**
     * Set the offset relative to the top left corner of the canvas for this box.
     *
     * @param offset Guess.
     */
    @Override
    public void setOffset(final Point2D offset) {

        if (getOffset().equals( offset ))
            return;

        super.setOffset( offset );
    }

    /**
     * Set the offset relative to the top left corner of the canvas for this box.
     *
     * @param offset Guess.
     */
    public void setCenter(Point2D offset) {

        offset = new Point2D.Double( offset.getX() - getWidth() / 2, offset.getY() - getHeight() / 2 );

        if (getOffset().equals( offset ))
            return;

        super.setOffset( offset );
    }

    /**
     * Get the font to use for the title of this box.
     *
     * @return Guess.
     */
    public Font getFont() {

        return font;
    }

    /**
     * Set the font to use for the title of this box.
     *
     * @param font Guess.
     */
    public void setFont(final Font font) {

        this.font = font;
    }

    /**
     * Retrieve the ratio of this {@link PBox}.
     *
     * @return Guess.
     */
    public int getRatio() {

        return ratio;
    }

    /**
     * Set the ratio of this {@link PBox}.
     *
     * @param ratio Guess.
     */
    public void setRatio(final int ratio) {

        this.ratio = ratio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void paint(final PPaintContext paintContext) {

        Graphics2D g2 = (Graphics2D) paintContext.getGraphics().create();

        /* Layout children. */
        Rectangle2D titleBounds = new Rectangle2D.Double( 0, 0, 0, 0 );
        if (title != null)
            titleBounds = g2.getFontMetrics( getFont() ).getStringBounds( title, g2 );

        double padLocked = isLocked()? ratio: 0;
        double padUnlocked = isLocked()? 0: ratio;
        double maxWidth = Math.max( titleBounds.getWidth() + PADDING * 3 + padLocked * 2, getWidth() );
        double y = titleBounds.getHeight() + PADDING;
        ArrayList<Object> children = new ArrayList<Object>( getChildrenReference() );

        for (final Object child : children) {
            if (!(child instanceof PBox))
                continue;
            PBox box = (PBox) child;

            if (box.isLocked())
                maxWidth = Math.max( maxWidth, box.getWidth() + PADDING * 2 - padUnlocked * 2 );
        }

        for (final Object child : children) {
            if (!(child instanceof PBox))
                continue;
            PBox box = (PBox) child;

            if (!box.isLocked())
                continue;

            if (box.getXOffset() != PADDING || box.getYOffset() != y)
                box.setOffset( PADDING, y );
            if (box.setBounds( box.getX(), box.getY(), maxWidth - PADDING * 2 + padUnlocked * 2, box.getHeight() ))
                invalidateFullBounds();

            y += box.getHeight() + PADDING;
        }

        if (autoSize || getWidth() < maxWidth || getHeight() < y)
            if (setBounds( -padLocked, 0, maxWidth, y ))
                return;

        /* Draw this box. */
        RoundRectangle2D box = (RoundRectangle2D) getShape();
        box = new RoundRectangle2D.Double( -padLocked + 2, 2, box.getWidth() - 4, box.getHeight() - 4, PADDING, PADDING );

        g2.draw( getBounds() );
        g2.setPaint( getPaint() );
        g2.fill( box );
        if (outlinePaint != null) {
            g2.setPaint( outlinePaint );
            g2.setStroke( new BasicStroke( 2 ) );
            g2.draw( box );
        }

        if (title != null) {
            g2.setFont( getFont() );
            g2.setPaint( textPaint );
            g2.setStroke( new BasicStroke( 1 ) );
            g2.drawString( title, (int) (getBounds().getCenterX() - titleBounds.getCenterX()),
                           (int) (getY() + titleBounds.getHeight() + 3) );
        }

        g2.dispose();
    }
}
