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
package com.lyndir.lhunath.opal.network;

import java.nio.channels.SocketChannel;


/**
 * This listener should be implemented by classes that wish to be notified of network events pertaining connection sockets.<br>
 *
 * @author lhunath
 */
public interface NetworkConnectionStateListener {

    /**
     * A new connection has been established to a remote server.
     *
     * @param channel The channel over which the new connection will take place.
     */
    void connected(SocketChannel channel);

    /**
     * A channel has been closed.
     *
     * @param channel     The channel whose connection has been terminated.
     * @param resetByPeer {@code true} if the remote side closed the connection, {@code false} if the local side hung up.
     */
    void closed(SocketChannel channel, boolean resetByPeer);
}
