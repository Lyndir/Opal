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
package com.lyndir.lhunath.lib.gui;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JWindow;

import com.lyndir.lhunath.lib.system.Utils;
import com.lyndir.lhunath.lib.system.logging.Logger;

/**
 * <i>Splash - Create and show a splash screen for your application.</i><br>
 * <br>
 * This splash screen captures the screen and paints an image on top of the capture. This allows for splash screens with
 * transparent or translucent images.<br>
 * The splash screen normally disappears after a given delay, but you can also call the {@link #dispose()} method to get
 * rid of it as soon as your application's com.lyndir.lhunath.lib.gui * 
 * @author lhunath
 */
public class Splash extends JWindow {

    protected static Splash instance;
    private Image           splash = null;
    private Icon            icon;

    private Splash(Icon icon) {

        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        setSize( new Dimension( width, height ) );
        setLocationRelativeTo( null );
        setAlwaysOnTop( true );
        setIcon( icon );
    }

    /**
     * @param icon
     *        The icon to show in the splash screen.
     */
    private void setIcon(Icon icon) {

        this.icon = icon;
        update();
    }

    /**
     * Update the content of this splash screen by taking a new snapshot of the background.<br>
     * Hides and unhides the window.
     */
    public void update() {

        setVisible( false );

        splash = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D) splash.getGraphics();

        try {
            BufferedImage capture = new Robot().createScreenCapture( getBounds() );
            g2.drawImage( capture, null, 0, 0 );
        } catch (AWTException e) {}

        icon.paintIcon( this, g2, 0, 0 );

        setVisible( true );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics g) {

        if (splash != null)
            g.drawImage( splash, 0, 0, null );
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
     * @param image
     *        The resource name of the image to load for the splash screen.
     * @return The splash screen.
     */
    public static Splash spawn(String image) {

        return spawn( image, 5000 );
    }

    /**
     * Spawn a splash screen with the given splash image and the given duration in milliseconds.
     * 
     * @param image
     *        The resource name of the image to load for the splash screen.
     * @param duration
     *        How long the splash screen should remain visible.
     * @return The splash screen.
     */
    public static Splash spawn(final String image, final long duration) {

        if (instance == null) {
            ImageIcon icon = Utils.getIcon( image );
            if (icon == null) {
                Logger.warn( "Splash disabled: Image not found: %s", image );
                return null;
            }

            instance = new Splash( icon );
            new Timer( "SplashTimer" ).schedule( new TimerTask() {

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
