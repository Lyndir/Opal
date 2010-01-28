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
package com.lyndir.lhunath.lib.gui.zui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.lyndir.lhunath.lib.math.Vec2;
import com.lyndir.lhunath.lib.system.Utils;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * <i>PDialog - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * FIXME
 *
 * @author lhunath
 */
public class PDialog extends PBox {

    private final PDialogClosedListener listener;
    private final PSwing                pSwing;
    private final JPanel                panel;
    private final PSwingCanvas          swingCanvas;

    /**
     * Create a new PDialog instance.
     *
     * @param title The title to show on the dialog.
     * @param canvas The canvas the dialog will be showed in.
     * @param listener
     *        The object that will be notified when the dialog is closed.
     */
    public PDialog(PSwingCanvas canvas, String title, PDialogClosedListener listener) {

        super( canvas, title );
        this.listener = listener;

        setSize( new Vec2( canvas.getCamera().getBoundsReference().getSize() ).multiply( Utils.GOLDEN ).toDimension() );
        setCenter( canvas.getCamera().getBoundsReference().getCenter2D() );

        JScrollPane scrollPane = new JScrollPane( panel = new JPanel() );
        addChild( pSwing = new PSwing( swingCanvas = canvas, scrollPane ) );
    }

    /**
     * Retrieve the panel of this PDialog.
     *
     * @return Guess.
     */
    public JPanel getPanel() {

        return panel;
    }

    /**
     * Retrieve the Swing Environment of this PDialog.
     *
     * @return Guess.
     */
    public PSwing getSwing() {

        return pSwing;
    }

    /**
     * Retrieve the Swing Canvas used by this PDialog.
     *
     * @return Guess.
     */
    public PSwingCanvas getSwingCanvas() {

        return swingCanvas;
    }

    /**
     * Enable or disable components.
     *
     * @param enabled
     *        Guess.
     * @param components
     *        The components to enable/disable.
     */
    public void setEnabled(boolean enabled, JComponent... components) {

        for (JComponent component : components) {
            /*if (enabled ^ component.isEnabled())
             if (enabled) {
             component.setFont( component.getFont().deriveFont( Font.PLAIN ) );
             Color fg = component.getForeground();
             component.setForeground( component.getBackground().darker() );
             component.setBackground( fg.brighter() );
             // component.setBackground( component.getBackground().darker().darker() );
             }
             else {
             component.setFont( component.getFont().deriveFont( Font.ITALIC ) );
             Color fg = component.getForeground();
             component.setForeground( component.getBackground().darker() );
             component.setBackground( fg.brighter() );
             // component.setBackground( component.getBackground().brighter().brighter() );
             }*/

            component.setEnabled( enabled );
        }
    }

    /**
     * Close this dialog.
     */
    public void close() {

        if (getParent() != null) {
            getParent().removeChild( this );
            listener.dialogClosed( this );
        }
    }

    private class PDialogInputHandler extends PBasicInputEventHandler {

        /**
         * @inheritDoc
         */
        @Override
        public void mouseClicked(PInputEvent event) {

            if (event.isLeftMouseButton())
                close();
        }
    }
}
