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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <h2>{@link Network}<br>
 * <sub>A non-blocking single-threaded TCP network layer with SSL/TLS support.</sub></h2>
 * 
 * <p>
 * TODO
 * </p>
 * 
 * <p>
 * <i>Jun 23, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
public class Network implements Runnable {

    private static final Logger                             logger             = Logger.get( Network.class );
    private static final Map<SelectionKey, Integer>         lastReadyOps       = new HashMap<SelectionKey, Integer>();
    private static final Map<SelectionKey, Integer>         lastInterestOps    = new HashMap<SelectionKey, Integer>();
    private static final Map<SelectionKey, HandshakeStatus> lastHSStatus       = new HashMap<SelectionKey, HandshakeStatus>();
    // TODO: Use SSLSession#getApplicationBufferSize
    private static final int                                READ_BUFFER        = 1024;
    // TODO: Use SSLSession#getPacketBufferSize
    private static final int                                WRITE_BUFFER       = 1024;
    private static final int                                WRITE_QUEUE_BUFFER = 1024 * 10;

    private Thread                                          networkThread;

    private List<NetworkDataListener>                       dataListeners;
    private List<NetworkServerStateListener>                serverStateListeners;
    private List<NetworkConnectionStateListener>            connectionStateListeners;

    private Map<SelectableChannel, SSLEngine>               sslEngines;
    private Map<SocketChannel, ByteBuffer>                  writeQueueBuffers;
    private Map<SocketChannel, Object>                      writeQueueLocks;

    protected Object                                        selectorGuard      = new Object();
    protected Selector                                      selector;

    private Map<SocketChannel, ByteBuffer>                  readBuffers;
    private Map<SocketChannel, ByteBuffer>                  writeBuffers;
    private Map<SocketChannel, Boolean>                     closedChannels;


    /**
     * Create a new {@link Network} instance.
     */
    public Network() {

        // Collections that are only modified by calling threads.
        dataListeners = new LinkedList<NetworkDataListener>();
        serverStateListeners = new LinkedList<NetworkServerStateListener>();
        connectionStateListeners = new LinkedList<NetworkConnectionStateListener>();

        // Collections that are modified by calling threads and the networking thread.
        sslEngines = Collections.synchronizedMap( new HashMap<SelectableChannel, SSLEngine>() );
        writeQueueBuffers = Collections.synchronizedMap( new HashMap<SocketChannel, ByteBuffer>() );
        writeQueueLocks = Collections.synchronizedMap( new HashMap<SocketChannel, Object>() );

        // Collections that are only modified by the networking thread.
        readBuffers = new HashMap<SocketChannel, ByteBuffer>();
        writeBuffers = new HashMap<SocketChannel, ByteBuffer>();
        closedChannels = new HashMap<SocketChannel, Boolean>();
    }

    /**
     * Start this network by executing a networking thread and bringing up the network selector.
     */
    public void startThread() {

        if (networkThread != null && networkThread.isAlive())
            throw logger.err( "Network thread is already running." ).toError();

        networkThread = new Thread( this );
        networkThread.start();
    }

    /**
     * Bring up the networking framework.
     * 
     * This initializes the networking {@link Selector} making it possible to start network operations. It does not
     * start the networking thread. See {@link #startThread()} to start the network and execute a networking thread for
     * it. This method is mostly helpful for manually bringing the network in an existing thread down and up again.
     */
    public synchronized void bringUp() {

        if (isUp())
            // Already up.
            return;

        try {
            selector = Selector.open();
            notifyAll();
        }

        catch (IOException e) {
            throw logger.err( e, "Failed to bring up the networking framework!" ).toError();
        }

        logger.inf( "Networking framework is up." );
    }

    /**
     * Bring the networking framework down.
     * 
     * This closes the networking {@link Selector} causing all connections to be terminated.
     */
    public synchronized void bringDown() {

        if (!isUp())
            // Already down.
            return;

        try {
            selector.close();
            notifyAll();
        }

        catch (IOException e) {
            throw logger.err( e, "Failed to shut down the networking framework!" ).toError();
        }

        logger.inf( "Networking framework is down." );
    }

    /**
     * @return <code>true</code>: The network is ready for network event processing (binding, accepting, connecting,
     *         reading and writing).
     */
    public synchronized boolean isUp() {

        return selector != null && selector.isOpen();
    }

    /**
     * Bind a socket on the wildcard address at a specified port and listen for connections.
     * 
     * @param port
     *            The local port to bind on.
     * @param sslEngine
     *            If you want to use SSL/TLS to encrypt the data sent over connections established from this socket,
     *            specify an SSL engine that has been initialized to for communication with remote clients. If you just
     *            want plain-text communication, pass <code>null</code> here.
     * 
     * @return The channel that will be listening for connections.
     * 
     * @throws IOException
     */
    public ServerSocketChannel bind(int port, SSLEngine sslEngine)
            throws IOException {

        return bind( new InetSocketAddress( port ), sslEngine );
    }

    /**
     * Bind a socket on the interface defined by the given address at a specified port and listen for connections.
     * 
     * @param address
     *            The address of the interface to bind on.
     * @param port
     *            The local port to bind on.
     * @param sslEngine
     *            If you want to use SSL/TLS to encrypt the data sent over connections established from this socket,
     *            specify an SSL engine that has been initialized to for communication with remote clients. If you just
     *            want plain-text communication, pass <code>null</code> here.
     * 
     * @return The channel that will be listening for connections.
     * 
     * @throws IOException
     */
    public ServerSocketChannel bind(InetAddress address, int port, SSLEngine sslEngine)
            throws IOException {

        return bind( new InetSocketAddress( address, port ), sslEngine );
    }

    /**
     * Bind a socket on the interface defined by the given hostname at a specified port and listen for connections.
     * 
     * @param hostname
     *            The hostname that resolves to the interface address of the interface to bind on.
     * @param port
     *            The local port to bind on.
     * @param sslEngine
     *            If you want to use SSL/TLS to encrypt the data sent over connections established from this socket,
     *            specify an SSL engine that has been initialized to for communication with remote clients. If you just
     *            want plain-text communication, pass <code>null</code> here.
     * 
     * @return The channel that will be listening for connections.
     * 
     * @throws IOException
     */
    public ServerSocketChannel bind(String hostname, int port, SSLEngine sslEngine)
            throws IOException {

        return bind( new InetSocketAddress( hostname, port ), sslEngine );
    }

    /**
     * Bind a socket on the wildcard address at a specified port and listen for connections.
     * 
     * @param socketAddress
     *            The socket address that defines the interface and port to bind the socket on.
     * @param sslEngine
     *            If you want to use SSL/TLS to encrypt the data sent over connections established from this socket,
     *            specify an SSL engine that has been initialized to for communication with remote clients. If you just
     *            want plain-text communication, pass <code>null</code> here.
     * 
     * @return The channel that will be listening for connections.
     * 
     * @throws IOException
     */
    public ServerSocketChannel bind(InetSocketAddress socketAddress, SSLEngine sslEngine)
            throws IOException {

        if (selector == null || !selector.isOpen())
            throw logger.err( "The networking framework is not (yet) up." ).toError( IllegalStateException.class );

        // Bind a new socket in non-blocking mode to the socketAddress.
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking( false );
        serverChannel.socket().bind( socketAddress );

        // Register the SSL engine for this socket, if SSL/TLS is desired.
        if (sslEngine != null) {
            sslEngine.setUseClientMode( false );
            sslEngines.put( serverChannel, sslEngine );
        }

        // The socket is interested in accepting connections.
        setOps( serverChannel, SelectionKey.OP_ACCEPT );

        logger.inf( "[====: %s] Bound.", //
                    nameChannel( serverChannel ) );
        notifyBound( serverChannel );

        return serverChannel;
    }

    /**
     * Accept a pending connection.
     * 
     * @param serverChannel
     *            The channel where a connection can be accepted.
     * 
     * @throws IOException
     */
    private void accept(ServerSocketChannel serverChannel)
            throws IOException {

        SocketChannel connectionChannel = serverChannel.accept();
        if (connectionChannel == null)
            // No connections waiting to be accepted.
            return;

        // Create a write queue lock for this channel.
        writeQueueLocks.put( connectionChannel, new Object() );

        // Register the SSL engine for this connection socket, if SSL/TLS is enabled on the server socket.
        SSLEngine sslEngine = sslEngines.get( serverChannel );
        if (sslEngine != null)
            sslEngines.put( connectionChannel, sslEngine );

        // New connection; configure it for non-blocking and read what it has to say.
        connectionChannel.configureBlocking( false );
        setOps( connectionChannel, SelectionKey.OP_READ );

        logger.inf( "[====: %s] Accepted a new connection to: %s", //
                    nameChannel( serverChannel ), connectionChannel.socket().getInetAddress() );
        notifyAccept( serverChannel, connectionChannel );
    }

    /**
     * Make a connection to the given destination.
     * 
     * @param hostname
     *            The name that resolves to the address that defines the destination host to connect to.
     * @param port
     *            The port of the remote listening socket on the given host to connect to.
     * @param sslEngine
     *            If you want to use SSL/TLS to encrypt the data sent over this connection, specify an SSL engine that
     *            has been initialized to for communication with the remote server. If you just want plain-text
     *            communication, pass <code>null</code> here.
     * 
     * @return The channel on which the connection runs.
     * 
     * @throws IOException
     */
    public SocketChannel connect(String hostname, int port, SSLEngine sslEngine)
            throws IOException {

        return connect( new InetSocketAddress( hostname, port ), sslEngine );
    }

    /**
     * Make a connection to the given destination.
     * 
     * @param hostAddress
     *            The address that defines the destination host to connect to.
     * @param port
     *            The port of the remote listening socket on the given host to connect to.
     * @param sslEngine
     *            If you want to use SSL/TLS to encrypt the data sent over this connection, specify an SSL engine that
     *            has been initialized to for communication with the remote server. If you just want plain-text
     *            communication, pass <code>null</code> here.
     * 
     * @return The channel on which the connection runs.
     * 
     * @throws IOException
     */
    public SocketChannel connect(InetAddress hostAddress, int port, SSLEngine sslEngine)
            throws IOException {

        return connect( new InetSocketAddress( hostAddress, port ), sslEngine );
    }

    /**
     * Make a connection to the given destination.
     * 
     * @param socketAddress
     *            The address that defines the destination host and port to connect to.
     * @param sslEngine
     *            If you want to use SSL/TLS to encrypt the data sent over this connection, specify an SSL engine that
     *            has been initialized to for communication with the remote server. If you just want plain-text
     *            communication, pass <code>null</code> here.
     * 
     * @return The channel on which the connection runs.
     * 
     * @throws IOException
     */
    public SocketChannel connect(InetSocketAddress socketAddress, SSLEngine sslEngine)
            throws IOException {

        if (selector == null || !selector.isOpen())
            throw logger.err( "The networking framework is not (yet) up." ).toError( IllegalStateException.class );

        // Begin a new non-blocking connection.
        SocketChannel connectionChannel = SocketChannel.open();
        connectionChannel.configureBlocking( false );

        // Create a write queue lock for this channel.
        writeQueueLocks.put( connectionChannel, new Object() );

        // Register the SSL engine for this socket, if SSL/TLS is desired.
        if (sslEngine != null) {
            sslEngine.setUseClientMode( true );
            sslEngines.put( connectionChannel, sslEngine );
        }

        logger.inf( "[>>>>: %s] Connecting to: %s", //
                    nameChannel( connectionChannel ), socketAddress );
        if (!connectionChannel.connect( socketAddress ))
            // The connection attempt has not yet finished: Set an interest in connection completion operations
            setOps( connectionChannel, SelectionKey.OP_CONNECT );
        else
            // The connection attempt has already been completed.
            finishConnect( connectionChannel );

        return connectionChannel;
    }

    /**
     * Finish a connection initiated by {@link #connect(InetSocketAddress)}.
     * 
     * @param socketChannel
     *            The channel on which the connection happening.
     * 
     * @throws IOException
     */
    private void finishConnect(SocketChannel socketChannel)
            throws IOException {

        if (!socketChannel.finishConnect())
            // Not yet done connecting.
            return;

        // Connection completed, see what the server has to say.
        setOps( socketChannel, SelectionKey.OP_READ );

        logger.inf( "[<<<<: %s] Connected.", //
                    nameChannel( socketChannel ) );
        notifyConnect( socketChannel );
    }

    /**
     * Read available chatter from the given socket and {@link #process(ByteBuffer, Socket)} it.
     * 
     * @throws IOException
     */
    private void read(SocketChannel socketChannel)
            throws IOException {

        // Get the connection's network data read buffer.
        ByteBuffer readBuffer = readBuffers.get( socketChannel );
        if (readBuffer == null)
            // No read buffer assigned to this connection yet; allocate one.
            readBuffers.put( socketChannel, readBuffer = ByteBuffer.allocate( READ_BUFFER ) );

        // Read available connection bytes until either the read buffer is full or all available bytes have been read.
        int bytesRead = socketChannel.read( readBuffer );

        // See if read buffer filled up completely; if so, make it bigger for the next read operation.
        if (readBuffer.limit() == readBuffer.capacity()) {
            ByteBuffer newReadBuffer = ByteBuffer.allocate( readBuffer.capacity() + READ_BUFFER );

            readBuffer.flip();
            readBuffers.put( socketChannel, readBuffer = newReadBuffer.put( readBuffer ) );
        }

        if (readBuffer.position() > 0) {
            // Data was received.
            logger.dbg( "[<<<<: %s] Read %d bytes into: %s", //
                        nameChannel( socketChannel ), bytesRead, renderBuffer( readBuffer ) );
            readBuffer.flip();

            ByteBuffer dataBuffer = ByteBuffer.allocate( READ_BUFFER );
            dataBuffer = toApplicationData( readBuffer, socketChannel, dataBuffer );
            if (dataBuffer.remaining() == 0)
                // The network data did not contain any application data.
                return;

            // Visualize incoming (plain-text) data.
            logger.inf( "[<<<<: %s] Received (plain): %s", //
                        nameChannel( socketChannel ), getCharset().decode( dataBuffer ) );
            dataBuffer.flip();

            // Pass incoming (plain-text) data to the application.
            notifyRead( dataBuffer, socketChannel );
        }

        else if (bytesRead < 0)
            // Socket connection was terminated by the client.
            closedChannels.put( socketChannel, true );
    }

    /**
     * Convert a buffer of network data into application data.
     * 
     * @param readBuffer
     *            The buffer that contains the network data, ready to be read (position set to zero, limit set to the
     *            end of the received network data).
     * @param socketChannel
     *            The channel over which the network data was received.
     * @param dataBuffer
     *            The buffer of application data, ready to be written/appended to. After this operation it will be ready
     *            to be read (position set to zero, limit set to the end of the application data).
     * 
     * @return The dataBuffer.
     *         <p>
     *         <b>Use the return value for processing, not the original data buffer!</b><br>
     *         The buffer might have been reallocated in which case the original buffer is obsolete.
     *         </p>
     */
    private ByteBuffer toApplicationData(ByteBuffer readBuffer, SocketChannel socketChannel, ByteBuffer dataBuffer)
            throws SSLException, IOException {

        SSLEngine sslEngine = sslEngines.get( socketChannel );
        if (sslEngine != null) {
            // SSL/TLS: Decrypt network data into application data (or into nothingness!).

            while (true) {

                // Try to decrypt readBuffer into dataBuffer.
                SSLEngineResult sslEngineResult = sslEngine.unwrap( readBuffer, dataBuffer );

                switch (sslEngineResult.getStatus()) {
                    case BUFFER_OVERFLOW:
                        // Data buffer overflow, make it bigger and try again.
                        logger.dbg(
                                    "[<<<<: %s] SSL %s: dataBuffer%s + %d]", //
                                    nameChannel( socketChannel ), sslEngineResult.getStatus(),
                                    renderBuffer( dataBuffer ), READ_BUFFER );
                        ByteBuffer newDataBuffer = ByteBuffer.allocate( dataBuffer.capacity() + READ_BUFFER );
                        dataBuffer.flip();
                        dataBuffer = newDataBuffer.put( dataBuffer );

                        // Retry
                        continue;

                    case BUFFER_UNDERFLOW:
                        // Not enough network data collected for a whole SSL/TLS packet.
                        logger.dbg(
                                    "[<<<<: %s] SSL %s: need_src: readBuffer%s", //
                                    nameChannel( socketChannel ), sslEngineResult.getStatus(),
                                    renderBuffer( readBuffer ) );
                    break;

                    case CLOSED:
                        // SSL Engine indicates it is closed or just closed itself.
                        logger.dbg( "[<<<: %s] SSL: %s", //
                                    nameChannel( socketChannel ), sslEngineResult.getStatus() );
                        closedChannels.put( socketChannel, true );
                    break;

                    case OK:
                        logger.dbg(
                                    "[>>>>: %s] SSL %s - %s: Produced %d bytes application data", //
                                    nameChannel( socketChannel ), sslEngineResult.getStatus(),
                                    sslEngineResult.getHandshakeStatus(), sslEngineResult.bytesProduced() );
                    break;
                }

                // Don't retry.
                break;
            }

            // Make the application data available and add new network data after what's left unprocessed.
            dataBuffer.flip();
            readBuffer.compact();

            return dataBuffer;
        }

        // Plain Text: Copy network data to application data and prepare both buffers for their next operations.
        if (dataBuffer.remaining() < readBuffer.remaining()) {
            // Not enough space in the dataBuffer for the readBuffer's data; make it bigger.
            ByteBuffer newDataBuffer = ByteBuffer.allocate( dataBuffer.position() + readBuffer.remaining() );

            dataBuffer.flip();
            dataBuffer = newDataBuffer.put( dataBuffer );
        }

        dataBuffer.put( readBuffer ).flip();
        readBuffer.limit( readBuffer.capacity() ).rewind();

        return dataBuffer;
    }

    /**
     * Stuff can be written to the channel.
     * 
     * @param socketChannel
     *            The channel to write data to.
     * 
     * @throws IOException
     */
    private void write(SocketChannel socketChannel)
            throws IOException {

        // Lock this connection's write queue.
        synchronized (writeQueueLocks.get( socketChannel )) {
            if (!socketChannel.isOpen() || !socketChannel.isConnected() || socketChannel.isConnectionPending()
                || socketChannel.socket().isOutputShutdown())
                return;

            // Obtain the application data write queue buffer. If none is allocated yet, make a dummy empty one.
            ByteBuffer writeQueueBuffer = writeQueueBuffers.get( socketChannel );
            if (writeQueueBuffer == null)
                writeQueueBuffers.put( socketChannel, writeQueueBuffer = ByteBuffer.allocate( 0 ) );
            else
                writeQueueBuffer.flip();

            // Get the connection's network data write buffer.
            ByteBuffer writeBuffer = writeBuffers.get( socketChannel );
            if (writeBuffer == null)
                // No write buffer assigned to this connection yet; allocate one.
                writeBuffers.put( socketChannel, writeBuffer = ByteBuffer.allocate( WRITE_BUFFER ) );

            // Perform translation from application data to network data and out the result.
            writeBuffer = fromApplicationData( writeQueueBuffer, socketChannel, writeBuffer );
            writeBuffers.put( socketChannel, writeBuffer );

            if (writeBuffer.remaining() > 0) {
                int bytesWritten = socketChannel.write( writeBuffer );
                logger.dbg( "[>>>>: %s] Wrote %d bytes of: writeBuffer%s", //
                            nameChannel( socketChannel ), bytesWritten, renderBuffer( writeBuffer ) );
                writeBuffer.compact();
            }

            if (writeBuffer.position() == 0)
                // Wrote all queued data, no longer interested in writing until we receive more application data.
                delOps( socketChannel, SelectionKey.OP_WRITE );
        }
    }

    /**
     * Convert a buffer of application data into network data.
     * 
     * @param dataBuffer
     *            The buffer that contains the application data, ready to be written (position set to zero, limit set to
     *            the end of the application data).
     * @param socketChannel
     *            The channel over which the network data will be sent.
     * @param writeBuffer
     *            The buffer that contains the connection's network data to be written, ready to be written/appended to
     *            (position set to the end of the network data). At the end of this operation, this buffer will be ready
     *            to be read/sent/written out (position set to zero, limit set to the end of the network data).
     * 
     * @return The writeBuffer.
     *         <p>
     *         <b>Use the return value for writing, not the original write buffer!</b><br>
     *         The buffer might have been reallocated in which case the original buffer is obsolete.
     *         </p>
     */
    private ByteBuffer fromApplicationData(ByteBuffer dataBuffer, SocketChannel socketChannel, ByteBuffer writeBuffer)
            throws SSLException, IOException {

        SSLEngine sslEngine = sslEngines.get( socketChannel );
        if (sslEngine != null) {
            // SSL/TLS: Encrypt network data from application data (or from nothingness!).

            while (true) {

                // Try to encrypt readBuffer into dataBuffer.
                SSLEngineResult sslEngineResult = sslEngine.wrap( dataBuffer, writeBuffer );

                switch (sslEngineResult.getStatus()) {
                    case BUFFER_OVERFLOW:
                        // Data buffer overflow, make it bigger and try again.
                        logger.dbg(
                                    "[>>>>: %s] SSL %s: writeBuffer%s + %d", //
                                    nameChannel( socketChannel ), sslEngineResult.getStatus(),
                                    renderBuffer( writeBuffer ), WRITE_BUFFER );
                        ByteBuffer newWriteBuffer = ByteBuffer.allocate( writeBuffer.capacity() + WRITE_BUFFER );
                        writeBuffer.flip();
                        writeBuffer = newWriteBuffer.put( writeBuffer );

                        // Retry
                        continue;

                    case BUFFER_UNDERFLOW:
                        // Not enough application data collected for a whole SSL/TLS packet.
                        logger.dbg(
                                    "[>>>>: %s] SSL %s: need_src: dataBuffer%s", //
                                    nameChannel( socketChannel ), sslEngineResult.getStatus(),
                                    renderBuffer( dataBuffer ) );
                    break;

                    case CLOSED:
                        // SSL Engine indicates it is closed or just closed itself.
                        logger.dbg( "[>>>>: %s] SSL %s", //
                                    nameChannel( socketChannel ), sslEngineResult.getStatus() );
                        closedChannels.put( socketChannel, false );
                    break;

                    case OK:
                        logger.dbg(
                                    "[>>>>: %s] SSL %s - %s: Consumed %d bytes application data", //
                                    nameChannel( socketChannel ), sslEngineResult.getStatus(),
                                    sslEngineResult.getHandshakeStatus(), sslEngineResult.bytesConsumed() );
                    break;
                }

                // Don't retry.
                break;
            }

            // Make the application data available and add new network data after what's left unprocessed.
            writeBuffer.flip();
            dataBuffer.compact();

            return writeBuffer;
        }

        // Plain Text: Copy application data to network data and prepare both buffers for their next operations.
        if (writeBuffer.remaining() < dataBuffer.remaining()) {
            // Not enough space in the writeBuffer for the dataBuffer's data; make it bigger.
            ByteBuffer newWriteBuffer = ByteBuffer.allocate( writeBuffer.position() + dataBuffer.remaining() );

            writeBuffer.flip();
            writeBuffer = newWriteBuffer.put( writeBuffer );
        }

        writeBuffer.put( dataBuffer ).flip();
        dataBuffer.limit( dataBuffer.capacity() ).rewind();

        return writeBuffer;
    }

    /**
     * Remove the channel from I/O operation scheduling. It will be closed.
     * 
     * @param socketChannel
     *            The channel that needs to be closed.
     * 
     * @throws IOException
     */
    private void closeChannel(SocketChannel socketChannel, boolean resetByPeer)
            throws IOException {

        synchronized (writeQueueLocks.get( socketChannel )) {
            SSLEngine sslEngine = sslEngines.get( socketChannel );
            if (sslEngine != null)
                if (resetByPeer)
                    sslEngine.closeInbound();
                else
                    sslEngine.closeOutbound();

            socketChannel.close();
            sslEngines.remove( socketChannel );

            readBuffers.remove( socketChannel );
            writeBuffers.remove( socketChannel );
            closedChannels.remove( socketChannel );

            writeQueueLocks.remove( socketChannel );
            writeQueueBuffers.remove( socketChannel );

            if (resetByPeer)
                logger.inf( "[<<<<: %s] Closed connection (reset by peer).", //
                            nameChannel( socketChannel ) );
            else
                logger.inf( "[>>>>: %s] Closed connection (terminated).", //
                            nameChannel( socketChannel ) );

            notifyClose( socketChannel, resetByPeer );
        }
    }

    /**
     * Queue a message to be sent to the given destination. The message will be added to the destination's write queue.
     * 
     * <p>
     * After this process, the application's data buffer's position will be set right after the bytes that have been
     * queued on the network. This is guaranteed to be the buffer's limit (eg. all data will be queued).
     * </p>
     * 
     * The connection's queue buffer's position will be right after the newly added bytes.
     * 
     * @param dataBuffer
     *            A byte buffer that holds the bytes to dispatch. Make sure that the buffer is set up read for reading
     *            (put the position at the start of the data to read and the limit at the end). The buffer will be
     *            flipped so its position should be right after the bytes to write. Use <code>null</code> to request a
     *            connection shutdown.
     * @param socketChannel
     *            The channel over which to send the message.
     * @throws ClosedChannelException
     *             The given channel is closed.
     */
    public void queue(ByteBuffer dataBuffer, SocketChannel socketChannel)
            throws ClosedChannelException {

        if (socketChannel.keyFor( selector ) == null) {
            // Destination is not supported by our selector.
            logger.wrn( "Tried to queue a message for a destination (%s) that is not managed by our selector.",
                        nameChannel( socketChannel ) );
            throw logger.toError( IllegalArgumentException.class );
        }

        ByteBuffer writeQueueBuffer;
        synchronized (writeQueueBuffers) {
            writeQueueBuffer = writeQueueBuffers.get( socketChannel );

            // Obtain or create the data buffer for the connection.
            if (writeQueueBuffer == null)
                // No writeQueueBuffer yet for this connection, allocate one.
                writeQueueBuffers.put( socketChannel,
                                       writeQueueBuffer = ByteBuffer.allocate( Math.max( dataBuffer.remaining(),
                                                                                         WRITE_QUEUE_BUFFER ) ) );
            else if (writeQueueBuffer.remaining() < dataBuffer.remaining())
                // Not enough space left in the writeQueueBuffer for the application data, make it bigger.
                synchronized (writeQueueLocks.get( socketChannel )) {
                    ByteBuffer newWriteQueueBuffer = ByteBuffer.allocate( Math.max( writeQueueBuffer.position()
                                                                                    + dataBuffer.remaining(),
                                                                                    writeQueueBuffer.capacity()
                                                                                            + WRITE_QUEUE_BUFFER ) );
                    writeQueueBuffer.flip();
                    writeQueueBuffers.put( socketChannel, writeQueueBuffer = newWriteQueueBuffer.put( writeQueueBuffer ) );
                }
        }

        // We are interested in writing stuff.
        synchronized (writeQueueLocks.get( socketChannel )) {
            writeQueueBuffer.put( dataBuffer );
        }
        addOps( socketChannel, SelectionKey.OP_WRITE );
    }

    /**
     * Request the given channel be closed for communication.
     * 
     * <p>
     * <b>NOTE: You should always use this method and never close the socket or channel directly!</b><br>
     * This method makes sure that closure is handled gracefully according to the wishes of the transport and optional
     * encryption protocols.
     * </p>
     * 
     * <p>
     * Non-{@link SocketChannel}s (such as the {@link ServerSocketChannel}) may be closed directly.
     * </p>
     * 
     * @param socketChannel
     *            The channel that is no longer interested in sending data.
     * 
     * @throws IOException
     *             The channel could not be closed gracefully.
     */
    public void close(SocketChannel socketChannel)
            throws IOException {

        synchronized (writeQueueLocks.get( socketChannel )) {
            SSLEngine sslEngine = sslEngines.get( socketChannel );
            if (sslEngine != null) {
                sslEngine.closeOutbound();
                addOps( socketChannel, SelectionKey.OP_WRITE );
            }

            else
                socketChannel.close();
        }
    }

    /**
     * Retrieves the operations that the given channel is interested in.
     * 
     * @param channel
     *            The channel whose operations are requested.
     * 
     * @return The channel's interested operations.
     */
    private int getOps(SelectableChannel channel) {

        SelectionKey key = channel.keyFor( selector );
        if (key == null || !key.isValid())
            return 0;

        return key.interestOps();
    }

    /**
     * Enable the given operation(s).
     * 
     * @param channel
     *            The channel whose operations must be modified.
     * @param addOps
     *            The operations that must be enabled.
     * 
     * @throws ClosedChannelException
     *             If applying ops on a closed channel (that is not yet registered with the network selector).
     */
    private void addOps(SelectableChannel channel, int... addOps)
            throws ClosedChannelException {

        if (addOps.length == 0)
            // Nothing to add.
            return;

        // OR all addOps together.
        int allAddOps = 0;
        for (int addOp : addOps)
            allAddOps |= addOp;

        // Apply the addOps.
        setOps( channel, getOps( channel ) | allAddOps );
    }

    /**
     * Disable the given operation(s).
     * 
     * @param channel
     *            The channel whose operations must be modified.
     * @param delOps
     *            The operations that must be disabled.
     * 
     * @throws ClosedChannelException
     *             If applying ops on a closed channel (that is not yet registered with the network selector).
     */
    private void delOps(SelectableChannel channel, int... delOps)
            throws ClosedChannelException {

        if (delOps.length == 0)
            // Nothing to add.
            return;

        // OR all addOps together.
        int allDelOps = 0;
        for (int delOp : delOps)
            allDelOps |= delOp;

        // Apply the addOps.
        setOps( channel, getOps( channel ) & ~allDelOps );
    }

    /**
     * Register the given operations with a channel. It is guaranteed that only operations that are valid for the type
     * of channel will be applied to it. The operations will be queued and applied to the channel as soon as it is
     * available.
     * 
     * @param channel
     *            The channel whose interest ops should be modified.
     * @param newOps
     *            The ops that should be set on the channel.
     * 
     * @throws ClosedChannelException
     *             If applying ops on a closed channel (that is not yet registered with the network selector).
     */
    private synchronized void setOps(SelectableChannel channel, int... newOps)
            throws ClosedChannelException {

        // OR all newOps together.
        int interestOps = 0;
        for (int newOp : newOps)
            interestOps |= newOp;
        interestOps &= channel.validOps();

        // Apply the newOps.
        SelectionKey regKey = channel.keyFor( selector );
        if (regKey != null && (!regKey.isValid() || regKey.interestOps() == interestOps))
            // Interest ops are unmodified.
            return;

        synchronized (selectorGuard) {
            selector.wakeup();
            regKey = channel.register( selector, interestOps );
            showKeyState( regKey );
        }
    }

    /**
     * Determine the interested operations for SSL enabled channels. The SSL protocol can request read or write
     * operations depending on what it needs to complete/initiate a handshake.
     * 
     * @throws ClosedChannelException
     *             If applying ops on a closed channel (that is not yet registered with the network selector).
     */
    private void processHandshakes()
            throws ClosedChannelException {

        for (Map.Entry<SelectableChannel, SSLEngine> ssl : sslEngines.entrySet()) {
            SelectableChannel channel = ssl.getKey();
            SSLEngine engine = ssl.getValue();

            while (true) {
                if (!channel.isOpen())
                    // There's no point, the connection is already gone.
                    continue;

                HandshakeStatus handshakeStatus = engine.getHandshakeStatus();
                switch (handshakeStatus) {
                    case NEED_TASK:
                        // A lengthy task must be performed.
                        final Runnable delegatedTask = engine.getDelegatedTask();
                        if (delegatedTask != null) {
                            logger.dbg( "[====: %s] SSL %s: Starting a task thread.", //
                                        nameChannel( channel ), handshakeStatus, delegatedTask );

                            new Thread( new Runnable() {

                                public void run() {

                                    delegatedTask.run();
                                    selector.wakeup();
                                }
                            } ).start();
                        }

                        // Recheck engine.
                        continue;

                    case NEED_WRAP:
                        // We have stuff to wrap and write to the client.
                        addOps( channel, SelectionKey.OP_WRITE );
                    break;

                    case NEED_UNWRAP:
                        // We need stuff from the client to unwrap.
                        addOps( channel, SelectionKey.OP_READ );
                    break;

                    case FINISHED:
                    case NOT_HANDSHAKING:
                        // No operations need be set.
                    break;
                }

                // Don't recheck engine.
                break;
            }
        }
    }

    /**
     * Process data left in network read/write buffers.
     * 
     * @throws IOException
     */
    private void processBuffers()
            throws IOException {

        // Read buffers
        for (Map.Entry<SocketChannel, ByteBuffer> entry : readBuffers.entrySet()) {
            SocketChannel socketChannel = entry.getKey();
            ByteBuffer readBuffer = entry.getValue();

            if (readBuffer.position() > 0) {
                logger.dbg( "[rbuf: %s] %s", //
                            nameChannel( socketChannel ), renderBuffer( readBuffer ) );
                read( socketChannel );
            }
        }

        // Write buffers
        for (Map.Entry<SocketChannel, ByteBuffer> entry : writeBuffers.entrySet()) {
            SocketChannel socketChannel = entry.getKey();
            ByteBuffer writeBuffer = entry.getValue();

            if (writeBuffer.position() > 0) {
                logger.dbg( "[wbuf: %s] %s", //
                            nameChannel( socketChannel ), renderBuffer( writeBuffer ) );
                addOps( socketChannel, SelectionKey.OP_WRITE );
            }
        }

        // Write queued application data
        for (Map.Entry<SocketChannel, ByteBuffer> entry : writeQueueBuffers.entrySet()) {
            SocketChannel socketChannel = entry.getKey();
            ByteBuffer writeQueueBuffer = entry.getValue();

            if (writeQueueBuffer.position() > 0 && socketChannel.isOpen())
                addOps( socketChannel, SelectionKey.OP_WRITE );
        }

        // Let each channel that has stuff to write send it out.
        for (SocketChannel socketChannel : writeQueueBuffers.keySet())
            write( socketChannel );
    }

    /**
     * Close all channels that requested closure.
     */
    private void processClosure()
            throws IOException {

        for (Map.Entry<SocketChannel, Boolean> entry : closedChannels.entrySet()) {
            SocketChannel socketChannel = entry.getKey();
            boolean resetByPeer = entry.getValue();

            closeChannel( socketChannel, resetByPeer );
        }
    }

    /**
     * Notify listeners that a new server channel is listening for connections.
     * 
     * @param serverChannel
     *            The channel that accepted the new connection.
     */
    private void notifyBound(ServerSocketChannel serverChannel) {

        for (NetworkServerStateListener listener : serverStateListeners)
            listener.bound( serverChannel );
    }

    /**
     * Notify listeners that a new connection has been accepted.
     * 
     * @param serverChannel
     *            The channel that accepted the new connection.
     * @param connectionChannel
     *            The channel over which the new connection will take place.
     */
    private void notifyAccept(ServerSocketChannel serverChannel, SocketChannel connectionChannel) {

        for (NetworkServerStateListener listener : serverStateListeners)
            listener.accepted( serverChannel, connectionChannel );
    }

    /**
     * Notify listeners that a new connection has been established.
     * 
     * @param socketChannel
     *            The channel which will now manage the new connection.
     */
    private void notifyConnect(SocketChannel socketChannel) {

        for (NetworkConnectionStateListener listener : connectionStateListeners)
            listener.connected( socketChannel );
    }

    /**
     * Notify listeners that something has been received over the network.
     * 
     * @param dataBuffer
     *            The buffer that contains the data which was received. The buffer has been flipped and is ready to be
     *            read from. You can flip it again after you read from it if you want to read the same data again.
     * @param socketChannel
     *            The channel on which the message was received.
     */
    private void notifyRead(ByteBuffer dataBuffer, SocketChannel socketChannel) {

        for (NetworkDataListener listener : dataListeners)
            listener.received( dataBuffer, socketChannel );
    }

    /**
     * Notify listeners that a network connection has been terminated.
     * 
     * @param channel
     *            The channel whose connection has been terminated.
     * @param resetByPeer
     *            <code>true</code> if the remote side closed the connection, <code>false</code> if the local side hung
     *            up.
     */
    private void notifyClose(SocketChannel channel, boolean resetByPeer) {

        for (NetworkConnectionStateListener listener : connectionStateListeners)
            listener.closed( channel, resetByPeer );
    }

    /**
     * Register a {@link NetworkDataListener} to be notified when new data arrives on the network.
     * 
     * @param listener
     *            The object that wishes to be notified.
     */
    public void registerDataListener(NetworkDataListener listener) {

        dataListeners.add( listener );
        logger.inf( "%s is now listening for network data.", listener.getClass().getSimpleName() );
    }

    /**
     * Unregister a {@link NetworkDataListener} so that it is no longer being notified data events on the network.
     * 
     * @param listener
     *            The object that no longer wishes to be notified.
     */
    public void unregisterDataListener(NetworkDataListener listener) {

        dataListeners.remove( listener );
        logger.inf( "%s is no longer listening for network data.", listener.getClass().getSimpleName() );
    }

    /**
     * Register a {@link NetworkServerStateListener} to be notified when server channel state changes occur on the
     * network.
     * 
     * @param listener
     *            The object that wishes to be notified.
     */
    public void registerServerStateListener(NetworkServerStateListener listener) {

        serverStateListeners.add( listener );
        logger.inf( "%s is now listening to network server state changes.", listener.getClass().getSimpleName() );
    }

    /**
     * Unregister a {@link NetworkServerStateListener} so it is no longer notified when server channel state changes
     * occur on the network.
     * 
     * @param listener
     *            The object that no longer wishes to be notified.
     */
    public void unregisterServerStateListener(NetworkServerStateListener listener) {

        serverStateListeners.remove( listener );
        logger.inf( "%s is no longer listening to network server state changes.", listener.getClass().getSimpleName() );
    }

    /**
     * Register a {@link NetworkConnectionStateListener} to be notified when connection state changes occur on the
     * network.
     * 
     * @param listener
     *            The object that wishes to be notified.
     */
    public void registerConnectionStateListener(NetworkConnectionStateListener listener) {

        connectionStateListeners.add( listener );
        logger.inf( "%s is now listening to network connection state changes.", listener.getClass().getSimpleName() );
    }

    /**
     * Unregister a {@link NetworkConnectionStateListener} so it is no longer notified when connection state changes
     * occur on the network.
     * 
     * @param listener
     *            The object that no longer wishes to be notified.
     */
    public void unregisterConnectionStateListener(NetworkConnectionStateListener listener) {

        connectionStateListeners.remove( listener );
        logger.inf( "%s is no longer listening to network connection state changes.",
                    listener.getClass().getSimpleName() );
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

        int errorThrottle = 10;
        Thread.currentThread().setName( "Networking" );

        bringUp();

        while (true)
            try {
                try {
                    // Wait for the networking framework to be brought up.
                    while (selector == null || !selector.isOpen())
                        synchronized (this) {
                            wait( 10 * 1000 );
                        }

                    // Tasks.
                    processHandshakes();
                    processBuffers();
                    processClosure();

                    // See if any keys in the selector are ready for I/O.
                    synchronized (selectorGuard) {}

                    // Visualize key state evolution.
                    for (SelectionKey key : selector.keys())
                        showKeyState( key );

                    // Wait for selector operations.
                    if (selector.select() <= 0)
                        continue;

                    // Visualize key state evolution.
                    for (SelectionKey key : selector.keys())
                        showKeyState( key );

                    // Perform I/O on the selected keys.
                    Iterator<SelectionKey> keysIt = selector.selectedKeys().iterator();
                    while (keysIt.hasNext()) {
                        SelectionKey key = keysIt.next();
                        keysIt.remove();

                        if (key.channel() instanceof ServerSocketChannel) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

                            if (key.isValid() && key.isAcceptable()) {
                                // Listening channel received connection request.
                                accept( serverSocketChannel );
                                showKeyState( key );
                                continue;
                            }
                        } else

                        if (key.channel() instanceof SocketChannel) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();

                            if (key.isValid() && key.isConnectable()) {
                                // Connect to a remote socket.
                                finishConnect( socketChannel );
                                showKeyState( key );
                                continue;
                            }

                            if (key.isValid() && key.isReadable()) {
                                // Read data from a socket.
                                read( socketChannel );
                                showKeyState( key );
                                continue;
                            }

                            if (key.isValid() && key.isWritable()) {
                                // Write data to a socket.
                                write( socketChannel );
                                showKeyState( key );
                                continue;
                            }
                        }
                    }

                    // Process completed successfully, reset the errorThrottle.
                    errorThrottle = 10;
                }

                catch (IOException e) {
                    logger.err( e, "Network error occurred" ).toError();

                    // TODO: Easily DoS-able.
                    if (--errorThrottle <= 0)
                        // We're receiving a mass of errors.
                        // Throttle down retries by one second longer for each new error we receive.
                        wait( -1000 * errorThrottle );
                }
            } catch (InterruptedException e) {
                bringDown();
            }
    }

    /**
     * Visualize the state of the current key.
     * 
     * @param key
     *            The key whose state must be shown.
     */
    private synchronized void showKeyState(SelectionKey key) {

        if (!lastReadyOps.containsKey( key ))
            lastReadyOps.put( key, 0 );
        if (!lastInterestOps.containsKey( key ))
            lastInterestOps.put( key, 0 );
        if (!lastHSStatus.containsKey( key ))
            lastHSStatus.put( key, HandshakeStatus.NOT_HANDSHAKING );

        String name = nameChannel( key.channel() );
        if (!key.isValid()) {
            if (lastReadyOps.get( key ) >= 0)
                logger.dbg( "[stat: %s] Closed.", name );

            lastReadyOps.put( key, -1 );
            return;
        }

        boolean keyUpdated = false, sslUpdated = false;
        SSLEngine sslEngine = sslEngines.get( key.channel() );

        int readyOps = key.readyOps();
        if (key.isValid() && key.channel().isOpen()) {
            if (keyUpdated |= lastReadyOps.get( key ) != readyOps)
                lastReadyOps.put( key, readyOps );
        } else if (keyUpdated |= lastReadyOps.get( key ) != null)
            lastReadyOps.put( key, null );
        int interestOps = key.interestOps();
        if (key.isValid() && key.channel().isOpen()) {
            if (keyUpdated |= lastInterestOps.get( key ) != interestOps)
                lastInterestOps.put( key, interestOps );
        } else if (keyUpdated |= lastInterestOps.get( key ) != null)
            lastInterestOps.put( key, null );

        HandshakeStatus handshakeStatus = null, lastHandshakeStatus = lastHSStatus.get( key );
        if (sslEngine != null) {
            handshakeStatus = sslEngine.getHandshakeStatus();
            if (sslUpdated |= !lastHandshakeStatus.equals( handshakeStatus ))
                lastHSStatus.put( key, handshakeStatus );
        }

        if (keyUpdated || sslUpdated) {
            StringBuffer out = new StringBuffer();

            if (key.isValid() && key.channel().isOpen()) {
                StringBuffer curOps = new StringBuffer();
                if (key.isReadable())
                    curOps.append( 'R' );
                if (key.isWritable())
                    curOps.append( 'W' );
                if (key.isAcceptable())
                    curOps.append( 'A' );
                if (key.isConnectable())
                    curOps.append( 'C' );

                out.append( String.format( "Can: %-3s", curOps ) );
            } else
                out.append( "D/C" );

            if (keyUpdated) {
                if (key.isValid() && key.channel().isOpen()) {
                    StringBuffer newOps = new StringBuffer();
                    if ((interestOps & SelectionKey.OP_READ) > 0)
                        newOps.append( 'R' );
                    if ((interestOps & SelectionKey.OP_WRITE) > 0)
                        newOps.append( 'W' );
                    if ((interestOps & SelectionKey.OP_ACCEPT) > 0)
                        newOps.append( 'A' );
                    if ((interestOps & SelectionKey.OP_CONNECT) > 0)
                        newOps.append( 'C' );

                    out.append( String.format( " Want: %-3s", newOps ) );
                }
            } else
                out.append( "       " );

            if (sslEngine != null)
                out.append( " SSL: " ).append( handshakeStatus );

            logger.dbg( "[stat: %s] %s", name, out );
        }
    }

    /**
     * @param channel
     *            The channel to describe.
     * 
     * @return A (short) string representation of the connection/socket on the given channel.
     */
    private String nameChannel(SelectableChannel channel) {

        if (channel instanceof SocketChannel) {
            SocketChannel socketChannel = (SocketChannel) channel;
            if (socketChannel.socket().isConnected())
                return String.valueOf( socketChannel.socket().getInetAddress().getHostAddress() );
            return "Not Connected";
        } else if (channel instanceof ServerSocketChannel) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) channel;
            if (serverSocketChannel.socket().isBound())
                return String.valueOf( serverSocketChannel.socket().getInetAddress().getHostAddress() );
            return "Not Bound";
        }

        return String.valueOf( channel );
    }

    /**
     * Render a string representation of the given buffer's counters.
     */
    private String renderBuffer(ByteBuffer buf) {

        float curStep = 0, length = 20;
        StringBuffer bufString = new StringBuffer( (int) length + 2 ).append( '[' );

        for (int i = 0; i < buf.capacity(); ++i) {
            float lastStep = curStep;
            curStep += length / buf.capacity();
            if (Math.ceil( curStep ) == Math.ceil( lastStep ) || i == buf.capacity() - 1)
                continue;

            if (i < buf.position())
                bufString.append( '|' );
            else if (i < buf.limit())
                bufString.append( '-' );
            else
                bufString.append( ' ' );
        }

        bufString.append( "] " ).append( Integer.toString( buf.position() ) );
        bufString.append( '/' ).append( Integer.toString( buf.limit() ) );
        if (buf.limit() != buf.capacity())
            bufString.append( '|' ).append( Integer.toString( buf.capacity() ) );

        return bufString.toString();
    }
}
