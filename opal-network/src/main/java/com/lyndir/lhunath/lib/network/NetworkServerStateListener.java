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

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


/**
 * This listener should be implemented by classes that wish to be notified of network events pertaining server sockets.<br>
 *
 * @author lhunath
 */
public interface NetworkServerStateListener {

    /**
     * A new server socket has begun listening for connections.
     *
     * @param serverChannel The channel on which new connections are now being accepted.
     */
    void bound(ServerSocketChannel serverChannel);

    /**
     * A new connection has been accepted by a listening server.
     *
     * @param serverChannel     The channel on which the connection has been requested.
     * @param connectionChannel The channel over which the new connection will take place.
     */
    void accepted(ServerSocketChannel serverChannel, SocketChannel connectionChannel);
}
