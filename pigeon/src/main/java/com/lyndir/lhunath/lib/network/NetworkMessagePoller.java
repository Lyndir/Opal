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
package com.lyndir.lhunath.lib.network;

import java.net.Socket;

import com.lyndir.lhunath.lib.system.Poller;


/**
 * Poller that offers messages from a certain network.<br>
 * 
 * @author lhunath
 */
public class NetworkMessagePoller extends Poller<Socket, String> implements NetworkMessageListener {

    /**
     * Create a new NetworkMessagePoller instance.
     * 
     * @param net
     *            The network whose messages we should be polling.
     */
    public NetworkMessagePoller(Network net) {

        net.registerMessageListener( this );
    }

    /**
     * @inheritDoc
     */
    public void received(String message, Socket socket) {

        offer( socket, message );
    }
}
