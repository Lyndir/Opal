/*
 *   Copyright 2007, Maarten Billemont
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

package com.lyndir.lhunath.lib.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.lyndir.lhunath.lib.math.Vec2;

/**
 * <h2>{@link GLabel} - [in short] (TODO).</h2>
 * <p>
 * [description / usage].
 * </p>
 * <p>
 * <i>Dec 2, 2007</i>
 * </p>
 * 
 * @author mbillemo
 */
public class GLabel extends JLabel {

    protected boolean     hasMouse, isPressed;
    private GradientPaint gradientBase;
    private int           arc = 5;
    protected int         padding;

    public GLabel(String text, Icon icon, int horizontalAlignment, Color gradient) {

        super( text, icon, horizontalAlignment );
        init( gradient );
    }

    public GLabel(String text, int horizontalAlignment, Color gradient) {

        super( text, horizontalAlignment );
        init( gradient );
    }

    public GLabel(String text, Color gradient) {

        super( text );
        init( gradient );
    }

    public GLabel(Icon image, int horizontalAlignment, Color gradient) {

        super( image, horizontalAlignment );
        init( gradient );
    }

    public GLabel(Icon image, Color gradient) {

        super( image );
        init( gradient );
    }

    public GLabel(Color gradient) {

        super();
        init( gradient );
    }

    private void init(Color gradient) {

        addMouseListener( new GLabelMouseAdapter() );
        setHorizontalAlignment( HORIZONTAL );
        setGradient( gradient );
        setPadding( 1 );
    }

    /**
     * @param padding
     *        The padding of this {@link GLabel}.
     */
    public void setPadding(int padding) {

        this.padding = padding;
        repaint();
    }

    /**
     * @param base
     *        The base color of the gradient for this {@link GLabel}'s background.
     */
    private void setGradient(Color base) {

        Color src = new Color( base.getRed() / 255f, base.getGreen() / 255f, base.getBlue() / 255f, 50 / 255f );
        gradientBase = new GradientPaint( new Point2D.Double( 0, 0 ), src, new Point2D.Double( 0, 1 ), base );
    }

    /**
     * @param arc
     *        The arc of this {@link GLabel}.
     */
    public void setArc(int arc) {

        this.arc = arc;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        Vec2 size = new Vec2( getSize() );
        Point2D src = new Vec2( gradientBase.getPoint1() ).multiply( size ).toPoint();
        Point2D dst = new Vec2( gradientBase.getPoint2() ).multiply( size ).toPoint();
        Color color1 = gradientBase.getColor1();
        Color color2 = gradientBase.getColor2();

        if (hasMouse) {
            color1 = new Color( color1.getAlpha() << 24 | color1.brighter().getRGB() - 0xff000000, true );
            color2 = new Color( color2.getAlpha() << 24 | color2.brighter().getRGB() - 0xff000000, true );
        }

        GradientPaint gradient = new GradientPaint( src, color1, dst, color2 );

        g2.setPaint( gradient );
        g2.fillRoundRect( padding, padding, getWidth() - 1 - padding * 2, getHeight() - 1 - padding * 2, arc, arc );

        g2.setPaint( gradientBase.getColor2().brighter() );
        g2.drawRoundRect( padding, padding, getWidth() - 1 - padding * 2, getHeight() - 1 - padding * 2, arc, arc );

        super.paintComponent( g );
    }

    class GLabelMouseAdapter extends MouseAdapter {

        private static final int CLICK_PADDING = 2;
        private int              originalPadding;

        /**
         * @{inheritDoc}
         */
        @Override
        public void mouseEntered(MouseEvent e) {

            hasMouse = true;
            repaint();
        }

        /**
         * @{inheritDoc}
         */
        @Override
        public void mouseExited(MouseEvent e) {

            hasMouse = false;
            repaint();
        }

        /**
         * @{inheritDoc}
         */
        @Override
        public void mousePressed(MouseEvent e) {

            isPressed = true;
            originalPadding = padding;
            setPadding( originalPadding + CLICK_PADDING );
        }

        /**
         * @{inheritDoc}
         */
        @Override
        public void mouseReleased(MouseEvent e) {

            isPressed = false;
            setPadding( originalPadding );
        }
    }
}
