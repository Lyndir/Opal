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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <i>{@link SslFactory} - Factory for creating {@link Protocol} objects that can be used by Jakarta's HttpClient for
 * establishing https communications.</i><br>
 * <br>
 * The Protocol trusts all keys provided by the keystore that is used to initialize this {@link SslFactory}.<br>
 * <br>
 *
 * @author lhunath
 */
public class SslFactory implements SecureProtocolSocketFactory {

    private static final Logger logger = Logger.get( SslFactory.class );

    private static SslFactory   instance;
    private SSLContext          context;


    /**
     * Initialize this factory with the given keystore.
     *
     * @param keyStore
     *            The keystore that contains the trusted server keys.
     * @param password
     *            The password to access the keystore.
     * @return The factory.
     */
    public static SslFactory initialize(File keyStore, String password) {

        if (instance == null)
            instance = new SslFactory( keyStore, password );

        return instance;
    }

    /**
     * Create a protocol for use with HttpClient for Jakarta that supports https and uses the keys from our keystore.
     *
     * @return Guess.
     */
    public static Protocol createHttpsProtocol() {

        if (instance == null)
            throw new IllegalStateException( "The factory has not yet been initialized." );

        return new Protocol( "https", (ProtocolSocketFactory) instance, 443 );
    }

    private SslFactory(File keyStore, String password) {

        try {
            KeyStore store = KeyStore.getInstance( "JKS" );
            store.load( new FileInputStream( keyStore ), password.toCharArray() );

            TrustManagerFactory tFactory = TrustManagerFactory.getInstance( "SunX509" );
            tFactory.init( store );

            context = SSLContext.getInstance( "TLS" );
            context.init( null, tFactory.getTrustManagers(), null );
        } catch (KeyStoreException e) {
            logger.err( e, "Keystore type not supported or keystore could not be initialized." );
        } catch (NoSuchAlgorithmException e) {
            logger.err( e, "Key algorithm not supported." );
        } catch (CertificateException e) {
            logger.err( e, "An unexpected error has occurred!" );
        } catch (FileNotFoundException e) {
            logger.err( e, "Keystore not found!" );
        } catch (IOException e) {
            logger.err( e, "Could not read the keys from the keystore!" );
        } catch (KeyManagementException e) {
            logger.err( e, "Could not add the keys as trusted!" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException, UnknownHostException {

        return context.getSocketFactory().createSocket( socket, host, port, autoClose );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(String host, int port)
            throws IOException, UnknownHostException {

        return context.getSocketFactory().createSocket( host, port );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort)
            throws IOException, UnknownHostException {

        return context.getSocketFactory().createSocket( host, port, localAddress, localPort );
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated See {@link SSLSocketFactory#createSocket(String, int, InetAddress, int)}.
     */
    @Override
    @Deprecated
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
                               HttpConnectionParams params)
            throws IOException, UnknownHostException, ConnectTimeoutException {

        if (params == null)
            throw new IllegalArgumentException( "Parameters may not be null." );

        int timeout = params.getConnectionTimeout();
        if (timeout == 0)
            return context.getSocketFactory().createSocket( host, port, localAddress, localPort );

        throw new IllegalArgumentException( "Timeout is not supported." );
    }

    /**
     * All instances of {@link SslFactory} are the same.
     */
    @Override
    public boolean equals(Object obj) {

        return obj != null && obj.getClass().equals( SSLProtocolSocketFactory.class );
    }

    /**
     * All instances of {@link SslFactory} have the same hash code.
     */
    @Override
    public int hashCode() {

        return SSLProtocolSocketFactory.class.hashCode();
    }
}
