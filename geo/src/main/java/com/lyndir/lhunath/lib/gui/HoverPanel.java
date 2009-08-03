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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import com.lyndir.lhunath.lib.system.Utils;


/**
 * TODO: {@link HoverPanel}<br>
 * 
 * @author lhunath
 */
public class HoverPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    private static final Color backgroundBase  = new Color( 0.9f, 0.9f, 1 );
    private static final Color backgroundHover = Utils.setAlpha( backgroundBase, 100 );
    private static boolean     alternateRow;
    private Color              background;


    /**
     * Create a new AddonPanel instance.
     * 
     * @param layout
     *            The layout to use for this panel.
     */
    public HoverPanel(LayoutManager layout) {

        super( layout );

        background = alternateRow? Utils.setAlpha( backgroundBase, 20 ): Utils.setAlpha( backgroundBase, 0 );
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

    protected void listen(Component c) {

        if (c instanceof Container)
            for (Component cc : ((Container) c).getComponents())
                listen( cc );

        c.addMouseMotionListener( this );
        c.addMouseListener( this );
    }

    protected void unlisten(Component c) {

        if (c instanceof Container)
            for (Component cc : ((Container) c).getComponents())
                unlisten( cc );

        c.removeMouseMotionListener( this );
        c.removeMouseListener( this );
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {

        if (getParent() != null)
            getParent().repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {

        if (getParent() != null)
            getParent().repaint( tm, getX(), getY(), getWidth(), getHeight() );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mouseClicked( e );
    }

    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mousePressed( e );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mouseReleased( e );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseEntered(MouseEvent e) {

        setBackground( backgroundHover );

        if (e != null && !equals( e.getComponent() ))
            for (MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mouseEntered( e );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseExited(MouseEvent e) {

        restoreBackground();

        if (!e.getComponent().equals( this ))
            for (MouseListener listener : getMouseListeners())
                if (!listener.equals( this ))
                    listener.mouseExited( e );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (MouseMotionListener listener : getMouseMotionListeners())
                if (!listener.equals( this ))
                    listener.mouseDragged( e );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseMoved(MouseEvent e) {

        if (!e.getComponent().equals( this ))
            for (MouseMotionListener listener : getMouseMotionListeners())
                if (!listener.equals( this ))
                    listener.mouseMoved( e );
    }
}
