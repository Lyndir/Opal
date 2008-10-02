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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import com.lyndir.lhunath.lib.system.Utils;


/**
 * <i>{@link HoverCellRenderer} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * 
 * @author lhunath
 */
public class HoverCellRenderer extends DefaultListCellRenderer implements MouseListener, MouseMotionListener {

    private HoverPanel panel;
    private int        hoveredIndex = -1;
    private JList      myList;


    /**
     * Create a new {@link HoverCellRenderer} instance.
     * 
     * @param list
     *            The list this renderer works for.
     */
    public HoverCellRenderer(JList list) {

        panel = new HoverPanel( new BorderLayout() );

        myList = list;
        list.addMouseListener( this );
        list.addMouseMotionListener( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {

        Component label = getObjectComponent( list, value, index, isSelected, cellHasFocus );
        if (label instanceof JComponent)
            ((JComponent) label).setOpaque( false );

        panel.removeAll();
        panel.add( label );
        panel.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        panel.restoreBackground();

        if (cellHasFocus)
            if (panel.isBackgroundRestored())
                panel.setBackground( Utils.setAlpha( list.getSelectionBackground(), 100 ) );
            else
                panel.setBorder( BorderFactory.createLineBorder( list.getSelectionBackground() ) );

        if (isSelected)
            if (panel.isBackgroundRestored())
                panel.setBackground( Utils.setAlpha( list.getSelectionBackground(), 200 ) );
            else
                panel.setBorder( BorderFactory.createLineBorder( list.getSelectionBackground() ) );

        if (index == hoveredIndex)
            panel.mouseEntered( null );

        return panel;
    }

    /**
     * Generate a {@link Component} that represents the given object when painted.<br>
     * <br>
     * <b>Override this method instead of {@link #getListCellRendererComponent(JList, Object, int, boolean, boolean)}
     * !</b>
     * 
     * @return The {@link Component} that will be painted to represent the given data.
     */
    private Component getObjectComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

        int padding = list.getFont().getSize() / 2;
        label.setBorder( BorderFactory.createEmptyBorder( padding, padding, padding, padding ) );

        return label;
    }

    private void hovered(MouseEvent e, boolean entered) {

        hoveredIndex = myList.locationToIndex( e.getPoint() );
        if (!entered || hoveredIndex != -1
            && !myList.getCellBounds( hoveredIndex, hoveredIndex ).contains( e.getPoint() ))
            hoveredIndex = -1;

        myList.repaint();
    }

    /**
     * {@inheritDoc}
     */
    public void mouseClicked(MouseEvent e) {

    // Not interested.
    }

    /**
     * {@inheritDoc}
     */
    public void mouseEntered(MouseEvent e) {

        hovered( e, true );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseExited(MouseEvent e) {

        hovered( e, false );
    }

    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent e) {

    // Not interested.
    }

    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent e) {

    // Not interested.
    }

    /**
     * {@inheritDoc}
     */
    public void mouseDragged(MouseEvent e) {

        hovered( e, true );
    }

    /**
     * {@inheritDoc}
     */
    public void mouseMoved(MouseEvent e) {

        hovered( e, true );
    }
}
