/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.lhunath.lib.system;

import com.lyndir.lhunath.lib.system.wrapper.Desktop;
import java.awt.*;
import java.awt.geom.Point2D;
import java.net.URL;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;


/**
 * <h2>{@link UIUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Oct 25, 2009</i> </p>
 *
 * @author lhunath
 */
public class UIUtils {

    /**
     * The transparent color.
     */
    public static final Color TRANSPARENT = new Color( 0, 0, 0, 0 );

    /**
     * A slightly custom RED color.
     */
    public static final Color LIGHT_RED = Color.decode( "#FFDDDD" );

    /**
     * A slightly custom GREEN color.
     */
    public static final Color LIGHT_GREEN = Color.decode( "#DDFFDD" );

    /**
     * A slightly custom BLUE color.
     */
    public static final Color LIGHT_BLUE = Color.decode( "#DDDDFF" );

    /**
     * A slightly custom YELLOW color.
     */
    public static final Color LIGHT_YELLOW = Color.decode( "#FFFFDD" );

    /**
     * A slightly custom RED color.
     */
    public static final Color RED = Color.decode( "#FF9999" );

    /**
     * A slightly custom GREEN color.
     */
    public static final Color GREEN = Color.decode( "#99FF99" );

    /**
     * A slightly custom BLUE color.
     */
    public static final Color BLUE = Color.decode( "#9999FF" );

    /**
     * A slightly custom YELLOW color.
     */
    public static final Color YELLOW = Color.decode( "#FFFF99" );

    /**
     * A slightly custom RED color.
     */
    public static final Color DARK_RED = Color.decode( "#993333" );

    /**
     * A slightly custom GREEN color.
     */
    public static final Color DARK_GREEN = Color.decode( "#339933" );

    /**
     * A slightly custom BLUE color.
     */
    public static final Color DARK_BLUE = Color.decode( "#333399" );

    /**
     * A slightly custom YELLOW color.
     */
    public static final Color DARK_YELLOW = Color.decode( "#999933" );

    /**
     * The maximum size for a component. Very useful to make components in a BoxLayout fill all available space.
     */
    public static final Dimension MAX_SIZE = new Dimension( Short.MAX_VALUE, Short.MAX_VALUE );

    /**
     * Calculate the width in pixels that are necessary to draw the given string in the given font on the given graphics.
     *
     * @param graphics The graphics configuration the string would be drawn on.
     * @param font     The font to use for rendering the string.
     * @param str      The string to measure.
     *
     * @return Guess.
     */
    public static double fontWidth(final Graphics2D graphics, final Font font, final String str) {

        return graphics.getFontMetrics( font ).getStringBounds( str, graphics ).getWidth();
    }

    /**
     * Calculate the height in pixels that are necessary to draw the given string in the given font on the given graphics.
     *
     * @param graphics The graphics configuration the string would be drawn on.
     * @param font     The font to use for rendering the string.
     * @param str      The string to measure.
     *
     * @return Guess.
     */
    public static double fontHeight(final Graphics2D graphics, final Font font, final String str) {

        return graphics.getFontMetrics( font ).getStringBounds( str, graphics ).getHeight();
    }

    /**
     * Align the given point on the given grid.
     *
     * @param point The point that needs to be aligned.
     * @param gridX The length of the grid cells.
     * @param gridY The height of the grid cells.
     *
     * @return A new point as close to the given as possible, nicely aligned on the given grid.
     */
    public static Point2D gridAlign(final Point2D point, final double gridX, final double gridY) {

        return new Point2D.Double( Math.round( point.getX() / gridX ) * gridX, Math.round( point.getY() / gridY ) * gridY );
    }

    /**
     * Check whether a component is the child of another, anywhere down the line.
     *
     * @param child  The possible child.
     * @param parent The container that possibly contains the child.
     *
     * @return The given child component exists in parent's hierarchy.
     */
    public static boolean isChild(final Component child, final Container parent) {

        if (child instanceof Container)
            for (final Component grandChild : ((Container) child).getComponents())

                if (child.equals( parent ))
                    return true;

                else if (isChild( grandChild, parent ))
                    return true;

        return false;
    }

    /**
     * Convert a color into an HTML-type hex string (#RRGGBB). This does not take transparency into account.
     *
     * @param color The color to convert to hexadecimal notation.
     *
     * @return The hex string.
     */
    public static String colorToHex(final Color color) {

        return String.format( "#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue() );
    }

    /**
     * Return a new color based on the given color with the given alpha.
     *
     * @param color The base color.
     * @param alpha The alpha to apply to the color (0-255).
     *
     * @return The resulting color.
     */
    public static Color setAlpha(final Color color, final int alpha) {

        if (color == null)
            return null;

        return new Color( color.getRed(), color.getGreen(), color.getBlue(), alpha );
    }

    /**
     * Check whether the Java 6+ Desktop API is supported.
     *
     * @return Guess.
     */
    public static boolean isDesktopSupported() {

        // noinspection ErrorNotRethrown
        try {
            return Desktop.isDesktopSupported();
        }
        catch (NoClassDefFoundError ignored) {
            return false;
        }
    }

    /**
     * Check whether the Java 6+ Desktop API is supported for the BROWSE action.
     *
     * @return Guess.
     */
    public static boolean isBrowseSupported() {

        // noinspection ErrorNotRethrown
        try {
            return isDesktopSupported() && Desktop.getDesktop().isSupported( Desktop.Action.BROWSE );
        }
        catch (NoClassDefFoundError ignored) {
            return false;
        }
    }

    /**
     * Check whether the Java 6+ Desktop API is supported for the MAIL action.
     *
     * @return Guess.
     */
    public static boolean isMailSupported() {

        // noinspection ErrorNotRethrown
        try {
            return isDesktopSupported() && Desktop.getDesktop().isSupported( Desktop.Action.MAIL );
        }
        catch (NoClassDefFoundError ignored) {
            return false;
        }
    }

    /**
     * Check whether the Java 6+ Desktop API is supported for the OPEN action.
     *
     * @return Guess.
     */
    public static boolean isOpenSupported() {

        // noinspection ErrorNotRethrown
        try {
            return isDesktopSupported() && Desktop.getDesktop().isSupported( Desktop.Action.OPEN );
        }
        catch (NoClassDefFoundError ignored) {
            return false;
        }
    }

    /**
     * Change the default font for all components.
     *
     * @param font The new default font.
     */
    public static void setUIFont(final Font font) {

        FontUIResource uiFont = new FontUIResource( font );

        UIManager.put( "Label.font", uiFont );
        UIManager.put( "TabbedPane.font", uiFont );
        UIManager.put( "TextField.font", uiFont );
        UIManager.put( "PasswordField.font", uiFont );
        UIManager.put( "Button.font", uiFont );
        UIManager.put( "RadioButton.font", uiFont );
        UIManager.put( "CheckBox.font", uiFont );
        UIManager.put( "ComboBox.font", uiFont );
        UIManager.put( "Menu.font", uiFont );
        UIManager.put( "List.font", uiFont );
        UIManager.put( "ListBox.font", uiFont );
        UIManager.put( "MenuItem.font", uiFont );
        UIManager.put( "Panel.font", uiFont );
        UIManager.put( "TitledBorder.font", uiFont );
    }

    /**
     * Load an icon for the given resource file.
     *
     * @param resource URI of the resource.
     *
     * @return The icon.
     */
    public static ImageIcon getIcon(final String resource) {

        URL url = Thread.currentThread().getContextClassLoader().getResource( resource );
        if (url == null)
            return null;

        return new ImageIcon( url );
    }

    /**
     * Create a debug border with a red coloured line bevel and a text label.
     *
     * @param text The text to put on the label.
     *
     * @return The label component.
     */
    public static Component createDebugLabel(final String text) {

        JLabel label = new JLabel( text, SwingConstants.CENTER );
        label.setBorder( BorderFactory.createLineBorder( RED ) );

        return label;
    }
}
