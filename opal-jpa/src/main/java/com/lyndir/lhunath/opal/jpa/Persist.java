/*
 *   Copyright 2009, Maarten Billemont
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
package com.lyndir.lhunath.opal.jpa;

import static com.google.common.base.Preconditions.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;


/**
 * <h2>{@link Persist}<br> <sub>Utility class for getting the entity manager and handling transactions.</sub></h2>
 *
 * <p> <i>Nov 10, 2010</i> </p>
 *
 * @author mbillemo
 */
public class Persist {

    public static final  String               DEFAULT_UNIT = "DefaultDS";
    private static final ThreadLocal<Persist> persistences = new ThreadLocal<>();

    private final EntityManagerFactory emf;
    @Nullable
    private       EntityManager        em;
    @Nullable
    private       Object               transactionOwner;

    @Nonnull
    public static Persist persistence() {

        return checkNotNull( persistences.get(), "No persistence active." );
    }

    @Nonnull
    public static EntityManager entityManager() {

        return persistence().getEntityManager();
    }

    public Persist() {

        this( Persistence.createEntityManagerFactory( DEFAULT_UNIT ) );
    }

    public Persist(final String persistenceUnitName) {

        this( Persistence.createEntityManagerFactory( persistenceUnitName ) );
    }

    public Persist(final EntityManagerFactory emf) {

        this.emf = emf;
    }

    /**
     * Obtain the entity manager.  A new one will be created if none is open yet/anymore.
     *
     * @return The current entity manager.
     */
    public EntityManager getEntityManager() {

        if (em == null || !em.isOpen())
            em = emf.createEntityManager();

        return checkNotNull( em, "Failed to create an entity manager." );
    }

    /**
     * Begin a new transaction if one is not active yet.
     *
     * @param caller The party responsible for completing the transaction later on with a {@link #complete(Object)} call. Generally, just
     *               {@code this}.
     *
     * @return The current transaction.
     */
    public EntityTransaction begin(final Object caller) {

        if (transactionOwner == null) {
            getEntityManager().getTransaction().begin();
            transactionOwner = caller;
        }

        persistences.set( this );
        return getEntityManager().getTransaction();
    }

    /**
     * Close and clean up the entity manager if one is open.
     *
     * @return {@code true}  if an entity manager was open and has been closed as a result of this call.  {@code false}  if there
     * was no entity manager or it was not open.
     */
    private boolean close() {

        boolean didClose = false;
        if (em != null && em.isOpen()) {
            em.close();
            didClose = true;
        }

        em = null;

        persistences.remove();
        return didClose;
    }

    /**
     * Abort the current transaction if one is active.
     *
     * @return {@code true}  if an active transaction was rolled back as a result of this call.  {@code false}  if there was no
     * transaction or if it was not active anymore.
     */
    public boolean abort() {

        boolean didRollBack = false;
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
            didRollBack = true;
        }

        transactionOwner = null;
        close();

        return didRollBack;
    }

    /**
     * If a transaction is active and owned by the caller, commit it.
     *
     * @param caller The object that was passed as caller to {@link #begin(Object)}.  Generally, just {@code this} .
     *
     * @return {@code true}  if a transaction was active and the caller owned it, and the transaction was successfully committed.
     * {@code false}  otherwise.
     */
    public boolean complete(final Object caller) {

        boolean didComplete = false;
        EntityTransaction transaction = getEntityManager().getTransaction();

        //noinspection ObjectEquality
        if (transactionOwner == caller && transaction != null && transaction.isActive()) {
            transaction.commit();
            didComplete = true;

            transactionOwner = null;
            close();
        }

        return didComplete;
    }
}
