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
package com.lyndir.lhunath.lib.network.messaging;

import java.util.LinkedList;
import java.util.List;

/**
 * <i>Command - Commands that can be issued over the network.</i><br>
 * <br>
 * This enumeration contains instances for every command that can be issued. <br>
 * 
 * @author lhunath
 * @author axxo
 */
public enum BasicCommandSet {

    /**
     * Do nothing at all.
     */
    NOOP (Message.class);

    private Class<? extends Message>     type;
    private static List<BasicCommandSet> subSets;

    private BasicCommandSet(Class<? extends Message> cmdType) {

        type = cmdType;

        // Register this object
        getSets().add( this );
    }

    /**
     * Returns the class that defines this command.
     * 
     * @return A Message class.
     */
    public Class<? extends Message> toClass() {

        return type;
    }

    protected static List<BasicCommandSet> getSets() {

        if (subSets == null)
            subSets = new LinkedList<BasicCommandSet>();

        return subSets;
    }

    /**
     * Retrieve the Command that stands for the given Message class.
     * 
     * @param cmd
     *        The Message class.
     * @return The Command.
     */
    public static BasicCommandSet fromClass(Class<? extends Message> cmd) {

        for (BasicCommandSet c : getSets())
            if (c.toClass().equals( cmd ))
                return c;

        return null;
    }

    /**
     * Retrieve the Command that is defined by the given name.
     * 
     * @param type
     *        The name of the Command.
     * @return The Command object.
     */
    public static BasicCommandSet fromType(String type) {

        for (BasicCommandSet c : getSets())
            if (c.name().equalsIgnoreCase( type ))
                return c;

        return NOOP;
    }
}
