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

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;


/**
 * <i>GZIPPostMethod - A extended post method for Apache Commons HttpClient that supports GZip compression.</i><br>
 * <br>
 * Using this post method allows GZip compression of the upload stream if supported by the remote server; if and only if
 * enabled using {@link #useGZip(boolean)}.<br>
 * <br>
 *
 * @author lhunath
 */
public class GZIPPostMethod extends PostMethod {

    private boolean useGZip;


    /** Creates a new instance of GZIPAwarePostMethod */
    public GZIPPostMethod() {

    }

    /**
     * Constructor specifying a URI.
     *
     * @param uri
     *            either an absolute or relative URI
     */
    public GZIPPostMethod(String uri) {

        super( uri );
    }

    /**
     * Check whether we're using GZip or not.
     *
     * @return Guess.
     */
    public boolean isUsingGZip() {

        return useGZip;
    }

    /**
     * Set the useGZip of this GZIPPostMethod.
     *
     * @param enabled
     *            Guess.
     */
    public void useGZip(boolean enabled) {

        useGZip = enabled;
    }

    /**
     * Notifies the server that we can process a GZIP-compressed response before sending the request.
     *
     * {@inheritDoc}
     */
    @Override
    public int execute(HttpState state, HttpConnection conn)
            throws IOException {

        if (useGZip)
            addRequestHeader( "Accept-Encoding", "gzip" );

        return super.execute( state, conn );
    }

    /**
     * If the response body was GZIP-compressed, responseStream will be set to a GZIPInputStream wrapping the original
     * InputStream used by the superclass.
     *
     * {@inheritDoc}
     */
    @Override
    protected void readResponse(HttpState state, HttpConnection conn)
            throws IOException {

        super.readResponse( state, conn );

        Header contentEncodingHeader = getResponseHeader( "Content-Encoding" );
        if (contentEncodingHeader != null && "gzip".equalsIgnoreCase( contentEncodingHeader.getValue() ))
            setResponseStream( new GZIPInputStream( getResponseStream() ) );
    }

}
