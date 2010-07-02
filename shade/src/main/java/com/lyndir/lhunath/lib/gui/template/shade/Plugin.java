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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import java.awt.*;
import java.util.EventObject;
import java.util.List;


/**
 * <h2>{@link Plugin}<br> <sub>A pluggable module that provides a feature to the shade application.</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Jun 14, 2008</i> </p>
 *
 * @author mbillemo
 */
public interface Plugin {

    /**
     * Handle the given UI event; if supported by this plugin.
     *
     * @param e The UI event that was triggered in the shade interface and was not handled by the application.
     *
     * @return <code>true</code> if the given event is handled by this plugin; <code>false</code> if not (so that other plugins can try to
     *         handle the event).
     */
    boolean handleEvent(EventObject e);

    /**
     * Handle the given application request; if supported by this plugin.
     *
     * @param element The application request that was triggered in the shade interface and was not handled by the application.
     *
     * @return <code>true</code> if the given request is handled by this plugin; <code>false</code> if not (so that other plugins can try to
     *         handle the event).
     */
    boolean handleRequest(Request element);

    /**
     * @return The list of tabs that this plugin wants to add to the interface or <code>null</code> for none.
     */
    List<? extends Tab> buildTabs();

    /**
     * Use this method to add entries to the application's system tray menu.
     *
     * @param sysMenu The object that contains the system tray's menu.
     */
    void buildSystray(PopupMenu sysMenu);

    /**
     * Use this method to add entries to the application's configuration panel.
     *
     * @param builder The component builder used to create the configuration panel.
     *
     * @see DefaultFormBuilder
     */
    void buildSettings(DefaultFormBuilder builder);

    /**
     * @return The name of this plugin.
     */
    String getName();
}
