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


/**
 * This listener should be implemented by classes that wish to be notified of network events pertaining connection
 * sockets.<br>
 * 
 * @author lhunath
 */
public interface NetworkConnectionStateListener {

    /**
     * A new connection has been established to a remote server.
     * 
     * @param connectionSocket
     *            The socket over which the new connection will take place.
     */
    public void connected(Socket connectionSocket);

    /**
     * A connection has been terminated.
     * 
     * @param connectionSocket
     *            The socket whose connection has been terminated.
     * @param resetByPeer
     *            <code>true</code> if the remote side closed the connection, <code>false</code> if the local side hung
     *            up.
     */
    public void closed(Socket connectionSocket, boolean resetByPeer);
}
