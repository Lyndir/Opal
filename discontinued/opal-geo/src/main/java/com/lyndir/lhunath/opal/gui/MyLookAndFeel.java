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

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.AbstractSkyTheme;
import com.lyndir.lhunath.opal.system.logging.Logger;
import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;


/**
 * TODO: {@link MyLookAndFeel}<br>
 *
 * @author lhunath
 */
public class MyLookAndFeel implements Serializable {

    static final Logger logger = Logger.get( MyLookAndFeel.class );

    private static MyLookAndFeel      active;
    private        Map<String, Color> defaults;
    private        MyThemeType        themeType;
    Color base;

    private transient MyTheme cachedTheme;

    /**
     * Version of the class.
     */
    public static final long serialVersionUID = 5L;

    static {
        // Make a default MyLookAndFeel for environments that don't load a specific one.
        active = new MyLookAndFeel( UIManager.getLookAndFeelDefaults().getColor( "Button.background" ), MyThemeType.PLASTIC );
    }

    /**
     * Create a new {@link MyLookAndFeel} instance.
     *
     * @param base      The shade upon which to base this theme's colors.
     * @param themeType The type of theme to use.
     */
    public MyLookAndFeel(final Color base, final MyThemeType themeType) {

        setBase( base );
        this.themeType = themeType;
    }

    /**
     * Set up certain defaults for UI elements.
     *
     * @return This instance.
     */
    public MyLookAndFeel setup() {

        getTheme().setup();
        // for (String key : defaults.keySet())
        // UIManager.getDefaults().put( key, defaults.get( key ) );

        return active = this;
    }

    /**
     * Reconfigure components that used UI defaults that change in this theme, recursively.
     *
     * @param container The container from which to remove this {@link DragListener}.
     *
     * @return This instance.
     */
    public MyLookAndFeel reconfigure(final Container container) {

        for (final Component component : container.getComponents()) {

            /* If this component uses the current default for its background, make it use ours. */
            for (final Map.Entry<String, Color> defaultEntry : defaults.entrySet())
                if (component.getBackground().equals( UIManager.getDefaults().get( defaultEntry.getKey() ) ))
                    component.setBackground( defaultEntry.getValue() );

            if (component instanceof Container)
                reconfigure( (Container) component );
        }

        return this;
    }

    /**
     * Set the base shade.
     *
     * @param base Guess.
     *
     * @return This instance.
     */
    public MyLookAndFeel setBase(final Color base) {

        this.base = base;

        defaults = new HashMap<String, Color>();
        defaults.put( "Plastic.brightenStop", new Color( 1, 1, 1, 0.3f ) );
        defaults.put( "Plastic.ltBrightenStop", new Color( 1, 1, 1, 0.1f ) );

        // defaults.put( "Button.background", getBright().brighter().brighter() );
        // defaults.put( "ToggleButton.background", getBright() );
        // defaults.put( "ComboBox.background", getBright() );
        // defaults.put( "List.background", getBright() );
        // defaults.put( "SimpleInternalFrame.activeTitleForeground", getBright() );
        // defaults.put( "SimpleInternalFrame.inactiveTitleForeground", getMedium() );
        // defaults.put( "SimpleInternalFrame.activeTitleBackground", getMedium() );
        // defaults.put( "SimpleInternalFrame.inactiveTitleBackground", getDark() );
        // defaults.put( "control", getMedium() );

        return this;
    }

    /**
     * @return The theme of this {@link MyLookAndFeel}.
     */
    public MyTheme getTheme() {

        if (themeType == null)
            themeType = MyThemeType.PLASTIC;

        if (cachedTheme == null)
            cachedTheme = themeType.create( this );

        return cachedTheme;
    }

    /**
     * Retrieve the base shade.
     *
     * @return Guess.
     */
    public Color getBase() {

        return base;
    }

    /**
     * @return a darker shade from this theme.
     */
    public Color getDark() {

        return getBase().darker();
    }

    /**
     * @return a darker shade from this theme.
     */
    public Color getMedium() {

        return getBase();
    }

    /**
     * @return a darker shade from this theme.
     */
    public Color getBright() {

        return getBase().brighter().brighter();
    }

    /**
     * @return a darker shade from the current theme.
     */
    public static Color getActiveDark() {

        if (active == null)
            return null;

        return active.getDark();
    }

    /**
     * @return a darker shade from the current theme.
     */
    public static Color getActiveMedium() {

        if (active == null)
            return null;

        return active.getMedium();
    }

    /**
     * @return a darker shade from the current theme.
     */
    public static Color getActiveBright() {

        if (active == null)
            return null;

        return active.getBright();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {

        if (o == null)
            return false;
        if (o == this)
            return true;

        return o instanceof MyLookAndFeel && ((MyLookAndFeel) o).getBase().equals( getBase() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return base.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return String.format( "L&F [#%08x]", base.getRGB() ); //$NON-NLS-1$
    }

    /**
     * <h2>{@link MyThemeType}<br> <sub>Swing Look and Feel type.</sub></h2>
     *
     * <p> <i>Apr 9, 2008</i> </p>
     *
     * @author mbillemo
     */
    public enum MyThemeType {

        /**
         * JGoodies Plastic theme.
         */
        PLASTIC( MyPlasticTheme.class ); // , SUBSTANCE (MySubstanceTheme.class);

        private final Class<? extends MyTheme> type;

        MyThemeType(final Class<? extends MyTheme> type) {

            this.type = type;
        }

        /**
         * @param lnf The {@link LookAndFeel} that colors the theme.
         *
         * @return Create a {@link MyTheme} of this type.
         */
        public MyTheme create(final MyLookAndFeel lnf) {

            try {
                return type.getConstructor().newInstance();
            }

            catch (InstantiationException e) {
                logger.bug( e, "Not instantiatable: %s!", type );
            }
            catch (IllegalAccessException e) {
                logger.bug( e, "No access to instantiate: %s!", type );
            }
            catch (NoSuchMethodException e) {
                logger.bug( e, "No default constructor: %s!", type );
            }
            catch (InvocationTargetException e) {
                logger.bug( e, "Instantiation failed: %s!", type );
            }

            return lnf.new MyPlasticTheme();
        }
    }


    /**
     * <h2>{@link MyTheme}<br> <sub>A wrapper for Java Look and Feels.</sub></h2>
     *
     * <p> <i>Apr 9, 2008</i> </p>
     *
     * @author mbillemo
     */
    public interface MyTheme extends Serializable {

        /**
         * Do any initialization required to make this theme active.
         */
        void setup();
    }

    // public class MySubstanceTheme extends SubstanceCremeTheme implements MyTheme {
    //
    // /**
    // * {@inheritDoc}
    // */
    // public void setup() {
    //
    // try {
    // UIManager.setLookAndFeel( new SubstanceLookAndFeel() );
    // SubstanceLookAndFeel.setCurrentTheme( this );
    // } catch (UnsupportedLookAndFeelException e) {
    // Logger.error( e, "err.lookAndFeel" );
    // }
    // }
    // }


    /**
     * <h2>{@link MyPlasticTheme}<br> <sub>A {@link MyTheme} implementation for the JGoodies theme.</sub></h2>
     *
     * <p> <i>Apr 9, 2008</i> </p>
     *
     * @author mbillemo
     */
    private class MyPlasticTheme extends AbstractSkyTheme implements MyTheme {

        private static final long serialVersionUID = 1L;

        MyPlasticTheme() {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setup() {

            try {
                PlasticLookAndFeel.setPlasticTheme( this );
                UIManager.setLookAndFeel( new PlasticXPLookAndFeel() );
            }
            catch (UnsupportedLookAndFeelException e) {
                logger.err( e, "err.lookAndFeel" );
            }
        }

        /**
         * Text color.<br>
         *
         * {@inheritDoc}
         */
        @Override
        protected ColorUIResource getPrimary1() {

            return new ColorUIResource( base.darker().darker() );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ColorUIResource getPrimary2() {

            return new ColorUIResource( base.brighter() );
        }

        /**
         * Control foreground (Scrollbar / Progress bar).<br>
         *
         * {@inheritDoc}
         */
        @Override
        protected ColorUIResource getPrimary3() {

            return new ColorUIResource( base.brighter().brighter() );
        }

        /**
         * Button borders.<br>
         *
         * {@inheritDoc}
         */
        @Override
        protected ColorUIResource getSecondary1() {

            return new ColorUIResource( base.brighter() );
        }

        /**
         * Selected button background.<br>
         *
         * {@inheritDoc}
         */
        @Override
        public ColorUIResource getSecondary2() {

            return new ColorUIResource( base );
        }

        /**
         * Control background.<br>
         *
         * {@inheritDoc}
         */
        @Override
        public ColorUIResource getSecondary3() {

            return new ColorUIResource( base.brighter().brighter() );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ColorUIResource getWhite() {

            return new ColorUIResource( base.brighter().brighter().brighter() );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ColorUIResource getBlack() {

            return new ColorUIResource( base.darker().darker().darker() );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ColorUIResource getSimpleInternalFrameForeground() {

            return getWhite();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ColorUIResource getSimpleInternalFrameBackground() {

            return new ColorUIResource( base.darker().darker() );
        }
    }
}
