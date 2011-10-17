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
package com.lyndir.lhunath.opal.gui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.*;


/**
 * <i>{@link PaintPanel} - A panel that uses a paint for its background, rather than a color.</i><br> <br> In an attempt to spice up the
 * com.lyndir.lhunath.opal.guis to generate panels that use {@link Paint} as background, rather than {@link Color}.<br> Several convenience
 * methods exist to quickly generate a nice gradient for the panel's paint.<br> <br>
 *
 * @author lhunath
 */
public class PaintPanel extends ScrollPanel {

    protected Image backgroundImage;
    private   int   autoColorControl;
    GradientPaint autoPaint;
    Paint         paint;

    /**
     * Create a new {@link PaintPanel} instance.
     *
     * @param paint The paint to use to fill the background of this panel with.
     */
    public PaintPanel(final Paint paint) {

        setPaint( paint );
        setAutoColorControl( 0 );
        setLayout( new BorderLayout() );
        addComponentListener( new PaintPanelComponentAdapter() );
    }

    /**
     * Create a {@link PaintPanel} that has a gradient background paint spanning from the top-left corner to the bottom-left corner using
     * the current look and feel for getting the appropriate colors.
     *
     * @return The resulting {@link PaintPanel}.
     */
    public static PaintPanel gradientPanel() {

        PaintPanel panel = new PaintPanel( null );
        panel.setAutoColorControl( 1 );
        panel.updateUI();

        return panel;
    }

    /**
     * Create a {@link PaintPanel} that has a gradient background paint spanning from the top-left corner to the bottom-left corner using
     * the current look and feel for getting the appropriate colors auto-corrected by the specified identifier.
     *
     * @param autoColorControl &gt; 0: Choose color automatically from L&F. Higher is brighter.
     *
     * @return The resulting {@link PaintPanel}.
     */
    public static PaintPanel gradientPanel(final int autoColorControl) {

        PaintPanel panel = new PaintPanel( null );
        panel.setAutoColorControl( autoColorControl );
        panel.updateUI();

        return panel;
    }

    /**
     * Create a {@link PaintPanel} that has a gradient background paint using the current look and feel for getting the appropriate colors.
     *
     * @param startPos The start of the gradient.
     * @param endPos   The end of the gradient.
     *
     * @return The resulting {@link PaintPanel}.
     */
    public static PaintPanel gradientPanel(final Point2D startPos, final Point2D endPos) {

        PaintPanel panel = new PaintPanel( new GradientPaint( startPos, Color.WHITE, endPos, Color.BLACK ) );
        panel.setAutoColorControl( 1 );
        panel.updateUI();

        return panel;
    }

    /**
     * Create a {@link PaintPanel} that has a gradient background paint spanning from the top-left corner to the bottom-left corner.
     *
     * @param startCol The color to start the gradient with.
     * @param endCol   The color to end the gradient with.
     *
     * @return The resulting {@link PaintPanel}.
     */
    public static PaintPanel gradientPanel(final Color startCol, final Color endCol) {

        return new PaintPanel( gradientPaint( startCol, endCol ) );
    }

    /**
     * Create a {@link PaintPanel} that has a gradient background paint.
     *
     * @param startPos The start of the gradient.
     * @param startCol The color to start the gradient with.
     * @param endPos   The end of the gradient.
     * @param endCol   The color to end the gradient with.
     *
     * @return The resulting {@link PaintPanel}.
     */
    public static PaintPanel gradientPanel(Point2D startPos, final Color startCol, final Point2D endPos, Color endCol) {

        return new PaintPanel( new GradientPaint( startPos, startCol, endPos, endCol ) );
    }

    /**
     * Create a gradient paint spanning from the top-left corner to the bottom-left corner.
     *
     * @param startCol The color to start the gradient with.
     * @param endCol   The color to end the gradient with.
     *
     * @return Guess.
     */
    public static GradientPaint gradientPaint(final Color startCol, final Color endCol) {

        return new GradientPaint( new Point2D.Double( 1, 1 ), startCol, new Point2D.Double( -1, -1 ), endCol );
    }

    /**
     * Create a {@link PaintPanel} that has a tiled background picture.
     *
     * @param image The image to tile on the background.
     *
     * @return The resulting {@link PaintPanel}.
     */
    public static PaintPanel tiledPanel(final Image image) {

        BufferedImage buffer = new BufferedImage( image.getWidth( null ), image.getHeight( null ), BufferedImage.TYPE_INT_ARGB );
        buffer.getGraphics().drawImage( image, 0, 0, null );
        return new PaintPanel( new TexturePaint( buffer, new Rectangle( 0, 0, buffer.getWidth(), buffer.getHeight() ) ) );
    }

    /**
     * @return The automatic coloring brightness active on this panel. Zero if not active.
     */
    public int getAutoColorControl() {

        return autoColorControl;
    }

    /**
     * Automatically color this panel based on the active look and feel. Set this to a value higher than zero to enable the effect. Higher
     * values result in brighter backgrounds.
     *
     * @param autoColorControl Guess.
     *
     * @return This instance.
     */
    public PaintPanel setAutoColorControl(final int autoColorControl) {

        this.autoColorControl = autoColorControl;
        updateUI();

        return this;
    }

    /**
     * @return The paint of this panel.
     */
    public Paint getPaint() {

        return paint;
    }

    /**
     * Set the paint of this {@link PaintPanel}.
     *
     * @param paint Guess.
     */
    public void setPaint(final Paint paint) {

        this.paint = paint;
        updateUI();
    }

    /**
     * @return The background image of this {@link PaintPanel}.
     */
    public Image getBackgroundImage() {

        return backgroundImage;
    }

    /**
     * Set the background image of this {@link PaintPanel}.
     *
     * @param image Guess.
     */
    public void setBackgroundImage(final Image image) {

        backgroundImage = image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI() {

        super.updateUI();

        /* Auto Color Control. */
        if (autoColorControl > 0) {
            int adjust = 0;
            Color background = MyLookAndFeel.getActiveDark();
            if (background == null)
                background = UIManager.getColor( "InternalFrame.activeTitleBackground" );
            while (autoColorControl - ++adjust > 0)
                background = background.brighter();

            if (paint instanceof GradientPaint)
                paint = new GradientPaint( ((GradientPaint) paint).getPoint1(), MyLookAndFeel.getActiveBright(),
                                           ((GradientPaint) paint).getPoint2(), background );
            else
                paint = gradientPaint( MyLookAndFeel.getActiveBright(), background );
        }

        /* Resize gradient. */
        if (!(paint instanceof GradientPaint))
            autoPaint = null;

        else {
            GradientPaint gradPaint = (GradientPaint) paint;
            Point2D point1 = (Point2D) gradPaint.getPoint1().clone();
            Point2D point2 = (Point2D) gradPaint.getPoint2().clone();

            if (point1.getX() < 0)
                point1.setLocation( getWidth() + point1.getX(), point1.getY() );
            if (point1.getY() < 0)
                point1.setLocation( point1.getX(), getHeight() + point1.getY() );
            if (point2.getX() < 0)
                point2.setLocation( getWidth() + point2.getX(), point2.getY() );
            if (point2.getY() < 0)
                point2.setLocation( point2.getX(), getHeight() + point2.getY() );

            autoPaint = new GradientPaint( point1, gradPaint.getColor1(), point2, gradPaint.getColor2() );
        }

        /* Resize Background Image (only if needed). */
        if (backgroundImage != null && getWidth() + getHeight() - backgroundImage.getWidth( null ) - backgroundImage.getHeight( null ) != 0)
            backgroundImage = backgroundImage.getScaledInstance( getWidth(), getHeight(), Image.SCALE_FAST );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(final Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        // Fill the panel with the paint.
        g2.setPaint( autoPaint == null? paint: autoPaint );
        g2.fill( g2.getClip() );

        // Draw a faint background image.
        if (backgroundImage != null && backgroundImage.getWidth( this ) > 0 && backgroundImage.getHeight( this ) > 0) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.2f ) );
            g2.drawImage( backgroundImage, 0, 0, this );
            g2.setComposite( originalComposite );
        }
    }

    private class PaintPanelComponentAdapter extends ComponentAdapter {

        PaintPanelComponentAdapter() {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void componentResized(final ComponentEvent e) {

            updateUI();
        }
    }
}
