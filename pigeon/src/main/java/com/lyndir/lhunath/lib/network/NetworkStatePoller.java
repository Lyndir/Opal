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

import java.net.ServerSocket;
import java.net.Socket;

import com.lyndir.lhunath.lib.system.Poller;


/**
 * Poller that offers messages from a certain network.<br>
 * 
 * @author lhunath
 */
public class NetworkStatePoller extends Poller<NetworkStatePoller.State, Socket> implements NetworkStateListener {

    /**
     * Create a new NetworkMessagePoller instance.
     * 
     * @param net
     *            The network whose messages we should be polling.
     */
    public NetworkStatePoller(Network net) {

        net.registerStateListener( this );
    }

    /**
     * @inheritDoc
     */
    public void accepted(ServerSocket server, Socket socket) {

        offer( State.ACCEPTED, socket );
    }

    /**
     * @inheritDoc
     */
    public void connected(Socket connection) {

        offer( State.CONNECTED, connection );
    }


    /**
     * A certain state that can be polled for by the NetworkStatePoller.
     */
    public enum State {

        /**
         * A new connection has been accepted by a listening socket. The Poller returns the socket that will be used for
         * the new connection.
         */
        ACCEPTED,

        /**
         * A connection has been established to a certain host. The Poller returns the socket that will be used for the
         * new connection.
         */
        CONNECTED;
    }
}
