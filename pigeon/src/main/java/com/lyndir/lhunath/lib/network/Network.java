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

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Charsets;
import com.lyndir.lhunath.lib.system.logging.Logger;
import java.io.IOException;
import java.net.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;


/**
 * <h2>{@link Network}<br> <sub>A non-blocking single-threaded TCP network layer with SSL/TLS support.</sub></h2>
 * <p/>
 * <p> TODO </p>
 * <p/>
 * <p> <i>Jun 23, 2009</i> </p>
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

    private Thread networkThread;

    private final List<NetworkDataListener>            dataListeners;
    private final List<NetworkServerStateListener>     serverStateListeners;
    private final List<NetworkConnectionStateListener> connectionStateListeners;

    // Collections that are modified by calling threads and the networking thread.
    private final Map<SelectableChannel, SSLEngine> sslEngines        = Collections.synchronizedMap(
            new HashMap<SelectableChannel, SSLEngine>() );
    private final Map<SocketChannel, ByteBuffer>    writeQueueBuffers = Collections.synchronizedMap(
            new HashMap<SocketChannel, ByteBuffer>() );
    private final Map<SocketChannel, Object>        writeQueueLocks   = Collections.synchronizedMap( new HashMap<SocketChannel, Object>() );

    protected final Object selectorGuard = new Object();
    private Selector selector; // TODO: Synchronize all access to/of the selector.

    // Collections that are only modified by the networking thread.
    private final Map<SocketChannel, ByteBuffer> readBuffers    = new HashMap<SocketChannel, ByteBuffer>();
    private final Map<SocketChannel, ByteBuffer> writeBuffers   = new HashMap<SocketChannel, ByteBuffer>();
    private final Map<SocketChannel, Boolean>    closedChannels = new HashMap<SocketChannel, Boolean>();
    private boolean running;

    /**
     * Create a new {@link Network} instance.
     */
    public Network() {

        // Collections that are only modified by calling threads.
        dataListeners = new LinkedList<NetworkDataListener>();
        serverStateListeners = new LinkedList<NetworkServerStateListener>();
        connectionStateListeners = new LinkedList<NetworkConnectionStateListener>();
    }

    /**
     * Start this network by executing a networking thread. When a networking thread begins, it invokes {@link #bringUp()}.
     */
    public void startThread() {

        checkState( networkThread == null || !networkThread.isAlive(), "Network thread is already running." );

        networkThread = new Thread( this );
        networkThread.start();
    }

    /**
     * Stop this network's thread. This will cause {@link #bringDown()} if the network is still up.
     */
    public void stopThread() {

        if (isUp())
            bringDown();

        if (!isThreadAlive())
            return;

        running = false;
        networkThread.interrupt();
    }

    /**
     * @return <code>true</code> if the network thread has been started and is currently running.
     */
    public boolean isThreadAlive() {

        return networkThread != null && networkThread.isAlive();
    }

    /**
     * Bring up the networking framework.
     * <p/>
     * This initializes the networking {@link Selector} making it possible to start network operations. It does not start the networking
     * thread. See {@link #startThread()} to start the network and execute a networking thread for it. This method is mostly helpful for
     * manually bringing the network in an existing thread down and up again.
     */
    public synchronized void bringUp() {

        if (!isThreadAlive())
            startThread();

        if (isUp())
            // Already up.
            return;

        try {
            selector = Selector.open();
            notifyAll();
        }

        catch (IOException e) {
            throw new RuntimeException( "Couldn't open a new network selector.", e );
        }

        logger.inf( "Networking framework is up." );
    }

    /**
     * Bring the networking framework down.
     * <p/>
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
            throw new IllegalStateException( "Couldn't close the network selector.", e );
        }

        logger.inf( "Networking framework is down." );
    }

    /**
     * @return <code>true</code>: The network is ready for network event processing (binding, accepting, connecting, reading and writing).
     */
    public synchronized boolean isUp() {

        return selector != null && selector.isOpen();
    }

    /**
     * Bind a socket on the wildcard address at a specified port and listen for connections.
     *
     * @param port      The local port to bind on.
     * @param sslEngine If you want to use SSL/TLS to encrypt the data sent over connections established from this socket, specify an SSL
     *                  engine that has been initialized to for communication with remote clients. If you just want plain-text
     *                  communication, pass <code>null</code> here.
     *
     * @return The channel that will be listening for connections.
     *
     * @throws IOException If the socket is already bound, the bind address is unavailable or the operation failed or was denied for some
     *                     other reason.
     */
    public ServerSocketChannel bind(final int port, final SSLEngine sslEngine)
            throws IOException {

        return bind( new InetSocketAddress( port ), sslEngine );
    }

    /**
     * Bind a socket on the interface defined by the given address at a specified port and listen for connections.
     *
     * @param address   The address of the interface to bind on.
     * @param port      The local port to bind on.
     * @param sslEngine If you want to use SSL/TLS to encrypt the data sent over connections established from this socket, specify an SSL
     *                  engine that has been initialized to for communication with remote clients. If you just want plain-text
     *                  communication, pass <code>null</code> here.
     *
     * @return The channel that will be listening for connections.
     *
     * @throws IOException If the socket is already bound, the bind address is unavailable or the operation failed or was denied for some
     *                     other reason.
     */
    public ServerSocketChannel bind(final InetAddress address, final int port, final SSLEngine sslEngine)
            throws IOException {

        return bind( new InetSocketAddress( address, port ), sslEngine );
    }

    /**
     * Bind a socket on the interface defined by the given hostname at a specified port and listen for connections.
     *
     * @param hostname  The hostname that resolves to the interface address of the interface to bind on.
     * @param port      The local port to bind on.
     * @param sslEngine If you want to use SSL/TLS to encrypt the data sent over connections established from this socket, specify an SSL
     *                  engine that has been initialized to for communication with remote clients. If you just want plain-text
     *                  communication, pass <code>null</code> here.
     *
     * @return The channel that will be listening for connections.
     *
     * @throws IOException If the socket is already bound, the bind address is unavailable or the operation failed or was denied for some
     *                     other reason.
     */
    public ServerSocketChannel bind(final String hostname, final int port, final SSLEngine sslEngine)
            throws IOException {

        return bind( new InetSocketAddress( hostname, port ), sslEngine );
    }

    /**
     * Bind a socket on the wildcard address at a specified port and listen for connections.
     *
     * @param socketAddress The socket address that defines the interface and port to bind the socket on.
     * @param sslEngine     If you want to use SSL/TLS to encrypt the data sent over connections established from this socket, specify an
     *                      SSL engine that has been initialized to for communication with remote clients. If you just want plain-text
     *                      communication, pass <code>null</code> here.
     *
     * @return The channel that will be listening for connections.
     *
     * @throws IOException If the socket is already bound, the bind address is unavailable or the operation failed or was denied for some
     *                     other reason.
     */
    public ServerSocketChannel bind(final SocketAddress socketAddress, final SSLEngine sslEngine)
            throws IOException {

        checkState( selector != null && selector.isOpen(), "The networking framework is not (yet) up." );

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

        logger.inf(
                "[====: %s] Bound.", //
                nameChannel( serverChannel ) );
        notifyBound( serverChannel );

        return serverChannel;
    }

    /**
     * Accept a pending connection.
     *
     * @param serverChannel The channel where a connection can be accepted.
     *
     * @throws IOException If the channel is in an unexpected state or the connection couldn't be accepted for some other reason.
     */
    private void accept(final ServerSocketChannel serverChannel)
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

        logger.inf(
                "[====: %s] Accepted a new connection to: %s", //
                nameChannel( serverChannel ), connectionChannel.socket().getInetAddress() );
        notifyAccept( serverChannel, connectionChannel );
    }

    /**
     * Make a connection to the given destination.
     *
     * @param hostname  The name that resolves to the address that defines the destination host to connect to.
     * @param port      The port of the remote listening socket on the given host to connect to.
     * @param sslEngine If you want to use SSL/TLS to encrypt the data sent over this connection, specify an SSL engine that has been
     *                  initialized to for communication with the remote server. If you just want plain-text communication, pass
     *                  <code>null</code> here.
     *
     * @return The channel on which the connection runs.
     *
     * @throws IOException If a channel couldn't be assigned and configured or a connection couldn't be initiated (or completed in blocking
     *                     mode).
     */
    public SocketChannel connect(final String hostname, final int port, final SSLEngine sslEngine)
            throws IOException {

        return connect( new InetSocketAddress( hostname, port ), sslEngine );
    }

    /**
     * Make a connection to the given destination.
     *
     * @param hostAddress The address that defines the destination host to connect to.
     * @param port        The port of the remote listening socket on the given host to connect to.
     * @param sslEngine   If you want to use SSL/TLS to encrypt the data sent over this connection, specify an SSL engine that has been
     *                    initialized to for communication with the remote server. If you just want plain-text communication, pass
     *                    <code>null</code> here.
     *
     * @return The channel on which the connection runs.
     *
     * @throws IOException If a channel couldn't be assigned and configured or a connection couldn't be initiated (or completed in blocking
     *                     mode).
     */
    public SocketChannel connect(final InetAddress hostAddress, final int port, final SSLEngine sslEngine)
            throws IOException {

        return connect( new InetSocketAddress( hostAddress, port ), sslEngine );
    }

    /**
     * Make a connection to the given destination.
     *
     * @param socketAddress The address that defines the destination host and port to connect to.
     * @param sslEngine     If you want to use SSL/TLS to encrypt the data sent over this connection, specify an SSL engine that has been
     *                      initialized to for communication with the remote server. If you just want plain-text communication, pass
     *                      <code>null</code> here.
     *
     * @return The channel on which the connection runs.
     *
     * @throws IOException If a channel couldn't be assigned and configured or a connection couldn't be initiated (or completed in blocking
     *                     mode).
     */
    public SocketChannel connect(final InetSocketAddress socketAddress, final SSLEngine sslEngine)
            throws IOException {

        checkState( selector != null && selector.isOpen(), "The networking framework is not (yet) up." );

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

        logger.inf(
                "[>>>>: %s] Connecting to: %s", //
                nameChannel( connectionChannel ), socketAddress );
        if (connectionChannel.connect( socketAddress ))
            finishConnect( connectionChannel );
        else
            setOps( connectionChannel, SelectionKey.OP_CONNECT );

        return connectionChannel;
    }

    /**
     * Finish a connection initiated by {@link #connect(InetSocketAddress, SSLEngine)}.
     *
     * @param socketChannel The channel on which the connection happening.
     *
     * @throws IOException If the connection couldn't be established. (Channel in an unexpected state or connection timeout, ..)
     */
    private void finishConnect(final SocketChannel socketChannel)
            throws IOException {

        if (!socketChannel.finishConnect())
            // Not yet done connecting.
            return;

        // Connection completed, see what the server has to say.
        setOps( socketChannel, SelectionKey.OP_READ );

        logger.inf(
                "[<<<<: %s] Connected.", //
                nameChannel( socketChannel ) );
        notifyConnect( socketChannel );
    }

    /**
     * Read available chatter from the given socket and {@link #notifyRead(ByteBuffer, SocketChannel)} it.
     *
     * @param socketChannel The socket to read input from.
     *
     * @throws IOException If the channel socket couldn't be read from.
     */
    private void read(final SocketChannel socketChannel)
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
            logger.dbg(
                    "[<<<<: %s] Read %d bytes into: %s", //
                    nameChannel( socketChannel ), bytesRead, renderBuffer( readBuffer ) );
            readBuffer.flip();

            ByteBuffer dataBuffer = ByteBuffer.allocate( READ_BUFFER );
            dataBuffer = toApplicationData( readBuffer, socketChannel, dataBuffer );
            if (dataBuffer.remaining() == 0)
                // The network data did not contain any application data.
                return;

            // Visualize incoming (plain-text) data.
            logger.inf(
                    "[<<<<: %s] Received (plain): %s", //
                    nameChannel( socketChannel ), Charsets.UTF_8.decode( dataBuffer ) );
            dataBuffer.flip();

            // Pass incoming (plain-text) data to the application.
            notifyRead( dataBuffer, socketChannel );
        } else if (bytesRead < 0)
            // Socket connection was terminated by the client.
            closedChannels.put( socketChannel, true );
    }

    /**
     * Convert a buffer of network data into application data.
     * <p/>
     * <p> <b>Use the return value for processing, not the original data buffer!</b><br> The buffer might have been reallocated in which
     * case the original buffer is obsolete. </p>
     *
     * @param readBuffer    The buffer that contains the network data, ready to be read (position set to zero, limit set to the end of the
     *                      received network data).
     * @param socketChannel The channel over which the network data was received.
     * @param dataBuffer    The buffer of application data, ready to be written/appended to. After this operation it will be ready to be
     *                      read (position set to zero, limit set to the end of the application data).
     *
     * @return The (possibly new) dataBuffer.
     *
     * @throws IOException If the read buffer couldn't be unwrapped by the SSL Engine.
     */
    private ByteBuffer toApplicationData(final ByteBuffer readBuffer, final SocketChannel socketChannel, final ByteBuffer dataBuffer)
            throws IOException {

        ByteBuffer newDataBuffer = dataBuffer;

        SSLEngine sslEngine = sslEngines.get( socketChannel );
        if (sslEngine != null) {
            // SSL/TLS: Decrypt network data into application data (or into nothingness!).

            while (true) {

                // Try to decrypt readBuffer into dataBuffer.
                SSLEngineResult sslEngineResult = sslEngine.unwrap( readBuffer, newDataBuffer );

                switch (sslEngineResult.getStatus()) {
                    case BUFFER_OVERFLOW:
                        // Data buffer overflow, make it bigger and try again.
                        logger.dbg(
                                "[<<<<: %s] SSL %s: dataBuffer%s + %d]", //
                                nameChannel( socketChannel ), sslEngineResult.getStatus(), renderBuffer( newDataBuffer ), READ_BUFFER );
                        ByteBuffer resizedDataBuffer = ByteBuffer.allocate( newDataBuffer.capacity() + READ_BUFFER );
                        newDataBuffer.flip();
                        newDataBuffer = resizedDataBuffer.put( newDataBuffer );

                        // Retry
                        continue;

                    case BUFFER_UNDERFLOW:
                        // Not enough network data collected for a whole SSL/TLS packet.
                        logger.dbg(
                                "[<<<<: %s] SSL %s: need_src: readBuffer%s", //
                                nameChannel( socketChannel ), sslEngineResult.getStatus(), renderBuffer( readBuffer ) );
                        break;

                    case CLOSED:
                        // SSL Engine indicates it is closed or just closed itself.
                        logger.dbg(
                                "[<<<: %s] SSL: %s", //
                                nameChannel( socketChannel ), sslEngineResult.getStatus() );
                        closedChannels.put( socketChannel, true );
                        break;

                    case OK:
                        logger.dbg(
                                "[>>>>: %s] SSL %s - %s: Produced %d bytes application data", //
                                nameChannel( socketChannel ), sslEngineResult.getStatus(), sslEngineResult.getHandshakeStatus(),
                                sslEngineResult.bytesProduced() );
                        break;
                }

                // Don't retry.
                break;
            }

            // Make the application data available and add new network data after what's left unprocessed.
            newDataBuffer.flip();
            readBuffer.compact();

            return newDataBuffer;
        }

        // Plain Text: Copy network data to application data and prepare both buffers for their next operations.
        if (newDataBuffer.remaining() < readBuffer.remaining()) {
            // Not enough space in the dataBuffer for the readBuffer's data; make it bigger.
            ByteBuffer resizedDataBuffer = ByteBuffer.allocate( newDataBuffer.position() + readBuffer.remaining() );

            newDataBuffer.flip();
            newDataBuffer = resizedDataBuffer.put( newDataBuffer );
        }

        newDataBuffer.put( readBuffer ).flip();
        readBuffer.limit( readBuffer.capacity() ).rewind();

        return newDataBuffer;
    }

    /**
     * Stuff can be written to the channel.
     *
     * @param socketChannel The channel to write data to.
     *
     * @throws IOException If the channel socket couldn't be written to.
     */
    private void write(final SocketChannel socketChannel)
            throws IOException {

        // Lock this connection's write queue.
        synchronized (writeQueueLocks.get( socketChannel )) {
            if (!socketChannel.isOpen() || !socketChannel.isConnected() || socketChannel.isConnectionPending() || socketChannel.socket()
                                                                                                                               .isOutputShutdown())
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
                logger.dbg(
                        "[>>>>: %s] Wrote %d bytes of: writeBuffer%s", //
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
     * <p/>
     * <p> <b>Use the return value for writing, not the original write buffer!</b><br> The buffer might have been reallocated in which case
     * the original buffer is obsolete. </p>
     *
     * @param dataBuffer    The buffer that contains the application data, ready to be written (position set to zero, limit set to the end
     *                      of the application data).
     * @param socketChannel The channel over which the network data will be sent.
     * @param writeBuffer   The buffer that contains the connection's network data to be written, ready to be written/appended to (position
     *                      set to the end of the network data). At the end of this operation, this buffer will be ready to be
     *                      read/sent/written out (position set to zero, limit set to the end of the network data).
     *
     * @return The (possibly new) writeBuffer.
     *
     * @throws IOException If the data buffer couldn't be wrapped by the SSL engine.
     */
    private ByteBuffer fromApplicationData(final ByteBuffer dataBuffer, final SocketChannel socketChannel, final ByteBuffer writeBuffer)
            throws IOException {

        ByteBuffer newWriteBuffer = writeBuffer;

        SSLEngine sslEngine = sslEngines.get( socketChannel );
        if (sslEngine != null) {
            // SSL/TLS: Encrypt network data from application data (or from nothingness!).

            while (true) {

                // Try to encrypt readBuffer into dataBuffer.
                SSLEngineResult sslEngineResult = sslEngine.wrap( dataBuffer, newWriteBuffer );

                switch (sslEngineResult.getStatus()) {
                    case BUFFER_OVERFLOW:
                        // Data buffer overflow, make it bigger and try again.
                        logger.dbg(
                                "[>>>>: %s] SSL %s: writeBuffer%s + %d", //
                                nameChannel( socketChannel ), sslEngineResult.getStatus(), renderBuffer( newWriteBuffer ), WRITE_BUFFER );
                        ByteBuffer resizedWriteBuffer = ByteBuffer.allocate( newWriteBuffer.capacity() + WRITE_BUFFER );
                        newWriteBuffer.flip();
                        newWriteBuffer = resizedWriteBuffer.put( newWriteBuffer );

                        // Retry
                        continue;

                    case BUFFER_UNDERFLOW:
                        // Not enough application data collected for a whole SSL/TLS packet.
                        logger.dbg(
                                "[>>>>: %s] SSL %s: need_src: dataBuffer%s", //
                                nameChannel( socketChannel ), sslEngineResult.getStatus(), renderBuffer( dataBuffer ) );
                        break;

                    case CLOSED:
                        // SSL Engine indicates it is closed or just closed itself.
                        logger.dbg(
                                "[>>>>: %s] SSL %s", //
                                nameChannel( socketChannel ), sslEngineResult.getStatus() );
                        closedChannels.put( socketChannel, false );
                        break;

                    case OK:
                        logger.dbg(
                                "[>>>>: %s] SSL %s - %s: Consumed %d bytes application data", //
                                nameChannel( socketChannel ), sslEngineResult.getStatus(), sslEngineResult.getHandshakeStatus(),
                                sslEngineResult.bytesConsumed() );
                        break;
                }

                // Don't retry.
                break;
            }

            // Make the application data available and add new network data after what's left unprocessed.
            newWriteBuffer.flip();
            dataBuffer.compact();

            return newWriteBuffer;
        }

        // Plain Text: Copy application data to network data and prepare both buffers for their next operations.
        if (newWriteBuffer.remaining() < dataBuffer.remaining()) {
            // Not enough space in the writeBuffer for the dataBuffer's data; make it bigger.
            ByteBuffer resizedWriteBuffer = ByteBuffer.allocate( newWriteBuffer.position() + dataBuffer.remaining() );

            newWriteBuffer.flip();
            newWriteBuffer = resizedWriteBuffer.put( newWriteBuffer );
        }

        newWriteBuffer.put( dataBuffer ).flip();
        dataBuffer.limit( dataBuffer.capacity() ).rewind();

        return newWriteBuffer;
    }

    /**
     * Remove the channel from I/O operation scheduling. It will be closed.
     *
     * @param socketChannel The channel that needs to be closed.
     * @param resetByPeer   <code>true</code> if the channel was closed by the remote party (inbound will be closed). <code>false</code> if
     *                      the channel is closed by us (outbound will be closed).
     *
     * @throws IOException When the channel couldn't be closed cleanly.
     */
    private void closeChannel(final SocketChannel socketChannel, final boolean resetByPeer)
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
                logger.inf(
                        "[<<<<: %s] Closed connection (reset by peer).", //
                        nameChannel( socketChannel ) );
            else
                logger.inf(
                        "[>>>>: %s] Closed connection (terminated).", //
                        nameChannel( socketChannel ) );

            notifyClose( socketChannel, resetByPeer );
        }
    }

    /**
     * Queue a message to be sent to the given destination. The message will be added to the destination's write queue.
     * <p/>
     * <p> After this process, the application's data buffer's position will be set right after the bytes that have been queued on the
     * network. This is guaranteed to be the buffer's limit (eg. all data will be queued). </p>
     * <p/>
     * The connection's queue buffer's position will be right after the newly added bytes.
     *
     * @param dataBuffer    A byte buffer that holds the bytes to dispatch. Make sure that the buffer is set up read for reading (put the
     *                      position at the start of the data to read and the limit at the end). The buffer will be flipped so its position
     *                      should be right after the bytes to write. Use <code>null</code> to request a connection shutdown.
     * @param socketChannel The channel over which to send the message.
     *
     * @throws ClosedChannelException The given channel is closed.
     */
    public void queue(final ByteBuffer dataBuffer, final SocketChannel socketChannel)
            throws ClosedChannelException {

        checkArgument(
                socketChannel.keyFor( selector ) != null,
                "Tried to queue a message for a destination (%s) that is not managed by our selector.", nameChannel( socketChannel ) );

        ByteBuffer writeQueueBuffer;
        synchronized (writeQueueBuffers) {
            writeQueueBuffer = writeQueueBuffers.get( socketChannel );

            // Obtain or create the data buffer for the connection.
            if (writeQueueBuffer == null)
                // No writeQueueBuffer yet for this connection, allocate one.
                writeQueueBuffers.put(
                        socketChannel, writeQueueBuffer = ByteBuffer.allocate( Math.max( dataBuffer.remaining(), WRITE_QUEUE_BUFFER ) ) );
            else if (writeQueueBuffer.remaining() < dataBuffer.remaining())
                // Not enough space left in the writeQueueBuffer for the application data, make it bigger.
                synchronized (writeQueueLocks.get( socketChannel )) {
                    ByteBuffer newWriteQueueBuffer = ByteBuffer.allocate(
                            Math.max(
                                    writeQueueBuffer.position() + dataBuffer.remaining(),
                                    writeQueueBuffer.capacity() + WRITE_QUEUE_BUFFER ) );
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
     * <p/>
     * <p> <b>NOTE: You should always use this method and never close the socket or channel directly!</b><br> This method makes sure that
     * closure is handled gracefully according to the wishes of the transport and optional encryption protocols. </p>
     * <p/>
     * <p> Non-{@link SocketChannel}s (such as the {@link ServerSocketChannel}) may be closed directly. </p>
     *
     * @param socketChannel The channel that is no longer interested in sending data.
     *
     * @throws IOException The channel could not be closed gracefully.
     */
    public void close(final SocketChannel socketChannel)
            throws IOException {

        synchronized (writeQueueLocks.get( socketChannel )) {
            SSLEngine sslEngine = sslEngines.get( socketChannel );
            if (sslEngine != null) {
                sslEngine.closeOutbound();
                addOps( socketChannel, SelectionKey.OP_WRITE );
            } else
                socketChannel.close();
        }
    }

    /**
     * Retrieves the operations that the given channel is interested in.
     *
     * @param channel The channel whose operations are requested.
     *
     * @return The channel's interested operations.
     */
    private int getOps(final SelectableChannel channel) {

        SelectionKey key = channel.keyFor( selector );
        if (key == null || !key.isValid())
            return 0;

        return key.interestOps();
    }

    /**
     * Enable the given operation(s).
     *
     * @param channel The channel whose operations must be modified.
     * @param addOps  The operations that must be enabled.
     *
     * @throws ClosedChannelException If applying ops on a closed channel (that is not yet registered with the network selector).
     */
    private void addOps(final SelectableChannel channel, final int... addOps)
            throws ClosedChannelException {

        if (addOps.length == 0)
            // Nothing to add.
            return;

        // OR all addOps together.
        int allAddOps = 0;
        for (final int addOp : addOps)
            allAddOps |= addOp;

        // Apply the addOps.
        setOps( channel, getOps( channel ) | allAddOps );
    }

    /**
     * Disable the given operation(s).
     *
     * @param channel The channel whose operations must be modified.
     * @param delOps  The operations that must be disabled.
     *
     * @throws ClosedChannelException If applying ops on a closed channel (that is not yet registered with the network selector).
     */
    private void delOps(final SelectableChannel channel, final int... delOps)
            throws ClosedChannelException {

        if (delOps.length == 0)
            // Nothing to add.
            return;

        // OR all addOps together.
        int allDelOps = 0;
        for (final int delOp : delOps)
            allDelOps |= delOp;

        // Apply the addOps.
        setOps( channel, getOps( channel ) & ~allDelOps );
    }

    /**
     * Register the given operations with a channel. It is guaranteed that only operations that are valid for the type of channel will be
     * applied to it. The operations will be queued and applied to the channel as soon as it is available.
     *
     * @param channel The channel whose interest ops should be modified.
     * @param newOps  The ops that should be set on the channel.
     *
     * @throws ClosedChannelException If applying ops on a closed channel (that is not yet registered with the network selector).
     */
    private synchronized void setOps(final SelectableChannel channel, final int... newOps)
            throws ClosedChannelException {

        // OR all newOps together.
        int interestOps = 0;
        for (final int newOp : newOps)
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
     * Determine the interested operations for SSL enabled channels. The SSL protocol can request read or write operations depending on
     * what
     * it needs to complete/initiate a handshake.
     *
     * @throws ClosedChannelException If applying ops on a closed channel (that is not yet registered with the network selector).
     */
    private void processHandshakes()
            throws ClosedChannelException {

        for (final Map.Entry<SelectableChannel, SSLEngine> ssl : sslEngines.entrySet()) {
            SelectableChannel channel = ssl.getKey();
            SSLEngine engine = ssl.getValue();

            while (true) {
                if (!channel.isOpen())
                    // There's no point, the connection is already gone.
                    break;

                HandshakeStatus handshakeStatus = engine.getHandshakeStatus();
                switch (handshakeStatus) {
                    case NEED_TASK:
                        // A lengthy task must be performed.
                        final Runnable delegatedTask = engine.getDelegatedTask();
                        if (delegatedTask != null) {
                            logger.dbg(
                                    "[====: %s] SSL %s: Starting a task thread.", //
                                    nameChannel( channel ), handshakeStatus, delegatedTask );

                            new Thread(
                                    new Runnable() {

                                        @Override
                                        public void run() {

                                            delegatedTask.run();
                                            selector.wakeup();
                                        }
                                    } ).start();
                        } else {
                            logger.dbg(
                                    "[====: %s] SSL %s: Task needed but none offered.", //
                                    nameChannel( channel ), handshakeStatus, delegatedTask );
                            break;
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
     * @throws IOException If any I/O errors occur during reading from and writing to channel sockets or performing SSL wrapping.
     */
    private void processBuffers()
            throws IOException {

        // Read buffers
        for (final Map.Entry<SocketChannel, ByteBuffer> entry : readBuffers.entrySet()) {
            SocketChannel socketChannel = entry.getKey();
            ByteBuffer readBuffer = entry.getValue();

            if (readBuffer.position() > 0) {
                logger.dbg(
                        "[rbuf: %s] %s", //
                        nameChannel( socketChannel ), renderBuffer( readBuffer ) );
                read( socketChannel );
            }
        }

        // Write buffers
        for (final Map.Entry<SocketChannel, ByteBuffer> entry : writeBuffers.entrySet()) {
            SocketChannel socketChannel = entry.getKey();
            ByteBuffer writeBuffer = entry.getValue();

            if (writeBuffer.position() > 0) {
                logger.dbg(
                        "[wbuf: %s] %s", //
                        nameChannel( socketChannel ), renderBuffer( writeBuffer ) );
                addOps( socketChannel, SelectionKey.OP_WRITE );
            }
        }

        // Write queued application data
        for (final Map.Entry<SocketChannel, ByteBuffer> entry : writeQueueBuffers.entrySet()) {
            SocketChannel socketChannel = entry.getKey();
            ByteBuffer writeQueueBuffer = entry.getValue();

            if (writeQueueBuffer.position() > 0 && socketChannel.isOpen())
                addOps( socketChannel, SelectionKey.OP_WRITE );
        }

        // Let each channel that has stuff to write send it out.
        for (final SocketChannel socketChannel : writeQueueBuffers.keySet())
            write( socketChannel );
    }

    /**
     * Close all channels that requested closure.
     *
     * @throws IOException When a channel couldn't be closed cleanly.  The operation was aborted and subsequent channels haven't been
     *                     closed.
     */
    private void processClosure()
            throws IOException {

        for (final Map.Entry<SocketChannel, Boolean> entry : closedChannels.entrySet()) {
            SocketChannel socketChannel = entry.getKey();
            boolean resetByPeer = entry.getValue();

            closeChannel( socketChannel, resetByPeer );
        }
    }

    /**
     * Notify listeners that a new server channel is listening for connections.
     *
     * @param serverChannel The channel that accepted the new connection.
     */
    private void notifyBound(final ServerSocketChannel serverChannel) {

        for (final NetworkServerStateListener listener : serverStateListeners)
            listener.bound( serverChannel );
    }

    /**
     * Notify listeners that a new connection has been accepted.
     *
     * @param serverChannel     The channel that accepted the new connection.
     * @param connectionChannel The channel over which the new connection will take place.
     */
    private void notifyAccept(final ServerSocketChannel serverChannel, final SocketChannel connectionChannel) {

        for (final NetworkServerStateListener listener : serverStateListeners)
            listener.accepted( serverChannel, connectionChannel );
    }

    /**
     * Notify listeners that a new connection has been established.
     *
     * @param socketChannel The channel which will now manage the new connection.
     */
    private void notifyConnect(final SocketChannel socketChannel) {

        for (final NetworkConnectionStateListener listener : connectionStateListeners)
            listener.connected( socketChannel );
    }

    /**
     * Notify listeners that something has been received over the network.
     *
     * @param dataBuffer    The buffer that contains the data which was received. The buffer has been flipped and is ready to be read from.
     *                      You can flip it again after you read from it if you want to read the same data again.
     * @param socketChannel The channel on which the message was received.
     */
    private void notifyRead(final ByteBuffer dataBuffer, final SocketChannel socketChannel) {

        for (final NetworkDataListener listener : dataListeners)
            listener.received( dataBuffer, socketChannel );
    }

    /**
     * Notify listeners that a network connection has been terminated.
     *
     * @param channel     The channel whose connection has been terminated.
     * @param resetByPeer <code>true</code> if the remote side closed the connection, <code>false</code> if the local side hung up.
     */
    private void notifyClose(final SocketChannel channel, final boolean resetByPeer) {

        for (final NetworkConnectionStateListener listener : connectionStateListeners)
            listener.closed( channel, resetByPeer );
    }

    /**
     * Register a {@link NetworkDataListener} to be notified when new data arrives on the network.
     *
     * @param listener The object that wishes to be notified.
     */
    public void registerDataListener(final NetworkDataListener listener) {

        dataListeners.add( listener );
        logger.inf( "%s is now listening for network data.", listener.getClass().getSimpleName() );
    }

    /**
     * Unregister a {@link NetworkDataListener} so that it is no longer being notified data events on the network.
     *
     * @param listener The object that no longer wishes to be notified.
     */
    public void unregisterDataListener(final NetworkDataListener listener) {

        dataListeners.remove( listener );
        logger.inf( "%s is no longer listening for network data.", listener.getClass().getSimpleName() );
    }

    /**
     * Register a {@link NetworkServerStateListener} to be notified when server channel state changes occur on the network.
     *
     * @param listener The object that wishes to be notified.
     */
    public void registerServerStateListener(final NetworkServerStateListener listener) {

        serverStateListeners.add( listener );
        logger.inf( "%s is now listening to network server state changes.", listener.getClass().getSimpleName() );
    }

    /**
     * Unregister a {@link NetworkServerStateListener} so it is no longer notified when server channel state changes occur on the network.
     *
     * @param listener The object that no longer wishes to be notified.
     */
    public void unregisterServerStateListener(final NetworkServerStateListener listener) {

        serverStateListeners.remove( listener );
        logger.inf( "%s is no longer listening to network server state changes.", listener.getClass().getSimpleName() );
    }

    /**
     * Register a {@link NetworkConnectionStateListener} to be notified when connection state changes occur on the network.
     *
     * @param listener The object that wishes to be notified.
     */
    public void registerConnectionStateListener(final NetworkConnectionStateListener listener) {

        connectionStateListeners.add( listener );
        logger.inf( "%s is now listening to network connection state changes.", listener.getClass().getSimpleName() );
    }

    /**
     * Unregister a {@link NetworkConnectionStateListener} so it is no longer notified when connection state changes occur on the network.
     *
     * @param listener The object that no longer wishes to be notified.
     */
    public void unregisterConnectionStateListener(final NetworkConnectionStateListener listener) {

        connectionStateListeners.remove( listener );
        logger.inf( "%s is no longer listening to network connection state changes.", listener.getClass().getSimpleName() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        running = true;
        int errorThrottle = 10;
        Thread.currentThread().setName( "Networking" );

        bringUp();

        while (running)
            try {
                try {
                    // Wait for the networking framework to be brought up.
                    while (selector == null || !selector.isOpen())
                        synchronized (this) {
                            wait( 10 * 1000L );
                        }

                    // Tasks.
                    processHandshakes();
                    processBuffers();
                    processClosure();

                    // See if any keys in the selector are ready for I/O.
                    synchronized (selectorGuard) {
                    }

                    // Visualize key state evolution.
                    for (final SelectionKey key : selector.keys())
                        showKeyState( key );

                    // Wait for selector operations.
                    if (selector.select() <= 0)
                        continue;

                    // Visualize key state evolution.
                    for (final SelectionKey key : selector.keys())
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
                        } else if (key.channel() instanceof SocketChannel) {
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
                    logger.err( e, "Network error occurred" );

                    // TODO: Easily DoS-able.
                    if (--errorThrottle <= 0)
                        // We're receiving a mass of errors.
                        // Throttle down retries by one second longer for each new error we receive.
                        synchronized (this) {
                            wait( -1000L * errorThrottle );
                        }
                }
            }
            catch (InterruptedException e) {
                logger.wrn( e, "Operation was interrupted." );
            }
            catch (Throwable t) {
                logger.err( t, "Caught unexpected throwable to save the network thread." );
            }
    }

    /**
     * Visualize the state of the current key.
     *
     * @param key The key whose state must be shown.
     */
    private synchronized void showKeyState(final SelectionKey key) {

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

        HandshakeStatus handshakeStatus = null;
        HandshakeStatus lastHandshakeStatus = lastHSStatus.get( key );
        if (sslEngine != null) {
            handshakeStatus = sslEngine.getHandshakeStatus();
            if (sslUpdated |= lastHandshakeStatus != handshakeStatus)
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

            if (handshakeStatus != null)
                out.append( " SSL: " ).append( handshakeStatus );

            logger.dbg( "[stat: %s] %s", name, out );
        }
    }

    /**
     * @param channel The channel to describe.
     *
     * @return A (short) string representation of the connection/socket on the given channel.
     */
    private static String nameChannel(final SelectableChannel channel) {

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
     * @param buf The buffer to represent.
     *
     * @return A string representation of the given buffer's counters.
     */
    private static String renderBuffer(final Buffer buf) {

        float curStep = 0;
        float length = 20;
        StringBuilder bufString = new StringBuilder( (int) length + 2 ).append( '[' );

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
