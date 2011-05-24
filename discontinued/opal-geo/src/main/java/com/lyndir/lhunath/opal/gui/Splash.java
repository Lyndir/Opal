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

import com.lyndir.lhunath.opal.system.UIUtils;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;


/**
 * <i>Splash - Create and show a splash screen for your application.</i><br> <br> This splash screen captures the screen and paints an
 * image
 * on top of the capture. This allows for splash screens with transparent or translucent images.<br> The splash screen normally disappears
 * after a given delay, but you can also call the {@link #dispose()} method to get rid of it as soon as your application's
 * com.lyndir.lhunath.opal.gui *
 *
 * @author lhunath
 */
public class Splash extends JWindow {

    private static final Logger logger = Logger.get( Splash.class );

    private static final long serialVersionUID = 1L;
    protected static Splash        instance;
    private          Icon          icon;
    private          Icon          initial;
    private          BufferedImage background;
    private          long          startTime;
    private          long          endTime;

    private Splash(Icon initial, final Icon icon) {

        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        if (initial != null) {
            int initialWidth = initial.getIconWidth();
            int initialHeight = initial.getIconHeight();

            if (initialWidth != width || initialHeight != height) {
                logger.wrn( "Initial icon has a different width or height.  Disabling fade." );
                initial = null;
            }
        }

        setSize( new Dimension( width, height ) );
        setLocationRelativeTo( null );
        setAlwaysOnTop( true );
        setIcons( initial, icon );
    }

    /**
     * @param initial The icon to show in the splash screen in the beginning of the transition or <code>null</code> to not use a
     *                transition.
     * @param icon    The icon to show in the splash screen at the end of the transition.
     */
    private void setIcons(final Icon initial, final Icon icon) {

        this.initial = initial;
        this.icon = icon;

        update();
    }

    /**
     * Update the content of this splash screen by taking a new snapshot of the background.<br> Hides and unhides the window.
     */
    public void update() {

        setVisible( false );

        background = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D) background.getGraphics();

        try {
            BufferedImage capture = new Robot().createScreenCapture( getBounds() );
            g2.drawImage( capture, null, 0, 0 );
        }

        catch (AWTException ignored) {
        }

        setVisible( true );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(final Graphics g) {

        if (background == null)
            return;

        BufferedImage backbuffer = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D) backbuffer.getGraphics();
        g2.drawImage( background, 0, 0, null );

        if (startTime > 0 && initial != null) {
            float alpha = (float) Math.max( 0, (endTime - System.currentTimeMillis()) / (double) (endTime - startTime) );
            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
            initial.paintIcon( this, g2, 0, 0 );

            g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1f - alpha ) );
        }

        icon.paintIcon( this, g2, 0, 0 );
        g.drawImage( backbuffer, 0, 0, this );
    }

    /**
     * Fade the initial image over into the final image.
     *
     * @param duration The duration over which to fade.
     */
    public void fade(final int duration) {

        startTime = System.currentTimeMillis();
        endTime = startTime + duration;

        new Timer( "Splash Fade Timer", true ).scheduleAtFixedRate(
                new TimerTask() {

                    @Override
                    public void run() {

                        repaint();
                    }
                }, 0, Math.max( 1, (endTime - startTime) / 1000 ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {

        super.dispose();
        instance = null;
    }

    /**
     * Spawn a splash screen with the given splash image for five seconds.
     *
     * @param image The resource name of the image to load for the splash screen.
     *
     * @return The splash screen.
     */
    public static Splash spawn(final String image) {

        return spawn( image, 5000 );
    }

    /**
     * Spawn a splash screen with the given splash image and the given duration in milliseconds.
     *
     * @param image    The resource name of the image to load for the splash screen.
     * @param duration How long the splash screen should remain visible.
     *
     * @return The splash screen.
     */
    public static Splash spawn(final String image, final long duration) {

        if (instance == null) {
            ImageIcon icon = UIUtils.getIcon( image );
            ImageIcon initial = UIUtils.getIcon( image.replaceFirst( "(\\.[^\\.]+$)", "-desat$1" ) );

            if (icon == null)
                if (initial != null) {
                    icon = initial;
                    initial = null;
                } else {
                    logger.wrn( "Splash disabled: Image not found: %s", image );
                    return null;
                }

            instance = new Splash( initial, icon );
            new Timer( "Splash Disposal Timer" ).schedule(
                    new TimerTask() {

                        @Override
                        public void run() {

                            if (instance != null)
                                instance.dispose();
                        }
                    }, duration );
        }

        return instance;
    }

    /**
     * Get the splash screen instance.
     *
     * @return The splash screen instance.
     */
    public static Splash getSplash() {

        return instance;
    }
}
