package com.jgoodies.uif_lite.component;

import javax.swing.*;
import java.awt.*;


/**
 * A very light version of the JGoodies <code>UIFactory</code> class. It consists only of static methods to create
 * frequently used components.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.2 $
 */
public final class Factory {

    private Factory() {

        // Overrides default constructor; prevents instantiation.
    }


    /**
     * Defines the margin used in toolbar buttons.
     */
    private static final Insets TOOLBAR_BUTTON_MARGIN = new Insets( 1, 1, 1, 1 );


    /**
     * Creates and answers a <code>JScrollPane</code> that has an empty border.
     *
     * @param component The component to scroll in the pane.
     *
     * @return Guess.
     */
    public static JScrollPane createStrippedScrollPane(final Component component) {

        JScrollPane scrollPane = new JScrollPane( component );
        scrollPane.setBorder( BorderFactory.createEmptyBorder() );
        return scrollPane;
    }

    /**
     * Creates and returns a <code>JSplitPane</code> that has empty borders. Useful to avoid duplicate decorations, for
     * example if the split pane is contained by other components that already provide a border.
     *
     * @param orientation  the split pane's orientation: horizontal or vertical
     * @param comp1        the top/left component
     * @param comp2        the bottom/right component
     * @param resizeWeight indicates how to distribute extra space
     *
     * @return a split panes that has an empty border
     */
    public static JSplitPane createStrippedSplitPane(int orientation, final Component comp1, Component comp2,
                                                     final double resizeWeight) {

        JSplitPane split = UIFSplitPane.createStrippedSplitPane( orientation, comp1, comp2 );
        split.setResizeWeight( resizeWeight );
        return split;
    }

    /**
     * Creates and answers an <code>AbstractButton</code> configured for use in a JToolBar.
     * <p>
     * Superceded by ToolBarButton from the JGoodies UI framework.
     *
     * @param action The action to perform when the button gets clicked.
     *
     * @return Guess.
     */
    public static AbstractButton createToolBarButton(final Action action) {

        JButton button = new JButton( action );
        button.setFocusPainted( false );
        button.setMargin( TOOLBAR_BUTTON_MARGIN );
        // button.setHorizontalTextPosition(SwingConstants.CENTER);
        // button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setText( "" );
        return button;
    }

}
