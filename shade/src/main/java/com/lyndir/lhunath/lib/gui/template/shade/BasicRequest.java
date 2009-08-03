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
package com.lyndir.lhunath.lib.gui.template.shade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * TODO: {@link BasicRequest}<br>
 * <br>
 * Any class that wishes to extend the amount of requests available should call {@link #register(Request[])} in a static
 * block to make its requests available to {@link BasicRequest}'s utility methods such as {@link #getAutoruns()}.
 * 
 * @author lhunath
 */
public enum BasicRequest implements Request {

    /**
     * Update the logos in the UI.
     */
    LOGO (100),

    /**
     * Show the UI panel that is selected and make sure the tab toggle buttons are set properly.
     */
    PANEL (200),

    /**
     * Update settings fields and buttons in the UI.
     */
    SETTINGS (300),

    /**
     * Configure the logging backend's verbosity and output level.
     */
    LOGGER (-1),

    /**
     * Causes the UI to redraw its components to accommodate for theme setting changes.
     */
    THEME (-1),

    /**
     * Convert the application to use fullscreen or windowed mode depending on the config.<br>
     * This setting is not set to auto because it is called manually at application invocation.
     */
    FULLSCREEN (-1),

    /**
     * Update the systray setting button and create/update the systray.
     */
    SYSTRAY (-1);

    private int autorunPriority;


    /**
     * Create a new {@link BasicRequest} instance.
     * 
     * @param autorunPriority
     *            Lower or equal to zero: No auto-running.<br>
     *            Larger than zero: Ascending order in which the request will be processed.
     */
    private BasicRequest(int autorunPriority) {

        this.autorunPriority = autorunPriority;
    }

    /**
     * {@inheritDoc}
     */
    public int getAutorunPriority() {

        return autorunPriority;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return name();
    }


    private static List<Request> requests = new ArrayList<Request>();


    /**
     * Retrieve a list of addons to run on autorun actions. The list will be sorted by addon priority.
     * 
     * @return Guess.
     */
    public static Collection<Request> getAutoruns() {

        SortedMap<Integer, Request> autoruns = new TreeMap<Integer, Request>();
        for (Request u : requests)
            if (u.getAutorunPriority() >= 0)
                autoruns.put( u.getAutorunPriority(), u );

        return autoruns.values();
    }

    /**
     * Register new requests that should be made available through this class' utility methods.
     * 
     * @param newRequests
     *            The new requests.
     */
    public static void register(Request[] newRequests) {

        requests.addAll( Arrays.asList( newRequests ) );
    }


    static {
        register( values() );
    }
}
