/*
 *   Copyright 2008, Maarten Billemont
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
package com.lyndir.lhunath.lib.gui.template.shade;

import javax.swing.*;


/**
 * <h2>{@link Tab}<br> <sub>Configuration of a tab in the shade interface.</sub></h2>
 *
 * <p> <i>Jun 14, 2008</i> </p>
 *
 * @author mbillemo
 */
public class Tab {

    private final String title;
    private final Icon icon;
    private final JComponent content;
    private AbstractAction action;

    /**
     * Create a new {@link Tab} instance.
     *
     * @param title   The name of this tab. It will be displayed when the tab is selected.
     * @param icon    An icon to show on the tab selection button.
     * @param content The component that will be shown when the tab is activated.
     */
    public Tab(String title, Icon icon, JComponent content) {

        this.title = title;
        this.icon = icon;
        this.content = content;
    }

    /**
     * @return The title of this {@link Tab}.
     */
    public String getTitle() {

        return title;
    }

    /**
     * @return The icon of this {@link Tab}.
     */
    public Icon getIcon() {

        return icon;
    }

    /**
     * @return The content of this {@link Tab}.
     */
    public JComponent getContent() {

        return content;
    }

    /**
     * @param action The action performed by the button that opens this tab.
     */
    public void setAction(AbstractAction action) {

        this.action = action;
    }

    /**
     * @return The action performed by the button that opens this tab.
     */
    public AbstractAction getAction() {

        return action;
    }
}
