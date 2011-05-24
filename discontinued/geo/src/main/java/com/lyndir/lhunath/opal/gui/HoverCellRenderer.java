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

import com.lyndir.lhunath.opal.system.UIUtils;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * <i>{@link HoverCellRenderer} - [in short] (TODO).</i><br> <br> [description / usage].<br> <br>
 *
 * @author lhunath
 */
public class HoverCellRenderer extends DefaultListCellRenderer implements MouseListener, MouseMotionListener {

    private final HoverPanel panel;
    private int hoveredIndex = -1;
    private final JList myList;

    /**
     * Create a new {@link HoverCellRenderer} instance.
     *
     * @param list The list this renderer works for.
     */
    public HoverCellRenderer(final JList list) {

        panel = new HoverPanel( new BorderLayout() );

        myList = list;
        list.addMouseListener( this );
        list.addMouseMotionListener( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getListCellRendererComponent(JList list, final Object value, final int index, boolean isSelected,
                                                  final boolean cellHasFocus) {

        Component label = getObjectComponent( list, value, index, isSelected, cellHasFocus );
        if (label instanceof JComponent)
            ((JComponent) label).setOpaque( false );

        panel.removeAll();
        panel.add( label );
        panel.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        panel.restoreBackground();

        if (cellHasFocus)
            if (panel.isBackgroundRestored())
                panel.setBackground( UIUtils.setAlpha( list.getSelectionBackground(), 100 ) );
            else
                panel.setBorder( BorderFactory.createLineBorder( list.getSelectionBackground() ) );

        if (isSelected)
            if (panel.isBackgroundRestored())
                panel.setBackground( UIUtils.setAlpha( list.getSelectionBackground(), 200 ) );
            else
                panel.setBorder( BorderFactory.createLineBorder( list.getSelectionBackground() ) );

        if (index == hoveredIndex)
            panel.mouseEntered( null );

        return panel;
    }

    /**
     * Generate a {@link Component} that represents the given object when painted.<br> <br> <b>Override this method instead of {@link
     * #getListCellRendererComponent(JList, Object, int, boolean, boolean)} !</b>
     *
     * @param list         See {@link #getListCellRendererComponent(JList, Object, int, boolean, boolean)}
     * @param value        See {@link #getListCellRendererComponent(JList, Object, int, boolean, boolean)}
     * @param index        See {@link #getListCellRendererComponent(JList, Object, int, boolean, boolean)}
     * @param isSelected   See {@link #getListCellRendererComponent(JList, Object, int, boolean, boolean)}
     * @param cellHasFocus See {@link #getListCellRendererComponent(JList, Object, int, boolean, boolean)}
     *
     * @return The {@link Component} that will be painted to represent the given data.
     */
    private Component getObjectComponent(JList list, final Object value, final int index, boolean isSelected, final boolean cellHasFocus) {

        JLabel label = (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

        int padding = list.getFont().getSize() / 2;
        label.setBorder( BorderFactory.createEmptyBorder( padding, padding, padding, padding ) );

        return label;
    }

    private void hovered(final MouseEvent e, final boolean entered) {

        hoveredIndex = myList.locationToIndex( e.getPoint() );
        if (!entered || hoveredIndex != -1 && !myList.getCellBounds( hoveredIndex, hoveredIndex ).contains( e.getPoint() ))
            hoveredIndex = -1;

        myList.repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(final MouseEvent e) {

        // Not interested.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(final MouseEvent e) {

        hovered( e, true );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(final MouseEvent e) {

        hovered( e, false );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(final MouseEvent e) {

        // Not interested.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(final MouseEvent e) {

        // Not interested.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseDragged(final MouseEvent e) {

        hovered( e, true );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved(final MouseEvent e) {

        hovered( e, true );
    }
}
