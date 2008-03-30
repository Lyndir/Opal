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

import static com.lyndir.lhunath.lib.system.Utils.getCharset;
import static com.lyndir.lhunath.lib.system.Utils.showKeyState;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.lyndir.lhunath.lib.system.logging.Logger;

/**
 * Non-blocking, single threaded, SSL enabled network module.
 * 
 * @author lhunath
 */
public class Network implements Runnable {

    protected static final int              READ_BUFFER = 1024;
    private Selector                        selector;
    private Map<SelectableChannel, Integer> opQueue;
    protected List<NetworkMessageListener>  msgListeners;
    protected List<NetworkStateListener>    stateListeners;
    protected StringBuffer                  msg;
    protected Map<Socket, List<ByteBuffer>> writeRequest;

    /**
     * Create a new ServerNet instance.
     */
    public Network() {

        opQueue = Collections.synchronizedMap( new HashMap<SelectableChannel, Integer>() );
        writeRequest = new HashMap<Socket, List<ByteBuffer>>();
        msgListeners = new LinkedList<NetworkMessageListener>();
        stateListeners = new LinkedList<NetworkStateListener>();
        msg = new StringBuffer();

        try {
            selector = Selector.open();
            notifyAll();
        } catch (IOException e) {
            Logger.fatal( e, "Failed to open the server's selector!" );
        }

        Logger.info( "Network set up.", "" );
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        while (true)
            try {

                while (selector == null)
                    wait( 10000 );

                registrations();
                // {TODO SSL} SSLLayer.process();

                for (SelectionKey key : selector.keys())
                    showKeyState( key );

                if (selector.select() == 0)
                    continue;

                Iterator<SelectionKey> i = selector.selectedKeys().iterator();
                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    i.remove();

                    // Listening sockets only
                    if (key.isValid() && key.isAcceptable()) {
                        accept( key );
                        continue;
                    }

                    // Connection sockets only
                    Socket clientSock = ((SocketChannel) key.channel()).socket();
                    if (key.isValid() && key.isConnectable())
                        connect( clientSock );

                    if (key.isValid() && key.isReadable())
                        read( clientSock );

                    if (key.isValid() && key.isWritable())
                        write( clientSock );
                }
            } catch (IOException e) {
                Logger.fatal( e, "Network error occurred!" );
            } catch (InterruptedException e) {
                /* ./care */
            }
    }

    /**
     * Perform all queued operation registrations.
     */
    private void registrations() {

        synchronized (opQueue) {
            try {
                for (Map.Entry<SelectableChannel, Integer> registration : opQueue.entrySet()) {

                    SelectableChannel channel = registration.getKey();
                    if (!channel.isOpen())
                        continue;

                    SelectionKey regKey = channel.keyFor( selector );
                    if (regKey != null && regKey.isValid())
                        // Channel is already registered, just modify the interest ops.
                        regKey.interestOps( registration.getValue() );
                    else
                        // Channel isn't registered yet, register it with the interest ops.
                        channel.register( selector, registration.getValue() );
                    // {TODO SSL} SSLLayer.tryReg(channel, this);
                }

            } catch (ClosedChannelException e) {
                Logger.fatal( e, "Failed to register a channel since it is closed!" );
            }
        }
    }

    /**
     * Finish a connection initiated by {@link #connect(InetSocketAddress)}.
     * 
     * @param clientSock
     *        The socket on which the connection happening.
     * @throws IOException
     */
    private void connect(Socket clientSock) throws IOException {

        if (!clientSock.getChannel().finishConnect()) {
            clientSock.close();
            return;
        }

        setOps( clientSock.getChannel(), SelectionKey.OP_CONNECT, false );
        setOps( clientSock.getChannel(), SelectionKey.OP_READ, true );

        notifyConnect( clientSock );
    }

    private void read(Socket socket) throws IOException {

        // {TODO SSL}
        // SSLLayer ssl = SSLLayer.getLayer( socket );
        // if (ssl != null)
        // ssl.read();
        // else
        // {
        // Not an SSL managed socket.
        ByteBuffer buf = ByteBuffer.allocate( READ_BUFFER );
        int bytes = socket.getChannel().read( buf );

        if (bytes < 0)
            socket.close();

        process( buf, socket );
        // }
    }

    /**
     * Bind a socket on the localhost on a specified port and listen for connections in this network object.
     * 
     * @param port
     *        The local port to bind on.
     * @return The socket that will be listening for connections. object controls the communication between the network
     *         layer and the application logic. It is responsible for sending all data sent to it from the application
     *         in correct form over the network; and delivering all neceserry information from the network; filtered and
     *         translated i
     */
    public ServerSocket bind(int port) {

        try {
            ServerSocketChannel serverConn = ServerSocketChannel.open();
            serverConn.configureBlocking( false );
            serverConn.socket().bind( new InetSocketAddress( port ) );
            setOps( serverConn, SelectionKey.OP_ACCEPT );

            Logger.info( "Bound to " + port, "" );
            return serverConn.socket();

        } catch (IOException e) {
            Logger.fatal( e, "Failed to bind socket on port " + port );
        }

        return null;
    }

    /**
     * Make a connection to destination using this network object to handle it's events.
     * 
     * @param destination
     *        The destination that should be connected to.
     * @return The socket on which the connection will run.
     */
    public Socket connect(InetSocketAddress destination) {

        try {
            SocketChannel conn = SocketChannel.open();
            conn.configureBlocking( false );
            conn.connect( destination );
            setOps( conn, SelectionKey.OP_CONNECT );

            Logger.info( "Connected to " + destination, "" );
            return conn.socket();

        } catch (IOException e) {
            Logger.fatal( e, "Failed to connect to " + destination.toString() + "!" );
        }

        return null;
    }

    /**
     * Accept a new connection.
     * 
     * @param key
     *        The key of the connection that can be accepted from.
     * @throws IOException
     * @throws ClosedChannelException
     */
    private void accept(SelectionKey key) throws IOException, ClosedChannelException {

        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel newConn = server.accept();
        if (newConn == null)
            return;

        // A new connection has been accepted!
        newConn.configureBlocking( false );
        setOps( newConn, SelectionKey.OP_READ );

        notifyAccept( server.socket(), newConn.socket() );
        Logger.info( "Accepted a new connection to " + newConn.socket().getInetAddress(), "" );
    }

    /**
     * Queue a message to be sent to the given destination. Each destination socket has a queue to which this message
     * will be added.
     * 
     * @param request
     *        The message to send wrapped in a bytebuffer. Use 'null' to request this connection be terminated.
     * @param destination
     *        The channel over which to send the message.
     */
    public void queue(ByteBuffer request, Socket destination) {

        List<ByteBuffer> destinationQueue = writeRequest.get( destination );
        if (destinationQueue == null)
            writeRequest.put( destination, destinationQueue = new LinkedList<ByteBuffer>() );
        setOps( destination.getChannel(), SelectionKey.OP_WRITE, true );

        if (request != null) {
            request.rewind();
            destinationQueue.add( request );
            Logger.info( "[>>>>] %27c[33m%s%27c[0m", getCharset().decode( request ), "" );
        } else
            Logger.info( "[>>>>] %27c[31mClose connection%27c[0m", "" );
    }

    /**
     * Stuff can be written to the socket.
     * 
     * @param socket
     *        The socket to write data to.
     * @return true: The queue has been emptied.<br>
     *         false: There's data left in the queue that couldn't be written yet.
     * @throws IOException
     *         Couldn't write to network.
     */
    private boolean write(Socket socket) throws IOException {

        boolean wroteAll;
        // {TODO SSL} SSLLayer ssl = SSLLayer.getLayer( socket ); if (ssl != null) wroteAll =
        // ssl.write(); else
        wroteAll = writeBuffer( socket );
        if (wroteAll)
            setOps( socket.getChannel(), SelectionKey.OP_WRITE, false );

        return wroteAll;
    }

    /**
     * Retrieve the active operations for a given channel, or the operations pending to be applied for that channel.
     * 
     * @param channel
     *        The channel whose operations are requested.
     * @return The channel's active operations.
     */
    private int getOps(SelectableChannel channel) {

        if (opQueue.containsKey( channel ))
            return opQueue.get( channel );

        SelectionKey key = channel.keyFor( selector );
        if (key == null)
            return 0;

        return key.interestOps();
    }

    /**
     * Change the specified channel's operations so that the given operation set is either enabled or disabled.
     * 
     * @param channel
     *        The channel whose operations must be modified.
     * @param newOps
     *        The operations that must be enabled or disabled.
     * @param enable
     *        true: enable the given operations.
     */
    private void setOps(SelectableChannel channel, int newOps, boolean enable) {

        if (enable)
            setOps( channel, getOps( channel ) | newOps );
        else
            setOps( channel, getOps( channel ) & ~newOps );
    }

    /**
     * Register specific ops with a channel. It is guaranteed that only those ops that are valid for the type of channel
     * will be registered with it. Registration will occur as soon as the selector is available for registration.
     * 
     * @param channel
     *        The channel whose interest ops should be modified.
     * @param newOps
     *        The ops that should be set on the channel.
     */
    private void setOps(SelectableChannel channel, int newOps) {

        opQueue.put( channel, channel.validOps() & newOps );
        selector.wakeup();
    }

    /**
     * Notify listeners that something has been received over the network.
     * 
     * @param message
     *        The message that was received.
     * @param destination
     *        The socket on which the message was received.
     */
    private void notifyRead(String message, Socket destination) {

        for (NetworkMessageListener listener : msgListeners)
            listener.received( message, destination );
    }

    /**
     * Notify listeners that a new connection has been accepted.
     * 
     * @param serverSock
     *        The socket that accepted the new connection.
     * @param newSock
     *        The socket over which the new connection will take place.
     */
    private void notifyAccept(ServerSocket serverSock, Socket newSock) {

        for (NetworkStateListener listener : stateListeners)
            listener.accepted( serverSock, newSock );
    }

    /**
     * Notify listeners that a new connection has been completed.
     * 
     * @param connection
     */
    private void notifyConnect(Socket connection) {

        for (NetworkStateListener listener : stateListeners)
            listener.connected( connection );
    }

    /**
     * Register an object as a message listener so that it will be notified when new messages arrive on the network.
     * 
     * @param listener
     *        The object that wishes to be notified.
     */
    public void registerMessageListener(NetworkMessageListener listener) {

        msgListeners.add( listener );
        Logger.info( listener.getClass() + " is now listening to network messages.", "" );
    }

    /**
     * Register an object as a state listener so that it will be notified when connection states change on the network.
     * 
     * @param listener
     *        The object that wishes to be notified.
     */
    public void registerStateListener(NetworkStateListener listener) {

        stateListeners.add( listener );
        Logger.info( listener.getClass() + " is now listening to network status events.", "" );
    }

    /**
     * Process network data after it's been read in.
     * 
     * @param buf
     *        A bytebuffer with data that has just been read in.
     * @param socket
     *        The socket that was used to obtain the data.
     */
    private void process(ByteBuffer buf, Socket socket) {

        if (buf.position() > 0) {
            buf.flip();
            if (socket.isConnected())
                Logger.info( "[<<<<] %s: %27c[32m%s%27c[0m", socket.getInetAddress(), getCharset().decode( buf ) );
            else
                Logger.info( "[<<<<] %27c[31mConnection has been disconnected%27c[0m", "" );
        }

        buf.flip();
        msg.append( getCharset().decode( buf ) );
        buf.compact();

        int eof;
        // Remove trailing NULLs.
        while ((eof = msg.indexOf( String.valueOf( '\n' ) )) == 0)
            msg.deleteCharAt( 0 );

        if (eof > 0) {
            // Received a complete message.
            notifyRead( msg.substring( 0, eof ), socket );
            msg.delete( 0, eof );
        }
    }

    /**
     * Write out as much data as possible that was queued for a certain socket.
     * 
     * @param destination
     *        The socket to which the remaining data should be written.
     * @return true: The queue has been emptied.<br>
     *         false: There's data left in the queue that couldn't be written yet.
     * @throws IOException
     */
    private boolean writeBuffer(Socket destination) throws IOException {

        List<ByteBuffer> queue = writeRequest.get( destination );
        if (queue == null)
            return true;

        Iterator<ByteBuffer> i = queue.iterator();
        while (i.hasNext()) {
            ByteBuffer buf = i.next();

            /*
             * {TODO SSL} SSLLayer ssl = SSLLayer.getLayer(destination); if(ssl != null) { bytes =
             * ssl.writeQueue( buf );
             * 
             * if (bytes < 0) return true; }
             * 
             * else {
             */
            // Not an SSL managed socket.
            if (buf == null) {
                destination.close();
                writeRequest.remove( destination );
                return true; // Ordered to close connection
            }

            if (destination.getChannel().write( buf ) < 0)
                destination.close(); // Connection interrupted
            // }

            if (buf.remaining() != 0)
                return false; // Couldn't write everything yet

            i.remove();
        }

        return true;
    }

    /**
     * Close all sockets managed by this network.
     * 
     * @return true: All sockets were closed successfully.<br>
     *         false: At least one socket didn't close properly.
     */
    public boolean close() {

        boolean flawless = true;
        for (SelectionKey key : selector.keys())
            if (key.isValid())
                try {
                    key.channel().close();
                } catch (IOException e) {
                    flawless = false;
                    Logger.warn( "Failed to clean up connection %s.", key.channel() );
                }

        return flawless;
    }
}
