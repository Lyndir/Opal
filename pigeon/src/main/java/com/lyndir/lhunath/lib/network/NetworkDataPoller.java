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

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.lyndir.lhunath.lib.system.Poller;


/**
 * A poller that collects data received from the network.
 * 
 * @author lhunath
 */
public class NetworkDataPoller extends Poller<SocketChannel, ByteBuffer> implements NetworkDataListener {

    /**
     * Create a new {@link NetworkDataPoller} instance. Register it on the network you're interested in.
     */
    public NetworkDataPoller() {

    }

    /**
     * Create a new {@link NetworkDataPoller} instance that listens on the given network.
     * 
     * @param network
     *            The network whose data we should be listening for.
     */
    public NetworkDataPoller(Network network) {

        network.registerDataListener( this );
    }

    /**
     * {@inheritDoc}
     */
    public void received(ByteBuffer dataBuffer, SocketChannel connectionSocket) {

        offer( connectionSocket, dataBuffer );
    }
}
