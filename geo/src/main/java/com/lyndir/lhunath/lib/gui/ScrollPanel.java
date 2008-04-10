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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;


/**
 * <i>ScrollPanel - A panel that will properly set its scroll unit size depending on its content.</i><br>
 * <br>
 * Since {@link JPanel}s don't like being wrapped in {@link JScrollPane}s (they scroll by 1 to 3 pixels, really
 * tedious), this attempt has been made to address the issue.<br>
 * {@link ScrollPanel} uses its first visible child component to determine the scroll unit.<br>
 * <br>
 * 
 * @author lhunath
 */
public class ScrollPanel extends JPanel implements Scrollable {

    private int     scrollUnit;
    private boolean horizontal;
    private boolean vertical;


    /**
     * Create a new {@link ScrollPanel} instance.<br>
     * The amount to scroll this panel will be calculated from the size of the first visible component of the panel.
     */
    public ScrollPanel() {

        this( 0 );
    }

    /**
     * Create a new {@link ScrollPanel} instance.<br>
     * The amount to scroll this panel will be calculated from the size of the first visible component of the panel.
     * 
     * @param horizontal
     *        <code>true</code>: track the horizontal size of the panel.
     * @param vertical
     *        <code>true</code>: track the vertical size of the panel.
     */
    public ScrollPanel(boolean horizontal, boolean vertical) {

        this( 0, horizontal, vertical );
    }

    /**
     * Create a new {@link ScrollPanel} instance.
     * 
     * @param scrollUnit
     *        The amount in pixels to scroll this panel on every tick of the scroll wheel.
     */
    public ScrollPanel(int scrollUnit) {

        this( scrollUnit, false, true );
    }

    /**
     * Create a new {@link ScrollPanel} instance.
     * 
     * @param scrollUnit
     *        The amount in pixels to scroll this panel on every tick of the scroll wheel.
     * @param horizontal
     *        <code>true</code>: track the horizontal size of the panel.
     * @param vertical
     *        <code>true</code>: track the vertical size of the panel.
     */
    public ScrollPanel(int scrollUnit, boolean horizontal, boolean vertical) {

        this.scrollUnit = scrollUnit;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredScrollableViewportSize() {

        return getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {

        return getScrollableUnitIncrement( visibleRect, orientation, direction ) * 3;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getScrollableTracksViewportHeight() {

        return !vertical;
    }

    /**
     * {@inheritDoc}
     */
    public boolean getScrollableTracksViewportWidth() {

        return !horizontal;
    }

    /**
     * {@inheritDoc}
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {

        if (scrollUnit > 0)
            return scrollUnit;

        for (Component c : getComponents())
            if (c.isValid() && c.isVisible())
                if (orientation == SwingConstants.HORIZONTAL)
                    return Math.min( c.getWidth(), getParent().getWidth() / 8 );
                else if (orientation == SwingConstants.VERTICAL)
                    return Math.min( c.getHeight(), getParent().getHeight() / 8 );
                else
                    break;

        return 3; // Empty panel.
    }
}
