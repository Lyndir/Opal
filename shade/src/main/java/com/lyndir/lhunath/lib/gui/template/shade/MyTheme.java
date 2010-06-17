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
package com.lyndir.lhunath.lib.gui.template.shade;

import com.lyndir.lhunath.lib.gui.MyLookAndFeel;
import com.lyndir.lhunath.lib.gui.MyLookAndFeel.MyThemeType;
import com.lyndir.lhunath.lib.gui.ToolTip;
import com.lyndir.lhunath.lib.system.Locale;
import com.lyndir.lhunath.lib.system.logging.Logger;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


/**
 * TODO: MyThemes<br>
 *
 * @author lhunath
 */
public enum MyTheme {

    /**
     * Guess.
     */
    FOG( Color.decode( "#9999BB" ) ),
    //$NON-NLS-1$

    /**
     * Guess.
     */
    DEW( Color.decode( "#CCFFFF" ) ),

    /**
     * Guess.
     */
    CANDY( Color.decode( "#FF6666" ) ),

    /**
     * Guess.
     */
    AZTEK( Color.decode( "#FF9933" ) ),

    /**
     * Guess.
     */
    NATURE( Color.decode( "#99CC66" ) ),

    /**
     * Guess.
     */
    FOREST( Color.decode( "#223922" ) ),

    /**
     * Guess.
     */
    OAK( Color.decode( "#DD9966" ) ),

    /**
     * Guess.
     */
    SALT( Color.decode( "#6666BB" ) ),

    /**
     * Guess.
     */
    BABY( Color.decode( "#FFCCCC" ) ),

    /**
     * Guess.
     */
    SUNLIT( Color.decode( "#CCBB44" ) ),

    /**
     * Guess.
     */
    CORAL( Color.decode( "#558899" ) ),

    /**
     * Guess.
     */
    TECH( Color.decode( "#333333" ) ),

    /**
     * Guess.
     */
    AURORA( Color.decode( "#332244" ) ),

    /**
     * Guess.
     */
    BLOOD( Color.decode( "#551919" ) ),

    /**
     * Guess.
     */
    CUSTOM( Color.DARK_GRAY );

    private static final MyTheme FALLBACK = OAK;
    MyLookAndFeel lookAndFeel;

    MyTheme(Color base) {

        lookAndFeel = new MyLookAndFeel( base, MyThemeType.PLASTIC );

        /* Must load CUSTOM theme out of the config file if we're using it. */
        if ("CUSTOM".equals( name() ))
            SwingUtilities.invokeLater( new Runnable() {

                @Override
                public void run() {

                    if (equals( activeTheme() ))
                        setLookAndFeel( ShadeConfig.theme.get() );
                }
            } );
    }

    /**
     * Return the look and feel theme for this template.
     *
     * @return Guess.
     */
    public MyLookAndFeel getLookAndFeel() {

        return lookAndFeel;
    }

    /**
     * Set the lookAndFeel of this template.
     *
     * @param lookAndFeel Guess.
     */
    public void setLookAndFeel(MyLookAndFeel lookAndFeel) {

        this.lookAndFeel = lookAndFeel;
    }

    /**
     * Find the theme that is currently used to provide colors for the look and feel.
     *
     * @return Guess.
     */
    public static MyTheme activeTheme() {

        for (MyTheme theme : values())
            if (theme.getLookAndFeel().equals( ShadeConfig.theme.get() ))
                return theme;

        return CUSTOM;
    }

    /**
     * Create a label to describe this theme template.
     *
     * @return Guess.
     */
    public Component getButton() {

        final MyTheme theme = this;
        final ToolTip toolTip = new ToolTip( "  ~ " + toString() + " ~  " );

        AbstractButton button = new JButton() {

            @Override
            public void updateUI() {

                super.updateUI();
                toolTip.updateUI();

                if (theme.getLookAndFeel().equals( ShadeConfig.theme.get() ))
                    setBackground( lookAndFeel.getBase() );
                else
                    setBackground( lookAndFeel.getBright() );
            }
        };
        button.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ShadeConfig.theme.set( theme.getLookAndFeel() );
                ShadeConfig.getUi().execute( BasicRequest.THEME );

                if (theme == CUSTOM)
                    if (customThemeDialog( ShadeConfig.getUi().getFrame() ))
                        ShadeConfig.getUi().execute( BasicRequest.THEME );
            }
        } );
        button.setPreferredSize( new Dimension( 20, 20 ) );
        button.setAlignmentX( 0 );
        toolTip.setContent( button );

        return button;
    }

    static boolean customThemeDialog(Component parent) {

        Color customColor = JColorChooser.showDialog( parent, Locale.explain( "ui.chooseBase" ), //$NON-NLS-1$
                                                      ShadeConfig.theme.get().getBase() );
        if (customColor != null) {
            CUSTOM.getLookAndFeel().setBase( customColor );
            return true;
        }

        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {

        return name();
    }

    /**
     * Make sure we have a theme set. Try to read a default value from the 'theme' property or use the fallback.
     */
    public static void initialize() {

        if (ShadeConfig.theme.isSet())
            return;

        MyTheme theme = null;
        if (System.getProperty( "theme" ) == null)
            theme = FALLBACK;

        else {
            try {
                theme = valueOf( System.getProperty( "theme" ).trim().toUpperCase( java.util.Locale.ENGLISH ) );
            }
            catch (IllegalArgumentException ignored) {
                /* No theme by the name given in the property 'theme'. */
            }

            if (theme == null) {
                Color customColor = null;
                try {
                    customColor = Color.decode( System.getProperty( "theme" ).trim() );
                }
                catch (NumberFormatException e) {
                    if (System.getProperty( "theme" ).trim().length() > 0)
                        Logger.get( MyTheme.class ).err( e, "err.invalidDefaultTheme", System.getProperty( "theme" ).trim() );
                }

                if (customColor != null) {
                    MyTheme.CUSTOM.getLookAndFeel().setBase( customColor );
                    theme = CUSTOM;
                } else
                    theme = FALLBACK;
            }
        }

        ShadeConfig.theme.set( theme.getLookAndFeel() );
    }
}
