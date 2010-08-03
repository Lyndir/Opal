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

import java.awt.*;
import javax.swing.*;


/**
 * <i>ScrollPanel - A panel that will properly set its scroll unit size depending on its content.</i><br> <br> Since {@link JPanel}s don't
 * like being wrapped in {@link JScrollPane}s (they scroll by 1 to 3 pixels, really tedious), this attempt has been made to address the
 * issue.<br> {@link ScrollPanel} uses its first visible child component to determine the scroll unit.<br> <br>
 *
 * @author lhunath
 */
public class ScrollPanel extends JPanel implements Scrollable {

    private final int     scrollUnit;
    private final boolean horizontal;
    private final boolean vertical;

    /**
     * Create a new {@link ScrollPanel} instance.<br> The amount to scroll this panel will be calculated from the size of the first visible
     * component of the panel.
     */
    public ScrollPanel() {

        this( 0 );
    }

    /**
     * Create a new {@link ScrollPanel} instance.<br> The amount to scroll this panel will be calculated from the size of the first visible
     * component of the panel.
     *
     * @param horizontal <code>true</code>: scroll horizontally.
     * @param vertical   <code>true</code>: scroll vertically.
     */
    public ScrollPanel(final boolean horizontal, final boolean vertical) {

        this( 0, horizontal, vertical );
    }

    /**
     * Create a new {@link ScrollPanel} instance.
     *
     * @param scrollUnit The amount in pixels to scroll this panel on every tick of the scroll wheel.
     */
    public ScrollPanel(final int scrollUnit) {

        this( scrollUnit, false, true );
    }

    /**
     * Create a new {@link ScrollPanel} instance.
     *
     * @param scrollUnit The amount in pixels to scroll this panel on every tick of the scroll wheel.
     * @param horizontal <code>true</code>: track the horizontal size of the panel.
     * @param vertical   <code>true</code>: track the vertical size of the panel.
     */
    public ScrollPanel(final int scrollUnit, final boolean horizontal, final boolean vertical) {

        this.scrollUnit = scrollUnit;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {

        return getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation, final int direction) {

        return getScrollableUnitIncrement( visibleRect, orientation, direction ) * 3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {

        return !vertical;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {

        return !horizontal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {

        if (scrollUnit > 0)
            return scrollUnit;

        for (final Component component : getComponents())
            if (component.isValid() && component.isVisible())
                if (orientation == SwingConstants.HORIZONTAL)
                    return Math.min( component.getWidth(), getParent().getWidth() / 8 );
                else if (orientation == SwingConstants.VERTICAL)
                    return Math.min( component.getHeight(), getParent().getHeight() / 8 );
                else
                    break;

        return 3; // Empty panel.
    }
}
