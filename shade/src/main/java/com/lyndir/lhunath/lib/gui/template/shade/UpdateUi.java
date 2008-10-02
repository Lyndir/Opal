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
package com.lyndir.lhunath.lib.gui.template.shade;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.lyndir.lhunath.lib.system.Locale;
import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <i>{@link UpdateUi} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * 
 * @author lhunath
 */
public class UpdateUi extends Thread {

    private BlockingQueue<UpdateRequest> requests;
    private AbstractUi                   ui;
    private UpdateRequest                currentRequest;


    /**
     * Create a new {@link UpdateUi} instance.
     * 
     * @param ui
     *            The user interface that will process the request.
     */
    public UpdateUi(AbstractUi ui) {

        super( "Update UI" );
        setDaemon( true );

        requests = new ArrayBlockingQueue<UpdateRequest>( 20 );
        this.ui = ui;
    }

    /**
     * Add a request to the stack of requests to execute in the update thread.
     * 
     * @param uiRequest
     *            The request to execute in the update thread.
     */
    public void request(Request uiRequest) {

        UpdateRequest newRequest = new UpdateRequest( uiRequest, new RuntimeException(
                Locale.explain( "err.originates" ) + Thread.currentThread().getName() ) ); //$NON-NLS-1$

        /* Don't process a request if the next pending or currently executing request is the same. */
        if (newRequest.equals( currentRequest ) || requests.contains( newRequest ))
            return;

        /* Add this request to the request list. */
        synchronized (requests) {
            try {
                if (!requests.offer( newRequest, 500, TimeUnit.MILLISECONDS ))
                    throw new InterruptedException( "Maximum wait time elapsed." );
            } catch (InterruptedException e) {
                Logger.error( "err.updateQueueFull", newRequest );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        while (requests != null) {

            /* Not processing a request. */
            currentRequest = null;

            /* Take a new request from the queue, waiting for one if none is available yet. */
            try {
                currentRequest = requests.take();
            }

            /* Interrupted? Well, try again! */
            catch (InterruptedException e) {
                continue;
            }

            /* Process the request. */
            try {
                ui.process( currentRequest.getRequest() );
            }

            /* Uncaught exception occurred during the request. */
            catch (Throwable e) {
                Logger.error( e );
                Logger.error( currentRequest.getCause(), "Caused by this request." );
            }
        }
    }


    private class UpdateRequest {

        private Request   request;
        private Throwable cause;


        /**
         * Create a new {@link UpdateUi.UpdateRequest} instance.
         * 
         * @param request
         *            The request this stack element should make.
         * @param cause
         *            In case an exception gets thrown during the request, this will be set as the exception's cause.
         */
        public UpdateRequest(Request request, Throwable cause) {

            this.request = request;
            this.cause = cause;
        }

        /**
         * Retrieve the request of this {@link UpdateUi.UpdateRequest}.
         * 
         * @return Guess.
         */
        public Request getRequest() {

            return request;
        }

        /**
         * Retrieve the cause of this {@link UpdateUi.UpdateRequest}.
         * 
         * @return Guess.
         */
        public Throwable getCause() {

            return cause;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {

            if (obj == this)
                return true;
            if (obj instanceof UpdateRequest)
                return request.equals( ((UpdateRequest) obj).request );

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {

            return request.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            return request.toString();
        }
    }
}
