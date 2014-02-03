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

import com.lyndir.lhunath.opal.system.logging.Logger;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import javax.net.ssl.*;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.*;


/**
 * <i>{@link SSLFactory} - Factory for creating {@link Protocol} objects that can be used by Jakarta's HttpClient for establishing https
 * communications.</i><br> <br> The Protocol trusts all keys provided by the keystore that is used to initialize this {@link
 * SSLFactory}.<br> <br>
 *
 * @author lhunath
 */
public class SSLFactory implements SecureProtocolSocketFactory {

    static final Logger logger = Logger.get( SSLFactory.class );

    private final SSLContext context;

    /**
     * Initialize this factory with the given keystore.
     *
     * @param keyStore The keystore that contains the trusted server keys.
     * @param password The password to access the keystore.
     *
     * @return The factory.
     */
    public static SSLFactory initialize(final File keyStore, final String password) {

        return new SSLFactory( keyStore, password );
    }

    /**
     * Create a protocol for use with HttpClient for Jakarta that supports https and uses the keys from our keystore.
     *
     * @return Guess.
     */
    public Protocol createHTTPSProtocol() {

        return new Protocol( "https", (ProtocolSocketFactory) this, 443 );
    }

    private SSLFactory(final File keyStore, final String password) {


        try (InputStream keyStoreStream = new FileInputStream( keyStore )) {
            KeyStore store = KeyStore.getInstance( "JKS" );
            store.load( keyStoreStream, password.toCharArray() );

            TrustManagerFactory tFactory = TrustManagerFactory.getInstance( "SunX509" );
            tFactory.init( store );

            context = SSLContext.getInstance( "TLS" );
            context.init( null, tFactory.getTrustManagers(), null );
        }
        catch (final KeyStoreException e) {
            throw new IllegalArgumentException( "Keystore type not supported or keystore could not be used to initialize trust.", e );
        }
        catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException( "Key algorithm not supported.", e );
        }
        catch (final CertificateException e) {
            throw new IllegalArgumentException( "Keystore could not be loaded.", e );
        }
        catch (final FileNotFoundException e) {
            throw new IllegalArgumentException( "Keystore not found.", e );
        }
        catch (final IOException e) {
            throw new RuntimeException( "Could not read the keys from the keystore.", e );
        }
        catch (final KeyManagementException e) {
            throw new RuntimeException( "Could not use the keys for trust.", e );
        }
    }

    @Override
    public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose)
            throws IOException {

        return context.getSocketFactory().createSocket( socket, host, port, autoClose );
    }

    @Override
    public Socket createSocket(final String host, final int port)
            throws IOException {

        return context.getSocketFactory().createSocket( host, port );
    }

    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort)
            throws IOException {

        return context.getSocketFactory().createSocket( host, port, localAddress, localPort );
    }

    @Override
    @Deprecated
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort,
                               final HttpConnectionParams params)
            throws IOException {

        if (params == null)
            throw new IllegalArgumentException( "Parameters may not be null." );

        int timeout = params.getConnectionTimeout();
        if (timeout == 0)
            return context.getSocketFactory().createSocket( host, port, localAddress, localPort );

        throw new IllegalArgumentException( "Timeout is not supported." );
    }

    /**
     * All instances of {@link SSLFactory} are the same.
     */
    @Override
    public boolean equals(final Object obj) {

        return obj != null && obj.getClass().equals( SSLProtocolSocketFactory.class );
    }

    /**
     * All instances of {@link SSLFactory} have the same hash code.
     */
    @Override
    public int hashCode() {

        return SSLProtocolSocketFactory.class.hashCode();
    }
}
