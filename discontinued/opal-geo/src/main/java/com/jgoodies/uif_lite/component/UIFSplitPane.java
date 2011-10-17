package com.jgoodies.uif_lite.component;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;


/**
 * A <code>JSplitPane</code> subclass that can try to remove the divider border. Useful if the splitted components render their own
 * borders.
 * Note that this feature is not supported by all look&amp;feels. Some look&amp;feel implementation will always show a divider border, and
 * conversely, others will never show a divider border.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.2 $
 * @see BasicSplitPaneUI
 */
public final class UIFSplitPane extends JSplitPane {

    /**
     * Holds the name of the bound property that tries to show or hide the split pane's divider border.
     *
     * @see #isDividerBorderVisible()
     * @see #setDividerBorderVisible(boolean)
     */
    public static final String PROPERTYNAME_DIVIDER_BORDER_VISIBLE = "dividerBorderVisible";

    /**
     * Determines whether the divider border shall be removed when the UI is updated.
     *
     * @see #isDividerBorderVisible()
     * @see #setDividerBorderVisible(boolean)
     */
    private boolean dividerBorderVisible;

    // Instance Creation *****************************************************

    /**
     * Constructs a <code>UIFSplitPane</code> configured to arrange the child components side-by-side horizontally with no continuous
     * layout, using two buttons for the components.
     */
    public UIFSplitPane() {

        this( HORIZONTAL_SPLIT, false, new JButton( UIManager.getString( "SplitPane.leftButtonText" ) ),
              new JButton( UIManager.getString( "SplitPane.rightButtonText" ) ) );
    }

    /**
     * Constructs a <code>UIFSplitPane</code> configured with the specified orientation and no continuous layout.
     *
     * @param newOrientation <code>JSplitPane.HORIZONTAL_SPLIT</code> or <code>JSplitPane.VERTICAL_SPLIT</code>
     *
     * @throws IllegalArgumentException if <code>orientation</code> is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT.
     */
    public UIFSplitPane(final int newOrientation) {

        this( newOrientation, false );
    }

    /**
     * Constructs a <code>UIFSplitPane</code> with the specified orientation and redrawing style.
     *
     * @param newOrientation      <code>JSplitPane.HORIZONTAL_SPLIT</code> or <code>JSplitPane.VERTICAL_SPLIT</code>
     * @param newContinuousLayout a boolean, true for the components to redraw continuously as the divider changes position, false to wait
     *                            until the divider position stops changing to redraw
     *
     * @throws IllegalArgumentException if <code>orientation</code> is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
     */
    public UIFSplitPane(final int newOrientation, final boolean newContinuousLayout) {

        this( newOrientation, newContinuousLayout, null, null );
    }

    /**
     * Constructs a <code>UIFSplitPane</code> with the specified orientation and the given componenents.
     *
     * @param orientation    <code>JSplitPane.HORIZONTAL_SPLIT</code> or <code>JSplitPane.VERTICAL_SPLIT</code>
     * @param leftComponent  the <code>Component</code> that will appear on the left of a horizontally-split pane, or at the top of a
     *                       vertically-split pane
     * @param rightComponent the <code>Component</code> that will appear on the right of a horizontally-split pane, or at the bottom of a
     *                       vertically-split pane
     *
     * @throws IllegalArgumentException if <code>orientation</code> is not one of: HORIZONTAL_SPLIT or VERTICAL_SPLIT
     */
    public UIFSplitPane(final int orientation, final Component leftComponent, final Component rightComponent) {

        this( orientation, false, leftComponent, rightComponent );
    }

    /**
     * Constructs a <code>UIFSplitPane</code> with the specified orientation, redrawing style, and given components.
     *
     * @param orientation      <code>JSplitPane.HORIZONTAL_SPLIT</code> or <code>JSplitPane.VERTICAL_SPLIT</code>
     * @param continuousLayout a boolean, true for the components to redraw continuously as the divider changes position, false to wait
     *                         until the divider position stops changing to redraw
     * @param leftComponent    the <code>Component</code> that will appear on the left of a horizontally-split pane, or at the top of a
     *                         vertically-split pane
     * @param rightComponent   the <code>Component</code> that will appear on the right of a horizontally-split pane, or at the bottom of a
     *                         vertically-split pane
     *
     * @throws IllegalArgumentException if <code>orientation</code> is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
     */
    public UIFSplitPane(int orientation, final boolean continuousLayout, final Component leftComponent, Component rightComponent) {

        super( orientation, continuousLayout, leftComponent, rightComponent );
        dividerBorderVisible = false;
    }

    /**
     * Constructs a <code>UIFSplitPane</code>, i.e. a <code>JSplitPane</code> that has no borders. Also disabled the one touch exandable
     * property.
     *
     * @param orientation    <code>JSplitPane.HORIZONTAL_SPLIT</code> or <code>JSplitPane.VERTICAL_SPLIT</code>
     * @param leftComponent  the <code>Component</code> that will appear on the left of a horizontally-split pane, or at the top of a
     *                       vertically-split pane
     * @param rightComponent the <code>Component</code> that will appear on the right of a horizontally-split pane, or at the bottom of a
     *                       vertically-split pane
     *
     * @return Guess.
     *
     * @throws IllegalArgumentException if <code>orientation</code> is not one of: HORIZONTAL_SPLIT or VERTICAL_SPLIT
     */
    public static UIFSplitPane createStrippedSplitPane(int orientation, final Component leftComponent, Component rightComponent) {

        UIFSplitPane split = new UIFSplitPane( orientation, leftComponent, rightComponent );
        split.setBorder( BorderFactory.createEmptyBorder() );
        split.setOneTouchExpandable( false );
        return split;
    }

    // Accessing Properties **************************************************

    /**
     * Checks and answers whether the divider border shall be visible or invisible. Note that this feature is not supported by all
     * look&amp;feels. Some look&amp;feel implementation will always show a divider border, and conversely, others will never show a
     * divider
     * border.
     *
     * @return the desired (but potentially inaccurate) divider border visiblity
     */
    public boolean isDividerBorderVisible() {

        return dividerBorderVisible;
    }

    /**
     * Makes the divider border visible or invisible. Note that this feature is not supported by all look&amp;feels. Some look&amp;feel
     * implementation will always show a divider border, and conversely, others will never show a divider border.
     *
     * @param newVisibility true for visible, false for invisible
     */
    public void setDividerBorderVisible(final boolean newVisibility) {

        boolean oldVisibility = isDividerBorderVisible();
        if (oldVisibility == newVisibility)
            return;
        dividerBorderVisible = newVisibility;
        firePropertyChange( PROPERTYNAME_DIVIDER_BORDER_VISIBLE, oldVisibility, newVisibility );
    }

    // Changing the Divider Border Visibility *********************************

    /**
     * Updates the UI and sets an empty divider border. The divider border may be restored by a L&F at UI installation time. And so, we try
     * to reset it each time the UI is changed.
     */
    @Override
    public void updateUI() {

        super.updateUI();
        if (!isDividerBorderVisible())
            setEmptyDividerBorder();
    }

    /**
     * Sets an empty divider border if and only if the UI is an instance of <code>BasicSplitPaneUI</code>.
     */
    private void setEmptyDividerBorder() {

        SplitPaneUI splitPaneUI = getUI();
        if (splitPaneUI instanceof BasicSplitPaneUI) {
            BasicSplitPaneUI basicUI = (BasicSplitPaneUI) splitPaneUI;
            basicUI.getDivider().setBorder( BorderFactory.createEmptyBorder() );
        }
    }
}
