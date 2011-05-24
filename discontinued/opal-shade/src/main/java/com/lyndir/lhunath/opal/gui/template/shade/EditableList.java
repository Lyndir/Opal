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
package com.lyndir.lhunath.opal.gui.template.shade;

import com.lyndir.lhunath.opal.system.Locale;
import com.lyndir.lhunath.opal.system.UIUtils;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * TODO: {@link EditableList}<br>
 *
 * @author lhunath
 */
public class EditableList extends JList {

    private static final long serialVersionUID = 1L;
    protected final DefaultListModel model;
    protected final String           a;

    /**
     * Create a new EditableList instance.
     *
     * @param contentTitle The title describing this list's content.
     */
    public EditableList(String contentTitle) {

        this( contentTitle, "" ); //$NON-NLS-1$
    }

    /**
     * Create a new EditableList instance.
     *
     * @param contentTitle The title describing this list's content.
     * @param newText      Additional information text to show up when adding a new item to this list in the popup dialog for it (ie.
     *                     syntax/purpose).
     */
    public EditableList(final String contentTitle, final String newText) {

        a = contentTitle.matches( "^(?i)[aeiou].*" )? Locale.explain( "ui.an" )
                : Locale.explain( "ui.a" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        final EditableList list = this;
        setModel( model = new DefaultListModel() );

        addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent mouseEvent) {

                        /* Only proceed on Popup Button. */
                        if (mouseEvent.getModifiers() != Event.META_MASK)
                            return;

                        JMenuItem item;
                        JPopupMenu popup = new JPopupMenu( contentTitle );

                        if (!isSelectionEmpty()) {

                            /* Title item. */
                            item = new JMenuItem( getSelectedValue().toString() );
                            item.setEnabled( false );
                            popup.add( item );

                            popup.addSeparator();
                        }

                        /* Add. */
                        item = new JMenuItem(
                                new AbstractAction(
                                        Locale.explain( "ui.add" ) + a + contentTitle + ' ' + Locale.explain( "ui.addsuffix" ),
                                        //$NON-NLS-1$ //$NON-NLS-2$
                                        UIUtils.getIcon( "add-ss.png" ) ) { //$NON-NLS-1$

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void actionPerformed(ActionEvent actionEvent) {

                                        String element = JOptionPane.showInputDialog(
                                                list, Locale.explain( "ui.new" ) + contentTitle + ": " + newText );

                                        if (element != null)
                                            model.addElement( element );
                                    }
                                } );
                        popup.add( item );

                        /* Delete. */
                        item = new JMenuItem(
                                new AbstractAction(
                                        Locale.explain( "ui.remove" ) + contentTitle, //$NON-NLS-1$
                                        UIUtils.getIcon( "del-ss.png" ) ) { //$NON-NLS-1$

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void actionPerformed(ActionEvent actionEvent) {

                                        model.remove( getSelectedIndex() );
                                    }
                                } );
                        item.setEnabled( !isSelectionEmpty() );
                        popup.add( item );

                        /* Modify. */
                        item = new JMenuItem(
                                new AbstractAction(
                                        Locale.explain( "ui.edit" ) + contentTitle, //$NON-NLS-1$
                                        UIUtils.getIcon( "edit-ss.png" ) ) { //$NON-NLS-1$

                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void actionPerformed(ActionEvent actionEvent) {

                                        String newVar = JOptionPane.showInputDialog(
                                                list, Locale.explain( "ui.modify" ) + contentTitle + ": "
                                                      //$NON-NLS-1$ //$NON-NLS-2$
                                                      + newText, getSelectedValue() );
                                        model.remove( getSelectedIndex() );
                                        model.addElement( newVar );
                                    }
                                } );
                        item.setEnabled( !isSelectionEmpty() );
                        popup.add( item );

                        popup.show( list, mouseEvent.getX(), mouseEvent.getY() );
                    }
                } );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultListModel getModel() {

        return model;
    }
}
