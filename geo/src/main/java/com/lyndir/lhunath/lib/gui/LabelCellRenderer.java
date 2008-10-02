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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;


/**
 * <i>LabelCellRenderer - A cell renderer for lists and combo boxes that sets up a {@link JLabel} for its cells.</i><br>
 * <br>
 * 
 * @author lhunath
 */
public class LabelCellRenderer extends DefaultListCellRenderer {

    /**
     * @inheritDoc
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {

        setHorizontalTextPosition( SwingConstants.CENTER );
        if (value instanceof JLabel) {
            setBackground( ((JLabel) value).getBackground() );
            setForeground( ((JLabel) value).getForeground() );
            setText( ((JLabel) value).getText() );
        } else
            super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

        return this;
    }
}
