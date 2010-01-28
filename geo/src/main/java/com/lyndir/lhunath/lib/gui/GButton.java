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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.lyndir.lhunath.lib.math.Vec2;
import com.lyndir.lhunath.lib.system.UIUtils;


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

    protected static final float ZOOM         = 0.9f;
    protected static final float ARC_FRACTION = 0.1f;
    protected static final float TRANSLUCENCY = 0.8f;

    protected Image              smallEnabledIcon, largeEnabledIcon;
    protected boolean            hover;
    protected GradientPaint      backgroundPaint;
    protected GradientPaint      borderPaint;
    private BufferedImage        smallDisabledIcon;
    private BufferedImage        largeDisabledIcon;


    /**
     * Create a new {@link GButton} instance.
     *
     * @param icon
     *            The icon to show on the button.
     */
    public GButton(Icon icon) {

        this( null, icon );
    }

    /**
     * Create a new {@link GButton} instance.
     *
     * @param text
     *            The text to put on the button.
     * @param icon
     *            The icon to show next to the button text.
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

        setHorizontalTextPosition( RIGHT );
        setVerticalTextPosition( CENTER );
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
    public void setHorizontalTextPosition(int textPosition) {

        super.setHorizontalTextPosition( textPosition );

        updateSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVerticalTextPosition(int textPosition) {

        super.setVerticalTextPosition( textPosition );

        updateSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIconTextGap(int iconTextGap) {

        super.setIconTextGap( iconTextGap );

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

        Vec2 textSize = new Vec2(), iconSize = new Vec2(), buttonSize = new Vec2();

        if (getText() != null && getText().length() > 0) {
            if (getFont() == null) {
                SwingUtilities.invokeLater( new Runnable() {

                    @Override
                    public void run() {

                        updateSize();
                    }
                } );

                return;
            }

            FontMetrics metrics = getFontMetrics( getFont() );
            textSize = new Vec2( metrics.stringWidth( getText() ), getFont().getSize() );
        }

        if (largeEnabledIcon != null) {
            int width = largeEnabledIcon.getWidth( this );
            int height = largeEnabledIcon.getHeight( this );
            iconSize = new Vec2( width, height );
        }

        switch (getHorizontalTextPosition()) {
            case LEFT:
            case RIGHT:
                buttonSize.setX( textSize.getX() + getIconTextGap() + iconSize.getX() );
            break;

            case CENTER:
            default:
                buttonSize.setX( Math.max( textSize.getX(), iconSize.getX() ) );
            break;
        }
        switch (getVerticalTextPosition()) {
            case TOP:
            case BOTTOM:
                buttonSize.setY( textSize.getY() + getIconTextGap() + iconSize.getY() );
            break;

            case CENTER:
            default:
                buttonSize.setY( Math.max( textSize.getY(), iconSize.getY() ) );
            break;
        }

        buttonSize.multiply( 1 / (ZOOM * ZOOM) );
        setPreferredSize( buttonSize.toDimension() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI() {

        super.updateUI();

        backgroundPaint = new GradientPaint( 0, 0, MyLookAndFeel.getActiveBright(), 0, getHeight(), UIUtils.setAlpha(
                MyLookAndFeel.getActiveDark(), 50 ) );
        borderPaint = new GradientPaint( 0, 0, MyLookAndFeel.getActiveBright().brighter(), 0, getHeight(),
                UIUtils.setAlpha( MyLookAndFeel.getActiveBright(), 50 ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {

        if (getParent() != null)
            getParent().repaint( tm, x, y, width, height );

        super.repaint( tm, x, y, width, height );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        Image image;

        // Set 'image' to the currently active image.
        boolean hovering = isEnabled() && hover;
        if (isEnabled()) {
            image = smallEnabledIcon;
            if (hovering)
                image = largeEnabledIcon;
        } else {
            image = smallDisabledIcon;
            if (hovering)
                image = largeDisabledIcon;
        }

        // Image Sizing.
        int largeHeight = largeEnabledIcon == null? 0: largeEnabledIcon.getHeight( this );
        int largeWidth = largeEnabledIcon == null? 0: largeEnabledIcon.getWidth( this );
        int smallWidth = smallEnabledIcon == null? 0: smallEnabledIcon.getWidth( this );
        int imageWidth = image == null? 0: image.getWidth( this );
        int imageHeight = image == null? 0: image.getHeight( this );
        int xPadding = (int) (getWidth() * (1 - ZOOM * ZOOM) / 2);
        int yPadding = (int) (getHeight() * (1 - ZOOM * ZOOM) / 2);
        int smallXPadding = xPadding + (largeWidth - smallWidth) / 2;
        int smallYPadding = yPadding + (largeWidth - smallWidth) / 2;

        // Text Sizing.
        Rectangle2D textBounds = new Rectangle2D.Double( 0, 0, 0, 0 );
        if (getText() != null) {
            g2.setFont( getFont() );
            textBounds = g2.getFontMetrics().getStringBounds( getText(), g2 );
        }

        int horizontal = getHorizontalTextPosition();
        int vertical = getVerticalTextPosition();
        if (getText() == null || getText().length() == 0)
            horizontal = vertical = CENTER;

        int iconX, iconY, textX, textY;
        switch (horizontal) {
            case LEFT:
                textX = xPadding;
                iconX = getWidth() - largeWidth - (hovering? xPadding: smallXPadding);
                break;

            case RIGHT:
                textX = xPadding + largeWidth + getIconTextGap();
                iconX = hovering? xPadding: smallXPadding;
                break;

            case CENTER:
            default:
            textX = (getWidth() - (int) textBounds.getWidth()) / 2;
            iconX = (getWidth() - imageWidth) / 2;
            break;
        }

        switch (vertical) {
            case TOP:
            textY = yPadding + g2.getFont().getSize();
            iconY = getHeight() - imageHeight - (hovering? yPadding: smallYPadding);
            break;

            case BOTTOM:
            textY = yPadding + largeHeight + getIconTextGap() + g2.getFont().getSize();
            iconY = hovering? yPadding: smallYPadding;
            break;

            case CENTER:
            default:
                textY = (getHeight() - (int) textBounds.getHeight()) / 2 + getFont().getSize();
                iconY = (getHeight() - imageHeight) / 2;
                break;
        }

        if (image != null)
            g2.drawImage( image, iconX, iconY, this );

        if (getText() != null) {
            if (Boolean.parseBoolean( System.getProperty( "swing.aatext" ) ))
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

            g2.drawString( getText(), textX, textY );
        }

        Composite origComposite = g2.getComposite();
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, TRANSLUCENCY ) );

        g2.setPaint( backgroundPaint );
        g2.fillRoundRect( 0, 0, getWidth(), getHeight(), (int) (getWidth() * ARC_FRACTION),
                (int) (getWidth() * ARC_FRACTION) );

        g2.setPaint( borderPaint );
        g2.drawRoundRect( 0, 0, getWidth() - 1, getHeight() - 1, (int) (getWidth() * ARC_FRACTION),
                (int) (getWidth() * ARC_FRACTION) );

        g2.setComposite( origComposite );
    }
}
