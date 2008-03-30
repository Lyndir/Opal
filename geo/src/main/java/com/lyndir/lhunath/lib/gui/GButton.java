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

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import com.lyndir.lhunath.lib.math.Vec2;
import com.lyndir.lhunath.lib.system.Utils;

/**
 * <h2>{@link GButton} - [in short] (TODO).</h2>
 * <p>
 * [description / usage].
 * </p>
 * <p>
 * <i>Dec 13, 2007</i>
 * </p>
 * 
 * @author mbillemo
 */
public class GButton extends JButton {

    protected static final float ZOOM              = 0.9f;
    protected static final float ARC_FRACTION      = 0.1f;
    protected static final float TRANSLUCENCY      = 0.8f;
    protected static final int   ICON_TEXT_SPACING = 5;

    protected Image              smallEnabledIcon, largeEnabledIcon;
    protected boolean            hover;
    protected GradientPaint      backgroundPaint;
    protected GradientPaint      borderPaint;
    private BufferedImage        smallDisabledIcon;
    private BufferedImage        largeDisabledIcon;

    /**
     * Create a new {@link GButton} instance.
     */
    public GButton(Icon onIcon) {

        this( null, onIcon );
    }

    /**
     * Create a new {@link GButton} instance.
     */
    public GButton(String text, Icon icon) {

        super( text, icon );
        setBorderPainted( false );
        setOpaque( true );

        addMouseListener( new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {

                hover = false;
                repaint();
            }
        } );

        addComponentListener( new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {

                updateUI();
            }
        } );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setText(String text) {

        super.setText( text );

        updateSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIcon(Icon newIcon) {

        super.setIcon( newIcon );
        if (newIcon == null)
            return;

        Graphics2D g2;
        Icon icon = getIcon();
        largeEnabledIcon = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
        icon.paintIcon( null, largeEnabledIcon.getGraphics(), 0, 0 );
        smallEnabledIcon = new BufferedImage( (int) (icon.getIconWidth() * ZOOM), (int) (icon.getIconHeight() * ZOOM),
                BufferedImage.TYPE_INT_ARGB );
        g2 = (Graphics2D) smallEnabledIcon.getGraphics();
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        g2.drawImage( largeEnabledIcon, 0, 0, smallEnabledIcon.getWidth( this ), smallEnabledIcon.getHeight( this ),
                this );

        icon = getDisabledIcon();
        largeDisabledIcon = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
        icon.paintIcon( null, largeDisabledIcon.getGraphics(), 0, 0 );
        smallDisabledIcon = new BufferedImage( (int) (icon.getIconWidth() * ZOOM), (int) (icon.getIconHeight() * ZOOM),
                BufferedImage.TYPE_INT_ARGB );
        g2 = (Graphics2D) smallDisabledIcon.getGraphics();
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
        g2.drawImage( largeDisabledIcon, 0, 0, smallDisabledIcon.getWidth( this ), smallDisabledIcon.getHeight( this ),
                this );

        updateSize();
    }

    /**
     * Recalculate the preferred size of this component.
     */
    protected void updateSize() {

        Vec2 size = new Vec2();

        if (getText() != null && getText().length() > 0) {
            if (getFont() == null) {
                SwingUtilities.invokeLater( new Runnable() {

                    public void run() {

                        updateSize();
                    }
                } );

                return;
            }

            FontMetrics metrics = getFontMetrics( getFont() );
            size.add( new Vec2( metrics.stringWidth( getText() ), getFont().getSize() ) );
        }

        if (largeEnabledIcon != null) {
            int width = largeEnabledIcon.getWidth( this );
            int height = largeEnabledIcon.getHeight( this );
            size.add( new Vec2( width, Math.max( 0, height - size.y ) ) );
        }

        size.multiply( 1 / (ZOOM * ZOOM) );
        size.x += ICON_TEXT_SPACING;
        setPreferredSize( size.toDimension() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI() {

        super.updateUI();

        backgroundPaint = new GradientPaint( 0, 0, MyLookAndFeel.getActiveBright(), 0, getHeight(), Utils.setAlpha(
                MyLookAndFeel.getActiveDark(), 50 ) );
        borderPaint = new GradientPaint( 0, 0, MyLookAndFeel.getActiveBright().brighter(), 0, getHeight(),
                Utils.setAlpha( MyLookAndFeel.getActiveBright(), 50 ) );
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {

        if (getParent() != null)
            getParent().repaint( tm, x, y, width, height );

        super.repaint( tm, x, y, width, height );
    }

    /**
     * @{inheritDoc}
     */
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        Image image = null;

        if (isEnabled()) {
            image = smallEnabledIcon;
            if (hover)
                image = largeEnabledIcon;
        } else {
            image = smallDisabledIcon;
            if (hover)
                image = largeDisabledIcon;
        }

        int largeWidth = 0, smallWidth = 0, iconX = 0, iconY = 0, textX = 0;

        if (image != null) {
            largeWidth = largeEnabledIcon.getWidth( this );
            smallWidth = smallEnabledIcon.getWidth( this );

            iconX = (getWidth() - image.getWidth( this )) / 2;
            iconY = (getHeight() - image.getHeight( this )) / 2;

            if (getText() != null && getText().length() > 0) {
                int hoverPadding = (int) (getWidth() * (1 - ZOOM * ZOOM) / 2);
                int normalPadding = hoverPadding + (largeWidth - smallWidth) / 2;

                iconX = hover ? hoverPadding : normalPadding;
                textX = hoverPadding + largeWidth + ICON_TEXT_SPACING;
            }

            g2.drawImage( image, iconX, iconY, this );
        }

        if (getText() != null) {
            g2.setFont( getFont() );
            Rectangle2D textBounds = g2.getFontMetrics().getStringBounds( getText(), g2 );
            int textY = (getHeight() - (int) textBounds.getHeight()) / 2 + getFont().getSize();

            g2.drawString( getText(), textX, textY );
        }

        Composite origComposite = g2.getComposite();
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, TRANSLUCENCY ) );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        g2.setPaint( backgroundPaint );
        g2.fillRoundRect( 0, 0, getWidth(), getHeight(), (int) (getWidth() * ARC_FRACTION),
                (int) (getWidth() * ARC_FRACTION) );

        g2.setPaint( borderPaint );
        g2.drawRoundRect( 0, 0, getWidth() - 1, getHeight() - 1, (int) (getWidth() * ARC_FRACTION),
                (int) (getWidth() * ARC_FRACTION) );

        g2.setComposite( origComposite );
    }
}
