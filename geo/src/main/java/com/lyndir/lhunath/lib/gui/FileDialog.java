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

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;


/**
 * <i>FileDialog - A file selection dialog.</i><br>
 * <br>
 * This is a convenience class for showing a file chooser in a dialog.<br>
 * <br>
 *
 * @author lhunath
 */
public abstract class FileDialog extends JFileChooser {

    private final JDialog dialog;


    /**
     * Create a new JFileDialog instance.
     *
     * @param start  The initial location to show when opening the dialog.
     * @param title  The title of this dialog.
     * @param parent The parent frame that will be inaccessible until the dialog is closed (or null).
     */
    protected FileDialog(File start, String title, Frame parent) {

        super( start );

        dialog = new JDialog( parent, title, true );
        dialog.add( this );
        dialog.pack();
        dialog.setLocationRelativeTo( parent );
    }

    /**
     * Bring the dialog up. You can make all initialization of the dialog inbetween the call to this method and the call
     * to the constructor. This method will block until the dialog has disappeared.
     */
    public void activate() {

        dialog.setVisible( true );
    }

    /**
     * Override this method to provide actions that need be performed when the dialog gets closed by approving the
     * selection in it.
     */
    public abstract void approved();

    /**
     * @inheritDoc
     */
    @Override
    public void approveSelection() {

        super.approveSelection();
        dialog.dispose();

        approved();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void cancelSelection() {

        super.cancelSelection();
        dialog.dispose();
    }

    /**
     * Create a {@link FileFilter} that filters out any non-directories.
     *
     * @return Guess.
     */
    public static FileFilter createDirectoryFilter() {

        return new FileFilter() {

            /**
             * @inheritDoc
             */
            @Override
            public boolean accept(File f) {

                return f.isDirectory();
            }

            @Override
            public String getDescription() {

                return "Folders";
            }
        };
    }

    /**
     * Creates a {@link FileFilter} that filters out any files that don't have the given extension.
     *
     * @param extension   The extension to allow.
     * @param description The description for the filetype that has this extension.
     *
     * @return Guess.
     */
    public static FileFilter createExtensionFilter(final String extension, final String description) {

        return new FileFilter() {

            /**
             * @inheritDoc
             */
            @Override
            public boolean accept(File f) {

                return f.getName().matches( ".*\\." + extension ) || f.isDirectory();
            }

            @Override
            public String getDescription() {

                return description + " (" + extension + "-files)";
            }
        };
    }
}
