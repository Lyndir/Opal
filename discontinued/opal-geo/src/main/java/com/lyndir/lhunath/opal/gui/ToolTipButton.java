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

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.UIUtils;
import javax.swing.*;


/**
 * <i>{@link ToolTipButton} - An extension to the default tooltip panel that assumes a button for content.</i><br> <br> If you plan on
 * showing the tooltip for a button, you may prefer this more specialized class. It currently does not implement special behavior.<br> <br>
 *
 * @author lhunath
 */
public class ToolTipButton extends ToolTip {

    private static final Logger logger = Logger.get( ToolTipButton.class );

    /**
     * Create a new {@link ToolTipButton} instance.
     *
     * @param toolTip The text to show when hovering this button.
     * @param button  The button to use as content.
     */
    public ToolTipButton(final String toolTip, final AbstractButton button) {

        super( toolTip, button );
    }

    /**
     * Create a new {@link ToolTipButton} instance.
     *
     * @param toolTip The text to show when hovering this button.
     */
    public ToolTipButton(final String toolTip) {

        super( toolTip, new JButton( UIUtils.getIcon( "help.png" ) ) );
    }

    /**
     * Get the button content.
     *
     * @return Guess.
     */
    public AbstractButton getButton() {

        if (getContent() == null)
            return null;

        if (getContent() instanceof AbstractButton)
            return (AbstractButton) getContent();

        logger.wrn( "ToolTip has a non-AbstractButton as content!" );
        return null;
    }

    /**
     * Set the button content.
     *
     * @param button The button to use for content.
     */
    public void setButton(final AbstractButton button) {

        setContent( button );
    }
}
