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

import com.lyndir.lhunath.lib.system.UIUtils;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * TODO: {@link HoverPanel}<br>
 *
 * @author lhunath
 */
public class HoverPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    private static final Color backgroundBase = new Color( 0.9f, 0.9f, 1 );
    private static final Color backgroundHover = UIUtils.setAlpha( backgroundBase, 100 );
    private static boolean alternateRow;
    private final Color background;

    /**
     * Create a new AddonPanel instance.
     *
     * @param layout The layout to use for this panel.
     */
    public HoverPanel(final LayoutManager layout) {

        super( layout );

        background = alternateRow? UIUtils.setAlpha( backgroundBase, 20 ): UIUtils.setAlpha( backgroundBase, 0 );
        alternateRow = !alternateRow;
        restoreBackground();
    }

    /**
     * Restore this panel's default background.
     */
    public void restoreBackground() {

        setBackground( background );
    }

    /**
     * Check whether this panel's active background is its default background.
     *
     * @return <code>true</code> if this panel's active background is its default background.
     */
    public boolean isBackgroundRestored() {

        return getBackground().equals( background );
    }

    /**
     * @param component The component (and all children of it) this panel should listen to for mouse events.
     */
    protected void listen(final Component component) {

        if (component instanceof Container)
            for (final Component child : ((Container) component).getComponents())
                listen( child );

        component.addMouseMotionListener( this );
        component.addMouseListener( this );
    }

    /**
     * @param component The component (and all children of it) this panel should remove mouse its event mouse listeners for.
     */
    protected void unlisten(final Component component) {

        if (component instanceof Container)
            for (final Component child : ((Container) component).getComponents())
                unlisten( child );

        component.removeMouseMotionListener( this );
        component.removeMouseListener( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        if (getParent() != null)
            getParent().repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void repaint(final long tm, final int x, final int y, final int width, final int height) {

        if (getParent() != null)
            getParent().repaint( tm, getX(), getY(), getWidth(), getHeight() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(final MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (final MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mouseClicked( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(final MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (final MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mousePressed( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(final MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (final MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mouseReleased( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(final MouseEvent e) {

        setBackground( backgroundHover );

        if (e != null && !equals( e.getComponent() ))
            for (final MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mouseEntered( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(final MouseEvent e) {

        restoreBackground();

        if (!e.getComponent().equals( this ))
            for (final MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mouseExited( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(final MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (final MouseMotionListener listener : getMouseMotionListeners())
                if (!listener.equals( this ))
                    listener.mouseDragged( e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(final MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (final MouseMotionListener listener : getMouseMotionListeners())
                if (!listener.equals( this ))
                    listener.mouseMoved( e );
    }
}
